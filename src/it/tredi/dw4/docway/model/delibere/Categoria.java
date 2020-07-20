package it.tredi.dw4.docway.model.delibere;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Categoria extends XmlEntity {
	
	private String cod = "";
	private String tipo = "";
	private String nome = "";

	@Override
	public XmlEntity init(Document dom) {
		this.setCod(XMLUtil.parseAttribute(dom, "categoria/@cod"));
		this.setTipo(XMLUtil.parseAttribute(dom, "categoria/@tipo"));
		this.setNome(XMLUtil.parseElement(dom, "categoria"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix, this.nome);
		return params;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
