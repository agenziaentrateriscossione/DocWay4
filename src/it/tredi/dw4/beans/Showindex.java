package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.acl.model.Vocabolario;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.IndexFormsAdapter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public abstract class Showindex extends Page {
	
	protected boolean active = false;
	
	protected ArrayList<Vocabolario> vocabolari;
	
	protected HashMap<String, Vocabolario> vocabolariSelezionati;
	
	protected Object model;
	
	protected String seekText = "";
	
	public abstract void init(Document dom);
	
	public abstract IndexFormsAdapter getFormsAdapter();
	
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
		
	public String getSeekText() {
		return seekText;
	}
	
	public void setSeekText(String text) {
		this.seekText = text;
	}
	
	public void setVocabolari(ArrayList<Vocabolario> vocabolari) {
		this.vocabolari = vocabolari;
	}

	public ArrayList<Vocabolario> getVocabolari() {
		return vocabolari;
	}

	public void setVocabolariSelezionati(HashMap<String, Vocabolario> vocabolari) {
		this.vocabolariSelezionati = vocabolari;
	}

	public HashMap<String, Vocabolario> getVocabolariSelezionati() {
		return vocabolariSelezionati;
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
		Vocabolario vocabolario = (Vocabolario) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vocabulary");
		return confirm(vocabolario);
	}
	
	public String confirm(Vocabolario vocabolario) throws Exception{
		List<Vocabolario> vocabolari = new ArrayList<Vocabolario>();
		if (vocabolario != null)
			vocabolari.add(vocabolario);
			
		fillFields(vocabolari);
		return close();
	}
	
	public String confirm(List<Vocabolario> vocabolari) throws Exception{
		if (vocabolari != null)
			fillFields(vocabolari);
		return close();
	}
	
	private void fillFields(List<Vocabolario> vocabolari) throws Exception {
		String propertyName = getFormsAdapter().getDefaultForm().getParam("fillField");
		Object obj = model;
		Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();
		
		String queryValue = "";
		if (vocabolari != null && vocabolari.size() > 0) {
			for (int i=0; i<vocabolari.size(); i++) {
				Vocabolario vocabolario = (Vocabolario) vocabolari.get(i);
				
				String value = vocabolario.getChiave();
				if (value.indexOf(' ') >= 0 || value.indexOf('\t') >= 0)
					queryValue += (queryValue.length() > 0 ? " or " : "") + "\"" + value.replaceAll("\"", "") + "\"";
				else
					queryValue += (queryValue.length() > 0 ? " or " : "") + value;
				
				if (propertyName.equals("persint_cognome"))
					queryValue = queryValue.replaceAll("\"", "");
			}
		}
		obj = setter.invoke(obj, queryValue);
	}
	
	/**
	 * Esegue il posizionamento all'interno del vocabolario (avviato
	 * dal comando 'Vai a')
	 * @return
	 * @throws Exception
	 */
	public String posiziona() throws Exception {
		try {
			if (seekText != null && seekText.length() > 0) {
				getFormsAdapter().posiziona(seekText);
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
				UserBean userBean = (UserBean) session.getAttribute("userBean");
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				getFormsAdapter().fillFormsFromResponse(response);
				init(response.getDocument());			
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * aggiorna i vocabolari selezionati sul check/uncheck su vocabolario
	 * @return
	 * @throws Exception
	 */
	public String updateVocabolariSelezionati() throws Exception {
		_updateVocabolariSelezionati();
		return null;
	}
	
	/**
	 * aggiorna i vocabolari selezionati sul check/uncheck su vocabolario
	 * @param event
	 * @throws Exception
	 */
	public void updateVocabolariSelezionati(AjaxBehaviorEvent event) throws Exception {
		_updateVocabolariSelezionati();
	}
	
	/**
	 * aggiorna i vocabolari selezionati sul check/uncheck su vocabolario
	 * @throws Exception
	 */
	private void _updateVocabolariSelezionati() throws Exception {
		// TODO Soluzione migliorabile con passaggio di parametri (ancora non funzionante)
		if (vocabolari != null && vocabolari.size() > 0) {
			for (int i=0; i<vocabolari.size(); i++) {
				Vocabolario vocabolario = (Vocabolario) vocabolari.get(i);
				if (vocabolario != null) {
					if (vocabolario.isSelezionato()) {
						// Aggiungo il vocabolario a quelli selezionati (se non presente)
						if (!vocabolariSelezionati.containsKey(vocabolario.getChiave()))
							vocabolariSelezionati.put(vocabolario.getChiave(), vocabolario);
					}
					else {
						// Elimino il vocabolario da quelli selezionati (se presente)
						if (vocabolariSelezionati.containsKey(vocabolario.getChiave()))
							vocabolariSelezionati.remove(vocabolario.getChiave());
					}
				}
			}
		}
	}
	
	/**
	 * Conferma di tutte le chiavi di vocabolario selezionate (check di 
	 * selezione multipla)
	 * @return
	 * @throws Exception
	 */
	public String confirmSelezionati() throws Exception {
		List<Vocabolario> listVocabolariSelezionati = null;
		if (vocabolariSelezionati != null && !vocabolariSelezionati.isEmpty())
			listVocabolariSelezionati = new ArrayList<Vocabolario>(vocabolariSelezionati.values());
			
		return confirm(listVocabolariSelezionati);
	}
	
	/**
	 * Selezione/Deselezione di un vocabolario da parte di un utente
	 * @return
	 * @throws Exception
	 */
	public String selectVocabulary(ValueChangeEvent e) throws Exception{
		try{
			boolean selected = new Boolean(e.getNewValue()+"").booleanValue();
			Vocabolario vocabolario = (Vocabolario) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vocabulary");
			if (vocabolario.isSelezionato() && !selected){
				vocabolario.setSelezionato(false);
				
				// elimino il vocabolario da quelli selezionati (se presente)
				if (vocabolariSelezionati.containsKey(vocabolario.getChiave()))
					vocabolariSelezionati.remove(vocabolario.getChiave());
			}
			else if (!vocabolario.isSelezionato() && selected){
				vocabolario.setSelezionato(true);
				
				// aggiungo il vocabolario a quelli selezionati (se non presente)
				if (!vocabolariSelezionati.containsKey(vocabolario.getChiave()))
					vocabolariSelezionati.put(vocabolario.getChiave(), vocabolario);
			}
			return null;
		} 
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;			
		}
	}
	
}