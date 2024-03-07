package it.tredi.dw4.docway.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.IntraAooDocumentFormsAdapter;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Popup (modale) di visualizzazione dei titoli derivanti da un lookup su intra-aoo (chiamata a 3diWS)
 */
public class DocWayRifintIntraAooLookup  extends Page {
	
	private String xml;
	private IntraAooDocumentFormsAdapter formsAdapter;
	
	private String codAmmAoo;
	private int pageCount;
	private int pageIndex;
	private boolean canFirst;
	private boolean canPrev;
	private boolean canNext;
	private boolean canLast;
	
	private Object model;
	private ArrayList<Titolo> titoli;
	
	private boolean active;
	
	public DocWayRifintIntraAooLookup(Object model) throws Exception {
		this.model = model;
		this.formsAdapter = new IntraAooDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public IntraAooDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml = dom.asXML();
		
    	this.codAmmAoo = XMLUtil.parseStrictAttribute(dom, "/response/titoli/@codAmmAoo", "");
    	this.pageCount = StringUtil.intValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@pageCount", "0"));
    	this.pageIndex = StringUtil.intValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@pageIndex", "0"));
    	this.canFirst = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@canFirst", "false"));
    	this.canPrev = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@canPrev", "false"));
    	this.canNext = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@canNext", "false"));
    	this.canLast = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/titoli/@canLast", "false"));
    	
    	// lettura dei titoli derivanti da lookup su intra-aoo
    	this.titoli = (ArrayList<Titolo>) XMLUtil.parseSetOfElement(dom, "//titolo", new Titolo());
    }
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getCodAmmAoo() {
		return codAmmAoo;
	}

	public void setCodAmmAoo(String codAmmAoo) {
		this.codAmmAoo = codAmmAoo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public boolean isCanFirst() {
		return canFirst;
	}

	public void setCanFirst(boolean canFirst) {
		this.canFirst = canFirst;
	}

	public boolean isCanPrev() {
		return canPrev;
	}

	public void setCanPrev(boolean canPrev) {
		this.canPrev = canPrev;
	}

	public boolean isCanNext() {
		return canNext;
	}

	public void setCanNext(boolean canNext) {
		this.canNext = canNext;
	}

	public boolean isCanLast() {
		return canLast;
	}

	public void setCanLast(boolean canLast) {
		this.canLast = canLast;
	}
	
	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * Selzione di un titolo del lookup per assegnazione a RPA in comunicazione intra-aoo
	 * @param titolo
	 * @return
	 * @throws Exception
	 */
	public String confirm() throws Exception {
		try {
			Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			if (titolo != null) {
				getFormsAdapter().loadByIdIUnit(titolo.getIdIUnit(), codAmmAoo);
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());
				
				if (titoli.size() == 1) {
					this.active = false;
					confirm(titoli.get(0));
				}
			}
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		} 
	}
	
	/**
	 * Selzione di un titolo del lookup per assegnazione a RPA in comunicazione intra-aoo
	 * @param titolo
	 * @return
	 * @throws Exception
	 */
	public String confirm(Titolo titolo) throws Exception {
		try {
			fillFields(titolo);
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Riempimento di tutti i campi dell'oggetto di model in base ai campi specificati nel titolo selezionato
	 * @param titolo
	 * @throws Exception
	 */
	private void fillFields(Titolo titolo) throws Exception {
		List<Campo> campiL = titolo.getCampi();
		for (int campiLIndex = 0; campiLIndex < campiL.size(); campiLIndex++) {
			String value = campiL.get(campiLIndex).getText();		
			String xpath = StringUtil.replace(campiL.get(campiLIndex).getNome(), "*rif_interni", "");
			String []splitL = xpath.split("\\.");
			Object obj = model;
			for (int splitLindex = 0; splitLindex < splitL.length - 1; splitLindex++) {
				String propertyName = splitL[splitLindex];
				String index = "";
				if (propertyName.startsWith("@"))
					propertyName = propertyName.substring(1);
				if (propertyName.endsWith("]")){
					index = propertyName.substring(propertyName.indexOf("[")+1, propertyName.length()-1);
					propertyName = propertyName.substring(0, propertyName.indexOf("["));
				}
				
				if (propertyName.length() > 0) {
					Method getter = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
					obj = getter.invoke(obj);
					
					if (!"".equals(index)){
						obj = ((ArrayList<?>)obj).get(Integer.valueOf(index));
					}
				}
			}
			String propertyName = splitL[splitL.length - 1];
			if (propertyName.startsWith("@"))
				propertyName = propertyName.substring(1);
			
			Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();
			obj = setter.invoke(obj, value);
		}
	}
	
	/**
	 * Chiusura del popup modale di visualizzazione dei titoli derivanti da una ricerca
	 * @return
	 * @throws Exception
	 */
	public String close() throws Exception {
		this.active = false;
		return null;
	}
	
	/**
	 * Caricamento della prima pagina dei risultati di lookup
	 * @return
	 * @throws Exception
	 */
	public String primaPagina() throws Exception {
		try {
			if (isCanFirst()) {
				getFormsAdapter().primaPagina(codAmmAoo);
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());		
			}
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento della pagina precedente dei risultati di lookup
	 * @return
	 * @throws Exception
	 */
	public String paginaPrecedente() throws Exception {
		try {
			if (isCanPrev()) {
				getFormsAdapter().paginaPrecedente(codAmmAoo);
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());		
			}
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento della pagina successiva dei risultati di lookup
	 * @return
	 * @throws Exception
	 */
	public String paginaSuccessiva() throws Exception {
		try {
			if (isCanNext()) {
				getFormsAdapter().paginaSuccessiva(codAmmAoo);
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());		
			}
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento dell'ultima pagina dei risultati di lookup
	 * @return
	 * @throws Exception
	 */
	public String ultimaPagina() throws Exception {
		try {
			if (isCanLast()) {
				getFormsAdapter().ultimaPagina(codAmmAoo);
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());		
			}
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
