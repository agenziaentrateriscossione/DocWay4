package it.tredi.dw4.docway.model;

import it.tredi.dw4.docway.model.dexia.Disposizione;
import it.tredi.dw4.docway.model.dexia.UnitaRichiamata;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class Extra extends XmlEntity {

	private String tipo_repertorio = "";
	private List<ConservazioneBridge> conservazione = new ArrayList<ConservazioneBridge>();
	private String stato_deposito = "";
	private String stato_deposito_minuta = "";
	private String codice_progetto = "";
	private String custom_select_1 = "";
	private String custom_select_2 = "";
	private List<ApprovazioneDoc> approvazione = new ArrayList<ApprovazioneDoc>(); // stati di approvazione del documento (sezione extra, legata a workflow di bonita) 
	
	// campi specifici del fascicolo di Sogin
	private String tipologia_fascicolo = "";
	private String numero_gara = "";
	private String numero_oda_sap = "";
	private String fornitore = ""; // ... e di un repertorio
	private String fornitore_codice = ""; // ... e di un repertorio
	
	// campi dei repertori di Sogin
	private String et_classificaelaborato = "";
	private String et_titoloelaborato = "";
	private String customSelectSedeArchivio = "";
	private String et_zonaarchivio = "";
	private String et_notearchivio = "";
	
	// campi per repertorio Norme Aziendali di Dexia
	private String na_codiceNorma;
	private String na_releaseNorma;
	private String na_statoNorma;
	private String na_competenza;
	private List<Disposizione> na_disposizioni = new ArrayList<Disposizione>();
	private List<UnitaRichiamata> na_unita = new ArrayList<UnitaRichiamata>();
	
	// 20150112 fcappelli - rimosso per richiesta cliente
	//private String na_dataEmanazioneNorma; 
	
	// lista utilizzata per individuare i parametri di extra che al momento del caricamento
	// del bean avevano un valore associato, in modo da passare al service solo i parametri 
	// corretti e creare un sottoelemento extra (del documento o del fascicolo) con i soli 
	// parametri da gestire e non con tutti i possibili extra gestiti dai diversi clienti/applicativi
	// Con questa gestione si genera un XML piu' "pulito"
	private List<String> notEmptyParams = new ArrayList<String>();
	
	// dpranteda 17/03/2015 : pubblicazione albo On-Line esterno
	private String alboExt_stato = "";
	private String alboExt_dataPubblicazione = "";
	private String alboExt_dataDefissione = "";
	
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		tipo_repertorio 			= assignExtraParam(dom, "extra/tipologie_repertorio/tipologia_repertorio", false, "tipo_repertorio");
		
		conservazione = XMLUtil.parseSetOfElement(dom, "extra/conservazione", new ConservazioneBridge());
		
		stato_deposito 				= assignExtraParam(dom, "extra/deposito/@stato", true, "stato_deposito");
		stato_deposito_minuta		= assignExtraParam(dom, "extra/deposito_minuta/@stato", true, "stato_deposito_minuta");
		codice_progetto 			= assignExtraParam(dom, "extra/progetto/@codice", true, "codice_progetto");
		custom_select_1 			= assignExtraParam(dom, "extra/customSelect1", false, "custom_select_1");
		custom_select_2 			= assignExtraParam(dom, "extra/customSelect2", false, "custom_select_2");
		
		approvazione 				= XMLUtil.parseSetOfElement(dom, "extra/approvazione", new ApprovazioneDoc()); // solo in lettura, viene settato tramite workflow
		
		// campi specifici del fascicolo di Sogin
		tipologia_fascicolo 		= assignExtraParam(dom, "extra/tipologia_fascicolo", false, "tipologia_fascicolo");
		numero_gara 				= assignExtraParam(dom, "extra/numero_gara", false, "numero_gara");
		numero_oda_sap 				= assignExtraParam(dom, "extra/numero_oda_sap", false, "numero_oda_sap");
		fornitore 					= assignExtraParam(dom, "extra/fornitori/fornitore", false, "fornitore");
		fornitore_codice 			= assignExtraParam(dom, "extra/fornitori/fornitore/@codice", true, "fornitore_codice");
		
		// campi dei repertori di Sogin
		et_classificaelaborato 		= assignExtraParam(dom, "extra/ET_ClassificaElaborato", false, "et_classificaelaborato");
		et_titoloelaborato 			= assignExtraParam(dom, "extra/ET_TitoloElaborato", false, "et_titoloelaborato");
		customSelectSedeArchivio	= assignExtraParam(dom, "extra/customSelectSedeArchivio", false, "customSelectSedeArchivio");
		et_zonaarchivio 			= assignExtraParam(dom, "extra/ET_ZonaArchivio", false, "et_zonaarchivio");
		et_notearchivio 			= assignExtraParam(dom, "extra/ET_NoteArchivio", false, "et_notearchivio", false);
		
		// campi per repertorio Norme Aziendali di Dexia
		na_codiceNorma			= assignExtraParam(dom, "extra/NA_codiceNorma", false, "na_codiceNorma");
		na_releaseNorma			= assignExtraParam(dom, "extra/NA_releaseNorma", false, "na_releaseNorma");
		na_statoNorma			= assignExtraParam(dom, "extra/NA_statoNorma", false, "na_statoNorma");
		na_competenza			= assignExtraParam(dom, "extra/NA_competenza", false, "na_competenza");
		na_disposizioni = XMLUtil.parseSetOfElement(dom, "extra/disposizioni/NA_disposizione", new Disposizione());
		na_unita = XMLUtil.parseSetOfElement(dom, "extra/unitaRichiamate/NA_unitaRichiamata", new UnitaRichiamata());
		// na_dataEmanazioneNorma	= assignExtraParam(dom, "extra/NA_codiceNorma/@NA_dataEmanazioneNorma", true, "na_dataEmanazioneNorma");
		
		if (na_disposizioni.size() == 0) na_disposizioni.add(new Disposizione());
		if (na_unita.size() == 0) na_unita.add(new UnitaRichiamata());
		
		//pubblicazione albo On-Line esterno
		alboExt_stato 				= assignExtraParam(dom, "extra/albo_ext/@stato", true, "stato");
		alboExt_dataPubblicazione 	= assignExtraParam(dom, "extra/albo_ext/@data_pubblicazione", true, "data_pubblicazione");
		alboExt_dataDefissione 		= assignExtraParam(dom, "extra/albo_ext/@data_defissione", true, "data_defissione");
		
		return this;
	}
	
	/**
	 * Assegnazione di un valore (tramite recupero con xpath) ad un parametro di Extra. Inclusa
	 * la gestione dei parametri extra valorizzati
	 * 
	 * @param dom
	 * @param xpath
	 * @param nomeParam
	 * @return
	 */
	private String assignExtraParam(Document dom, String xpath, boolean isAttribute, String nomeParam) {
		return assignExtraParam(dom, xpath, isAttribute, nomeParam, true);
	}
	
	/**
	 * Assegnazione di un valore (tramite recupero con xpath) ad un parametro di Extra. Inclusa
	 * la gestione dei parametri extra valorizzati
	 * 
	 * @param dom
	 * @param xpath
	 * @param nomeParam
	 * @return
	 */
	private String assignExtraParam(Document dom, String xpath, boolean isAttribute, String nomeParam, boolean trimText) {
		if (dom == null || xpath == null || xpath.length() == 0)
			return "";
		
		String value = "";
		if (isAttribute)
			value = XMLUtil.parseStrictAttribute(dom, xpath);
		else
			value = XMLUtil.parseStrictElement(dom, xpath, trimText);
		
		if (value.length() > 0)
			notEmptyParams.add(nomeParam);
		
		return value;
	}
	
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, false);
	}

	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		if (null == prefix) prefix = "";
		Map<String, String> params = new HashMap<String, String>();
		
		if (tipo_repertorio.length() > 0 || notEmptyParams.contains("tipo_repertorio"))
			params.put(prefix+".tipologie_repertorio.tipologia_repertorio", tipo_repertorio);
		
		if (modify) { // dati di conservazione da inviare solo in caso di modifica
			for (int i = 0; i < conservazione.size(); i++) {
	    		ConservazioneBridge cons = conservazione.get(i);
	    		if (cons != null && cons.getId() != null && cons.getId().length() > 0)
	    			params.putAll(cons.asFormAdapterParams(prefix+".conservazione["+String.valueOf(i)+"]"));
			}
		}
		
		if (stato_deposito.length() > 0 || notEmptyParams.contains("stato_deposito"))
			params.put(prefix+".deposito.@stato", stato_deposito);
		if (stato_deposito_minuta.length() > 0 || notEmptyParams.contains("stato_deposito_minuta"))
			params.put(prefix+".deposito_minuta.@stato", stato_deposito_minuta);
		if (codice_progetto.length() > 0 || notEmptyParams.contains("codice_progetto"))
			params.put(prefix+".progetto.@codice", codice_progetto);
		if (custom_select_1.length() > 0 || notEmptyParams.contains("custom_select_1"))
			params.put(prefix+".customSelect1", custom_select_1);
		if (custom_select_2.length() > 0 || notEmptyParams.contains("custom_select_2"))
			params.put(prefix+".customSelect2", custom_select_2);
    	
		// campi specifici del fascicolo di Sogin
		if (tipologia_fascicolo.length() > 0 || notEmptyParams.contains("tipologia_fascicolo"))
			params.put(prefix+".tipologia_fascicolo", tipologia_fascicolo);
		if (numero_gara.length() > 0 || notEmptyParams.contains("numero_gara"))
			params.put(prefix+".numero_gara", numero_gara);
		if (numero_oda_sap.length() > 0 || notEmptyParams.contains("numero_oda_sap"))
			params.put(prefix+".numero_oda_sap", numero_oda_sap);
		if (fornitore.length() > 0 || notEmptyParams.contains("fornitore"))
			params.put(prefix+".fornitori.fornitore", fornitore);
		if (fornitore_codice.length() > 0 || notEmptyParams.contains("fornitore_codice"))
			params.put(prefix+".fornitori.fornitore.@codice", fornitore_codice);
		
		// campi dei repertori di Sogin
		if (et_classificaelaborato.length() > 0 || notEmptyParams.contains("et_classificaelaborato"))
			params.put(prefix+".ET_ClassificaElaborato", et_classificaelaborato);
		if (et_titoloelaborato.length() > 0 || notEmptyParams.contains("et_titoloelaborato"))
			params.put(prefix+".ET_TitoloElaborato", et_titoloelaborato);
		if (customSelectSedeArchivio.length() > 0 || notEmptyParams.contains("customSelectSedeArchivio"))
			params.put(prefix+".customSelectSedeArchivio", customSelectSedeArchivio);
		if (et_zonaarchivio.length() > 0 || notEmptyParams.contains("et_zonaarchivio"))
			params.put(prefix+".ET_ZonaArchivio", et_zonaarchivio);
		if (et_notearchivio.length() > 0 || notEmptyParams.contains("et_notearchivio"))
			params.put(prefix+".ET_NoteArchivio", et_notearchivio);
		
		// campi per repertorio Norme Aziendali di Dexia
		if (na_codiceNorma.length() > 0 || notEmptyParams.contains("na_codiceNorma"))
			params.put(prefix+".NA_codiceNorma", na_codiceNorma);
		if (na_releaseNorma.length() > 0 || notEmptyParams.contains("na_releaseNorma"))
			params.put(prefix+".NA_releaseNorma", na_releaseNorma);
		
		// 20150112 fcappelli - rimosso per richiesta cliente
