package it.tredi.dw4.docway.beans;

import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Showthes;
import it.tredi.dw4.docway.adapters.DocWayShowthesFormsAdapter;
import it.tredi.dw4.docway.model.Thes;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocWayShowthes extends Showthes {
	private DocWayShowthesFormsAdapter formsAdapter;
	
	// Identifica se si sta visualizzando il titolario di classificazione
	// in modalita' vista gerarchica
	private boolean vista_gerarchica = false;
	
	private Thes thes_node = null;
	
	private String cPath = "";
	private String startkey = "";
	
	private String thesNome = "";
	private String codBreadcrumbs = "";
	
	private String classifPrefix = ""; // Prefisso da anteporre alla classificazione in ritorno (caso minuta)
	
	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isVista_gerarchica() {
		return vista_gerarchica;
	}

	public void setVista_gerarchica(boolean vista_gerarchica) {
		this.vista_gerarchica = vista_gerarchica;
	}

	public String getcPath() {
		return cPath;
	}

	public void setcPath(String cPath) {
		this.cPath = cPath;
	}

	public String getStartkey() {
		return startkey;
	}

	public void setStartkey(String startKey) {
		this.startkey = startKey;
	}

	public Thes getThes_node() {
		return thes_node;
	}

	public void setThes_node(Thes node) {
		this.thes_node = node;
	}
	
	public String getClassifPrefix() {
		return classifPrefix;
	}

	public void setClassifPrefix(String classifPrefix) {
		this.classifPrefix = classifPrefix;
	}

	public DocWayShowthes() throws Exception {
		this.formsAdapter = new DocWayShowthesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		this.xml = dom.asXML();
		this.vista_gerarchica = new Boolean(XMLUtil.parseAttribute(dom, "response/thes/@vista_gerarchica", "false")).booleanValue();
		this.startkey = XMLUtil.parseAttribute(dom, "@startkey");
		this.cPath = XMLUtil.parseAttribute(dom, "@cPath");
		
		Element el = (Element) dom.selectSingleNode("response/thes");
		if (el != null) {
			int thPathLength = 0;
			List<?> thPath = dom.selectNodes("response/thPath/thnode");
			if (thPath != null)
				thPathLength = thPath.size();
			this.thes_node = new Thes();
			this.thes_node.setThPathLength((thPathLength > 0) ? thPathLength-1 : thPathLength);
			this.thes_node.init(DocumentHelper.createDocument(el.createCopy()));
		}
		else {
			this.thes_node = null;
		}
		//this.thes_node = (Thes) XMLUtil.parseElement(dom, "response/thes", new Thes());
	}
	
	@Override
	public DocWayShowthesFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	/**
	 * Lettura dell'attributo thesauro passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListener(ActionEvent event){
		this.thesNome = (String) event.getComponent().getAttributes().get("thesNome");
		this.codBreadcrumbs = (String) event.getComponent().getAttributes().get("codBreadcrumbs");
	}
	  
	/**
	 * Assegnazione della classificazione da navigazione della gerarchia di 
	 * classificazione
	 *   
	 * @return
	 * @throws Exception
	 */
	public String assignClassifAndClose() throws Exception {
		if (this.thesNome != null && this.thesNome.length() > 0) {
			String classifCod = "";
			if (this.codBreadcrumbs != null && this.codBreadcrumbs.length() > 0)
				classifCod = this.codBreadcrumbs;
			
			String classif = classifCod + " - " + this.thesNome;
			String classifRo = ClassifUtil.formatClassif(classif, getClassifFormat());
			
			// Assegno i valori all'oggetto Classif
			HashMap<String, String> campiThes = new HashMap<String, String>();
			campiThes.put(classifPrefix+".classif.@cod", classifCod);
			campiThes.put(classifPrefix+".classif.text", classif);
			campiThes.put(classifPrefix+".classif.text_ro", classifRo);
			campiThes.put(classifPrefix+".voce_indice.text", ""); // Azzero la voce di indice
			confirm(campiThes);
		}
		this.close();
		return null;
	}
	
	/**
	 * Navigazione del titolario di classificazione (caricamento del padre del nodo corrente)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showthesRelBack() throws Exception {
		if (this.thes_node != null)
			return showthesRel(this.thes_node, true); 
		return null;
	}
	
	/**
	 * Navigazione del titolario di classificazione (apertura dei figli del nodo corrente)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showthesRel() throws Exception {
		Thes thes = (Thes) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("thes");
		if (thes != null)
			return showthesRel(thes, false); 
		return null;
	}
	
	/**
	 * Navigazione del titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showthesRel(Thes thes, boolean back) throws Exception {
		try {
			// TODO Andrebbe recuperato come parametro
			String keypath = "classif"; // Canale del thes
			
			if (thes == null || thes.getChiave() == null || thes.getChiave().length() == 0)
				startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;
			else
				startkey = thes.getChiave();
			
			cPath = getFormsAdapter().getDefaultForm().getParam("cPath");
			if (back) {
				cPath = cPath.substring(0, cPath.lastIndexOf("|"));
				cPath = cPath.substring(0, cPath.lastIndexOf("|"));
			}
			
			getFormsAdapter().showThesRel(keypath, startkey, cPath);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			getFormsAdapter().fillFormsFromResponse(response);		
			this.init(response.getDocument());
			
			this.selectThes();
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public void selectThes() throws Exception {
		// Verifico se sono ancora in navigazione del thesauro di classificazione
		// oppure se devo assegnare un valore al campo relativo al thesauro
		if (this.getThes_node() == null) {
			// Ho terminato la navigazione e devo assegnare il valore di classificazione
			// al form sottostante
			
			if (this.cPath != null && this.cPath.length() > 0) {
				this.cPath = StringUtil.replace(this.cPath, "|" + Const.TITOLARIO_CLASSIF_NODO_RADICE + "|", "");
			
				String classifRo = "";
				String classif = "";
				String classifCod = "";
				
				String[] selezione = StringUtil.split(this.cPath, "|");
				if (selezione != null && selezione.length > 0) {
					for (int i=0; i<selezione.length; i++) {
						Thes thes = new Thes();
						thes.initThesByChiave(selezione[i], (i==0) ? true : false);
						
						classifRo = classifRo + thes.getCodice();
						classifCod = classifCod + thes.getIndice();
						if (i != selezione.length-1) {
							classifRo = classifRo + "/";
							classifCod = classifCod + "/";
						}
						
						if (i == selezione.length-1) {
							classifRo = classifRo + " - " + thes.getTitolo();
							classif = classifCod + " - " + thes.getTitolo();
						}
						
						// Assegno i valori all'oggetto Classif
						HashMap<String, String> campiThes = new HashMap<String, String>();
						campiThes.put(classifPrefix+".classif.@cod", classifCod);
						campiThes.put(classifPrefix+".classif.text", classif);
						campiThes.put(classifPrefix+".classif.text_ro", classifRo);
						campiThes.put(classifPrefix+".voce_indice.text", ""); // Azzero la voce di indice
						confirm(campiThes);
					}
				}
				
				
			}
			
			this.close();
		}
	}
	
}
