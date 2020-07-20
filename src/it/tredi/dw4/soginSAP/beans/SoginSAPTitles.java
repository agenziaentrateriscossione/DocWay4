package it.tredi.dw4.soginSAP.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.beans.DocWayTitles;
import it.tredi.dw4.docway.doc.adapters.DocDocWayTitlesFormsAdapter;
import it.tredi.dw4.utils.Const;

public class SoginSAPTitles extends DocWayTitles {
	
	public SoginSAPTitles() throws Exception {
		this.formsAdapter = new DocDocWayTitlesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, "it.tredi.dw4.docway.beans", personalPackage, suffix, response, pageType, popup);
	}

	public String getPageTemplate() {
		if (this.isPopupPage())
			return Const.DOCWAY_CONTEXT_NAME + Const.TEMPLATE_POPUP_FILENAME;
		else if (!this.isShowSxCol())
			return Const.DOCWAY_CONTEXT_NAME + Const.TEMPLATE_NOSX_FILENAME;
		else
			return Const.DOCWAY_CONTEXT_NAME + Const.TEMPLATE_DEFAULT_FILENAME;
	}
}
