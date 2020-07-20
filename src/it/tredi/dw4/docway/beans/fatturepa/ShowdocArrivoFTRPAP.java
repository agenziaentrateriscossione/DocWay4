package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.beans.ReloadMsg;
import it.tredi.dw4.docway.beans.ShowdocArrivo;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.docway.model.fatturepa.DatiFattura;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.docway.model.fatturepa.Notifica;
import it.tredi.dw4.docway.model.fatturepa.notifiche.EsitoCommittenteFattura;
import it.tredi.dw4.docway.model.fatturepa.notifiche.NotificaEsitoCommittente;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class ShowdocArrivoFTRPAP extends ShowdocArrivo {

	// campi specifici del repertorio di fatturePA passive
	private FatturaPA fatturaPA = new FatturaPA();
	
	private String fileidFattura = "";
	private String xslVisualizzazioneFattura = ""; // eventuale XSLT da applicare all'XML della fattura (specificato da file di properties)
	
	// codici da utilizzare per l'invio della notifica di 
	// esito committente (accettazione/rifiuto di fatture passive)
	private final String CODICE_ESITO_COMMITTENTE_ACCETTAZIONE = "EC01";
	private final String CODICE_ESITO_COMMITTENTE_RIFIUTO = "EC02";
	
	private final String NOTIFICA_ESITO_COMMITTENTE = "EC";
	//private final String NOTIFICA_SCARTO_ESITO_COMMITTENTE = "SE";
	private final String NOTIFICA_DECORRENZA_TERMINI = "DT";
	
	// parametri necessari alla gestione delle notifiche sulle
	// fatture passive
	private String statoFattura = ""; // identifica lo stato della fattura (es. '', 'DT', 'EC', ecc.)
	private boolean esitoCommittentePopupOpened = false; // popup di notifica esito committente su lotto di fatture
	private NotificaEsitoCommittente notificaEsitoCommittente = new NotificaEsitoCommittente();
	
	private boolean motivazioneRifiutoEsitoCommittenteOpened = false; // popup di inserimento motivazione del rifiuto di una fattura
	
	public ShowdocArrivoFTRPAP() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		Document docFattura = XMLUtil.createDocument(dom, "/response/doc/extra/fatturaPA");
		fatturaPA.init(docFattura);
		
		// recupero dell'xslt di visualizzazione
		xslVisualizzazioneFattura = FatturePaUtilis.getXsltFileForPreview(fatturaPA.getVersione());
		
		// richiesta al service del download del file XML della fattura (in caso di file P7M viene richiesto
		// il contenuto del file firmato (XML)
		fileidFattura = "";
		if (fatturaPA.getFileNameFattura() != null && !fatturaPA.getFileNameFattura().equals("") && getDoc().getFiles() != null && getDoc().getFiles().size() > 0) {
			int i = 0;
			while (fileidFattura.equals("") && i < getDoc().getFiles().size()) {
				XwFile xwFile = (XwFile) getDoc().getFiles().get(i);
				if (xwFile != null && xwFile.getTitle() != null && xwFile.getTitle().equals(fatturaPA.getFileNameFattura() + "." + fatturaPA.getExtensionFattura()))
					fileidFattura = xwFile.getName();
				i++;
			}
		}
		
		initStatoFattura();
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/fatturepa/showdoc@arrivo@FTRPAP");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	public FatturaPA getFatturaPA() {
		return fatturaPA;
	}

	public void setFatturaPA(FatturaPA fatturaPA) {
		this.fatturaPA = fatturaPA;
	}
	
	/**
	 * dato l'xml del documento recupera lo stato corrente della fattura in base all'analisi delle notifiche
	 */
	private void initStatoFattura() {
		String stato = "";
		
		List<String> fattureNotificate = new ArrayList<String>();
		if (fatturaPA.getNotifica().size() > 0) {
			for (int i=0; i<fatturaPA.getNotifica().size(); i++) {
				Notifica notifica = fatturaPA.getNotifica().get(i);
				if (notifica.getTipo().toUpperCase().equals(NOTIFICA_DECORRENZA_TERMINI)) {
					// decorrenza termini
					stato = NOTIFICA_DECORRENZA_TERMINI;
				}
				else if (!stato.equals(NOTIFICA_DECORRENZA_TERMINI) && notifica.getTipo().toUpperCase().equals(NOTIFICA_ESITO_COMMITTENTE) && notifica.getNumeroFattura().equals("") && notifica.getAnnoFattura().equals("") && notifica.getRiferita().equals("")) {
					// esito committente inviato su tutto il documento (singola fattura o lotto di fatture)
					stato = NOTIFICA_ESITO_COMMITTENTE;
				}
				else if (!stato.equals(NOTIFICA_DECORRENZA_TERMINI) && notifica.getTipo().toUpperCase().equals(NOTIFICA_ESITO_COMMITTENTE) && !notifica.getNumeroFattura().equals("") && !notifica.getAnnoFattura().equals("") && notifica.getRiferita().equals("")) {
					// esito committente su singola fattura
					fattureNotificate.add(notifica.getNumeroFattura());
				}
			}
						
			// se lo stato non e' ancora stato individuato verifico se e' stato inviato un esito committente
			// separato per ogni fattura presente nel documento (notifiche su singole fatture di un lotto)
			if (stato.equals("") && fattureNotificate.size() > 0 && fattureNotificate.size() == fatturaPA.getDatiFattura().size())
				stato = NOTIFICA_ESITO_COMMITTENTE;
		}
		statoFattura = stato;
		
		// inizializzazione dei dati di notifica
		notificaEsitoCommittente = new NotificaEsitoCommittente();
		if (statoFattura.equals("")) {
			if (fattureNotificate.size() == 0) {
				// nessuna fattura notificata, possibile ancora la notifica sull'intero documento/lotto di fatture
				notificaEsitoCommittente.setAccettazioneInteroDocumentoEnabled(true);
			}
			for (int i=0; i<fatturaPA.getDatiFattura().size(); i++) {
				DatiFattura datiFattura = fatturaPA.getDatiFattura().get(i);
				if (!fattureNotificate.contains(datiFattura.getDatiGeneraliDocumento().getNumero())) {
					// la fattura corrente non e' stata notificata
					EsitoCommittenteFattura esitoCommittenteFattura = new EsitoCommittenteFattura(datiFattura.getDatiGeneraliDocumento().getTipoDocumento(), 
															datiFattura.getDatiGeneraliDocumento().getNumero(), 
															datiFattura.getDatiGeneraliDocumento().getData(), 
															i);
					notificaEsitoCommittente.addEsitoFattura(esitoCommittenteFattura);
				}
			}
		}
		else {
			// il documento e' gia' stato notificato (stato = EC) o e' stata ricevuta una decorrenza
			// termini (stato = DT)
			// nessuna notifica possibile sul documento corrente
			notificaEsitoCommittente.setAccettazioneInteroDocumentoEnabled(false);
			notificaEsitoCommittente.setEsitiFatture(new ArrayList<EsitoCommittenteFattura>());
		}
	}
	
	/**
	 * mostra/nasconde la sezione relativi ai dettagli della fattura
	 * @return
	 * @throws Exception
	 */
	public String mostraNascondiDettagliFattura(String indexFattura) throws Exception {
		DatiFattura fattura = getFatturaByIndex(indexFattura);
		if (fattura != null)
			fattura.setShowDettagli(!fattura.isShowDettagli());
		return null;
	}
	
	/**
	 * mostra/nasconde la sezione relativi ai riferimenti della fattura
	 * @return
	 * @throws Exception
	 */
	public String mostraNascondiRiferimentiFattura(String indexFattura) throws Exception {
		DatiFattura fattura = getFatturaByIndex(indexFattura);
		if (fattura != null)
			fattura.setShowRiferimenti(!fattura.isShowRiferimenti());
		return null;
	}
	
	/**
	 * dato l'indice della lista restituisce la relativa fattura
	 * @param indexFattura
	 * @return
	 */
	private DatiFattura getFatturaByIndex(String indexFattura) {
		DatiFattura fattura = null;
		try {
			if (indexFattura != null && StringUtil.isNumber(indexFattura) && getFatturaPA() != null && getFatturaPA().getDatiFattura() != null) {
				int index = new Integer(indexFattura).intValue();
				if (index >= 0 && index < getFatturaPA().getDatiFattura().size()) 
					fattura = getFatturaPA().getDatiFattura().get(index);
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return fattura;
	}

	public String getXslVisualizzazioneFattura() {
		return xslVisualizzazioneFattura;
	}

	public void setXslVisualizzazioneFattura(String xslVisualizzazioneFattura) {
		this.xslVisualizzazioneFattura = xslVisualizzazioneFattura;
	}
	
	public String getFileidFattura() {
		return fileidFattura;
	}

	public void setFileidFattura(String fileidFattura) {
		this.fileidFattura = fileidFattura;
	}
	
	public String getStatoFattura() {
		return statoFattura;
	}

	public void setStatoFattura(String statoFattura) {
		this.statoFattura = statoFattura;
	}
	
	public boolean isEsitoCommittentePopupOpened() {
		return esitoCommittentePopupOpened;
	}

	public void setEsitoCommittentePopupOpened(boolean esitoCommittentePopupOpened) {
		this.esitoCommittentePopupOpened = esitoCommittentePopupOpened;
	}
	
	public NotificaEsitoCommittente getNotificaEsitoCommittente() {
		return notificaEsitoCommittente;
	}

	public void setNotificaEsitoCommittente(NotificaEsitoCommittente notificaEsitoCommittente) {
		this.notificaEsitoCommittente = notificaEsitoCommittente;
	}
	
	public boolean isMotivazioneRifiutoEsitoCommittenteOpened() {
		return motivazioneRifiutoEsitoCommittenteOpened;
	}

	public void setMotivazioneRifiutoEsitoCommittenteOpened(boolean motivazioneRifiutoEsitoCommittenteOpened) {
		this.motivazioneRifiutoEsitoCommittenteOpened = motivazioneRifiutoEsitoCommittenteOpened;
	}
	
    /**
     * ritorna l'id del file di fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedFileidFattura() {
		String encodedId = fileidFattura;
    	try {
    		encodedId = URLEncoder.encode(encodedId, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedId;
	}
	
	/**
     * ritorna il nome del file di fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedFileNameFattura() {
		String encodedTitle = fatturaPA.getFileNameFattura() + "." + fatturaPA.getExtensionFattura();
    	try {
    		encodedTitle = URLEncoder.encode(encodedTitle, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedTitle;
	}
	
	/**
     * ritorna il nome file xsl di preview della fattura encodato per l'utilizzo in URL
     * @return
     */
	public String getUrlEncodedXsltFileName() {
		String encodedXslt = xslVisualizzazioneFattura;
    	try {
    		encodedXslt = URLEncoder.encode(encodedXslt, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedXslt;
	}
	
	/**
	 * selezione delle fatture presenti in un lotto per la generazione della notifica di 
	 * esito committente
	 * @return
	 * @throws Exception
	 */
	public String openPopupEsitoCommittente() throws Exception {
		esitoCommittentePopupOpened = true;
		return null;
	}
	
	/**
	 * chiusura del popup di selezione delle fatture presenti in un lotto per la generazione della notifica
	 * di esito committente
	 * @return
	 * @throws Exception
	 */
	public String closePopupEsitoCommittente() throws Exception {
		esitoCommittentePopupOpened = false;
		return null;
	}
	
	/**
	 * visualizzazione del campo tramite il quale indicare l'eventuale motivazione di rifiuto di una fattura
	 * @return
	 * @throws Exception
	 */
	public String openPopupMotivazioneRifiuto() throws Exception {
		motivazioneRifiutoEsitoCommittenteOpened = true;
		return null;
	}
	
	/**
	 * chiusura del popup di inserimento motivazione di rifiuto di una fattura
	 * @return
	 * @throws Exception
	 */
	public String closePopupMotivazioneRifiuto() throws Exception {
		motivazioneRifiutoEsitoCommittenteOpened = false;
		return null;
	}
	
	/**
	 * invio della notifica di esito committente POSITIVO al Servizio di Intersambio
	 * @return
	 * @throws Exception
	 */
	public String sendEsitoCommittentePositivo() throws Exception {
		return sendEsitoCommittente("*="+CODICE_ESITO_COMMITTENTE_ACCETTAZIONE);
	}
	
	/**
	 * invio della notifica di esito committente NEGATIVO al Servizio di Intersambio
	 * @return
	 * @throws Exception
	 */
	public String sendEsitoCommittenteNegativo() throws Exception {
		return sendEsitoCommittente("*="+CODICE_ESITO_COMMITTENTE_RIFIUTO);
	}
	
	/**
	 * invio dell'esito committente in caso di lotto di fatture (possibile accettazione/rifiuto di singole
	 * fatture)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String sendEsitoCommittenteSingoleFatture() throws Exception {
		try {
			String dettaglioEsito = "";
			if (notificaEsitoCommittente.getEsitoInteroDocumento() != null && notificaEsitoCommittente.getEsitoInteroDocumento().length() > 0) {
				// notifica su intero documento (lotto di fatture)
				if (notificaEsitoCommittente.getEsitoInteroDocumento().equals(CODICE_ESITO_COMMITTENTE_ACCETTAZIONE) || notificaEsitoCommittente.getEsitoInteroDocumento().equals(CODICE_ESITO_COMMITTENTE_RIFIUTO))
					dettaglioEsito = "*=" + notificaEsitoCommittente.getEsitoInteroDocumento();
			}
			else {
				// notifica su singole fatture presenti nel documento
				for (int i=0; i<notificaEsitoCommittente.getEsitiFatture().size(); i++) {
					EsitoCommittenteFattura esitoCommittente = notificaEsitoCommittente.getEsitiFatture().get(i);
					if (esitoCommittente.getEsito() != null && esitoCommittente.getEsito().length() > 0) {
						if (esitoCommittente.getEsito().equals(CODICE_ESITO_COMMITTENTE_ACCETTAZIONE) || esitoCommittente.getEsito().equals(CODICE_ESITO_COMMITTENTE_RIFIUTO))
							dettaglioEsito += esitoCommittente.getPosizione() + "=" + esitoCommittente.getEsito() + ",";
					}
				}
				if (dettaglioEsito.endsWith(","))
					dettaglioEsito = dettaglioEsito.substring(0, dettaglioEsito.length()-1);
			}
			
			if (dettaglioEsito.length() > 0)
				return sendEsitoCommittente(dettaglioEsito);
			else {
				
				Errormsg errormsg = new Errormsg();
				Errore errore = new Errore();
				errore.setLevel(ErrormsgFormsAdapter.WARNING);
				errore.setUnexpected(false);
				errore.setErrtype(I18N.mrs("dw4.non_e_stato_selezionato_alcun_esito_per_l_intero_documento_o_per_le_singole_fatture"));
				errormsg.setErrore(errore);
				errormsg.setActive(true);
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("errormsg", errormsg);
				
				return null; 
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * invio della notifica di esito committente al Servizio di Intersambio
	 * @param dettaglioEsito 
	 * @return
	 * @throws Exception
	 */
	private String sendEsitoCommittente(String dettaglioEsito) throws Exception {
		try {
			
			if (checkDatiObbligatoriPerEsitoCommittente()) {
				formsAdapter.sendEsitoCommittente(dettaglioEsito, notificaEsitoCommittente.getMotivazioneRifiuto());
				
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				esitoCommittentePopupOpened = false;
				motivazioneRifiutoEsitoCommittenteOpened = false;
				
				// lettura del messaggio di ritorno
				ReloadMsg message = new ReloadMsg();
				message.setActive(true);
				message.init(response.getDocument());
				message.setLevel(Const.MSG_LEVEL_SUCCESS);
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				reload(); // reload del documento
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("reloadmsg", message);
				
				return null;
			}
			else {
				// non tutti i campi necessari all'invio della notifica esito committente
				// sono stati registrati sul documento, genero un messaggio di ritorno per l'operatore
				
				Errormsg errormsg = new Errormsg();
				Errore errore = new Errore();
				errore.setLevel(ErrormsgFormsAdapter.ERROR);
				errore.setUnexpected(false);
				errore.setErrtype(I18N.mrs("dw4.parametri_necessari_all_invio_della_notifica_committente_mancanti"));
				errormsg.setErrore(errore);
				errormsg.setActive(true);
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("errormsg", errormsg);
			}
						
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * controlla la presenza dei dati della fattura (all'interno del documento) necessari all'invio
	 * della notifica di esito committente
	 * @return true se tutti i dati obbligatori sono registrati sul documento, false atrimenti
	 */
	private boolean checkDatiObbligatoriPerEsitoCommittente() {
		boolean check = false;
		
		if (getFatturaPA().getIdentificativoSdI() != null && getFatturaPA().getIdentificativoSdI().length() > 0
				&& getFatturaPA().getEmailSdI() != null && getFatturaPA().getEmailSdI().length() > 0
				&& getFatturaPA().getFileNameFattura() != null && getFatturaPA().getFileNameFattura().length() > 0)
			check = true;
		
		return check;
	}
	
}
