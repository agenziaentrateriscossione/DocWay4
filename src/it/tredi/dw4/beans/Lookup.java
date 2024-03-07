package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclTitles;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LookupFormsAdapter;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.LookupUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public abstract class Lookup extends Page {

	protected boolean active = false;

	protected Object model;

	protected List<Titolo> titoli;

	protected String physDoc = "";

	protected String xml = "";

	private String lookupType = ""; // Tipologia di lookup

	private String lookupFieldVal = "";//valore da ricercare

	private String lookupXq = ""; // extra query utilizzata per la ricerca sul lookup

	public abstract void init(Document dom);

	public String getLookupFieldVal() {
		return lookupFieldVal;
	}

	public void setLookupFieldVal(String fieldValue) {
		this.lookupFieldVal = fieldValue;
	}

	public String getLookupXq() {
		return lookupXq;
	}

	public void setLookupXq(String lookupXq) {
		this.lookupXq = lookupXq;
	}

	public abstract LookupFormsAdapter getFormsAdapter();

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public void setTitles(List<Titolo> titoli) {
		this.titoli = titoli;
	}

	public List<Titolo> getTitles() {
		return this.titoli;
	}

	public String getPhysDoc() {
		if (physDoc == null)
			return "";
		else
			return this.physDoc;
	}

	public void setPhysDoc(String physDoc) {
		if (physDoc == null)
			physDoc = "";
		this.physDoc = physDoc;
	}

	public String getXml() {
		if (xml == null)
			return "";
		else
			return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String close() {
		this.active = false;
		return null;
	}

	public String getLookupType() {
		return lookupType;
	}

	public void setLookupType(String lookupType) {
		this.lookupType = lookupType;
	}

	/**
	 * Dato il dom dei titoli di lookup restituisce la tipologia di lookup
	 *
	 * @param domTitoli
	 * @return
	 */
	public void setLookupType(Document domTitoli) {
		this.lookupType = "standard";
		if (domTitoli != null) {
			if ((XMLUtil.countElements(domTitoli, "//titolo[@type='ATTIV']") > 0)
					|| (XMLUtil.countElements(domTitoli, "//titolo[@type='PERS']") > 0)) {
				this.lookupType = "mittente";
			}
			else if (XMLUtil.countElements(domTitoli, "//campo[@nome='tit_rpa_cc']") > 0) {
				this.lookupType = "voceIndice";
			}
			else if (XMLUtil.countElements(domTitoli, "//titolo[campo/@nome='dx']") > 0) {
				this.lookupType = "firmatario";
			}
		}
	}

	public String primaPagina() throws Exception {
		if (getFormsAdapter().primaPagina()) {

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String ultimaPagina() throws Exception {
		if (getFormsAdapter().ultimaPagina()) {

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean);
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String getFirstPosition(){
		return String.valueOf(getFormsAdapter().getFirstPosition());
	}

	public String getLastPosition(){
		return String.valueOf(getFormsAdapter().getLastPosition());
	}

	public int getCount(){
		return getFormsAdapter().getDefaultForm().getParamAsInt("count");
	}

	/**
	 * Caricamento di una specifica pagina della lista titoli
	 * @param pageNumber
	 * @return
	 * @throws Exception
	 */
	public String paginaSpecifica(int pageNumber) throws Exception {
		try {
			if (pageNumber <= 0)
				pageNumber = 1;
			else if (pageNumber > getFormsAdapter().getTotal())
				pageNumber = getFormsAdapter().getTotal();

			getFormsAdapter().paginaSpecifica(pageNumber);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());

			return null;
		}
		catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Selezione di un titolo di lookup e riempimento delle proprieta'
	 * del bean
	 *
	 * @return
	 * @throws Exception
	 */
	public String confirm() throws Exception{
		Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		return confirm(titolo);
	}

	/**
	 * Ripulisce i campi di lookup passati come argomento
	 * @param campi
	 * @throws Exception
	 */
	public void cleanFields(String campi) throws Exception {
		List<Campo> campiL = new ArrayList<Campo>();
		String []campiArr = campi.split(" ; ");
		Titolo titolo = new Titolo();
		for (int i = 0; i < campiArr.length; i++) {
			String nomeCampo = campiArr[i].trim();
			nomeCampo = nomeCampo.substring(0, nomeCampo.indexOf("="));
			Campo campo = new Campo();
			campo.setNome(nomeCampo);
			if (!nomeCampo.equals(".oggetto")) // il campo oggetto (voce di indice) non deve essere svuotato
				campo.setText("");
			campiL.add(campo);
		}
		titolo.setCampi(campiL);
		fillFields(titolo.getCampi(), false);
	}

	/**
	 * Selezione di un titolo di lookup e riempimento delle proprieta'
	 * del bean
	 *
	 * @param titolo
	 * @return
	 * @throws Exception
	 */
	public String confirm(Titolo titolo) throws Exception{
		fillFields(titolo.getCampi(), true);
		return close();
	}

	/**
	 * Riempimento delle proprieta' di un bean in base ai campi di un titolo
	 * di lookup
	 *
	 * @param campiL campi del titolo di lookup da assegnare al bean
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void fillFields(List<Campo> campiL, boolean addRow) throws Exception {
		String instanceName = "";

		if (campiL != null && campiL.size() > 0) {
			for (int campiLIndex = 0; campiLIndex < campiL.size(); campiLIndex++) {
				try {
					String value = campiL.get(campiLIndex).getText();
					String xpath = campiL.get(campiLIndex).getNome();

					boolean fill = true;
					if (xpath.equals(".oggetto")
							&& (value == null || value.equals(""))) // il campo oggetto (voce di indice) non deve essere sovrascritto se non valorizzato
						fill = false;

					if (fill) {

						String []splitL = xpath.split("\\.");
						Object obj = model;
						for (int splitLindex = 0; splitLindex < splitL.length - 1; splitLindex++) {
							String propertyName = splitL[splitLindex];
							String index = "";
							if (propertyName.startsWith("@"))
								propertyName = propertyName.substring(1);
							if (propertyName.endsWith("]")){
								index = propertyName.substring(propertyName.indexOf("[")+1, propertyName.length()-1);
								propertyName = propertyName.substring(0, propertyName.indexOf("["));
							}
							if (propertyName.length() > 0) {
								if (addRow && instanceName.equals("")) instanceName = propertyName; // nome dell'istanza (caso di lookup su multi riga)

								Method getter = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
								obj = getter.invoke(obj);

								if (!"".equals(index)){
									obj = ((ArrayList)obj).get(Integer.valueOf(index));
								}
							}
						}
						String propertyName = splitL[splitL.length - 1];
						if (propertyName.startsWith("@"))
							propertyName = propertyName.substring(1);

						// TODO Verificare se sportare all'interno di uno specifico package (docway, acl, ecc.)
						value = customFillFieldsLookup(xpath, value);

						Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();
						obj = setter.invoke(obj, value);

					}
				}
				catch (Exception ex) {
					// I campi possono avere dei riferimenti xpath che non corrispondono effettivamente
					// a delle proprieta' dei bean (es. il campo sx per lookup su rif esterni documenti)
				}
			}

			if (addRow)
				LookupUtil.addRowOnLookup(model, instanceName); // eventuale aggiunta di nuove righe (es. in caso di CC o CDS)
		}
	}

	/**
	 * Personalizzazione del valore 'value' su alcuni campi specifici. Corrisponde a
	 * customFillFieldsLookup(campi) del javascript
	 *
	 * @param xpath
	 * @param value
	 * @return
	 */
	private String customFillFieldsLookup(String xpath, String value) throws Exception {
		if (xpath != null && value != null) {
			if (xpath.endsWith(".classif.text_ro"))
				value = ClassifUtil.formatClassif(value, getClassifFormat());
		}
		return value;
	}

	/**
	 * Aggiunta di un nuovo record sul lookup (attivazione da pulsante
	 * new)
	 *
	 * @return
	 * @throws Exception
	 */
	public String newRecord() throws Exception {
		try {
			getFormsAdapter().newRecord();

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				return null;
			}

			// Ritorno in base a Reflection
			return super.buildSpecificDocEditPageAndReturnNavigationRule(getFormsAdapter().getDefaultForm().getParam("dbTable"), response, true);

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;
		}
	}

	/**
	 * Imposta il valore di lookup dopo un'attivita' di inserimento di un
	 * nuovo record
	 *
	 * @return
	 */
	public String redoLookupAfterInsert() throws Exception {
		try {
			getFormsAdapter().redoLookupAfterInsert(physDoc);

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				return null;
			}

			// Elaborazione del titolo e chiamata a 'confirm'
			AclTitles titles = new AclTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());

			setLookupType(response.getDocument()); // necessario per identificare il tipo di lookup e la corretta lettura dei dati

			// Viene restituito un e un solo titolo
			if (titles.titoli != null && titles.titoli.size() == 1) {
				return confirm(titles.titoli.get(0));
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;
		}
	}

}