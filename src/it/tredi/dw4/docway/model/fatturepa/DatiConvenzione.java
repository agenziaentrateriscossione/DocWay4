package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class DatiConvenzione extends XmlEntity {

	private String riferimentoNumeroLinea = "";
	private String idDocumento = "";
	private String data = "";
	private String numItem = "";
	private String codiceCommessaConvenzione = "";
	private String codiceCUP = "";
	private String codiceCIG = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.riferimentoNumeroLinea 	= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@riferimentoNumeroLinea");
		this.idDocumento 				= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@idDocumento");
		this.data 						= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@data");
		this.numItem 					= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@numItem");
		this.codiceCommessaConvenzione 	= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@codiceCommessaConvenzione");
		this.codiceCUP 					= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@codiceCUP");
		this.codiceCIG 					= XMLUtil.parseStrictAttribute(dom, "datiConvenzione/@codiceCIG");
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.riferimentoNumeroLinea != null && this.riferimentoNumeroLinea.length() > 0)
    		params.put(prefix+".@riferimentoNumeroLinea", this.riferimentoNumeroLinea);
    	if (this.idDocumento != null && this.idDocumento.length() > 0)
    		params.put(prefix+".@idDocumento", this.idDocumento);
    	if (this.data != null && this.data.length() > 0)
    		params.put(prefix+".@data", this.data);
    	if (this.numItem != null && this.numItem.length() > 0)
    		params.put(prefix+".@numItem", this.numItem);
    	if (this.codiceCommessaConvenzione != null && this.codiceCommessaConvenzione.length() > 0)
    		params.put(prefix+".@codiceCommessaConvenzione", this.codiceCommessaConvenzione);
    	if (this.codiceCUP != null && this.codiceCUP.length() > 0)
    		params.put(prefix+".@codiceCUP", this.codiceCUP);
    	if (this.codiceCIG != null && this.codiceCIG.length() > 0)
    		params.put(prefix+".@codiceCIG", this.codiceCIG);
    	
    	return params;
	}
	
	public String getRiferimentoNumeroLinea() {
		return riferimentoNumeroLinea;
	}

	public void setRiferimentoNumeroLinea(String riferimentoNumeroLinea) {
		this.riferimentoNumeroLinea = riferimentoNumeroLinea;
	}

	public String getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getData() {
		return DateUtil.changeDateFormat(data, "yyyy-MM-dd", "dd/MM/yyyy", "([0-9]{4})-([0-9]{2})-([0-9]{2})"); // TODO il formato della data andrebbe caricato in base al locale
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getNumItem() {
		return numItem;
	}

	public void setNumItem(String numItem) {
		this.numItem = numItem;
	}

	public String getCodiceCommessaConvenzione() {
		return codiceCommessaConvenzione;
	}

	public void setCodiceCommessaConvenzione(String codiceCommessaConvenzione) {
		this.codiceCommessaConvenzione = codiceCommessaConvenzione;
	}

	public String getCodiceCUP() {
		return codiceCUP;
	}

	public void setCodiceCUP(String codiceCUP) {
		this.codiceCUP = codiceCUP;
	}

	public String getCodiceCIG() {
		return codiceCIG;
	}

	public void setCodiceCIG(String codiceCIG) {
		this.codiceCIG = codiceCIG;
	}

}
