package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Modello extends XmlEntity {
	
	private String nome = "";
	private XwFile file = new XwFile();

	@Override
	public XmlEntity init(Document dom) {
		
		this.nome		=	dom.getRootElement().getName();
		this.file 		= 	(XwFile) XMLUtil.parseElement(dom, nome + "/node()[name()='xw:file']", new XwFile());
		
		if(this.file == null)
			this.file = new XwFile();
		
		return this;
	}
	
	public Modello() {
		// TODO Auto-generated constructor stub
	}

	public Modello(String nome){
		this.nome = nome;
		this.file = new XwFile();
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@title", this.file.getTitle());
    	params.put(prefix+".@name", this.file.getName());
		return params;
	}
	
	public void clear(){
		this.file = new XwFile();
	}

	/*
	 * getter / setter
	 * */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public XwFile getFile() {
		return file;
	}

	public void setFile(XwFile file) {
		this.file = file;
	}

}
