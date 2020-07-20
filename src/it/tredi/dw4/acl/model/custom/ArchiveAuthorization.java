package it.tredi.dw4.acl.model.custom;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;

public class ArchiveAuthorization extends XmlEntity {

	private String userid = "";
	private String password = "";
	private String dominio = "";
	private List<Archivio> archivi = new ArrayList<Archivio>();
	
	private final String PWD_SKIP = "*PWD_SKIP*";
	
	// lista utilizzata per individuare i parametri di extra che al momento del caricamento
	// del bean avevano un valore associato, in modo da passare al service solo i parametri 
	// corretti e creare un sottoelemento extra (del documento o del fascicolo) con i soli 
	// parametri da gestire e non con tutti i possibili extra gestiti dai diversi clienti/applicativi
	// Con questa gestione si genera un XML piu' "pulito"
	private List<String> notEmptyParams = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		userid 			= assignExtraParam(dom, "extra/userid", false, "userid");
		password 		= assignExtraParam(dom, "extra/password", false, "password");
		dominio 		= assignExtraParam(dom, "extra/dominio", false, "dominio");
		archivi 		= XMLUtil.parseSetOfElement(dom, "extra/archivi/archivio", new Archivio());
		
		if (password != null && password.length() > 0)
			password 	= PWD_SKIP;
		
		if (archivi.isEmpty())
			archivi.add(new Archivio());
		
		return this;
	}
	
	/**
	 * Assegnazione di un valore (tramite recupero con xpath) ad un parametro di Extra. Inclusa
	 * la gestione dei parametri extra valorizzati
	 * 
	 * @param dom
	 * @param xpath
	 * @param nomeParam
	 * @return
	 */
	private String assignExtraParam(Document dom, String xpath, boolean isAttribute, String nomeParam) {
		return assignExtraParam(dom, xpath, isAttribute, nomeParam, true);
	}
	
	/**
	 * Assegnazione di un valore (tramite recupero con xpath) ad un parametro di Extra. Inclusa
	 * la gestione dei parametri extra valorizzati
	 * 
	 * @param dom
	 * @param xpath
	 * @param nomeParam
	 * @return
	 */
	private String assignExtraParam(Document dom, String xpath, boolean isAttribute, String nomeParam, boolean trimText) {
		if (dom == null || xpath == null || xpath.length() == 0)
			return "";
		
		String value = "";
		if (isAttribute)
			value = XMLUtil.parseStrictAttribute(dom, xpath);
		else
			value = XMLUtil.parseStrictElement(dom, xpath, trimText);
		
		if (value.length() > 0)
			notEmptyParams.add(nomeParam);
		
		return value;
	}
	
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
		Map<String, String> params = new HashMap<String, String>();
		
		if (userid.length() > 0 || notEmptyParams.contains("userid"))
			params.put(prefix+".userid", userid);
		
		if (!password.equals(PWD_SKIP)) {
			if (password.length() > 0 || notEmptyParams.contains("password")) {
				if (password.length() > 0 
						&& DocWayProperties.readProperty("acl.authaddon.password.encryption", "no").toLowerCase().equals("si"))
					password = DigestUtils.md5Hex(password); // TODO gestire altri algoritmi di encryption
			
				params.put(prefix+".password", password); 
			}
		}
		
		if (dominio.length() > 0 || notEmptyParams.contains("dominio"))
			params.put(prefix+".dominio", dominio);
		
		for (int i = 0; i < archivi.size(); i++) {
			Archivio archivio = (Archivio) archivi.get(i);
			if (archivio != null 
					&& archivio.getId() != null 
					&& archivio.getId().length() > 0)
				params.putAll(archivio.asFormAdapterParams(prefix+".archivi.archivio["+String.valueOf(i)+"]"));
		}
		
    	return params;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

	public List<Archivio> getArchivi() {
		return archivi;
	}

	public void setArchivi(List<Archivio> archivi) {
		this.archivi = archivi;
	}
	
	/**
	 * Aggiunta di un nuovo archivio
	 * @return
	 */
	public String addArchivio(){
		Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
		int index = archivi.indexOf(archivio);
		if (index == archivi.size()-1)
			archivi.add(index+1, new Archivio());
		else 
			archivi.add(new Archivio());
		return null;
	}
	
	/**
	 * cancellazione di un archivio
	 * @return
	 */
	public String deleteArchivio(){
		Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
		this.archivi.remove(archivio);
		if (archivi.size() == 0) archivi.add(new Archivio());
		return null;
	}
	
	/**
	 * spostamento in alto di un archivio
	 * @return
	 */
	public String moveUpArchivio(){
		Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
		int index = archivi.indexOf(archivio);
		if (index > 0 ) {
			archivi.remove(index);
			this.archivi.add(index-1, archivio);
		}
		return null;
	}
	
	/**
	 * spostamento in basso di un archivio
	 * @return
	 */
	public String moveDownArchivio(){
		Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
		int index = archivi.indexOf(archivio);
		if (index < archivi.size()-1 ) {
			archivi.remove(index);
			this.archivi.add(index+1, archivio);
		}
		return null;
	}
	
}
