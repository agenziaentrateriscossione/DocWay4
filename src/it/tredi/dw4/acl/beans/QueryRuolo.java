package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class QueryRuolo extends AclQuery {
	private String ruoli_nome;
	private String ruoli_descrizione;
	private String focusElement = "ruoli_descrizione";
	private String xml;
	private List<Societa> societa;
	private Societa selected;
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryRuolo() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		xml = dom.asXML();
		this.societa = XMLUtil.parseSetOfElement(dom, "//societa_select/societa", new Societa());
		Societa test = new Societa();
    	test.setText("");
    	test.setCod("");
    	societa.add(0, test);
    	for (Iterator<Societa> iterator = societa.iterator(); iterator.hasNext();) {
			Societa society = (Societa) iterator.next();
			if ("selected".equals(society.getSelected())){
				setSelected(society);
			}
		}
    	if (null == selected) selected= new Societa();
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	//TEMP
	public String queryPlain() throws Exception {
		String query =  "";
		query +=  addQueryField("ruoli_nome", ruoli_nome);
		query +=  addQueryField("ruoli_descrizione", ruoli_descrizione);
		if (null != selected.getCod() && selected.getCod().length() > 0)
			query +=  addQueryField("/ruolo/societa/@cod", selected.getCod());
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "([UD,/xw/@UdType]=ruolo)";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [ruoli_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [ruoli_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/ruolo/nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.ruoli_nome = null;
		this.ruoli_descrizione = null;
		return null;
	}

	public void setRuoli_nome(String cod_profilo) {
		this.ruoli_nome = cod_profilo;
	}

	public String getRuoli_nome() {
		return ruoli_nome;
	}

	public void setRuoli_descrizione(String nome_profilo) {
		this.ruoli_descrizione = nome_profilo;
	}

	public String getRuoli_descrizione() {
		return ruoli_descrizione;
	}
	
	public String openIndexRuoliDescrizione() throws Exception {
		this.focusElement = "ruoli_descrizione";
		this.openIndex("ruoli_descrizione", this.ruoli_descrizione, "0", null);
		return null;
	}
	public String openIndexRuoliNome() throws Exception {
		this.focusElement = "ruoli_nome";
		this.openIndex("ruoli_nome", this.ruoli_nome, "0", " ");
		return null;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	public void setSocieta(List<Societa> societa) {
		this.societa = societa;
	}

	public List<Societa> getSocieta() {
		return societa;
	}

	public void setSelected(Societa selected) {
		this.selected = selected;
	}

	public Societa getSelected() {
		return selected;
	}
	public void societaValueChange(ValueChangeEvent vce) {  
        String societa_name = (String)vce.getNewValue();
        for (Iterator<Societa> iterator = societa.iterator(); iterator.hasNext();) {
			Societa society = (Societa) iterator.next();
			if (society.getText().equals(societa_name) || (societa_name == null && society.getText().length() == 0)) {
				setSelected(society);
			}
		}
        
    }
}
