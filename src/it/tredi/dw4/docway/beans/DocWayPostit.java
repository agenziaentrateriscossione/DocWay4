package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayPostitFormsAdapter;
import it.tredi.dw4.docway.model.Postit;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocWayPostit extends DocEdit {
	private DocWayPostitFormsAdapter formsAdapter;
	private String userInfo;
	private String currDate;
	private Postit postit;
	private boolean visible = false;
	private Showdoc showdoc;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocWayPostit() throws Exception {
		this.formsAdapter = new DocWayPostitFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
    	this.postit = new Postit();
    	postit.setText(XMLUtil.parseElement(dom, "postitText"));
    	this.userInfo = XMLUtil.parseAttribute(dom, "response/@userInfo", "");
    	this.userInfo = this.userInfo.replaceAll("\\(", "- ").replaceAll("\\)", "");
    	this.currDate = XMLUtil.parseAttribute(dom, "response/@currDate", "");
    	visible = true;
    }	
	
	public DocWayPostitFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setPostit(Postit postit) {
		this.postit = postit;
	}

	public Postit getPostit() {
		return postit;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;			
	}

	/**
	 * Annullamento della creazione del postit sul documento
	 */
	@Override
	public String clearDocument() throws Exception {
		visible = false;
		setSessionAttribute("postit", null);
		return null;
	}
	
	/**
	 * Conferma di salvataggio del postit sul documento
	 * @return
	 * @throws Exception
	 */
	public String confirmPostit() throws Exception{
		if (postit.getText().trim().length() == 0) {
			this.setErrorMessage("templateForm:annotazione_text", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.annotazione") + "'");
			return null;
		}
		formsAdapter.confirmPostit(postit.getText());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		visible = false;
		String toDo = response.getAttributeValue("/response/@toDo", "");
		if (null != showdoc) {
			if (toDo.equals("%doRigetta%"))
				((ShowdocArrivo)showdoc).rigettaDoc();
			else 
				//showdoc.reload();
				showdoc._reloadWithoutNavigationRule();
		}
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

	public void setCurrDate(String data) {
		this.currDate = data;
	}

	public String getCurrDate() {
		return currDate;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getUserInfo() {
		return userInfo;
	}
	
}