//		if (na_dataEmanazioneNorma.length() > 0 || notEmptyParams.contains("na_dataEmanazioneNorma"))
//			params.put(prefix+".NA_codiceNorma.@NA_dataEmanazioneNorma", na_dataEmanazioneNorma);
		
		
		if (na_statoNorma.length() > 0 || notEmptyParams.contains("na_statoNorma"))
			params.put(prefix+".NA_statoNorma", na_statoNorma);
		if (na_competenza.length() > 0 || notEmptyParams.contains("na_competenza"))
			params.put(prefix+".NA_competenza", na_competenza);
		
		for (int i = 0; i < na_disposizioni.size(); i++) {
    		Disposizione disposizione = na_disposizioni.get(i);
    		if (disposizione != null && disposizione.getText() != null)
    			params.putAll(disposizione.asFormAdapterParams(prefix+".disposizioni.NA_disposizione["+String.valueOf(i)+"]"));
		}
		
		for (int i = 0; i < na_unita.size(); i++) {
    		UnitaRichiamata unitaRichiamata = na_unita.get(i);
    		if (unitaRichiamata != null && unitaRichiamata.getCod_uff() != null)
    			params.putAll(unitaRichiamata.asFormAdapterParams(prefix+".unitaRichiamate.NA_unitaRichiamata["+String.valueOf(i)+"]"));
		}
		
    	return params;
	}
	
	/**
	 * Eliminazione di una disposizione del doc
	 */
	public String deleteDisposizione() {
		Disposizione disposizione = (Disposizione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("disposizione");
		if (disposizione != null) {
			na_disposizioni.remove(disposizione);
			if (na_disposizioni.isEmpty()) 
				na_disposizioni.add(new Disposizione());
		}
		return null;
	}
	
	/**
	 * Aggiunta di una disposizione del doc
	 */
	public String addDisposizione() {
		Disposizione disposizione = (Disposizione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("disposizione");
		int index = 0;
		if (disposizione != null)
			index = na_disposizioni.indexOf(disposizione);
		
		if (na_disposizioni != null) {
			if (na_disposizioni.size() > index)
				na_disposizioni.add(index+1,  new Disposizione());
			else
				na_disposizioni.add(new Disposizione());
		}
		return null;
	}
	
	/**
	 * Eliminazione di una unità richiamata del doc
	 */
	public String deleteUnitaRichiamata() {
		UnitaRichiamata unitaRichiamata = (UnitaRichiamata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("unita");
		if (unitaRichiamata != null) {
			na_unita.remove(unitaRichiamata);
			if (na_unita.isEmpty()) 
				na_unita.add(new UnitaRichiamata());
		}
		return null;
	}
	
	/**
	 * Aggiunta di una unità richiamata del doc
	 */
	public String addUnitaRichiamata() {
		UnitaRichiamata unitaRichiamata = (UnitaRichiamata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("unita");
		int index = 0;
		if (unitaRichiamata != null)
			index = na_unita.indexOf(unitaRichiamata);
		
		if (na_unita != null) {
			if (na_unita.size() > index)
				na_unita.add(index+1,  new UnitaRichiamata());
			else
				na_unita.add(new UnitaRichiamata());
		}
		return null;
	}
	
	public String getTipologia_fascicolo() {
		return tipologia_fascicolo;
	}

	public void setTipologia_fascicolo(String tipologia_fascicolo) {
		this.tipologia_fascicolo = tipologia_fascicolo;
	}
	
	public String getNumero_gara() {
		return numero_gara;
	}

	public void setNumero_gara(String numero_gara) {
		this.numero_gara = numero_gara;
	}

	public String getNumero_oda_sap() {
		return numero_oda_sap;
	}

	public void setNumero_oda_sap(String numero_oda_sap) {
		this.numero_oda_sap = numero_oda_sap;
	}

	public String getFornitore() {
		return fornitore;
	}

	public void setFornitore(String fornitore) {
		this.fornitore = fornitore;
	}

	public String getFornitore_codice() {
		return fornitore_codice;
	}

	public void setFornitore_codice(String fornitore_codice) {
		this.fornitore_codice = fornitore_codice;
	}
	
	public String getTipo_repertorio() {
		return tipo_repertorio;
	}

	public void setTipo_repertorio(String tipo_repertorio) {
		this.tipo_repertorio = tipo_repertorio;
	}

	public String getEt_classificaelaborato() {
		return et_classificaelaborato;
	}

	public void setEt_classificaelaborato(String et_classificaelaborato) {
		this.et_classificaelaborato = et_classificaelaborato;
	}

	public String getEt_titoloelaborato() {
		return et_titoloelaborato;
	}

	public void setEt_titoloelaborato(String et_titoloelaborato) {
		this.et_titoloelaborato = et_titoloelaborato;
	}

	public String getCustomSelectSedeArchivio() {
		return customSelectSedeArchivio;
	}

	public void setCustomSelectSedeArchivio(String customSelectSedeArchivio) {
		this.customSelectSedeArchivio = customSelectSedeArchivio;
	}

	public String getEt_zonaarchivio() {
		return et_zonaarchivio;
	}

	public void setEt_zonaarchivio(String et_zonaarchivio) {
		this.et_zonaarchivio = et_zonaarchivio;
	}

	public String getEt_notearchivio() {
		return et_notearchivio;
	}

	public void setEt_notearchivio(String et_notearchivio) {
		this.et_notearchivio = et_notearchivio;
	}

	public List<ConservazioneBridge> getConservazione() {
		return conservazione;
	}

	public void setConservazione(List<ConservazioneBridge> conservazione) {
		this.conservazione = conservazione;
	}

	public String getStato_deposito() {
		return stato_deposito;
	}

	public void setStato_deposito(String stato_deposito) {
		this.stato_deposito = stato_deposito;
	}
	
	public String getStato_deposito_minuta() {
		return stato_deposito_minuta;
	}

	public void setStato_deposito_minuta(String stato_deposito_minuta) {
		this.stato_deposito_minuta = stato_deposito_minuta;
	}

	public String getCodice_progetto() {
		return codice_progetto;
	}

	public void setCodice_progetto(String codice_progetto) {
		this.codice_progetto = codice_progetto;
	}

	public String getCustom_select_1() {
		return custom_select_1;
	}

	public void setCustom_select_1(String custom_select_1) {
		this.custom_select_1 = custom_select_1;
	}

	public String getCustom_select_2() {
		return custom_select_2;
	}

	public void setCustom_select_2(String custom_select_2) {
		this.custom_select_2 = custom_select_2;
	}

	public List<ApprovazioneDoc> getApprovazione() {
		return approvazione;
	}

	public void setApprovazione(List<ApprovazioneDoc> approvazione) {
		this.approvazione = approvazione;
	}

	public String getNa_codiceNorma() {
		return na_codiceNorma;
	}

	public void setNa_codiceNorma(String na_codiceNorma) {
		this.na_codiceNorma = na_codiceNorma;
	}

	// 20150112 fcappelli - rimosso per richiesta cliente
//	public String getNa_dataEmanazioneNorma() {
//		return new DateConverter().getAsString(null, null, this.na_dataEmanazioneNorma);
//	}

	// 20150112 fcappelli - rimosso per richiesta cliente
//	public void setNa_dataEmanazioneNorma(String na_dataEmanazioneNorma) {
//		this.na_dataEmanazioneNorma = DateUtil.formatDate2XW(na_dataEmanazioneNorma, null);
//	}

	public String getNa_statoNorma() {
		return na_statoNorma;
	}

	public void setNa_statoNorma(String na_statoNorma) {
		this.na_statoNorma = na_statoNorma;
	}

	public String getNa_competenza() {
		return na_competenza;
	}

	public void setNa_competenza(String na_competenza) {
		this.na_competenza = na_competenza;
	}

	public List<String> getNotEmptyParams() {
		return notEmptyParams;
	}

	public void setNotEmptyParams(List<String> notEmptyParams) {
		this.notEmptyParams = notEmptyParams;
	}

	public String getNa_releaseNorma() {
		return na_releaseNorma;
	}

	public void setNa_releaseNorma(String na_releaseNorma) {
		this.na_releaseNorma = na_releaseNorma;
	}

	public List<Disposizione> getDisposizioni() {
		return na_disposizioni;
	}

	public void setDisposizioni(List<Disposizione> disposizioni) {
		this.na_disposizioni = disposizioni;
	}

	public List<UnitaRichiamata> getUnitaRichiamate() {
		return na_unita;
	}

	public void setUnitaRichiamate(List<UnitaRichiamata> na_unita) {
		this.na_unita = na_unita;
	}

	public String getAlboExt_stato() {
		return alboExt_stato;
	}

	public void setAlboExt_stato(String alboExt_stato) {
		this.alboExt_stato = alboExt_stato;
	}

	public String getAlboExt_dataPubblicazione() {
		return alboExt_dataPubblicazione;
	}

	public void setAlboExt_dataPubblicazione(String alboExt_dataPubblicazione) {
		this.alboExt_dataPubblicazione = alboExt_dataPubblicazione;
	}

	public String getAlboExt_dataDefissione() {
		return alboExt_dataDefissione;
	}

	public void setAlboExt_dataDefissione(String alboExt_dataDefissione) {
		this.alboExt_dataDefissione = alboExt_dataDefissione;
	}
	
}
