package it.tredi.dw4.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.adapters.DocWayLookupFormsAdapter;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.model.TitoloComposto;
import it.tredi.dw4.model.customfields.CustomFields;
import it.tredi.dw4.model.customfields.Field;
import it.tredi.dw4.model.customfields.FieldInstance;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

public class CustomFieldsLookup extends Lookup {
	
	private static final String XPATH_CUSTOM_FIELDS_IDENTIFIER = "section_";
	
	private DocWayLookupFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;
	private ArrayList<TitoloComposto> titoliComposti;
	
	private Field field = null;
	private FieldInstance instance = null;
	
	private CustomFields customFieldsModel; 
	
	public CustomFieldsLookup() throws Exception {
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

	
	public DocWayLookupFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}

	public ArrayList<TitoloComposto> getTitoliComposti() {
		return titoliComposti;
	}

	public void setTitoliComposti(ArrayList<TitoloComposto> titoliComposti) {
		this.titoliComposti = titoliComposti;
	}
	
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public FieldInstance getInstance() {
		return instance;
	}

	public void setInstance(FieldInstance instance) {
		this.instance = instance;
	}
	
	public Object getCustomFieldsModel() {
		return customFieldsModel;
	}

	public void setCustomFieldsModel(CustomFields customFieldsModel) {
		this.customFieldsModel = customFieldsModel;
	}
	
	/**
	 * Ripulisce i campi di lookup
	 */
	public void cleanFields(String campi) throws Exception {
		if (campi != null && campi.length() > 0) {
			// mbernardini 08/07/2019 : lookup misti fra campi custome campi specifici del documento
			List<Campo> campiL = new ArrayList<Campo>();
			Titolo titolo = new Titolo();
			
			String []campiArr = campi.split(" ; ");
			for (int i = 0; i < campiArr.length; i++) {
				try {
					String nomeCampo = campiArr[i].trim();
					nomeCampo = nomeCampo.substring(0, nomeCampo.indexOf("="));
					if (nomeCampo.startsWith(XPATH_CUSTOM_FIELDS_IDENTIFIER)) {
						// campo di destinazione -> campo custom
						this.customFieldsModel.setFieldValueFromLookup(nomeCampo, "");
					}
					else {
						// campo di destinazione -> campo specifico del documento
						if (this.model != null) {
							Campo campo = new Campo();
							campo.setNome(nomeCampo);
							if (!nomeCampo.equals(".oggetto")) // il campo oggetto (voce di indice) non deve essere svuotato
								campo.setText("");
							campiL.add(campo);
						}
						else {
							Logger.error("Unable to crean lookup field " + nomeCampo + "... Model is NULL!");
						}
					}
				}
				catch (Exception ex) {
					// campo di lookup che non fa riferimento ad un campo custom
					Logger.error(ex.getMessage(), ex);
				}
			}
			
			if (!campiL.isEmpty()) {
				titolo.setCampi(campiL);
				fillFields(titolo.getCampi(), false);
			}
		}
	}
	
	/**
	 * Personalizzazione della selezione di un titolo sui lookup
	 * di DocWay
	 */
	public String confirm(Titolo titolo) throws Exception{
		fillCustomFields(titolo.getCampi());
		return close();
	}
	
	/**
	 * Riempimento dei valori dei campi custom in base ai campi di un titolo
	 * di lookup
	 * 
	 * @param campiL campi del titolo di lookup da assegnare al bean
	 * @throws Exception
	 */
	private void fillCustomFields(List<Campo> campiL) throws Exception {
		if (campiL != null && campiL.size() > 0) {
			// mbernardini 08/07/2019 : lookup misti fra campi custome campi specifici del documento
			List<Campo> notCustom = new ArrayList<Campo>();
			
			for (int campiLIndex = 0; campiLIndex < campiL.size(); campiLIndex++) {
				try {
					String xpath = campiL.get(campiLIndex).getNome();
					String value = campiL.get(campiLIndex).getText();		
										
					if (xpath != null && !xpath.equals("")) {
						if (xpath.startsWith(XPATH_CUSTOM_FIELDS_IDENTIFIER)) {
							// campo di destinazione -> campo custom
							
							if (value == null)
								value = "";
							
							// assegnazione del/i valore/i selezionato/i tramite lookup al/i campo/i custom
							this.customFieldsModel.setFieldValueFromLookup(xpath, value); 
						}
						else {
							// campo di destinazione -> campo specifico del documento
							
							if (this.model != null)
								notCustom.add(campiL.get(campiLIndex));
							else {
								Logger.error("Unable to manage lookup field " + xpath + " [value = '" + value + "']... Model is NULL!");
							}
						}
					}
				}
				catch (Exception ex) {
					// campo di lookup che non fa riferimento ad un campo custom
					Logger.error(ex.getMessage(), ex);
				}
			}
			
			if (!notCustom.isEmpty()) {
				this.fillFields(notCustom, true); // set di eventuali campi sepcifici del model (documento/fascicolo/etc.)
			}
		}
	}
	
}
