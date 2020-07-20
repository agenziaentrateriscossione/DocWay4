package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Vaschetta extends XmlEntity {

	private String nome;
	private String title = "";
	private String num;
	private String selid;
	private String tipo;
	private String label;
	
	@Override
	public XmlEntity init(Document dom) {
		this.nome = dom.getRootElement().getName();
		this.title = XMLUtil.parseAttribute(dom, this.nome+"/@title");
		this.num = XMLUtil.parseAttribute(dom, this.nome+"/@num");
		this.selid = XMLUtil.parseAttribute(dom, this.nome+"/@selid");
		this.tipo = nome.substring(0, nome.lastIndexOf("_"));
		
		this.label = nome.substring(nome.lastIndexOf("_") + 1);
		if (nome.startsWith("fasc"))
			this.label = "fasc_" + this.label;
		else if (nome.startsWith("rac_"))
			this.label = "rac_" + this.label;
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public void setNome(String query) {
		this.nome = query;
	}

	public String getNome() {
		return nome;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getNum() {
		return num;
	}

	public void setSelid(String selid) {
		this.selid = selid;
	}

	public String getSelid() {
		return selid;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}