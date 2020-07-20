package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DataOra extends XmlEntity {
	
	private String data = "";
	private String ora = "";
	private String nome = "";

	@Override
	public XmlEntity init(Document dom) {
		this.setNome(dom.getRootElement().getName());
		this.setData(XMLUtil.parseAttribute(dom, this.nome + "/@data"));
		this.setOra(XMLUtil.parseAttribute(dom, this.nome + "/@ora"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	/*
	 * getter /setter
	 * */
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOra() {
		return ora;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
