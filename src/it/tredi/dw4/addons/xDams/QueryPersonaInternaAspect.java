package it.tredi.dw4.addons.xDams;

import it.tredi.dw4.acl.beans.QueryPersonaInterna;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Page;

import java.util.Map;

import org.dom4j.Document;

public class QueryPersonaInternaAspect extends BaseAddOn {

	private String extra_userid = "";
	private String extra_dominio = "";
	private String extra_archivio = "";
	private String extra_archivio_livello = "";
	
	public QueryPersonaInternaAspect(String template, Page host) {
		super(template, host);
	}
	
	@Override
	public QueryPersonaInterna getHost() {
		return (QueryPersonaInterna) super.getHost();
	}
	
	@Override
	public void init(Document dom) {
		clear();
	}

	@Override
	public Map<String, String> asFormAdapterParams() {
		return null; // non utilizzato sul bean di ricerca
	}
	
	@Override
	public String asQuery() {
		String query =  "";
		
		query +=  getHost().addQueryField("persint_extra_userid", extra_userid);
		query +=  getHost().addQueryField("persint_extra_dominio", extra_dominio);
		query +=  getHost().addQueryField("persint_extra_archivio", extra_archivio);
		
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		
		if (query.trim().length() > 0)
			query = "(" + query + ")";
		
		return query;
	}

	@Override
	public void clear() {
		extra_userid = "";
		extra_dominio = "";
		extra_archivio = "";
		extra_archivio_livello = "";
	}
	
	/**
	 * apertura vocabolario su campo userid
	 * 
	 * @return
	 * @throws Exception
	 */
	public String openIndexExtraUserId() throws Exception {
		getHost().setFocusElement("extra_userid");
		getHost().openIndex(this, "extra_userid", "persint_extra_userid", this.extra_userid, "0", null);
		return null;
	}
	
	/**
	 * apertura vocabolario su campo dominio
	 * 
	 * @return
	 * @throws Exception
	 */
	public String openIndexExtraDominio() throws Exception {
		getHost().setFocusElement("extra_dominio");
		getHost().openIndex(this, "extra_dominio", "persint_extra_dominio", this.extra_dominio, "0", null);
		return null;
	}
	
	/**
	 * apertura vocabolario su campo archivio
	 * 
	 * @return
	 * @throws Exception
	 */
	public String openIndexExtraArchivio() throws Exception {
		getHost().setFocusElement("extra_archivio");
		getHost().openIndex(this, "extra_archivio", "persint_extra_archivio", this.extra_archivio, "0", " ");
		return null;
	}
	
	public String getExtra_userid() {
		return extra_userid;
	}

	public void setExtra_userid(String extra_userid) {
		this.extra_userid = extra_userid;
	}

	public String getExtra_dominio() {
		return extra_dominio;
	}

	public void setExtra_dominio(String extra_dominio) {
		this.extra_dominio = extra_dominio;
	}

	public String getExtra_archivio() {
		return extra_archivio;
	}

	public void setExtra_archivio(String extra_archivio) {
		this.extra_archivio = extra_archivio;
	}

	public String getExtra_archivio_livello() {
		return extra_archivio_livello;
	}

	public void setExtra_archivio_livello(String extra_archivio_livello) {
		this.extra_archivio_livello = extra_archivio_livello;
	}
	
}
