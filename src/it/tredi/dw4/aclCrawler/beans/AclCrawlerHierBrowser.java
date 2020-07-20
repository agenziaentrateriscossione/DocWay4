package it.tredi.dw4.aclCrawler.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclHierBrowser;

public class AclCrawlerHierBrowser extends AclHierBrowser {

	public AclCrawlerHierBrowser() throws Exception {
		super();
	}

	@Override
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, "it.tredi.dw4.acl.beans", personalPackage, suffix, response, pageType, popup);
	}
}
