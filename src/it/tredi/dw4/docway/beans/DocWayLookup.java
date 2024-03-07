package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Lookup;
import it.tredi.dw4.docway.adapters.DocWayLookupFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.model.TitoloComposto;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dom4j.Document;

public class DocWayLookup extends Lookup {
	private DocWayLookupFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;
	private ArrayList<TitoloComposto> titoliComposti;
	
	private String tipoDoc = ""; // Tipologia di documento al quale il lookup fa riferimento
	private String xverbDoc = ""; // xverb della docedit del documento (inserimento/modifica)
	
	public DocWayLookup() throws Exception {
		this.formsAdapter = new DocWayLookupFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {
    	xml = domTitoli.asXML();
    	this.titoli = (ArrayList<Titolo>) XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
    	
    	// Recupero la tipologia di lookup in base al formato dei titoli
    	setLookupType(domTitoli);
    	
    	setLookupXq(XMLUtil.parseAttribute(domTitoli, "response/@lookup_xq")); // assegnazione di lookup_xq
    	
    	if (getLookupType().length() > 0 && !getLookupType().equals("standard")) {
    		// Genero i titoli composti in base ai titoli restituiti dal lookup. Devo utilizzare
    		// una sortedMap di appoggio perche' il alcuni casi i titoli restituiti dal service
    		// non sono ordinati nel modo corretto (es. due indirizzi di una stessa struttura sono
    		// separati da un'altra struttura/persona)
    		this.titoliComposti = new ArrayList<TitoloComposto>();
    		SortedMap<Integer, TitoloComposto> sortedTitles = new TreeMap<Integer, TitoloComposto>();
    		for (int i=0; i<titoli.size(); i++) {
    			Titolo titolo = (Titolo) titoli.get(i);
    			
    			// Genero l'eventuale sottotitolo da agganciare al titolo corrente
    			titolo.setSubtitle(_recuperaSottotitolo(titolo));
    			
    			if (titolo != null) {
    				int indice = new Integer(titolo.getIndice()).intValue();
    				
    				if (sortedTitles.containsKey(indice)) {
    					// Esiste gia' un titolo composto con l'indice specificato
    					TitoloComposto titoloComposto = sortedTitles.get(indice);
    					titoloComposto.addTitolo(titolo);
    					sortedTitles.put(indice, titoloComposto);
    				}
    				else {
    					// Non esiste ancora un titolo composto con l'indice specifciato
    					TitoloComposto titoloComposto = new TitoloComposto();
    					titoloComposto.addTitolo(titolo);
    					sortedTitles.put(indice, titoloComposto);
    				}
    			}
    			// TODO Gestire eventuali errori interni al ciclo?
    		}
    		this.titoliComposti = new ArrayList<TitoloComposto>(sortedTitles.values());
    	}
    }
	
	/**
	 * Recupera l'eventuale sottotitolo del titolo di lookup in base
	 * alla tipologia di lookup
	 * @param titolo
	 * @return
	 */
	private String _recuperaSottotitolo(Titolo titolo) {
		String sottotitolo = "";
		if (titolo != null && titolo.getCampi() != null && titolo.getCampi().size() > 0) {
			if (getLookupType().equals("mittente") || getLookupType().equals("firmatario")) {
				String indirizzo = "";
				String email = "";
				String emailCert = "";
				for (int i=0; i<titolo.getCampi().size(); i++) {
					Campo campo = (Campo) titolo.getCampi().get(i);
					if (campo != null) {
						if (indirizzo.equals("") && campo.getNome().endsWith(".indirizzo")) {
							if (!campo.getText().equals("- () -"))
								indirizzo = campo.getText();
						}
						else if (email.equals("") && campo.getNome().endsWith(".@email")) { 
							int index = campo.getText().indexOf(";");
							if (index != -1)
								email = campo.getText().substring(0, index);
							else
								email = campo.getText();
						}
						else if (emailCert.equals("") && campo.getNome().endsWith(".@email_certificata")) {
							emailCert = campo.getText();
						}
					}
				}
				
				if (indirizzo.trim().length() > 0) {
					if (titolo.getType() != null && titolo.getType().length() > 0)
						sottotitolo = titolo.getType().substring(0, 1) + indirizzo;
					else
						sottotitolo = indirizzo;
				}
				if (emailCert.trim().length() > 0) 
					sottotitolo = sottotitolo + " [" + emailCert + "]";
				else if (email.trim().length() > 0)
					sottotitolo = sottotitolo + " [" + email + "]";
			}
			else if (getLookupType().equals("voceIndice")) {
				String oggetto = "";
				int i=0;
				while (oggetto.equals("") && i<titolo.getCampi().size()) {
					Campo campo = (Campo) titolo.getCampi().get(i);
					if (campo != null && campo.getNome().endsWith(".oggetto"))
						oggetto = campo.getText();
					
					i = i + 1;
				}
				sottotitolo = oggetto;
			}
		}
		return sottotitolo;
	}
	

	
	public DocWayLookupFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	public String getXverbDoc() {
		return xverbDoc;
	}

	public void setXverbDoc(String xverb) {
		this.xverbDoc = xverb;
	}

	public ArrayList<TitoloComposto> getTitoliComposti() {
		return titoliComposti;
	}

	public void setTitoliComposti(ArrayList<TitoloComposto> titoliComposti) {
		this.titoliComposti = titoliComposti;
	}
	
	/**
	 * Ripulisce i campi di lookup
	 */
	public void cleanFields(String campi) throws Exception {
		// In caso di lookup su rifesterni (mittente, firmatario, ecc.) rielabora il formato
		// del nome del campo in modo da adattarlo al formato del bean
		super.cleanFields(StringUtil.replace(campi, ".rif_esterni.rif", ".rif_esterni"));
	}
	
	/**
	 * Personalizzazione della selezione di un titolo sui lookup
	 * di DocWay
	 */
	public String confirm(Titolo titolo) throws Exception{
		if (getLookupType().equals("mittente") || getLookupType().equals("firmatario")) {
			// Lookup su campo mittente o firmatario
			
			String xPathRifEst = ""; // Recupera l'xPath relativo al rif esterno: .rif_esterni.rif[x] dove x e' l'indice del rif esterno
			
			List<Campo> campiElaborati = new ArrayList<Campo>();
			if (titolo != null && titolo.getCampi() != null && titolo.getCampi().size() > 0) {
				for (int i=0; i<titolo.getCampi().size(); i++) {
					Campo campi = (Campo) titolo.getCampi().get(i);
					if (campi != null) {
						String value = campi.getText();		
						String xpath = campi.getNome();
						
						if (xPathRifEst.length() == 0 && xpath.startsWith(".rif_esterni")) {
							xPathRifEst = xpath.substring(0, xpath.indexOf("]")+1); // .rif_esterni.rif[x]
							xPathRifEst = xPathRifEst.replace(".rif[", "["); // .rif_esterni[x]
						}
						
						if (xpath.equals("telefono") && value != null) {
							// gestione del campo telefono/fax
							String[] datiTelefoni = StringUtil.split(value, "!");
							if (datiTelefoni.length == 2) {
								String[] telfax = StringUtil.split(datiTelefoni[0], ";");
								String[] tipi = StringUtil.split(datiTelefoni[1], ";"); 
								
								String telefono = getFirstInst(telfax, tipi, "tel");
								if (telefono.length() > 0) {
									Campo tmpCampi = new Campo();
									tmpCampi.setNome(xPathRifEst + ".@tel");
									tmpCampi.setText(telefono);
									
									campiElaborati.add(tmpCampi);
								}
								
								String fax = getFirstInst(telfax, tipi, "fax");
								if (fax.length() > 0) {
									Campo tmpCampi = new Campo();
									tmpCampi.setNome(xPathRifEst + ".@fax");
									tmpCampi.setText(fax);
									
									campiElaborati.add(tmpCampi);
								}
							}
						}
						else {
							if (xpath.startsWith(".rif_esterni.rif")) {
								// elaborazione dell'xPath in modo da permettere il corretto
								// riempimento del bean
								campi.setNome(StringUtil.replace(xpath, ".rif_esterni.rif", ".rif_esterni"));
								
								if (xpath.endsWith(".indirizzo") && value.equals("- () -"))
									campi.setText("");
							}
							campiElaborati.add(campi);
							
							if (xpath.startsWith(".rif_esterni.rif") && xpath.endsWith(".nome")) { // oltre al nome registro anche il tipo di rif
								// mbernardini 23/10/2015 : in caso di lookup su mittente/destinatario che restituisce una persona non deve essere possibile
								// eseguire il secondo lookup su firmatario/cortese attenzione
								try {
									Campo campo = new Campo();
									String xpathtipo = xpath.replaceAll(".rif_esterni.rif", ".rif_esterni").replaceAll(".nome", ".tipo");
									campo.setNome(xpathtipo);
									if (titolo.getType() != null && titolo.getType().equals("PERS"))
										campo.setText(RifEsterno.TIPORIF_PERSONA_ESTERNA);
									else
										campo.setText(RifEsterno.TIPORIF_STRUTTURA_ESTERNA);
									campiElaborati.add(campo);
								}
								catch (Exception e) {
									Logger.warn("DocWayLookup.config(): got exception... " + e.getMessage());
								}
							}
						}
					}
				}
				
				super.fillFields(campiElaborati, true);
			}
		
			return close();
		}
		else if (getLookupType().equals("voceIndice")) {
			// Lookup su campo voce di indice
			
			List<Campo> campiElaborati = new ArrayList<Campo>();
			if (titolo != null && titolo.getCampi() != null && titolo.getCampi().size() > 0) {
				for (int i=0; i<titolo.getCampi().size(); i++) {
					Campo campi = (Campo) titolo.getCampi().get(i);
					if (campi != null) {
						String value = campi.getText();		
						String xpath = campi.getNome();
						
						if (xpath.equals("tit_rpa_cc") && value != null) {
							// Aggiornamento dei responsabili del doc
							String validitaTabella = "";
							if (titolo.getCampiTitolo().containsKey("validita_tabella") && titolo.getCampiTitolo().get("validita_tabella") != null)
								validitaTabella = (String) titolo.getCampiTitolo().get("validita_tabella");
							
							if (isEnabledRifIntUpdateFromVoceIndice(validitaTabella)) {
								int index = value.indexOf("!");
								int index1 = value.indexOf("!", index+1);
								String[] nomePersona = value.substring(0, index).split(";");
								String[] nomeUff = value.substring(index+1, index1).split(";");
								
								// Federico 29/10/07: introdotti ruoli nelle voci di indice [RW 0047756]
								int index2 = value.indexOf("!", index1+1);
			                    String diritti = "";
			                    String[] tipoUff = {};
								if (index2 != -1) {
			                    	diritti = value.substring(index1+1, index2);
			                    	String tipoUfftmp = value.substring(index2+1);
			                    	if (tipoUfftmp.endsWith(";"))
			                    		tipoUfftmp = tipoUfftmp + " "; // Per evitare una dimensione minore dell'array in fase di split
			                    	tipoUff = tipoUfftmp.split(";");
								}
								else {
									diritti = value.substring(index1+1);
								}
								
								String diritto[] = diritti.split(";");
								
								// In questo caso opero sempre su un documento, quindi per semplicita' posso
								// eseguire il casting da XMLEntity a Doc
								Doc doc = (Doc) model;
								if (doc != null) {
									if (titolo.isOverrideCC()) {
										// Azzero tutti i CC del documento
										// mbernardini 17/12/2015 : azzeramento di CC e CDS solo se effettivamente esistono CC e CDS all'interno del titolo
										if (value.indexOf("CC") != -1)
											doc.setAssegnazioneCC(new ArrayList<Rif>());
										if (value.indexOf("CDS") != -1)
											doc.setAssegnazioneCDS(new ArrayList<Rif>());
									}
									
									// Assegno tutti i responsabili al documento
									for (int j=0; j<nomeUff.length; j++) {
										if (diritto[j].equals("RPA")) {
											if (nomeUff[j].length() > 0) {
												// Federico 29/10/07: introdotti ruoli nelle voci di indice [RW 0047756]
												if (tipoUff != null && tipoUff.length > 0) {
													if (tipoUff[j].equals("ruolo")) {
														// Aggiunta di un ruolo come RPA
														doc.getAssegnazioneRPA().setTipo_uff("ruolo");
													}
													else {
														// Aggiunta di un ufficio/persona come RPA
														doc.getAssegnazioneRPA().setTipo_uff("");
													}
												}
												doc.getAssegnazioneRPA().setCod_uff("");
												doc.getAssegnazioneRPA().setNome_uff(nomeUff[j]);
												doc.getAssegnazioneRPA().setCod_persona("");
												doc.getAssegnazioneRPA().setNome_persona(nomePersona[j]);
											}
										}
										else {
											if (nomeUff[j].length() > 0) {
												if (diritto[j].equals("CDS")) {
													doc.setShowCDSSection(true); // Attivo la sezione CDS della maschera del doc
													
													Rif rifCDS = new Rif();
													
													// Federico 29/10/07: introdotti ruoli nelle voci di indice [RW 0047756]
													if (tipoUff != null && tipoUff.length > 0 && tipoUff[j].equals("ruolo")) {
														// Aggiunta di un ruolo come CDS
														rifCDS.setTipo_uff("ruolo");
													}
													rifCDS.setCod_uff("");
													rifCDS.setNome_uff(nomeUff[j]);
													rifCDS.setCod_persona("");
													rifCDS.setNome_persona(nomePersona[j]);
													
													doc.getAssegnazioneCDS().add(rifCDS);
												}
												else if (diritto[j].equals("CC")) {
													Rif rifCC = new Rif();
													
													// Federico 29/10/07: introdotti ruoli nelle voci di indice [RW 0047756]
													if (tipoUff != null && tipoUff.length > 0 && tipoUff[j].equals("ruolo")) {
														// Aggiunta di un ruolo come CDS
														rifCC.setTipo_uff("ruolo");
													}
													rifCC.setCod_uff("");
													rifCC.setNome_uff(nomeUff[j]);
													rifCC.setCod_persona("");
													rifCC.setNome_persona(nomePersona[j]);
													
													doc.getAssegnazioneCC().add(rifCC);
												}
											}
										}
									}
									
									// Nel caso le liste di CC e CDS siano vuote dopo la sostituzione aggiungo la 
									// prima istanza (necessaria alla generazione del form di inserimento/modifica)
									if (doc.getAssegnazioneCC() == null || doc.getAssegnazioneCC().isEmpty())
										doc.addRifintCC(new Rif());
									if (doc.getAssegnazioneCDS() == null || doc.getAssegnazioneCDS().isEmpty())
										doc.addRifintCDS(new Rif());
								}
							}
						}
						else {
							// Gli altri campi vengono passati inalterati a 'fillFields()'
							campiElaborati.add(campi);
						}
					}
				}
				
				super.fillFields(campiElaborati, true);
			}
		
			return close();
		}
		
		return super.confirm(titolo);
	}
	
	/**
	 * Verifica se e' abilitato o meno l'aggiornamento dei rif interni (assegnatari) derivante da 
	 * una selezione di voce di indice sul documento corrente
	 * 
	 * @param validitaTabella  
	 * @return true se occorre aggiornare gli assegnatari del documento, false altrimenti
	 */
	private boolean isEnabledRifIntUpdateFromVoceIndice(String validitaTabella) {
		// l'aggiornamento e' possibile solo in fase di inserimento (a meno che non sia 
		// attivato l'aggiornamento anche in modifica da properties)
		if (getXverbDoc().equals("@modify") && !getFormsAdapter().checkBooleanFunzionalitaDisponibile("rifInterniModificabiliDaVoceIndice", false))
			return false;
		
		// l'aggiornamento e' possibile solo se verificata validitaTabella
		if (validitaTabella.indexOf(tipoDoc) == -1) 
			return false;
		
		try {
			if (isDoc() && getXverbDoc().equals("@modify")) {
				Doc doc = (Doc) model;
				
				// Aggiornamento possibile solo se si tratta di un doc non protocollato o di un protocollo in bozza. In entrambi
				// i casi il documento non deve essere fascicolato
				
				if (!doc.getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE) && !doc.isBozza())
					return false;
				
				if (doc.getFasc_rpa().getNum() != null && doc.getFasc_rpa().getNum().length() > 0)
					return false;
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * verifica se il model corrente fa riferimento ad un documento o ad un altro oggetto (es. fascicolo, raccoglitore)
	 */
	private boolean isDoc() {
		if (tipoDoc.equals(Const.DOCWAY_TIPOLOGIA_ARRIVO) 
				|| tipoDoc.equals(Const.DOCWAY_TIPOLOGIA_PARTENZA)
				|| tipoDoc.equals(Const.DOCWAY_TIPOLOGIA_INTERNO)
				|| tipoDoc.equals(Const.DOCWAY_TIPOLOGIA_VARIE))
			return true;
		else
			return false;
	}
	
	/**
	 * Recupera il numero di telefono in base alla tipologia richiesta dall'elenco dei telefoni (con
	 * relativo tipo) restituito dal lookup su mittente/firmatario in doc in arrivo
	 * 
	 * @param telefoni elenco di telefoni restituiti dal lookup
	 * @param tipiTelefono elenco di tipologie di telefoni restituiti dal lookup
	 * @param tipo tipologia di telefono da ricerca
	 * @return
	 */
	private String getFirstInst(String[] telefoni, String[] tipiTelefono, String tipo) {
		if (telefoni != null && tipiTelefono != null && telefoni.length == tipiTelefono.length) {
			for (int i = 0; i < tipiTelefono.length; i++)
				if (tipiTelefono[i].equals(tipo))
					return telefoni[i];
		}
		return ""; // Se non trovato elemento con tipo corrispondente
	} 
	
}
