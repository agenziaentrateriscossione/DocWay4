package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Doc_contenuti extends XmlEntity {
	private String numero;
	private String primo_doc_prot;
	private String primo_doc_data_prot;
	private String ultimo_doc_prot;
	private String ultimo_doc_data_prot;
    
	public Doc_contenuti() {}
    
	public Doc_contenuti(String xmlDiritto) throws Exception {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }
    
    public Doc_contenuti init(Document dom) {
    	this.numero = XMLUtil.parseAttribute(dom, "doc_contenuti/@numero");
    	this.primo_doc_prot = XMLUtil.parseAttribute(dom, "doc_contenuti/@primo_doc_prot");
    	this.primo_doc_data_prot = XMLUtil.parseAttribute(dom, "doc_contenuti/@primo_doc_data_prot");
    	this.ultimo_doc_prot = XMLUtil.parseAttribute(dom, "doc_contenuti/@ultimo_doc_prot");
    	this.ultimo_doc_data_prot = XMLUtil.parseAttribute(dom, "doc_contenuti/@ultimo_doc_data_prot");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@numero", this.numero);
    	params.put(prefix+".@primo_doc_prot", this.primo_doc_prot);
    	params.put(prefix+".@primo_doc_data_prot", this.primo_doc_data_prot);
    	params.put(prefix+".@ultimo_doc_prot", this.ultimo_doc_prot);
    	params.put(prefix+".@ultimo_doc_data_prot", this.ultimo_doc_data_prot);
    	return params;
    }
    
    public String getNumero() {
		return numero;
	}

	public void setNumero(String cod) {
		this.numero = cod;
	}

	public void setPrimo_doc_prot(String value) {
		this.primo_doc_prot = value;
	}

	public String getPrimo_doc_prot() {
		return primo_doc_prot;
	}

	public void setPrimo_doc_data_prot(String primo_doc_data_prot) {
		this.primo_doc_data_prot = primo_doc_data_prot;
	}

	public String getPrimo_doc_data_prot() {
		return primo_doc_data_prot;
	}

	public void setUltimo_doc_prot(String ultimo_doc_prot) {
		this.ultimo_doc_prot = ultimo_doc_prot;
	}

	public String getUltimo_doc_prot() {
		return ultimo_doc_prot;
	}

	public void setUltimo_doc_data_prot(String ultimo_doc_data_prot) {
		this.ultimo_doc_data_prot = ultimo_doc_data_prot;
	}

	public String getUltimo_doc_data_prot() {
		return ultimo_doc_data_prot;
	}
}

