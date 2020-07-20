package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;

public class AclQueryFormsAdapter extends QueryFormsAdapter {

	public AclQueryFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
//		Element root = response.getRootElement();
//defaultForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
	}
	
	@Override
	protected void fillHierBrowserFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillHierBrowserFormFromResponse(response);
		
//		Element root = response.getRootElement();
//hierBrowserForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
	}
	
	/**
     * caricamento della pagina di showdoc di un organo 
     * */
    public void mostraDocumentoQ(String posizione, String sel, String db, String customTuple, String hxpFormDbValue){
    	if(db.equals("acl") || db.indexOf("acl-")== 0){
    		String uri = defaultForm.getParam("uri").replaceAll("application", "base");
    		String tipo = "";
    		
    		if(uri.indexOf("xdocwayaooadm") != -1){
    			tipo = "xdocwayaooadm";
    		}else if(uri.indexOf("xdocwayadm") != -1){
    			tipo = "xdocwayadm";
    		}else {
    			tipo = "xdocway";
    		}
    		
    		uri = uri.replaceAll("xdocwayaooadm", "acl");
    		uri = uri.replaceAll("xdocwayadm", "acl");
    		uri = uri.replaceAll("xdocway", "acl");
    		
    		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("bckOrgUrl", hxpFormDbValue + "|" + tipo, customTuple));
    		
    		//defaultForm.addParam("uri", uri);
    		defaultForm.addParam("verbo", "showdoc");
    		defaultForm.addParam("pos", posizione);
    		defaultForm.addParam("selid", sel);
    		defaultForm.addParam("db", db);
    		defaultForm.addParam("userRights", "");
    	//	defaultForm.addParam("selTable","@to_adm_tools");
    	//	defaultForm.addParam("dbTable","@to_adm_tools");
    		defaultForm.addParam("userRights", "");
    	}else{
    		defaultForm.addParam("verbo", "showdoc");
    		defaultForm.addParam("pos", posizione);
    		defaultForm.addParam("selid", sel);
    		defaultForm.addParam("db", db);
    	}
    }

	public void inserisciDocInACL(String table, String customTuple, String hxpFormDbValue, String hxpFormAclDbValue) {
		String uri = defaultForm.getParam("uri").replaceAll("application", "base");
		String tipo = "";
		
		if(uri.indexOf("xdocwayaooadm") != -1){
			tipo = "xdocwayaooadm";
		}else if(uri.indexOf("xdocwayadm") != -1){
			tipo = "xdocwayadm";
		}else {
			tipo = "xdocway";
		}
		
		uri = uri.replaceAll("xdocwayaooadm", "acl");
		uri = uri.replaceAll("xdocwayadm", "acl");
		uri = uri.replaceAll("xdocway", "acl");
		
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("bckOrgUrl", hxpFormDbValue + "|" + tipo, customTuple));
		
		defaultForm.addParam("uri", uri);
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("pos", "");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", "");
		defaultForm.addParam("db", hxpFormAclDbValue);
		defaultForm.addParam("dbTable", table);
		defaultForm.addParam("userRights", "");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'toDo'.
	 * @return Valore impostato sull'attributo toDo
	 */
	public String getToDo() {
		return defaultForm.getParam("toDo");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'db'.
	 * @return Valore impostato sull'attributo db
	 */
	public String getDb() {
		return defaultForm.getParam("db");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'aclDb'.
	 * @return Valore impostato sull'attributo aclDb
	 */
	public String getAclDb() {
		return defaultForm.getParam("aclDb");
	}
	
	/**
	 * Ritorna il parametro _cd del formsAdapter (necessario all'upload di files in presenza 
	 * di multisocieta') 
	 * @return
	 */
	public String getCustomTupleName() {
		String _cd = defaultForm.getParam("_cd");
		if (_cd == null)
			_cd = "";
		
		return _cd;
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'attach'.
	 * @return Valore impostato sull'attributo attach
	 */
	public String getAttach() {
		return defaultForm.getParam("attach");
	}
}
