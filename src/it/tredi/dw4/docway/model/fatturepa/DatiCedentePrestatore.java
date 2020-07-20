package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class DatiCedentePrestatore extends XmlEntity {

	// TODO andrebbero portati in ACL su strutture/persone esterne
	private String idPaese = "";
	private String idCodice = "";
	private String alboProfessionale = "";
	private String provinciaAlbo = "";
	private String numeroIscrizioneAlbo = "";
	private String dataIscrizioneAlbo = "";
	private String regimeFiscale = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.idPaese 				= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/@idPaese");
		this.idCodice 				= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/@idCodice");
		this.alboProfessionale 		= XMLUtil.parseStrictElement(dom, "datiCedentePrestatore/alboProfessionale");
		this.provinciaAlbo 			= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/alboProfessionale/@provinciaAlbo");
		this.numeroIscrizioneAlbo 	= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/alboProfessionale/@numeroIscrizioneAlbo");
		this.dataIscrizioneAlbo 	= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/alboProfessionale/@dataIscrizioneAlbo");
		this.regimeFiscale 			= XMLUtil.parseStrictAttribute(dom, "datiCedentePrestatore/@regimeFiscale");
		
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
    	if (this.alboProfessionale != null && this.alboProfessionale.length() > 0)
    		params.put(prefix+".alboProfessionale", this.alboProfessionale);
    	if (this.provinciaAlbo != null && this.provinciaAlbo.length() > 0)
    		params.put(prefix+".alboProfessionale.@provinciaAlbo", this.provinciaAlbo);
    	if (this.numeroIscrizioneAlbo != null && this.numeroIscrizioneAlbo.length() > 0)
    		params.put(prefix+".alboProfessionale.@numeroIscrizioneAlbo", this.numeroIscrizioneAlbo);
    	if (this.dataIscrizioneAlbo != null && this.dataIscrizioneAlbo.length() > 0) {
	    	if (this.dataIscrizioneAlbo.length() == 10)
	    		params.put(prefix+".alboProfessionale.@dataIscrizioneAlbo", DateUtil.changeDateFormat(this.dataIscrizioneAlbo, Const.DEFAULT_DATE_FORMAT, "yyyyMMdd")); // TODO formato da caricare da file di properties
	    	else 
	    		params.put(prefix+".alboProfessionale.@dataIscrizioneAlbo", this.dataIscrizioneAlbo);
    	}
    	if (this.regimeFiscale != null && this.regimeFiscale.length() > 0)
    		params.put(prefix+".@regimeFiscale", this.regimeFiscale);
    	
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

	public String getAlboProfessionale() {
		return alboProfessionale;
	}

	public void setAlboProfessionale(String alboProfessionale) {
		this.alboProfessionale = alboProfessionale;
	}

	public String getProvinciaAlbo() {
		return provinciaAlbo;
	}

	public void setProvinciaAlbo(String provinciaAlbo) {
		this.provinciaAlbo = provinciaAlbo;
	}

	public String getNumeroIscrizioneAlbo() {
		return numeroIscrizioneAlbo;
	}

	public void setNumeroIscrizioneAlbo(String numeroIscrizioneAlbo) {
		this.numeroIscrizioneAlbo = numeroIscrizioneAlbo;
	}

	public String getDataIscrizioneAlbo() {
		return dataIscrizioneAlbo;
	}

	public void setDataIscrizioneAlbo(String dataIscrizioneAlbo) {
		this.dataIscrizioneAlbo = dataIscrizioneAlbo;
	}

	public String getRegimeFiscale() {
		return regimeFiscale;
	}

	public void setRegimeFiscale(String regimeFiscale) {
		this.regimeFiscale = regimeFiscale;
	}

}
