package it.tredi.dw4.docway.model.rep_standard;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Extra richiedente per repertorio Albo online
 * 
 * @author mbernardini
 */
public class Richiedente extends XmlEntity {

	private String nome = "";
	private String cod = "";
	private String tipo = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.nome 	= XMLUtil.parseStrictElement(dom, "richiedente/nome");
    	this.cod 	= XMLUtil.parseStrictAttribute(dom, "richiedente/@cod");
    	this.tipo 	= XMLUtil.parseStrictAttribute(dom, "richiedente/@tipo");
		
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".nome", this.nome);
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".@tipo", this.tipo);
    	
    	return params;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
