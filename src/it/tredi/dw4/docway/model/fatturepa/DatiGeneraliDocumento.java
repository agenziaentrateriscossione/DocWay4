package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class DatiGeneraliDocumento extends XmlEntity {

	private String tipoDocumento = "";
	private String divisa = "";
	private String data = "";
	private String numero = "";
	private String importoTotaleDocumento = "";
	private String arrotondamento = "";
	private String art73 = "";
	private String causale = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.tipoDocumento 				= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@tipoDocumento");
		this.divisa 					= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@divisa");
		this.data 						= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@data");
		this.numero 					= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@numero");
		this.importoTotaleDocumento 	= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@importoTotaleDocumento");
		this.arrotondamento 			= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@arrotondamento");
		this.art73 						= XMLUtil.parseStrictAttribute(dom, "datiGeneraliDocumento/@art73");
		this.causale					= XMLUtil.parseStrictElement(dom, "datiGeneraliDocumento/causale");
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.tipoDocumento != null && this.tipoDocumento.length() > 0)
    		params.put(prefix+".@tipoDocumento", this.tipoDocumento);
    	if (this.divisa != null && this.divisa.length() > 0)
    		params.put(prefix+".@divisa", this.divisa);
    	if (this.data != null && this.data.length() > 0) {
	    	if (this.data.length() == 10)
	    		params.put(prefix+".@data", DateUtil.changeDateFormat(this.data, Const.DEFAULT_DATE_FORMAT, "yyyyMMdd")); // TODO formato da caricare da file di properties
	    	else
	    		params.put(prefix+".@data", this.data);
    	}
    	if (this.numero != null && this.numero.length() > 0)
    		params.put(prefix+".@numero", this.numero);
    	if (this.importoTotaleDocumento != null && this.importoTotaleDocumento.length() > 0)
    		params.put(prefix+".@importoTotaleDocumento", this.importoTotaleDocumento);
    	if (this.arrotondamento != null && this.arrotondamento.length() > 0)
    		params.put(prefix+".@arrotondamento", this.arrotondamento);
    	if (this.art73 != null && this.art73.length() > 0)
    		params.put(prefix+".@art73", this.art73);
    	if (this.causale != null && this.causale.length() > 0)
    		params.put(prefix+".causale", this.causale);
    	
    	return params;
	}
			
	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDivisa() {
		return divisa;
	}

	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}

	public String getData() {
		return DateUtil.changeDateFormat(data, "yyyy-MM-dd", "dd/MM/yyyy", "([0-9]{4})-([0-9]{2})-([0-9]{2})"); // TODO il formato della data andrebbe caricato in base al locale
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getImportoTotaleDocumento() {
		return importoTotaleDocumento;
	}

	public void setImportoTotaleDocumento(String importoTotaleDocumento) {
		this.importoTotaleDocumento = importoTotaleDocumento;
	}

	public String getArrotondamento() {
		return arrotondamento;
	}

	public void setArrotondamento(String arrotondamento) {
		this.arrotondamento = arrotondamento;
	}

	public String getArt73() {
		return art73;
	}

	public void setArt73(String art73) {
		this.art73 = art73;
	}
	
	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}
	
}
