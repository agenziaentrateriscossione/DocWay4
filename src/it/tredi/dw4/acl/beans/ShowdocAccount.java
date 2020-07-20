package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.Account;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;

import org.dom4j.Document;

public class ShowdocAccount extends AclShowdoc{
	private AclDocumentFormsAdapter formsAdapter;
	private String xml;
	private Account account = new Account();
	
	public ShowdocAccount() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	@Override
	public void init(Document dom) {
		this.setXml(dom.asXML());
		this.account.init(dom);
	}

	@Override
	public DocumentFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public void reload() throws Exception {
		super._reload("showdoc@account");
	}
	
	//getter / setter
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
