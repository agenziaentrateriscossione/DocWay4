package it.tredi.dw4.docway.beans;

import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayPostitFormsAdapter;
import it.tredi.dw4.docway.model.Postit;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DocWayPostit extends DocEdit {
	private DocWayPostitFormsAdapter formsAdapter;
	private String userInfo;
	private String currDate;
	private Postit postit;
	private boolean visible = false;
	private Showdoc showdoc;
	private boolean abilitaFiles = false;
	private List<XwFile> files = new ArrayList<>();

	// utilizzati per l'aggiunta di nuovi doc informatici tramite iwx o upload classico
	protected String xwFileNamesAttached = "";
	protected String xwFileTitlesAttached = "";
	protected String xwFileIdsAttached = "";

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
		this.postit.setText(XMLUtil.parseElement(dom, "postitText"));
		this.userInfo = XMLUtil.parseAttribute(dom, "response/@userInfo", "");
		this.userInfo = this.userInfo.replaceAll("\\(", "- ").replaceAll("\\)", "");
		this.currDate = XMLUtil.parseAttribute(dom, "response/@currDate", "");
		this.abilitaFiles = Boolean.parseBoolean(XMLUtil.parseAttribute(dom, "response/@abilitaAllegatiAnnotazioni", "false"));
		// Aggiunta slow di inserimento file
		if (this.abilitaFiles)
			this.files.add(new XwFile());
		this.visible = true;
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
		formsAdapter.confirmPostit(postit.getText(), files);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		visible = false;
		if (null != showdoc) {
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

	public List<XwFile> getFiles() {
		return files;
	}

	public void setFiles(List<XwFile> files) {
		this.files = files;
	}

	public String getXwFileNamesAttached() {
		return xwFileNamesAttached;
	}

	public void setXwFileNamesAttached(String xwFileNamesAttached) {
		this.xwFileNamesAttached = xwFileNamesAttached;
	}

	public String getXwFileTitlesAttached() {
		return xwFileTitlesAttached;
	}

	public void setXwFileTitlesAttached(String xwFileTitlesAttached) {
		this.xwFileTitlesAttached = xwFileTitlesAttached;
	}

	public String getXwFileIdsAttached() {
		return xwFileIdsAttached;
	}

	public void setXwFileIdsAttached(String xwFileIdsAttached) {
		this.xwFileIdsAttached = xwFileIdsAttached;
	}

	public boolean isAbilitaFiles() {
		return abilitaFiles;
	}

	public void setAbilitaFiles(boolean abilitaFiles) {
		this.abilitaFiles = abilitaFiles;
	}

	/**
	 * Aggiunta di files al documento (dopo upload e caricamento
	 * su xway)
	 *
	 * @return
	 */
	public String addFiles() {
		if (xwFileIdsAttached != null && xwFileNamesAttached != null) {
			String[] fileIds = StringUtil.split(this.xwFileIdsAttached, "|");
			String[] fileNames = StringUtil.split(this.xwFileNamesAttached, "|");
			String[] fileTitles = StringUtil.split(this.xwFileTitlesAttached, "|");
			if (fileIds != null && fileIds.length > 0
					&& fileNames != null && fileNames.length > 0
					&& fileIds.length == fileNames.length
					&& fileTitles != null && fileTitles.length > 0
					&& fileIds.length == fileTitles.length) {

				// se l'ultima istanza e' vuota deve essere rimossa (solo
				// per scopo grafico di usabilita' per l'utente)
				if (files.get(files.size()-1).getXwayId().equals("") && files.get(files.size()-1).getName().equals(""))
					files.remove(files.size()-1);

				// per ogni file caricato viene istanziato e agganciato l'xwFile
				for (int i=0; i<fileIds.length; i++) {
					String id = fileIds[i];
					String name = fileNames[i];
					String title = fileTitles[i];
					if (id != null && !id.equals("")
							&& name != null && !name.equals("")
							&& title != null && !title.equals("")) {
						XwFile xwFile = new XwFile(id, name, title);

						if (files != null && files.size() == 1 && files.get(0) != null
								&& (files.get(0).getName() == null || files.get(0).getName().equals("")))
							files.remove(0);
						files.add(xwFile);
					}
				}

				files.add(new XwFile()); // aggiunta di una istanza vuota per migliorare usabilita' utente
			}
		}

		// azzeramento dei valori relativi a files caricati
		setXwFileIdsAttached("");
		setXwFileNamesAttached("");
		setXwFileTitlesAttached("");

		return null;
	}


	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile() {
		return deleteXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile(String index) {
		return deleteXwFile(getXwFileByPosition(index));
	}

	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile(XwFile xwFile) {
		if (xwFile != null) {
			files.remove(xwFile);
			if (files.isEmpty())
				files.add(new XwFile());
		}
		return null;
	}

	/**
	 * Eliminazione di tutti gli xwFile di tipo file del doc
	 */
	public String deleteAllXwFile() {
		files.clear();
		files.add(new XwFile());
		return null;
	}

	/**
	 * Aggiunta di un xwFile di tipo file del doc
	 */
	public String addXwFile() {
		XwFile xwFile = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file");
		int index = 0;
		if (xwFile != null)
			index = files.indexOf(xwFile);

		if (files != null) {
			if (files.size() > index)
				files.add(index+1,  new XwFile());
			else
				files.add(new XwFile());
		}
		return null;
	}

	/**
	 * dato un indice ritorna l'xwFile recuperandolo dalla lista
	 * @param index
	 * @return
	 */
	private XwFile getXwFileByPosition(String index) {
		XwFile xwfile = null;
		if (StringUtil.isNumber(index)) {
			int indice = StringUtil.intValue(index);
			if (indice >= 0 && indice < files.size())
				xwfile = files.get(indice);
		}
		return xwfile;
	}

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	public String moveUpXwFile() {
		return moveUpXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));

	}

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	public String moveUpXwFile(String index) {
		return moveUpXwFile(getXwFileByPosition(index));
	}

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	private String moveUpXwFile(XwFile xwFile) {
		if (xwFile != null && files != null) {
			int index = files.indexOf(xwFile);
			if (index > 0 ) {
				files.remove(index);
				files.add(index-1, xwFile);
			}
		}
		return null;
	}

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile() {
		return moveDownXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile(String index) {
		return moveDownXwFile(getXwFileByPosition(index));
	}

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile(XwFile xwFile) {
		if (xwFile != null && files != null) {
			int index = files.indexOf(xwFile);
			if (index < files.size()-1 ) {
				files.remove(index);
				files.add(index+1, xwFile);
			}
		}
		return null;
	}
}
