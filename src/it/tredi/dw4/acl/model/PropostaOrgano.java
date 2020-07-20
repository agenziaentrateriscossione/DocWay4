package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.delibere.Proponente;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class PropostaOrgano extends XmlEntity {
	
	private String cod = "";
	private String cod_organo = "";
	private String organo = "";
	private String text = "";
	private String tipo = "";
	private Proponente proponente = new Proponente();
	private TitoloWorkflowOrgano workflow = new TitoloWorkflowOrgano();

	@Override
	public XmlEntity init(Document dom) {
		this.cod			= 	XMLUtil.parseAttribute(dom, "proposta/@cod");
		this.cod_organo 	=	XMLUtil.parseAttribute(dom, "proposta/@cod_organo");
		this.organo			=	XMLUtil.parseAttribute(dom, "proposta/@organo");
		this.text			=	XMLUtil.parseElement(dom, "proposta");
		this.setTipo(XMLUtil.parseAttribute(dom, "proposta/@tipo"));
		this.proponente.init(XMLUtil.createDocument(dom, "proposta/proponente"));
		this.getWorkflow().init(XMLUtil.createDocument(dom, "proposta/workflow"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix + ".@cod_organo", this.cod_organo);
    	params.put(prefix + ".@organo", this.organo);
    	params.put(prefix, this.text);
    	
    	return params;
	}
	
	//utilizzato per la creazione / modifica di un organo
	public Map<String, String> asFormAdapterParamsForOrgano(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix + ".@tipo", this.tipo);
    	params.put(prefix, this.text);
    	
    	return params;
	}


	/*
	 * getter / setter
	 * */
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getCod_organo() {
		return cod_organo;
	}

	public void setCod_organo(String cod_organo) {
		this.cod_organo = cod_organo;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Proponente getProponente() {
		return proponente;
	}

	public void setProponente(Proponente proponente) {
		this.proponente = proponente;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public TitoloWorkflowOrgano getWorkflow() {
		return workflow;
	}

	public void setWorkflow(TitoloWorkflowOrgano workflow) {
		this.workflow = workflow;
	}

}
