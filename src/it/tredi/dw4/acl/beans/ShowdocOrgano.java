package it.tredi.dw4.acl.beans;

import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.Modello;
import it.tredi.dw4.acl.model.Organo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;

import org.dom4j.Document;

public class ShowdocOrgano extends AclShowdoc {

	private AclDocumentFormsAdapter formsAdapter;
	private String xml;
	private Organo organo;
	private boolean almenoUnModello = false;
	
	private String categorieLabel = "";
	private String customTuple = "";
	
	public ShowdocOrgano() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@Override
	public void init(Document dom) {
		setXml(dom.asXML());
		setOrgano(new Organo());
		getOrgano().init(dom);
		
		String dicitCategoria 		=	FormsAdapter.getParameterFromCustomTupleValue("dicitCategoria", formsAdapter.getDefaultForm().getParam("_cd")); 
		if(!dicitCategoria.equals(""))
			this.setCategorieLabel(dicitCategoria.substring(dicitCategoria.indexOf("|") +1 ));
		
		// inizializzazione di componenti common
		initCommons(dom);
	}

	@Override
	public AclDocumentFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@organo");
	}
	
	/**
	 * leggermente modificato rispetto al metodo downloadModello in ShowdocSeduta
	 * */
	public String downloadFile(String name, String title) throws Exception{
		try{
			String id = StringUtil.substringAfter(name, ".");
			
			formsAdapter.getFilePost(name, title, "acl");
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				if(!title.endsWith("."+id))
					title+="."+id;
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(id));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + title + "\"");
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	public String inserisciSedutaSospesa() throws Exception{
		try{
			formsAdapter.inserisciSedutaSospesa(this.organo.getFullcod(), this.organo.getNome());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			String warningMessage = response.getAttributeValue("/response/@warnings", "");
			if(!warningMessage.isEmpty())
				showMessageWarning(warningMessage); //mi allineo con la prassi di docway 4 e mostro il messaggio del service
//				showMessageWarningInsSedSospesa();
			else
				showMessageWarningInsSedSospesaOk();
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			reload();
			return null;
		}catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
//	/**
//	 * apertura di un popup di messaggio in caso di warning ricevuto
//	 *  
//	 * @return
//	 * @throws Exception
//	 */
//	private String showMessageWarning(String warningMessage) throws Exception {
//		Msg message = new Msg();
//		message.setActive(true);
//		message.setTitle(I18N.mrs("dw4.info"));
//		message.setType(Const.MSG_LEVEL_INFO);
//		message.setMessage(warningMessage);
//		
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		session.setAttribute("msg", message);
//		
//		return null;
//	}
	
	/**
	 * apertura di un popup di messaggio in caso di warning ricevuto in seguito a
	 * inserimento di una seduta sospesa
	 * @return
	 * @throws Exception
	 */
	private String showMessageWarningInsSedSospesa() throws Exception {
		showMessageWarning(I18N.mrs("dw4.warning_inssedsospesa"));
		return null;
	}
	
	/**
	 * apertura di un popup di messaggio in caso di warning vuoto ricevuto in seguito a
	 * inserimento di una seduta sospesa
	 * @return
	 * @throws Exception
	 */
	private String showMessageWarningInsSedSospesaOk() throws Exception {
		showMessageWarning(I18N.mrs("dw4.warning_inssedsospesa_ok"));
		return null;
	}
	
	public boolean checkAlmenoUnModello(List<Modello> modelli){
		for(Modello modello : modelli)
			if(modello.getFile() != null && !modello.getFile().getTitle().isEmpty())
				return true;
		return false;
	}
	
	/*
	 * getter / setter
	 * */
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Organo getOrgano() {
		return organo;
	}

	public void setOrgano(Organo organo) {
		this.organo = organo;
	}

	public String getCategorieLabel() {
		return categorieLabel;
	}

	public void setCategorieLabel(String categorieLabel) {
		this.categorieLabel = categorieLabel;
	}

	public String getCustomTuple() {
		return customTuple;
	}

	public void setCustomTuple(String customTuple) {
		this.customTuple = customTuple;
	}

	public boolean isAlmenoUnModello() {
		return checkAlmenoUnModello(organo.getModelli());
	}

	public void setAlmenoUnModello(boolean almenoUnModello) {
		this.almenoUnModello = almenoUnModello;
	}
	
//	public void reload(ComponentSystemEvent event) throws Exception {
//		reload();
//	}
}


