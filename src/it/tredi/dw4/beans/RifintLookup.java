package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.RifintLookupFormsAdapter;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.LookupUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public abstract class RifintLookup extends Page {
	
	protected boolean active = false;
	
	protected Object model;
	
	protected List<Titolo> titoli;
	
	protected String physDoc = "";
	
	public abstract void init(Document dom);
	
	public abstract RifintLookupFormsAdapter getFormsAdapter();
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
 
	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public void setTitles(List<Titolo> titoli) {
		this.titoli = titoli;
	}

	public List<Titolo> getTitles() {
		return this.titoli;
	}
	
	public String getPhysDoc() {
		if (physDoc == null)
			return "";
		else
			return this.physDoc;
	}
	
	public void setPhysDoc(String physDoc) {
		if (physDoc == null)
			physDoc = "";
		this.physDoc = physDoc;
	}
	
	public String close() {
		this.active = false;
		return null;
	}
	
	public String primaPagina() throws Exception {
		if (getFormsAdapter().primaPagina()) {
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}		
	
	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}	
	
	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}
	
	public String ultimaPagina() throws Exception {
		if (getFormsAdapter().ultimaPagina()) {
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}
	
	public String getFirstPosition(){
		return String.valueOf(getFormsAdapter().getFirstPosition());
	}
	
	public String getLastPosition(){
		return String.valueOf(getFormsAdapter().getLastPosition());
	}
	
	public int getCount(){
		return getFormsAdapter().getDefaultForm().getParamAsInt("count");
	}
	
	/**
	 * Caricamento di una specifica pagina della lista titoli
	 * @param pageNumber
	 * @return
	 * @throws Exception
	 */
	public String paginaSpecifica(int pageNumber) throws Exception {
		try {
			if (pageNumber <= 0)
				pageNumber = 1;
			else if (pageNumber > getFormsAdapter().getTotal())
				pageNumber = getFormsAdapter().getTotal();
			
			getFormsAdapter().paginaSpecifica(pageNumber);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
			
			return null;
		} 
		catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String confirm(Object todoOnCompleteLookupObject, String todoOnCompleteLookupMethod) throws Exception{
		Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		return confirm(titolo, todoOnCompleteLookupObject, todoOnCompleteLookupMethod);
	}
	
	public String confirm() throws Exception{
		return confirm(null, "");
	}
	
	public void cleanFields(String campi) throws Exception {
		List<Campo> campiL = new ArrayList<Campo>();
		String []campiArr = campi.split("\\|");
		Titolo titolo = new Titolo();
		for (int i = 0; i < campiArr.length; i++) {
			String nomeCampo = StringUtil.replace(campiArr[i].trim(), "*rif_interni", "");
			Campo campo = new Campo();
			campo.setNome(nomeCampo);
			campo.setText("");
			campiL.add(campo);
		}
		titolo.setCampi(campiL);
		fillFields(titolo, false, null, "");
	}

	public String confirm(Titolo titolo, Object todoOnCompleteLookupObject, String todoOnCompleteLookupMethod) throws Exception{
		if (isFieldValInfoForSecondLookup(titolo)) {
			// Eseguo un secondo lookup per recuperare tutti i dati relativi al rif int
			titolo = doSecondLookup(titolo);
		}
		
		if (titolo != null)
			fillFields(titolo, true, todoOnCompleteLookupObject, todoOnCompleteLookupMethod);
		return close();
	}
	
	public String confirm(Titolo titolo) throws Exception{
		return confirm(titolo, null, "");
	}
	
	/**
	 * Verifica se occorre o meno un secondo lookup per recuperare i dati del rif int
	 * @param titolo
	 * @return
	 */
	private boolean isFieldValInfoForSecondLookup(Titolo titolo) {
		if (titolo != null && titolo.getExtra() != null) {
			String extra = titolo.getExtra();
			int sepPos = extra.indexOf("|");
			if (sepPos != -1)
				extra = extra.substring(0, sepPos);
			
			if (extra.length() == 0)
				return false;
			
			return true;
		}
		else
			return false;
	}
		
	/**
	 * Esegue il secondo lookup per il refintlookup per il caricamento di tutti 
	 * i dati (es. se ho eseguito un lookup su una struttura procedo al secondo 
	 * lookup necessario a recuperare il responsabile della struttura selezionata)
	 * 
	 * @param titolo titolo del precedente lookup selezionato
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Titolo doSecondLookup(Titolo titolo) throws Exception {
		try {
			String alias = "";
			String xverb = "";
			String fieldVal = "";
			String lookup_alias = getFormsAdapter().getDefaultForm().getParam("lookup_alias");
			if (lookup_alias.equals("persint_nomcogn")) {
				alias = "struint_nome";
				xverb = "@search_uff";
				fieldVal = titolo.getExtra() + "|" + titolo.getTesto();
			}
			else if (lookup_alias.indexOf("struint_nome") == 0) {
				alias = "persint_nomcognom";
				xverb = "@search_persona";
				fieldVal = titolo.getTesto() + "|" + titolo.getExtra();
			}
			
			// Imposto i parametri del formsAdapter per la chiamata al secondo lookup
			getFormsAdapter().doSecondLookup(alias, xverb, fieldVal);
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			
			List<Titolo> secondLookupTitoli = (List<Titolo>) XMLUtil.parseSetOfElement(response.getDocument(), "//titolo", new Titolo());
			if (secondLookupTitoli != null && secondLookupTitoli.size() > 0)
				return (Titolo) XMLUtil.parseSetOfElement(response.getDocument(), "//titolo", new Titolo()).get(0);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void fillFields(Titolo titolo, boolean addRow, Object todoOnCompleteLookupObject, String todoOnCompleteLookupMethod) throws Exception {
		String instanceName = "";
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
					
					if (addRow && instanceName.equals("")) instanceName = propertyName; // nome dell'istanza (caso di lookup su multi riga)
				}
				
				if (propertyName.length() > 0) {
					Method getter = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
					obj = getter.invoke(obj);
					
					if (!"".equals(index)){
						obj = ((ArrayList)obj).get(Integer.valueOf(index));
					}
				}
			}
			String propertyName = splitL[splitL.length - 1];
			if (propertyName.startsWith("@"))
				propertyName = propertyName.substring(1);
			
			Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();
			obj = setter.invoke(obj, value);
		}
		
		if (addRow)
			LookupUtil.addRowOnLookup(model, instanceName);
		
		//tiommi: salvataggio dell'oggetto che ha chiamato il lookup su cui compiere l'azione in todo
		if (todoOnCompleteLookupObject != null && todoOnCompleteLookupMethod != null && !todoOnCompleteLookupMethod.isEmpty()) {
			try {
				// invocazione del metodo di callback
				Method method = todoOnCompleteLookupObject.getClass().getMethod(todoOnCompleteLookupMethod);
				method.invoke(todoOnCompleteLookupObject);
			} 
			catch (Exception ex) {
				//TODO messaggio di errore
			}
		}
	}

}