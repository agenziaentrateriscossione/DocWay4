package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.adapters.DocWayChangeClassifFormsAdapter;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public class DocWayChangeClassif extends DocWayDocedit {
	private DocWayChangeClassifFormsAdapter formsAdapter;
	private Fascicolo fascicolo;
	private boolean view;
	private Object showdoc;
	
	private String classifOD = ""; // TODO da non gestire sul cambio di classificazione?
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setFascicolo(Fascicolo doc) {
		this.fascicolo = doc;
	}

	public Fascicolo getFascicolo() {
		return fascicolo;
	}
	
	public String getClassifOD() {
		return classifOD;
	}

	public void setClassifOD(String classifOD) {
		this.classifOD = classifOD;
	}

	public DocWayChangeClassif() throws Exception {
		this.formsAdapter = new DocWayChangeClassifFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
		fascicolo = new Fascicolo();
		fascicolo.init(dom);

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
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("changeclassif", null);
		return null;
	}
	
	/**
	 * Conferma del cambio di classificazione su una selezione di fascicoli
	 * 
	 * @throws Exception
	 */
	public String confirmChangeclassif() throws Exception{
		if ((this.fascicolo.getClassif().getCod() == null || this.fascicolo.getClassif().getCod().isEmpty()) 
				&& (this.fascicolo.getClassifNV().getCod() == null || this.fascicolo.getClassifNV().getCod().isEmpty())) {
			
			setErroreResponse(I18N.mrs("dw4.classificazione_non_selezionata") + ".<br/>" + I18N.mrs("dw4.probabilmente_non_e_stato_completato_il_lookup_sul_campo_relativo_alla_nuova_classif"), Const.MSG_LEVEL_WARNING);
			return null;
		}
		else {
			formsAdapter.getDefaultForm().addParams(this.fascicolo.getClassif().asFormAdapterParams(".classif"));
			formsAdapter.getDefaultForm().addParams(this.fascicolo.getClassifNV().asFormAdapterParams("classif_nv"));
			
			this.formsAdapter.newClassifForSel("@CAMBIA_CLASSIF_FASC");
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
	//		setSessionAttribute("changeclassif", null);
			return null;
		}
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
		String value = (fascicolo.getClassif() != null && !"".equals(fascicolo.getClassif().getFiltroCod())) ? fascicolo.getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatNumberClassifCode(value);
			
			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}
		
		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";
		
		callShowThesRel(fascicolo, I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value);
		
		// Azzero il campo nel form
		this.fascicolo.getClassif().setFiltroCod("");
		
		return null;
	}
	
	@Override
	public XmlEntity getModel() {
		return null;
	}
	
}
