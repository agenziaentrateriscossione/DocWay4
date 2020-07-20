package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditAcquisizione extends DocEditDoc {
	private Doc doc = new Doc();
	
	private final String DEFAULT_ACQUISIZIONE_TITLE = "dw4.acquisizione_immagini";
	
	private String tipoDoc = "acquisizione"; // tipologia di documento da gestire in acquisizione immagini
	private String currYear = "";
	private String numProt = "";
	
	public DocEditAcquisizione() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document domDocumento) {
		doc = new Doc();
		doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		currYear = XMLUtil.parseStrictAttribute(domDocumento, "/response/@currYear", "");
		if (doc.getData_prot() == null || doc.getData_prot().equals(""))
			doc.setData_prot(XMLUtil.parseStrictAttribute(domDocumento, "/response/@currDate", ""));
		if (doc.getTipo() != null && !doc.getTipo().equals(""))
			tipoDoc = doc.getTipo();
		
		docEditTitle = I18N.mrs(DEFAULT_ACQUISIZIONE_TITLE);
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			// in caso di salvataggio occorre impostare a false il reset dei jobs di iwx
			// perche' si potrebbe tornare alla maschera di inserimento/modifica (in caso ad esempio di 
			// campi obbligatori non compilati)
			setResetJobsIWX(false);
			
			if (checkRequiredField()) return null;
			
			boolean fotoOriginale = formsAdapter.checkBooleanFunzionalitaDisponibile("foto_originale", false);
			
			formsAdapter.acquisizioneImmagini(fotoOriginale);
			formsAdapter.getDefaultForm().addParams(doc.asFormAdapterImagesParams(""));
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			if (fotoOriginale) {
				// redirect a showdoc del documento
				
				buildSpecificShowdocPageAndReturnNavigationRule(tipoDoc, response);		
				return "showdoc@" + tipoDoc + "@reload"; // TODO verificare se e' corretto in tutti i casi questa chiamata (esistono casi particolari?)
			}
			else {
				// azzeramento della pagina di acquisizione immagini
				
				DocEditAcquisizione docEditAcquisizione = new DocEditAcquisizione();
				docEditAcquisizione.getFormsAdapter().fillFormsFromResponse(response);
				docEditAcquisizione.init(response.getDocument());
				setSessionAttribute("docEditAcquisizione", docEditAcquisizione);
				return "docEdit@acquisizione";
			}
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Clean su maschera di acquisizione delle immagini (reload della pagina senza documento
	 * caricato)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String pulisciAcquisizioneImmagini() throws Exception {
		try {
			formsAdapter.pulisciAcquisizioneImmagini(currYear);
			getDoc().setNum_prot("");
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// relaod della pagina
			DocEditAcquisizione docEditAcquisizione = new DocEditAcquisizione();
			docEditAcquisizione.getFormsAdapter().fillFormsFromResponse(response);
			docEditAcquisizione.init(response.getDocument());
			setSessionAttribute("docEditAcquisizione", docEditAcquisizione);
			return "docEdit@acquisizione";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Abbandono della pagina di acquisizione immagini (foto originale) e ritorno a showdoc
	 * del documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String abbandonaFotoOriginale() throws Exception {
		try {
			formsAdapter.abbandonaFotoOriginale();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificShowdocPageAndReturnNavigationRule(doc.getTipo(), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}
	
	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	public String getNumProt() {
		return numProt;
	}

	public void setNumProt(String numprot) {
		this.numProt = numprot;
	}
	
	public String getCurrYear() {
		return currYear;
	}

	public void setCurrYear(String currYear) {
		this.currYear = currYear;
	}
	
	/**
	 * Caricamento del documento in base al numero di protocollo
	 * specificato
	 * 
	 * @return
	 * @throws Exception
	 */
	public String cercaProtocollo() throws Exception {
		try {
			if (currYear != null && !currYear.equals("") && numProt != null && !numProt.equals("")) {
				if (StringUtil.isNumber(numProt)) {
					
					// gestione multisocieta'
					String codSocieta = "";
					if (doc != null && doc.getCodSocieta() != null)
						codSocieta = doc.getCodSocieta();
					
					formsAdapter.cercaProtocollo(currYear, numProt, codSocieta);
					XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
						return null;
					}
					
					// aggiornamento del bean
					this.init(response.getDocument());
					this.formsAdapter.fillFormsFromResponse(response);
				}
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
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		String toDo = formsAdapter.getDefaultForm().getParam("toDo");
		if (toDo == null || toDo.equals("")) {
			// nessun documento selezionato
			this.setErrorMessage("templateForm:numProt", I18N.mrs("dw4.effettuare_la_ricerca_del_protocollo"));
			result = true;
		}
		else {
			// questo secondo controllo deve essere eseguito
			// solo se il primo e' verificato
			
			if (doc.getImmagini() == null || doc.getImmagini().size() == 0 
					|| doc.getImmagini().get(0).getName() == null || doc.getImmagini().get(0).getName().equals("")) {
				// nessun allegato caricato
				this.setErrorMessage("", I18N.mrs("dw4.inserire_almeno_un_immagine"));
				result = true;
			}
		}
		
		return result;
	}
	
}
