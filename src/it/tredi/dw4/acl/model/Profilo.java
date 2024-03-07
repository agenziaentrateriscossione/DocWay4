package it.tredi.dw4.acl.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.dom4j.Document;

public class Profilo extends XmlEntity {
	private Document response;
	private String nrecord = ".";
	private String nome_profilo;
	private String matricola_profilo;
	private String cod_aoo;
	private String cod_amm;
	private String descrizione;
	private List<Listof_rights> listof_rights;
	private List<Right> personal_rights;
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	private List<Profile> profili = new ArrayList<Profile>();
	private String pers_ass_count;
	
	private HashMap<String, Right> rights = new HashMap<String, Right>();
	
	
	public Profilo() {}
    
    public Profilo(String xmlProfilo) throws Exception {
        this.init(XMLUtil.getDOM(xmlProfilo));
    }
    
    @SuppressWarnings("unchecked")
	public Profilo init(Document domProfilo) {
    	this.response = domProfilo;
    	this.nrecord = 	 		 XMLUtil.parseAttribute(domProfilo, "persona_interna/@nrecord", ".");
    	this.nome_profilo = 	 XMLUtil.parseAttribute(domProfilo, "persona_interna/@nome_profilo");
    	this.matricola_profilo = XMLUtil.parseAttribute(domProfilo, "persona_interna/@matricola_profilo");
    	this.cod_amm =			 XMLUtil.parseAttribute(domProfilo, "persona_interna/@cod_amm");
		this.cod_aoo = 	 		 XMLUtil.parseAttribute(domProfilo, "persona_interna/@cod_aoo");
    	this.pers_ass_count = 	 XMLUtil.parseAttribute(domProfilo, "persona_interna/@pers_ass_count");
    	this.descrizione = 		 XMLUtil.parseElement(domProfilo, "persona_interna/descrizione", false);
    	this.creazione.init(XMLUtil.createDocument(domProfilo, "//persona_interna/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domProfilo, "//persona_interna/storia/ultima_modifica"));
    	this.modifiche = XMLUtil.parseSetOfElement(domProfilo, "//persona_interna/storia/modifica", new Modifica());

    	this.personal_rights = XMLUtil.parseSetOfElement(domProfilo, "//persona_interna/personal_rights/right", new Right());
    	initListOfRights();
        if (personal_rights.size() > 0) fillPersonalRights();
        
    	// mbernardini 03/04/2015 : spostata la valutazione dei disabled perche' non ancora caricati i diritti del profilo
    	evaluateDisabled();
        
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".@nome_profilo", this.nome_profilo);
    	params.put(prefix+".@tipo", "profilo");
    	params.put(prefix+".@matricola_profilo", (this.matricola_profilo != null) ? this.matricola_profilo.trim() : null);
    	params.put(prefix+".@cod_amm", (this.cod_amm != null) ? this.cod_amm.trim() : null);
    	params.put(prefix+".@cod_aoo", (this.cod_aoo != null) ? this.cod_aoo.trim() : null);
    	params.put(prefix+".descrizione", this.descrizione);
    	for ( Iterator<?> i = this.rights.entrySet().iterator() ; i.hasNext() ; ) {  
    	    @SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) i.next();  
    	    Right right = (Right) e.getValue();
			if(right.getType().equals("alfa"))
				params.put("*tRight_"+right.getCod(), right.getValue());
			else if(right.getSelected())
				params.put("*right_"+right.getCod(), "1");
		}
    	return params;
    }
    
	public String getNome_profilo() {
		return nome_profilo;
	}

	public void setNome_profilo(String nome) {
		this.nome_profilo = nome;
	}

	

	public String getMatricola_profilo() {
		return matricola_profilo;
	}

