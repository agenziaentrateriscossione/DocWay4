package it.tredi.dw4.docway.model.delibere;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.PersonalView;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiProposta extends XmlEntity {

	private Proposta proposta = new Proposta();
	private PersonalView personalView = new PersonalView();
	private Categoria categoria = new Categoria();
	private Odg_seduta odg_seduta = new Odg_seduta();
	private List<Componente> delibera;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		
		this.getProposta().init(XMLUtil.createDocument(dom, "doc/proposta"));
		this.getPersonalView().init(XMLUtil.createDocument(dom, "doc/personalView"));
		this.getCategoria().init(XMLUtil.createDocument(dom, "doc/categoria"));
		this.getOdg_seduta().init(XMLUtil.createDocument(dom, "doc/odg_seduta"));
		this.delibera = XMLUtil.parseSetOfElement(dom, "doc/delibera/componente", new Componente());
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, false, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.putAll(this.getCategoria().asFormAdapterParams(prefix + ".categoria"));
    	
    	if(!modify){
    		params.putAll(this.getProposta().asFormAdapterParams(prefix + ".proposta"));
    	}
    	
		return params;
	}
	
	
	/*
	 * getter / setter
	 * */
	public Proposta getProposta() {
		return proposta;
	}

	public void setProposta(Proposta proposta) {
		this.proposta = proposta;
	}

	public PersonalView getPersonalView() {
		return personalView;
	}

	public void setPersonalView(PersonalView personalView) {
		this.personalView = personalView;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Odg_seduta getOdg_seduta() {
		return odg_seduta;
	}

	public void setOdg_seduta(Odg_seduta odg_seduta) {
		this.odg_seduta = odg_seduta;
	}

	public List<Componente> getDelibera() {
		return delibera;
	}

	public void setDelibera(List<Componente> delibera) {
		this.delibera = delibera;
	}

}
