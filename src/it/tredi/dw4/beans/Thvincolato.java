package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclTitles;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.ThesauroFormsAdapter;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public abstract class Thvincolato extends Page {
	
	protected boolean active = false;
	
	protected Object model;
	
	private String termine = "";
	
	public abstract void init(Document dom);
	
	public abstract ThesauroFormsAdapter getFormsAdapter();
	
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
	
	public String getTermine() {
		if (termine == null)
			termine = "";
		return termine;
	}
	
	public void setTermine(String text) {
		this.termine = text;
	}

	public String close() {
		this.active = false;
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
	
	
	public String confirm() throws Exception{
		Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		return confirm(titolo);
	}
	
	public void cleanFields(String fieldName) throws Exception {
		List<Campo> campiL = new ArrayList<Campo>();
		Titolo titolo = new Titolo();
		Campo campo = new Campo();
		campo.setNome(fieldName);
		campo.setText("");
		campiL.add(campo);
		titolo.setCampi(campiL);
		fillFields(titolo);
	}

	public String confirm(Titolo titolo) throws Exception{
		fillFields(titolo);
		return close();
	}
	
	@SuppressWarnings("rawtypes")
	private void fillFields(Titolo titolo) throws Exception {
		List<Campo> campiL = titolo.getCampi();
		for (int campiLIndex = 0; campiLIndex < campiL.size(); campiLIndex++) {
			String value = campiL.get(campiLIndex).getText();		
			String xpath = campiL.get(campiLIndex).getNome();
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
	}
	
	/**
	 * Salvataggio di un nuovo termine da associare ad un thesauro
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveNewTermine() throws Exception {
		try {
			String fieldsArray = ""; // TODO fieldsArray dovrebbe contenere i termini selezionati dalla lista, di default = ''
			
			getFormsAdapter().getDefaultForm().addParam("lookup_fieldVal", termine);
			getFormsAdapter().getDefaultForm().addParam("xverb", "@new");
			getFormsAdapter().getDefaultForm().addParam("selected_campi", fieldsArray);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				return null;
			}
			
			// Elaborazione del titolo e chiamata a 'confirm'
			AclTitles titles = new AclTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			
			// Viene restituito un e un solo titolo
			if (titles.titoli != null && titles.titoli.size() == 1) {
				return confirm(titles.titoli.get(0));
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;			
		}
	}
	
}