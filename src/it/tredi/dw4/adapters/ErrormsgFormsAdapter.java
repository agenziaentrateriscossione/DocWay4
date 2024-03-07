package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ErrormsgFormsAdapter extends FormsAdapter {
	public static final String FATAL = "fatal";
	public static final String ERROR = "error";
	public static final String WARNING = "warning";
	public static final String INFO = "info";
	
	protected FormAdapter defaultForm;
	
	public ErrormsgFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("query", root.attributeValue("query", ""));
		defaultForm.addParam("selid", root.attributeValue("selid", ""));
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	public static boolean isResponseErrorMessage(XMLDocumento response) {
		return response.testAttributeValue("/response/@verbo", "errormsg");		
	}
	
	private static XMLDocumento buildErrorResponse(String message, String detail, String level, String httpStatusCode, boolean unexpected) throws Exception {
		Element responseEl = DocumentHelper.createElement("response");
		XMLDocumento document = new XMLDocumento(responseEl);
		responseEl.addAttribute("verbo", "errormsg");
		Element erroreEl = DocumentHelper.createElement("errore");
		responseEl.add(erroreEl);
		erroreEl.addAttribute("unexpected", Boolean.toString(unexpected));
		erroreEl.addAttribute("level", level);
		if (httpStatusCode == null)
			httpStatusCode = "";
		erroreEl.addAttribute("httpStatusCode", httpStatusCode);
		Element errtypeEl = DocumentHelper.createElement("errtype");
		erroreEl.add(errtypeEl);
		errtypeEl.setText(message);
		
		if (detail != null && detail.length() > 0) {
			Element detailEl = DocumentHelper.createElement("extra");
			erroreEl.add(detailEl);
			detailEl.setText(detail);
		}
		
		if (unexpected) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			String emailDetail = "|^||^|ERROR TIMESTAMP: " + df.format(new Date()) + "|^||^||^|ERROR DETAILS:|^||^|" + detail.replaceAll("\r\n", "\n").replaceAll("\n", "|^|");
   			Element emailDetailEl = DocumentHelper.createElement("emailDetail");
			erroreEl.add(emailDetailEl);
			emailDetailEl.setText(emailDetail);    			
		}
		
		return document;
	}
	
	public static XMLDocumento buildErrorResponse(String message, String detail, String level) throws Exception {
		return buildErrorResponse(message, detail, level, null);
	}
	
	public static XMLDocumento buildErrorResponse(String message, String detail, String level, String httpStatusCode) throws Exception {
		return buildErrorResponse(message, detail, level, httpStatusCode, false);
	}
	
	public static XMLDocumento buildErrorResponse(Throwable t) throws Exception {
		return buildErrorResponse(t, ErrormsgFormsAdapter.FATAL, true);
	}
	
	public static XMLDocumento buildErrorResponse(Throwable t, String level, boolean unexpected) throws Exception {
		String message = t.getMessage() == null? "" : t.getMessage();
		
		Logger.error(message, t);
		
   	 	java.io.ByteArrayOutputStream detailedOut = new java.io.ByteArrayOutputStream();
   	 	t.printStackTrace(new java.io.PrintStream(detailedOut));
   	 	
   	 	if (level == null || level.isEmpty())
   	 		level = ErrormsgFormsAdapter.FATAL;
   	 	return buildErrorResponse(message, detailedOut.toString(), level, null, unexpected);
	}
	
	
	/* ACTIONS - DEFAULTFORM */
	
	public void sendErrorEmail(String emailDetail) {
		this.defaultForm.addParam("verbo", "errormsg");
		this.defaultForm.addParam("xverb", "@sendErrorEmail");
		this.defaultForm.addParam("emailDetail", emailDetail);
		this.defaultForm.addParam("_cd", "_%26");
		this.defaultForm.addParam("ssInMS=", "_%26");
	}	
	
}
