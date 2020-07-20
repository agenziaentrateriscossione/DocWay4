package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Msg;
import it.tredi.dw4.beans.ThEdit;
import it.tredi.dw4.docway.adapters.DocWayThEditFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docwayproc.model.NodoTitolario;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

/**
 * editing del titolario di classificazione
 * 
 * @author mbernardini
 */
public class ThEditTitolarioClassificazione extends ThEdit  {
	
	private DocWayThEditFormsAdapter formsAdapter;
	private String xml = "";
	
	private List<NodoTitolario> nt = new ArrayList<NodoTitolario>(); // nodi figli del nodo corrente
	private boolean levelEdited = false; // indica se sono state applicate modifiche al livello corrente o meno
	
	private String levelToLoad = ""; // nome del livello da caricare
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public List<NodoTitolario> getNt() {
		return nt;
	}

	public void setNt(List<NodoTitolario> nt) {
		this.nt = nt;
	}

	public boolean isLevelEdited() {
		return levelEdited;
	}

	public void setLevelEdited(boolean levelEdited) {
		this.levelEdited = levelEdited;
	}
	
	public String getLevelToLoad() {
		return levelToLoad;
	}

	public void setLevelToLoad(String levelToLoad) {
		this.levelToLoad = levelToLoad;
	}
	
	@Override
	public DocWayThEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public ThEditTitolarioClassificazione() throws Exception {
		this.formsAdapter = new DocWayThEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml 				= dom.asXML();
		
		keypath 			= XMLUtil.parseStrictAttribute(dom, "/response/@keypath");
		name 				= XMLUtil.parseStrictAttribute(dom, "/response/@name");
		values 				= "";
		
		relDescrOptions 	= new ArrayList<Option>();
		nt					= new ArrayList<NodoTitolario>();
		
		loadRelDescr();
		rels2Form();
		
		if (values != null && values.length() > 0) {
			String[] currLevel = values.split("\n");
			if (currLevel != null && currLevel.length > 0) {
				for (int i=0; i<currLevel.length; i++) {
					if (currLevel[i] != null)
						nt.add(new NodoTitolario(currLevel[i]));
				}
			}
		}
		
		levelEdited = false;
		levelToLoad = "";
	}
	
	/**
	 * Salvataggio dei valori per il mezzo di trasmissione o tipologia corrente
	 */
	@Override
	public String salvaNodo() throws Exception {
		try {
			if (keypath != null && !keypath.equals("") 
						&& name != null && !name.equals("")) {
				
				// conversione da lista nodi a stringa values
				if (nt != null && nt.size() > 0) {
					values = "";
					for (int i=0; i<nt.size(); i++) {
						NodoTitolario nodo = (NodoTitolario) nt.get(i);
						if (nodo != null && nodo.getText() != null && nodo.getText().length() > 0)
							values = values + nodo.getText() + "\n"; 
					}
				}
				
				String rels = form2Rels();
				if (rels != null && !rels.equals("")) {
					formsAdapter.salvaNodo(rels, name);
					
					XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
						return null;
					}
					
					formsAdapter.fillFormsFromResponse(response);
					this.init(response.getDocument());
				}
			}
			
			Msg message = new Msg();
			message.setType("success");
			message.setTitle(I18N.mrs("dw4.titolario_di_classificazione"));
			message.setMessage(I18N.mrs("dw4.aggiornamento_del_titolario_completato_con_successo"));
			message.setActive(true);
			setSessionAttribute("msg", message);
			
			levelEdited = false;
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Reload della maschera per modifica del canale o del tipo
	 */
	@Override
	public String reload() throws Exception {
		try {
			formsAdapter.reload(keypath, name);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * caricamento di un nodo del titolario di classificazione
	 * @deprecated problemi con versione 2.2.6 di JSF
	 * @return
	 * @throws Exception
	 */
	public String cambiaNodo() throws Exception {
		try {
			NodoTitolario node = (NodoTitolario) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("node");
			return cambiaNodo(node.getText());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * caricamento di un nodo del titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String loadLevel() throws Exception {
		try {
			return cambiaNodo(this.levelToLoad);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * caricamento di un nodo del titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	private String cambiaNodo(String nodo) throws Exception {
		try {
			formsAdapter.cambiaNodo(nodo);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * risalita su titolario di classificazione
	 * @return
	 * @throws Exception
	 */
	public String risaliNodo() throws Exception {
		try {
			String bt = getBtFromRels();
			if (bt != null && bt.length() > 0) {
				formsAdapter.cambiaNodo(bt);
				
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(response);
				this.init(response.getDocument());
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * in caso di modifica di un nodo viene impostato a true il flag di modifica 
	 * del livello corrente
	 * @param vce
	 */
	public void nodeValueChange(ValueChangeEvent vce) throws Exception {  
		levelEdited = true;
    }
	
	/**
	 * aggiunta di un nodo di classificazione al livello corrente
	 * @return
	 * @throws Exception
	 */
	public String addNodo() throws Exception {
		try {
			NodoTitolario nodo = (NodoTitolario) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("node");
			int index = 0;
			if (nodo != null)
				index = nt.indexOf(nodo);
			
			if (nt != null) {
				if (nt.size() > index)
					nt.add(index+1,  new NodoTitolario());
				else
					nt.add(new NodoTitolario());
				
				levelEdited = true;
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * eliminazione di un nodo di classificazione dal livello corrente
	 * @return
	 * @throws Exception
	 */
	public String removeNodo() throws Exception {
		try {
			NodoTitolario nodo = (NodoTitolario) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("node");
			if (nodo != null) {
				nt.remove(nodo);
				if (nt.isEmpty()) 
					nt.add(new NodoTitolario());
				
				levelEdited = true;
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
