package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Repertorio extends XmlEntity {
	private String cod;
	private String codice;
	private String numero;
	private String text;
	private String descrizione;
	private boolean selected;
    private Tabella tabella = new Tabella();
    
	public Repertorio() {}
    
	public Repertorio(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Repertorio init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "repertorio/@cod");
    	this.codice = XMLUtil.parseAttribute(dom, "repertorio/@codice");
    	this.numero = XMLUtil.parseAttribute(dom, "repertorio/@numero", ".");
    	this.text = XMLUtil.parseElement(dom, "repertorio");
    	this.tabella.init(XMLUtil.createDocument(dom, "repertorio/tabella"));
    	this.descrizione = (XMLUtil.parseElement(dom, "repertorio/descrizione")+" "+XMLUtil.parseElement(dom, "repertorio/tabella")).trim();
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".@numero", this.numero);
    	return params;
    }
    
    public Map<String, String> asFormAdapterParamsTrasformaRep(String prefix) throws Exception {
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.numero != null && !this.numero.isEmpty() && !this.numero.equals("."))
    		throw new Exception("Trasformazione in repertorio non possibile sul documento corrente. Documento già repertoriato!");
    	
    	params.put(prefix, this.text);
    	params.put(prefix+".@cod", this.cod);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public void setTabella(Tabella tabella) {
		this.tabella = tabella;
	}

	public Tabella getTabella() {
		return tabella;
	}

	public void setSelected(boolean select) {
		this.selected = select;
	}

	public boolean isSelected() {
		return selected;
	}

}

