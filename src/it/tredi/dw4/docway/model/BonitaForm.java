package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class BonitaForm extends XmlEntity {

	private String url = "";
	private CookieBonita cookie = new CookieBonita();
	
	@Override
	public XmlEntity init(Document dom) {
		this.url = XMLUtil.parseStrictAttribute(dom, "/response/bonitaForm/@url");
		this.cookie.init(XMLUtil.createDocument(dom, "/response/bonitaForm/cookieBonita"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public CookieBonita getCookie() {
		return cookie;
	}

	public void setCookie(CookieBonita cookie) {
		this.cookie = cookie;
	}

}
