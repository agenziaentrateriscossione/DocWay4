package it.tredi.dw4.docway.beans.dexia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayAbrogazioneFormsAdapter;
import it.tredi.dw4.docway.model.DocAbrogato;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocWayAbrogazioneDoc extends DocEdit {
	private DocWayAbrogazioneFormsAdapter formsAdapter;
	private boolean visible = false;
	private DocAbrogato docAbrogato = new DocAbrogato();
	private String text;
	private String userInfo;
	private String currDate;
	private Showdoc showdoc;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocWayAbrogazioneDoc() throws Exception {
		this.formsAdapter = new DocWayAbrogazioneFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
    	visible = true;
    	this.userInfo = XMLUtil.parseAttribute(dom, "response/@userInfo", "");
    	this.userInfo = this.userInfo.replaceAll("\\(", "- ").replaceAll("\\)", "");
    	this.currDate = XMLUtil.parseAttribute(dom, "response/@currDate", "");
    	setDocAbrogato(new DocAbrogato());
    	getDocAbrogato().init(dom);
    }	
	
	public DocWayAbrogazioneFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;			
	}

	@Override
	public String clearDocument() throws Exception {
		visible = false;
		setSessionAttribute("docwayAbrogazioneDoc", null);
		return null;
	}
	
	public String confirmAbrogazione() throws Exception{
		if (text == null || text.trim().length() == 0) {
			this.setErrorMessage("templateForm:abrogazione_text", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.estremi") + "'");
			return null;
		}
		
		formsAdapter.confirmAbrogazioneDoc_response(text);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		visible = false;
		if (null != showdoc) showdoc.reload();
		return null;

	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}

	public Showdoc getShowdoc() {
		return showdoc;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	public String getCurrDate() {
		return currDate;
	}

	public DocAbrogato getDocAbrogato() {
		return docAbrogato;
	}

	public void setDocAbrogato(DocAbrogato docAbrogato) {
		this.docAbrogato = docAbrogato;
	}
	
}
