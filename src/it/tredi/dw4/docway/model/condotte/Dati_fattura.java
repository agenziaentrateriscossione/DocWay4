package it.tredi.dw4.docway.model.condotte;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Dati_fattura extends XmlEntity {

	private Centro_costo centro_costo = new Centro_costo();
	private String partita_iva = "";
	private String data_fatt = "";
	private String numero_fatt = "";
	private String importo = "";
	private String stato = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.centro_costo.init(XMLUtil.createDocument(dom, "dati_fattura/centro_costo"));
		this.partita_iva = XMLUtil.parseStrictElement(dom, "dati_fattura/partita_iva");
		this.data_fatt = XMLUtil.parseStrictElement(dom, "dati_fattura/data_fatt");
		this.numero_fatt = XMLUtil.parseStrictElement(dom, "dati_fattura/numero_fatt");
		this.importo = XMLUtil.parseStrictElement(dom, "dati_fattura/importo");
		this.stato = XMLUtil.parseStrictElement(dom, "dati_fattura/stato");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.putAll(centro_costo.asFormAdapterParams(prefix + ".centro_costo"));
    	params.put(prefix + ".partita_iva", this.partita_iva);
    	params.put(prefix + ".data_fatt", this.data_fatt);
    	params.put(prefix + ".numero_fatt", this.numero_fatt);
    	params.put(prefix + ".importo", this.importo);
    	params.put(prefix + ".stato", this.stato);
    	
    	return params;
	}
	
	public Centro_costo getCentro_costo() {
		return centro_costo;
	}

	public void setCentro_costo(Centro_costo centroCosto) {
		this.centro_costo = centroCosto;
	}
	
	public String getPartita_iva() {
		return partita_iva;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}
	
	public String getData_fatt() {
		return data_fatt;
	}

	public void setData_fatt(String data_fatt) {
		this.data_fatt = data_fatt;
	}

	public String getNumero_fatt() {
		return numero_fatt;
	}

	public void setNumero_fatt(String numero_fatt) {
		this.numero_fatt = numero_fatt;
	}

	public String getImporto() {
		return importo;
	}

	public void setImporto(String importo) {
		this.importo = importo;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

}
