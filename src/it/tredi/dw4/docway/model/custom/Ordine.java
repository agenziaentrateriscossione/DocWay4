package it.tredi.dw4.docway.model.custom;

import it.tredi.dw4.docway.model.Arrivo;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;

/**
 * Doc in arrivo di tipo ordine (repertorio personalizzato)
 * 
 * @author mbernardini
 */
public class Ordine extends Arrivo {

	// campi specifici del doc in partenza di tipo Ordine
	private DatiOrdine datiOrdine = new DatiOrdine();
	
	@Override
	public XmlEntity init(Document dom) {
		super.init(dom);
		
		// inizializzazione dei parametri specifici dell'ordine
		this.datiOrdine.init(XMLUtil.createDocument(dom, "/response/doc/ordine"));
		
		return null;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, true);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, modify, true);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		return this.asFormAdapterParams(prefix, modify, true, isRepertorio);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean modifyRifEsterni, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, modifyRifEsterni, isRepertorio);
    	
    	// invio dei parametri specifici dell'ordine
    	params.putAll(datiOrdine.asFormAdapterParams(prefix+".ordine"));
    	
    	return params;
	}
	
	public DatiOrdine getDatiOrdine() {
		return datiOrdine;
	}

	public void setDatiOrdine(DatiOrdine datiOrdine) {
		this.datiOrdine = datiOrdine;
	}
	
}