	public void setMatricola_profilo(String matr) {
		this.matricola_profilo = matr;
	}
	
	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String titolo) {
		this.descrizione = titolo;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setListof_rights(List<Listof_rights> listof_rights) {
		this.listof_rights = listof_rights;
	}

	public List<Listof_rights> getListof_rights() {
		return listof_rights;
	}

	public void setPersonal_rights(List<Right> personal_rights) {
		this.personal_rights = personal_rights;
	}

	public List<Right> getPersonal_rights() {
		return personal_rights;
	}

	public void setRights(HashMap<String, Right> rights) {
		this.rights = rights;
	}

	public HashMap<String, Right> getRights() {
		return rights;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}
	
	@SuppressWarnings("unchecked")
	private void initListOfRights() {
    	this.listof_rights = XMLUtil.parseSetOfElement(response, "//listof_rights", new Listof_rights());
    	resetRights();
	}

	private void resetRights(){
    	List<Right> group_rights = new ArrayList<Right>();
    	this.rights.clear();
    	boolean openedFirst = true;
    	for (Iterator<Listof_rights> iter = listof_rights.iterator(); iter.hasNext();) {
			Listof_rights liste = (Listof_rights) iter.next();
			List<Group> gruppi = liste.getGroups();
			for (Iterator<Group> iterator = gruppi.iterator(); iterator.hasNext();) {
				Group gruppo = (Group) iterator.next();
				gruppo.setOpened(openedFirst);
				openedFirst = false;
				group_rights.addAll(gruppo.getRights());
				List<Group> sottogruppi = gruppo.getGroups();
				for (Iterator<Group> iterator2 = sottogruppi.iterator(); iterator2.hasNext();) {
					Group sottogruppo = (Group) iterator2.next();
					group_rights.addAll(sottogruppo.getRights());
				}
			}
			List<Db> dbs = liste.getDbs();
			for (Iterator<Db> iterator = dbs.iterator(); iterator.hasNext();) {
				Db db = (Db) iterator.next();
				List<Group> gruppi_db = db.getGroups();
				for (Iterator<Group> iterator2 = gruppi_db.iterator(); iterator2.hasNext();) {
					Group gruppo = (Group) iterator2.next();
					gruppo.setOpened(openedFirst);
					openedFirst = false;
					group_rights.addAll(gruppo.getRights());
					List<Group> sottogruppi = gruppo.getGroups();
					for (Iterator<Group> iterator3 = sottogruppi.iterator(); iterator3.hasNext();) {
						Group sottogruppo = (Group) iterator3.next();
						group_rights.addAll(sottogruppo.getRights());
					}
				}
			}
		}
    	for (Iterator<Right> iterator = group_rights.iterator(); iterator.hasNext();) {
			Right right = (Right) iterator.next();
			this.rights.put(right.getCod(), right);
		}
    	
    	// mbernardini 03/04/2015 : spostata la valutazione dei disabled perche' non ancora caricati i diritti del profilo
    	//evaluateDisabled();
    }
    
	public void changeOpenedGroup(){
		Group group = (Group) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("group");
		Db dbFather = (Db) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("db");
		for (Iterator<Listof_rights> iter = listof_rights.iterator(); iter.hasNext();) {
			Listof_rights liste = (Listof_rights) iter.next();
			List<Group> gruppi = liste.getGroups();
			for (Iterator<Group> iterator = gruppi.iterator(); iterator.hasNext();) {
				Group gruppo = (Group) iterator.next();
				if (gruppo.getLabel().equals(group.getLabel()))
					gruppo.setOpened(true);
				else
					gruppo.setOpened(false);
			}
			List<Db> dbs = liste.getDbs();
			for (Iterator<Db> iterator = dbs.iterator(); iterator.hasNext();) {
				Db db = (Db) iterator.next();
				List<Group> gruppi_db = db.getGroups();
				for (Iterator<Group> iterator2 = gruppi_db.iterator(); iterator2.hasNext();) {
					Group gruppo = (Group) iterator2.next();
					if (gruppo.getLabel().equals(group.getLabel())
							&& dbFather != null
							&& dbFather.getCod().equals(db.getCod()))
						gruppo.setOpened(true);
					else
						gruppo.setOpened(false);
				}
			}
		}
    }
	
    public void fillPersonalRights(){
    	for (Iterator<Right> iterator = personal_rights.iterator(); iterator.hasNext();) {
			Right right = (Right) iterator.next();
			if (null != this.rights.get(right.getCod()))
				this.rights.get(right.getCod()).setValue(right.getValue());
		}
    }
    
    public String evaluateDisabled(){
		for ( Iterator<?> i = this.rights.entrySet().iterator() ; i.hasNext() ; ) {  
    	    @SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) i.next();  
    	    Right right = (Right) e.getValue();
    	    String disabled = right.getDisabled().getIfVar();
    	    if (null != disabled && disabled.trim().length() > 0){
		        JexlEngine jexl = new JexlEngine();
		        // Create an expression object
		        Expression ex = jexl.createExpression( disabled );
		
		        // Create a context and add data
		        JexlContext jc = new MapContext();
		        jc.set("rights", this.rights);
		        jc.set("this", this);
		
		        // Now evaluate the expression, getting the result
		        boolean enabled = (Boolean) ex.evaluate(jc);
	        	right.setDisable(enabled);
	        	if ( enabled ) right.setValue("false");
    	    }
		}
        return null;
	}

	public void setProfili(List<Profile> profili) {
		this.profili = profili;
	}

	public List<Profile> getProfili() {
		return profili;
	}
	
	public String modifyRight(){
		Right right = (Right) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("right");
		boolean newVal = !right.getSelected();
		
		// mbernardini 04/09/2017 : migliorata la modifica di diritti di tipo 'alfa'
		if (right.getType() != null && right.getType().equals("alfa")) {
			if (right.getValue().equals("") || right.getValue().equals("*NHL*"))
				newVal = false;
			else
				newVal = true;
		}
		else
			right.setValue(String.valueOf(newVal));
		
		if (newVal){
			List<Oncheck> operation = right.getOnchecks();
			for (Iterator<Oncheck> iterator = operation.iterator(); iterator.hasNext();) {
				Oncheck oncheck = (Oncheck) iterator.next();
				Right changed = this.rights.get(oncheck.getWhat());
				if( oncheck.getAction().equals("check") ) 
					changed.setValue("true");
				else if( oncheck.getAction().equals("uncheck") )
					changed.setValue("false");
			}
		}
		else{
			List<Onuncheck> operation = right.getOnunchecks();
			for (Iterator<Onuncheck> iterator = operation.iterator(); iterator.hasNext();) {
				Onuncheck onuncheck = (Onuncheck) iterator.next();
				Right changed = this.rights.get(onuncheck.getWhat());
				if( onuncheck.getAction().equals("check") ) 
					changed.setValue("true");
				else if( onuncheck.getAction().equals("uncheck") )
					changed.setValue("false");
			}
		}
		evaluateDisabled();
		return null;
	}

	public void setPers_ass_count(String pers_ass_count) {
		this.pers_ass_count = pers_ass_count;
	}

	public String getPers_ass_count() {
		return pers_ass_count;
	}
	
}
