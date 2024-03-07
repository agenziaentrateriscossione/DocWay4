package it.tredi.dw4.docway.model.workflow;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Ex_Action extends XmlEntity {

	private String descrizione = "";
	private String taskId = "";
	private String cod_persona = "";
	private String cod_uff = "";
	private String nome_persona = "";
	private String nome_uff = "";
	private String data = "";
	private String ora = "";
	private String state = "";
	private String login_persona = "";
	
	public String getDescrizione() {
		return descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
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
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCod_persona() {
		return cod_persona;
	}

	public void setCod_persona(String cod_persona) {
		this.cod_persona = cod_persona;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getNome_persona() {
		return nome_persona;
	}

	public void setNome_persona(String nome_persona) {
		this.nome_persona = nome_persona;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	@Override
	public XmlEntity init(Document dom) {
		this.descrizione 	= XMLUtil.parseElement(dom, "ex_action");
    	this.cod_persona	= XMLUtil.parseAttribute(dom, "ex_action/@cod_persona");
    	this.cod_uff 		= XMLUtil.parseAttribute(dom,"ex_action/@cod_uff");
    	this.nome_persona 	= XMLUtil.parseAttribute(dom,"ex_action/@nome_persona");
    	this.nome_uff 		= XMLUtil.parseAttribute(dom,"ex_action/@nome_uff");
    	this.data			= XMLUtil.parseAttribute(dom, "ex_action/@data");
    	this.ora 			= XMLUtil.parseAttribute(dom, "ex_action/@ora");
    	this.taskId			= XMLUtil.parseAttribute(dom, "ex_action/@taskId");
    	this.state			= XMLUtil.parseAttribute(dom, "ex_action/@state");
    	this.login_persona	= XMLUtil.parseAttribute(dom, "ex_action/@login_persona");

    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getLogin_persona() {
		return login_persona;
	}

	public void setLogin_persona(String login_persona) {
		this.login_persona = login_persona;
	}
	
}
