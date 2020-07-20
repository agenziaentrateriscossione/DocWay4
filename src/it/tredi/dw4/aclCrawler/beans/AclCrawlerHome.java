package it.tredi.dw4.aclCrawler.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclPage;
import it.tredi.dw4.acl.beans.AclShowindex;
import it.tredi.dw4.aclCrawler.adapters.AclCrawlerFormsAdapter;
import it.tredi.dw4.aclCrawler.model.AclGroup;
import it.tredi.dw4.aclCrawler.model.AclSection;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.Logger;

public class AclCrawlerHome extends AclPage {
	private String printableQuery = ""; 
	private List<SelectItem> aoosItems = new ArrayList<SelectItem>();
	private String aoo = "";
	private String appartenenza = "";
	private String altreUOR = "";
	
	private Rights rights;
	
	private String xml;
	private AclCrawlerFormsAdapter formsAdapter;
	
	public AclCrawlerHome() throws Exception {
		this.formsAdapter = new AclCrawlerFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document dom) throws Exception {
		getFormsAdapter().init();
		try {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			String nonBooleanRightsText = response.getElementText("//response/nonBooleanRights");
			
			if (nonBooleanRightsText == null)
				nonBooleanRightsText = "";
			
			Logger.debug("[aclCrawler] nonBooleanRightsText: " + nonBooleanRightsText);
			
			String[] nonBooleanRights = nonBooleanRightsText.split(";");
			AclGroup.initNonBooleanRights(Arrays.asList(nonBooleanRights));
			
			this.rights = new Rights();
			
			@SuppressWarnings("unchecked")
			List<Element> rightsFiles = response.selectNodes("//response/rightsFiles/*");
			for (Element rightsFile : rightsFiles) {
				this.rights.loadRightFile((Element) rightsFile.detach());
			}
			
			String AOOListText = response.getElementText("//response/AOOList");
			
			if (AOOListText == null)
				AOOListText = "";
			
			String[] AOOList = AOOListText.split(";");
			populateAOOs(AOOList);
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
	}
	
	public String getAltreUOR() {
		return altreUOR;
	}

	public void setAltreUOR(String altreUOR) {
		this.altreUOR = altreUOR;
	}

	public String getAppartenenza() {
		return appartenenza;
	}

	public void setAppartenenza(String appartenenza) {
		this.appartenenza = appartenenza;
	}

	public String getAoo() {
		return aoo;
	}

	public void setAoo(String aoo) {
		this.aoo = aoo;
	}

	public void populateAOOs(String[] aoos) {
		SelectItem item = new SelectItem("");
		this.aoosItems.add(item);
		
		for (String aoo : aoos) {
			item = new SelectItem(aoo);
			this.aoosItems.add(item);
		}
	}
	
	public List<SelectItem> getAoosItems() {
		return aoosItems;
	}

	public String getPrintableQuery() {
		return printableQuery;
	}

	public AclCrawlerFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	public String getXml() {
		return this.xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	

	public Rights getRights() {
		return rights;
	}

	public String search() throws Exception {
		String query = "";
		this.printableQuery = "";
		
		Iterator<AclSection> aclSectionsIt = rights.getRightsDocs().iterator();
		while (aclSectionsIt.hasNext()) {
			AclSection section = aclSectionsIt.next();
			for (Map.Entry<String, List<AclGroup>> groupList : section.getGroups().entrySet()) {
				Iterator<AclGroup> groupsit = groupList.getValue().iterator();
				while (groupsit.hasNext()) {
					AclGroup group = groupsit.next();
					Iterator<Map<String, Object>> rightsIt = group.getRights().iterator();
					while (rightsIt.hasNext()) {
						Map<String, Object> rightInfo = rightsIt.next();
						if ( rightInfo.get("checked").equals("true")) {
							
							if (query.length() > 0) {
								query += " AND ";
								this.printableQuery += " E ";
							}
							query += "(([/persona_interna/personal_rights/right/@cod/]=\"" + rightInfo.get("cod") + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
							this.printableQuery += rightInfo.get("label") + "(" + rightInfo.get("cod") + ")";
						}
						else if (rightInfo.get("checked").equals("false")) {
							if (query.length() > 0) {
								query += " AND ";
								this.printableQuery += " E ";
							}
							query += "(([/persona_interna/personal_rights/right/@cod/]=\"" + rightInfo.get("cod") + "\") ADJ ([/persona_interna/personal_rights/right]=\"FALSE\"))";
							this.printableQuery += "NON " + rightInfo.get("label") + "(" + rightInfo.get("cod") + ")";
						}
					}
				}
			}
			
		}
		
		for (Map.Entry<String, List<Map<String, Object>>> repList : rights.getRepSection().getRepertori().entrySet()) {
			Iterator<Map<String, Object>> repsInfosIt = repList.getValue().iterator();
			while (repsInfosIt.hasNext()) {
				Map<String, Object> repInfo = repsInfosIt.next();
				
				if (((String) repInfo.get("checkedI")).equals("true")) {
					if (query.length() > 0)
						query += " AND ";
					query += "(([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-InsRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
				else if (((String) repInfo.get("checkedI")).equals("false")) {
					if (query.length() > 0)
						query += " AND ";
					query += "NOT (([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-InsRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
				
				if (((String) repInfo.get("checkedV")).equals("true")) {
					if (query.length() > 0)
						query += " AND ";
					query += "(([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-VisRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
				else if (((String) repInfo.get("checkedV")).equals("false")) {
					if (query.length() > 0)
						query += " AND ";
					query += "NOT (([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-VisRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
				
				if (((String) repInfo.get("checkedC")).equals("true")) {
					if (query.length() > 0)
						query += " AND ";
					query += "(([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-CompRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
				else if (((String) repInfo.get("checkedC")).equals("true")) {
					if (query.length() > 0)
						query += " AND ";
					query += "NOT (([/persona_interna/personal_rights/right/@cod/]=\"" + repInfo.get("cod") + "-CompRep" + "\") ADJ ([/persona_interna/personal_rights/right]=\"TRUE\"))";
				}
			}
		}
		
		if (!this.aoo.equals("")) {
			if (query.length() > 0)
				query += " AND ";
			
			query += "([/persona_interna/@cod_aoo/]=\"" + this.aoo + "\")";
		}
		
		if (!this.appartenenza.equals("")) {
			if (query.length() > 0)
				query += " AND ";
			
			query += "(([/persona_interna/@cod_uff/]=" + this.appartenenza + ")";
			query += ")";
		}
		
		if (!this.altreUOR.equals("")) {
			if (query.length() > 0)
				query += " AND ";
			
			query += "(([/persona_interna/personal_rights/right/@cod/]={";
			
			List<String> nonBooleanRights = AclGroup.getNonBooleanRights(); 
			for (int i = 0; i < nonBooleanRights.size(); i++) {
				query += nonBooleanRights.get(i);
				if (i != nonBooleanRights.size() - 1)
					query += ",";
			}
			
			query += "}) ADJ  ([/persona_interna/personal_rights/right]=" + altreUOR + "))";
		}
		
		setSessionAttribute("AclCrawlerPrintableQuery", this.printableQuery);
		
		return queryPlain(query);
	}
	
	public String clean() {
		Iterator<AclSection> aclSectionsIt = this.rights.getRightsDocs().iterator();
		while (aclSectionsIt.hasNext()) {
			AclSection section = aclSectionsIt.next();
			for (Map.Entry<String, List<AclGroup>> groupList : section.getGroups().entrySet()) {
				Iterator<AclGroup> groupsit = groupList.getValue().iterator();
				while (groupsit.hasNext()) {
					AclGroup group = groupsit.next();
					Iterator<Map<String, Object>> rightsIt = group.getRights().iterator();
					while (rightsIt.hasNext()) {
						Map<String, Object> rightInfo = rightsIt.next();
						rightInfo.put("checked", "not");
						rightInfo.put("image", "css/images/not_checked.gif");
					}
				}
			}
		}
		
		for (Map.Entry<String, List<Map<String, Object>>> repList : this.rights.getRepSection().getRepertori().entrySet()) {
			Iterator<Map<String, Object>> repsInfosIt = repList.getValue().iterator();
			while (repsInfosIt.hasNext()) {
				Map<String, Object> repInfo = repsInfosIt.next();
//				repInfo.put("checkedI", false);
//				repInfo.put("checkedV", false);
//				repInfo.put("checkedC", false);
				repInfo.put("checkedI", "not");
				repInfo.put("imageI", "css/images/not_checked.gif");
				repInfo.put("checkedV", "not");
				repInfo.put("imageV", "css/images/not_checked.gif");
				repInfo.put("checkedC", "not");
				repInfo.put("imageC", "css/images/not_checked.gif");
			}
		}
		
		this.altreUOR = "";
		this.aoo = "";
		this.appartenenza = "";
		
		return null;
	}
	
	public String openIndexAppartenenza() throws Exception {
		this.openIndex("appartenenza", "xml,/struttura_interna/nome", this.appartenenza, "0", " ");
		return null;
	}
	
	public String openIndexAltreUOR() throws Exception {
		this.openIndex("altreUOR", "xml,/struttura_interna/nome", this.altreUOR, "0", " ");
		return null;
	}
	
	public void openIndex(String keyPath, String value, String startKey, String common) throws Exception {
		openIndex("", keyPath, value, startKey, common);
	}

	public void openIndex(String inputName, String keyPath, String value, String startKey, String common) throws Exception {
		AclShowindex aclShowindex = new AclShowindex();
		setShowindex(aclShowindex);
		aclShowindex.setModel(this);

		XMLDocumento response = this._openIdex(keyPath, // keyPath
				startKey, // startKey
				null, // shwMode
				common, // common
				null, // threl
				inputName, // inputName
				null, // windowTitle
				value // value
				);

		aclShowindex.getFormsAdapter().fillFormsFromResponse(response);
		aclShowindex.init(response.getDocument());

		/*
		 * if ( aclShowindex().size() ==1 )
		 * aclShowindex(aclThvincolato.getTitoli().get(0)); else
		 */
		aclShowindex.setActive(true);
	}
	
	protected String queryPlain(String query) throws Exception {
		try {
			if (!this.appartenenza.equals("")) {
				getFormsAdapter().getDefaultForm().addParam("appartenenza", this.appartenenza);
			}
			
			getFormsAdapter().queryPlain(query);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
				return null;
			}
			
			if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
			} else {
				return buildTitlePageAndReturnNavigationRule(response);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return null;
		}
	}
	
	protected XMLDocumento _openIdex(String keyPath, String startKey,
			String shwMode, String common, String threl, String inputName,
			String windowTitle, String value) throws Exception {
		getFormsAdapter().openIndex(keyPath, startKey, shwMode, common, threl, inputName, windowTitle, value, false);

		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di Query
		return response;
	}
}
