package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiTrasmissione extends XmlEntity {

	private String idPaese = "";
	private String idCodice = "";
	private String progressivoInvio = "";
	private String formatoTrasmissione = "";
	private String codiceDestinatario = "";
	private String emailTrasmittente = "";
	private String telefonoTrasmittente = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.idPaese 				= XMLUtil.parseStrictAttribute(dom, "datiTrasmissione/@idPaese");
		this.idCodice 				= XMLUtil.parseStrictAttribute(dom, "datiTrasmissione/@idCodice");
		this.progressivoInvio 		= XMLUtil.parseStrictAttribute(dom, "datiTrasmissione/@progressivoInvio");
		this.formatoTrasmissione 	= XMLUtil.parseStrictAttribute(dom, "datiTrasmissione/@formatoTrasmissione");
		this.codiceDestinatario 	= XMLUtil.parseStrictAttribute(dom, "datiTrasmissione/@codiceDestinatario");
		this.emailTrasmittente 		= XMLUtil.parseStrictElement(dom, "datiTrasmissione/emailTrasmittente");
		this.telefonoTrasmittente 	= XMLUtil.parseStrictElement(dom, "datiTrasmissione/telefonoTrasmittente");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.idPaese != null && this.idPaese.length() > 0)
    		params.put(prefix+".@idPaese", this.idPaese);
    	if (this.idCodice != null && this.idCodice.length() > 0)
    		params.put(prefix+".@idCodice", this.idCodice);
    	if (this.progressivoInvio != null && this.progressivoInvio.length() > 0)
    		params.put(prefix+".@progressivoInvio", this.progressivoInvio);
    	if (this.formatoTrasmissione != null && this.formatoTrasmissione.length() > 0)
    		params.put(prefix+".@formatoTrasmissione", this.formatoTrasmissione);
    	if (this.codiceDestinatario != null && this.codiceDestinatario.length() > 0)
    		params.put(prefix+".@codiceDestinatario", this.codiceDestinatario);
    	if (this.emailTrasmittente != null && this.emailTrasmittente.length() > 0)
    		params.put(prefix+".emailTrasmittente", this.emailTrasmittente);
    	if (this.telefonoTrasmittente != null && this.telefonoTrasmittente.length() > 0)
    		params.put(prefix+".telefonoTrasmittente", this.telefonoTrasmittente);
    	
    	return params;
	}
	
	public String getIdPaese() {
		return idPaese;
	}

	public void setIdPaese(String idPaese) {
		this.idPaese = idPaese;
	}

	public String getIdCodice() {
		return idCodice;
	}

	public void setIdCodice(String idCodice) {
		this.idCodice = idCodice;
	}

	public String getProgressivoInvio() {
		return progressivoInvio;
	}

	public void setProgressivoInvio(String progressivoInvio) {
		this.progressivoInvio = progressivoInvio;
	}

	public String getFormatoTrasmissione() {
		return formatoTrasmissione;
	}

	public void setFormatoTrasmissione(String formatoTrasmissione) {
		this.formatoTrasmissione = formatoTrasmissione;
	}

	public String getCodiceDestinatario() {
		return codiceDestinatario;
	}

	public void setCodiceDestinatario(String codiceDestinatario) {
		this.codiceDestinatario = codiceDestinatario;
	}

	public String getEmailTrasmittente() {
		return emailTrasmittente;
	}

	public void setEmailTrasmittente(String emailTrasmittente) {
		this.emailTrasmittente = emailTrasmittente;
	}

	public String getTelefonoTrasmittente() {
		return telefonoTrasmittente;
	}

	public void setTelefonoTrasmittente(String telefonoTrasmittente) {
		this.telefonoTrasmittente = telefonoTrasmittente;
	}

}
