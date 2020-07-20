package it.tredi.dw4.soginSAP.beans;

import java.util.Properties;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocDoc;
import it.tredi.dw4.soginSAP.model.SAPDoc;
import it.tredi.dw4.soginSAP.ws.stubs.ServiceLocator;
import it.tredi.dw4.soginSAP.ws.stubs.ZSOGIN_DOC;
import it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_PortType;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLDocumento;

public abstract class SoginSAPShowdocDoc extends ShowdocDoc {
	
	public SoginSAPShowdocDoc() throws Exception {
		super();
	}

	// TODO mbernardini 29/04/2015 : non credo fosse necessaria, visto che e' definita allo stesso modo nella classe estesa
	//protected DocDocWayDocumentFormsAdapter formsAdapter;
	
	@Override
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		if (suffix == null || suffix.trim().equals(""))
			suffix = ".soginSAP";
		
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, "it.tredi.dw4.soginSAP.beans", personalPackage, suffix, response, pageType, popup);
	}
	
	@Override
	public String paginaTitoli() throws Exception {
		XMLDocumento response = this._paginaTitoli();	
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		SoginSAPTitles titles = (SoginSAPTitles)getSessionAttribute("soginSAPTitles");		
		
		titles.getFormsAdapter().fillFormsFromResponse(response);
		titles.init(response.getDocument());
		
		return "soginSAP@showtitles";
	}
	
	public boolean isSapTitlesExists() {
		if (getSessionAttribute("soginSAPTitles") != null)
			return true;
		else
			return false;
	}
	
	public String asssignToSAP() throws Exception {
		SoginSAPConnector soginSAPConnector = (SoginSAPConnector) getSessionAttribute("soginSAPConnector");
		if (soginSAPConnector != null) {
			
			Properties soginSAPProperties = (Properties) getSessionAttribute("soginSAPProperties");
			if (soginSAPProperties == null) {
				// mbernardini 25/11/2015 : caricamento delle properties di soginSAP tramite configuratore
				soginSAPProperties = DocWayProperties.getProperties(DocWayProperties.SOGINSAP_NAMESPACE);
				setSessionAttribute("soginSAPProperties", soginSAPProperties);
			}
			
			try {
				ServiceLocator loc = new ServiceLocator();
				loc.setZWS_SOGIN_UPDATE_TABEndpointAddress(soginSAPProperties.getProperty("SAPEndpoint"));
				ZWS_SOGIN_UPDATE_TAB_PortType zws = loc.getZWS_SOGIN_UPDATE_TAB();
				
				// to use Basic HTTP Authentication:
				((Stub) zws)._setProperty(Call.USERNAME_PROPERTY, soginSAPProperties.getProperty("SAPUser"));
				((Stub) zws)._setProperty(Call.PASSWORD_PROPERTY, soginSAPProperties.getProperty("SAPPassword"));
				
				String idDoc = ((SAPDoc) this.getDoc()).getIdDoc();
				
				ZSOGIN_DOC zsoginDoc = new ZSOGIN_DOC();
				zsoginDoc.setIDDOC(idDoc);
				zsoginDoc.setKEYSAP(soginSAPConnector.getKeySap());
				zsoginDoc.setDOCSAP("");
				zsoginDoc.setMANDT("010");
				zsoginDoc.setBUKRS("SOGI");
				zsoginDoc.setBELNR("0000000000");
				zsoginDoc.setGJAHR("2000");
				zsoginDoc.setAWTYP("01");
				zsoginDoc.setZZST_IDSOG("id doc");
				zsoginDoc.setTCODE("0000");
				zsoginDoc.setUNAME("000000000000");
				zsoginDoc.setSTBLG("0000000000");
				zsoginDoc.setSTJAH("0000");
				zsoginDoc.setZLOCK("X");
				
				String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
				if (!result.trim().startsWith("01"))
					throw new Exception(result);
			}
			catch (Exception e) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			
			soginSAPConnector.setKeySap(null);
		}
		
		return null;
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