package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class VaschettaDelega extends XmlEntity {

	private String codPersona;
	private String nomePersona;
	private String codUfficio;
	private String nomeUfficio;
	private String loginName;

	public String getCodPersona() {
		return codPersona;
	}

	public void setCodPersona(String codPersona) {
		this.codPersona = codPersona;
	}

	public String getNomePersona() {
		return nomePersona;
	}

	public void setNomePersona(String nomePersona) {
		this.nomePersona = nomePersona;
	}

	public String getCodUfficio() {
		return codUfficio;
	}

	public void setCodUfficio(String codUfficio) {
		this.codUfficio = codUfficio;
	}

	public String getNomeUfficio() {
		return nomeUfficio;
	}

	public void setNomeUfficio(String nomeUfficio) {
		this.nomeUfficio = nomeUfficio;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public XmlEntity init(Document dom) {

		String path = "/delega";

		this.codPersona = XMLUtil.parseStrictAttribute(dom, path+"/@cod_persona", "");
		this.nomePersona = XMLUtil.parseStrictAttribute(dom, path+"/@nome_persona", "");
		this.codUfficio = XMLUtil.parseStrictAttribute(dom, path+"/@cod_ufficio", "");
		this.nomeUfficio = XMLUtil.parseStrictAttribute(dom, path+"/@nome_ufficio", "");
		this.loginName = XMLUtil.parseStrictAttribute(dom, path+"/@login_name", "");

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}


}
