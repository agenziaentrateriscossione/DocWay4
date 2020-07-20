package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.adapters.DocWayChangeClassifFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.AppUtil;

import org.dom4j.Document;

public class DocWayChangeClassifDoc extends DocWayDocedit {
	private DocWayChangeClassifFormsAdapter formsAdapter;
	private Doc doc;
	private boolean view;
	private Object showdoc;
	
	private boolean cambioSuMinuta = false; // definisce se il cambio di classificazione avviene o meno sulla minuta del documento
	
	private String classifOD = ""; // TODO da non gestire sul cambio di classificazione?
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Doc getDoc() {
		return doc;
	}

	public boolean isCambioSuMinuta() {
		return cambioSuMinuta;
	}

	public void setCambioSuMinuta(boolean minuta) {
		this.cambioSuMinuta = minuta;
	}
	
	public String getClassifOD() {
		return classifOD;
	}

	public void setClassifOD(String classifOD) {
		this.classifOD = classifOD;
	}
	
	public DocWayChangeClassifDoc() throws Exception {
		this.formsAdapter = new DocWayChangeClassifFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
		doc = new Doc();
		doc.init(dom);

    }	
	
	public DocWayChangeClassifFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;			
	}

	@Override
	public String clearDocument() throws Exception {
		return null;
	}
	
	public void setShowdoc(Object showdoc) {
		this.showdoc = showdoc;
	}

	public Object getShowdoc() {
		return showdoc;
	}

	public void setView(boolean viewAddCC) {
		this.view = viewAddCC;
	}

	public boolean isView() {
		return view;
	}
	
	public String closeView() throws Exception{
		view = false;
//		try {
//			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
//			method.invoke(showdoc);
//		} catch (SecurityException e) {
//		  e.fillInStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.fillInStackTrace();
//		}
		setSessionAttribute("changeclassifDoc", null);
		setLoadingbar(null);
		return null;
	}

	/**
	 * Conferma del cambio di classificazione su una selezione di documenti
	 * 
	 * @throws Exception
	 */
	public String confirmChangeclassif() throws Exception{
		formsAdapter.getDefaultForm().addParams(this.doc.getClassif().asFormAdapterParams(".classif"));
		formsAdapter.getDefaultForm().addParams(this.doc.getClassifNV().asFormAdapterParams("classif_nv"));
		
		String operation = "@CAMBIA_CLASSIF_DOC";
		if (isCambioSuMinuta())
			operation = "@CAMBIA_CLASSIF_DOC_MINUTA"; // TODO da completare
		
		this.formsAdapter.newClassifForSel(operation);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		String verbo = response.getAttributeValue("/response/@verbo");
		if (verbo.equals("loadingbar")) {
			DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
			docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
			docWayLoadingbar.init(response);
			setLoadingbar(docWayLoadingbar);
			docWayLoadingbar.setActive(true);
		}
		formsAdapter.fillFormsFromResponse(response); //restore delle form
		view = false;
//		showdoc.reload();
//		setSessionAttribute("changeclassifDoc", null);
		return null;
	}
	
	/**
	 * Thesauro vincolato su titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazione() throws Exception {
		return showThesRel(false);
	}
	
	/**
	 * Gerarchia su titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gerarchiaTitolarioClassificazione() throws Exception {
		return showThesRel(true);
	}
	
	/**
	 * Caricamento del titolario di classificazione
	 * 
	 * @param showGerarchia
	 * @return
	 * @throws Exception
	 */
	private String showThesRel(boolean showGerarchia) throws Exception {
		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;
		
		// Filtro impostato su campo codice
		String value = (getDoc().getClassif() != null && !"".equals(getDoc().getClassif().getFiltroCod())) ? getDoc().getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatClassifCode(value);
			
			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}
		
		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";
		
		callShowThesRel(getDoc(), I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value);
		
		// Azzero il campo nel form
		getDoc().getClassif().setFiltroCod("");
		
		return null;
	}
	
}
