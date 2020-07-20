package it.tredi.dw4.docway.model.fatturepa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class FatturaPA extends XmlEntity {

	private boolean fatturaCartacea = false;
	
	// campi extra relativi alle fatturePA
	private String versione = "";
	private String state = "";
	private String identificativoSdI = "";
	private String emailSdI = "";
	private String fileNameFattura = "";
	private String extensionFattura = "";
	private String codiceDestinatario = "";
	private String formato = "";
	private String tentativiInvio = "";
	private String messageId = "";
	private String note = "";
	private DatiTrasmissione datiTrasmissione = new DatiTrasmissione();
	private DatiCedentePrestatore datiCedentePrestatore = new DatiCedentePrestatore();
	private DatiCessionarioCommittente datiCessionarioCommittente = new DatiCessionarioCommittente();
	private List<DatiFattura> datiFattura = new ArrayList<DatiFattura>();
	private boolean lottoFatture = false;
	private List<Notifica> notifica = new ArrayList<Notifica>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.versione			= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@versione");
		this.state 				= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@state");
		this.identificativoSdI 	= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@identificativoSdI");
		this.emailSdI 			= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@emailSdI");
		this.fileNameFattura 	= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@fileNameFattura");
		this.extensionFattura 	= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@extensionFattura");
		this.codiceDestinatario = XMLUtil.parseStrictAttribute(dom, "fatturaPA/@codiceDestinatario");
		this.formato 			= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@formato");
		this.tentativiInvio 	= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@tentativiInvio");
		this.messageId 			= XMLUtil.parseStrictAttribute(dom, "fatturaPA/@messageId");
		this.note 				= XMLUtil.parseStrictElement(dom, "fatturaPA/note");
		this.datiFattura 		= XMLUtil.parseSetOfElement(dom, "fatturaPA/datiFattura", new DatiFattura());
		this.notifica	 		= XMLUtil.parseSetOfElement(dom, "fatturaPA/notifica", new Notifica());
		
		this.datiTrasmissione.init(XMLUtil.createDocument(dom, "fatturaPA/datiTrasmissione"));
		this.datiCedentePrestatore.init(XMLUtil.createDocument(dom, "fatturaPA/datiCedentePrestatore"));
		this.datiCessionarioCommittente.init(XMLUtil.createDocument(dom, "fatturaPA/datiCessionarioCommittente"));
		
		if (datiFattura != null && datiFattura.size() > 1) // se sono presenti piu' fatture si tratta di un lotto
			lottoFatture = true;
		else
			lottoFatture = false;
		
		if (this.fileNameFattura != null && this.fileNameFattura.trim().length() > 0
				&& this.extensionFattura != null && this.extensionFattura.trim().length() > 0)
			fatturaCartacea = false;
		else
			fatturaCartacea = true;
		
		return this;
	}
	
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (fatturaCartacea)
    		params.put("*fatturaCartacea", "true"); // salvataggio di fatture cartacee
    	else
    		params.put("*fatturaCartacea", "false"); // salvataggio di fatturePA
    	
    	// la modifica dei dati e' previsto solo per il caso di fattura attiva
    	if (this.versione != null && this.versione.length() > 0)
    		params.put(prefix+".@versione", this.versione);
    	if (this.state != null && this.state.length() > 0)
    		params.put(prefix+".@state", this.state);
    	if (this.identificativoSdI != null && this.identificativoSdI.length() > 0)
    		params.put(prefix+".@identificativoSdI", this.identificativoSdI);
    	if (this.emailSdI != null && this.emailSdI.length() > 0)
    		params.put(prefix+".@emailSdI", this.emailSdI);
    	if (this.fileNameFattura != null && this.fileNameFattura.length() > 0)
    		params.put(prefix+".@fileNameFattura", this.fileNameFattura);
    	if (this.extensionFattura != null && this.extensionFattura.length() > 0)
    		params.put(prefix+".@extensionFattura", this.extensionFattura);
    	if (this.codiceDestinatario != null && this.codiceDestinatario.length() > 0)
    		params.put(prefix+".@codiceDestinatario", this.codiceDestinatario);
    	if (this.formato != null && this.formato.length() > 0)
    		params.put(prefix+".@formato", this.formato);
    	if (this.tentativiInvio != null && this.tentativiInvio.length() > 0)
    		params.put(prefix+".@tentativiInvio", this.tentativiInvio);
    	if (this.messageId != null && this.messageId.length() > 0)
    		params.put(prefix+".@messageId", this.messageId);
    	if (this.note != null && this.note.length() > 0)
    		params.put(prefix+".note", this.note);
    	
    	
    	for (int i = 0; i < datiFattura.size(); i++) {
    		DatiFattura dati = (DatiFattura) datiFattura.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiFattura["+String.valueOf(i)+"]"));
		}
    	
    	params.putAll(datiTrasmissione.asFormAdapterParams(prefix+".datiTrasmissione"));
    	params.putAll(datiCedentePrestatore.asFormAdapterParams(prefix+".datiCedentePrestatore"));
    	params.putAll(datiCessionarioCommittente.asFormAdapterParams(prefix+".datiCessionarioCommittente"));
    	
    	return params;
	}
	
	public boolean isFatturaCartacea() {
		return fatturaCartacea;
	}

	public void setFatturaCartacea(boolean fatturaCartacea) {
		this.fatturaCartacea = fatturaCartacea;
	}
	
	public String getFileNameFattura() {
		return fileNameFattura;
	}

	public void setFileNameFattura(String fileNameFattura) {
		this.fileNameFattura = fileNameFattura;
	}
	
	public String getExtensionFattura() {
		return extensionFattura;
	}

	public void setExtensionFattura(String extensionFattura) {
		this.extensionFattura = extensionFattura;
	}

	public List<DatiFattura> getDatiFattura() {
		return datiFattura;
	}

	public void setDatiFattura(List<DatiFattura> datiFattura) {
		this.datiFattura = datiFattura;
	}
	
	public String getIdentificativoSdI() {
		return identificativoSdI;
	}

	public void setIdentificativoSdI(String identificativoSdI) {
		this.identificativoSdI = identificativoSdI;
	}

	public String getEmailSdI() {
		return emailSdI;
	}

	public void setEmailSdI(String emailSdI) {
		this.emailSdI = emailSdI;
	}

	public String getCodiceDestinatario() {
		return codiceDestinatario;
	}

	public void setCodiceDestinatario(String codiceDestinatario) {
		this.codiceDestinatario = codiceDestinatario;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getTentativiInvio() {
		return tentativiInvio;
	}

	public void setTentativiInvio(String tentativiInvio) {
		this.tentativiInvio = tentativiInvio;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public List<Notifica> getNotifica() {
		return notifica;
	}

	public void setNotifica(List<Notifica> notifica) {
		this.notifica = notifica;
	}
	
	public DatiTrasmissione getDatiTrasmissione() {
		return datiTrasmissione;
	}

	public void setDatiTrasmissione(DatiTrasmissione datiTrasmissione) {
		this.datiTrasmissione = datiTrasmissione;
	}

	public DatiCedentePrestatore getDatiCedentePrestatore() {
		return datiCedentePrestatore;
	}

	public void setDatiCedentePrestatore(DatiCedentePrestatore datiCedentePrestatore) {
		this.datiCedentePrestatore = datiCedentePrestatore;
	}

	public DatiCessionarioCommittente getDatiCessionarioCommittente() {
		return datiCessionarioCommittente;
	}

	public void setDatiCessionarioCommittente(
			DatiCessionarioCommittente datiCessionarioCommittente) {
		this.datiCessionarioCommittente = datiCessionarioCommittente;
	}
	
	public boolean isLottoFatture() {
		return lottoFatture;
	}

	public void setLottoFatture(boolean lottoFatture) {
		this.lottoFatture = lottoFatture;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getVersione() {
		return versione;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}
	
	/**
	 * aggiunta di una fattura
	 */
	public String addDatiFattura() {
		DatiFattura fattura = (DatiFattura) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fattura");
		int index = 0;
		if (fattura != null)
			index = datiFattura.indexOf(fattura);
		
		if (datiFattura != null) {
			if (datiFattura.size() > index)
				datiFattura.add(index+1,  new DatiFattura());
			else
				datiFattura.add(new DatiFattura());
		}
		return null;
	}
	
	/**
	 * eliminazione di una fattura
	 */
	public String deleteDatiFattura() {
		DatiFattura fattura = (DatiFattura) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fattura");
		if (fattura != null) {
			datiFattura.remove(fattura);
			if (datiFattura.isEmpty()) 
				datiFattura.add(new DatiFattura());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di una fattura
	 */
	public String moveUpDatiFattura() {
		DatiFattura fattura = (DatiFattura) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fattura");
		if (fattura != null && datiFattura != null) {
			int index = datiFattura.indexOf(fattura);
			if (index > 0 ) {
				datiFattura.remove(index);
				datiFattura.add(index-1, fattura);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di una fattura
	 */
	public String moveDownDatiFattura() {
		DatiFattura fattura = (DatiFattura) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fattura");
		if (fattura != null && datiFattura != null) {
			int index = datiFattura.indexOf(fattura);
			if (index < datiFattura.size()-1 ) {
				datiFattura.remove(index);
				datiFattura.add(index+1, fattura);
			}
		}
		return null;
	}
	
}
