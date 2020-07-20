package it.tredi.dw4.model.customfields;

import it.tredi.dw4.beans.Page;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.Element;

public class Field extends XmlEntity {

	private String xml = ""; // xml di definizione del singolo campo
	private String label = "";
	private String type = "";
	private String xpath = "";
	private boolean repeteable = false;
	
	// docEdit
	private String defaultvalue = "";
	private boolean required = false;
	private boolean numeric = false;
	private String size = "";
	private String helpmessage = "";
	private String placeholder = "";
	private boolean readonly = false;
	private HashMap<String, String> lookupParams = new HashMap<String, String>(); // parametri da inviare per il lookup (se e solo se campo di tipo lookup)
	
	// showdoc
	private String xslOut = "";
	
	private List<FieldInstance> instances = new ArrayList<FieldInstance>();
	
	@Override
	public XmlEntity init(Document dom) {
		this.xml			= dom.asXML();
		this.label 			= XMLUtil.parseStrictAttribute(dom, "field/@label");
		this.helpmessage	= XMLUtil.parseStrictAttribute(dom, "field/input/@helpmessage");
		this.type 			= XMLUtil.parseStrictAttribute(dom, "field/@type");
		this.defaultvalue 	= XMLUtil.parseStrictAttribute(dom, "field/input/@defaultValue");
		this.required 		= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "field/input/@required"));
		this.numeric 		= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "field/input/@numeric"));
		this.size	 		= XMLUtil.parseStrictAttribute(dom, "field/input/@size");
		this.placeholder	= XMLUtil.parseStrictAttribute(dom, "field/input/@placeholder"); // TODO attributo non ancora gestito a livello di template
		this.readonly	 	= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "field/input/@readonly"));
		
		if (type.equals("group")) {
			// gruppo di campi
			this.xpath		= XMLUtil.parseStrictAttribute(dom, "field/@path");
			
			if (!this.xpath.equals(""))
				this.repeteable = true;
			else
				this.repeteable = false;
			
		}
		else {
			// campo semplice
			
			this.xpath 		= XMLUtil.parseStrictAttribute(dom, "field/input/@path"); // docEdit/docEditModify
			if (this.xpath.equals(""))
				this.xpath	= XMLUtil.parseStrictAttribute(dom, "field/textField/@value"); // showdoc
			
			if (type.equals("lookup")) {
				// caricamento di tutti i parametri necessari ad eseguire il lookup 
				// sul campo custom
				List<?> params = dom.selectNodes("field/input/lookupParam"); 
				if ((params != null) && (params.size() > 0)) {
					for (int index = 0; index < params.size(); index++) {
						if (params.get(index) != null) {
							Element param = (Element)params.get(index);
							if (param != null 
										&& param.attributeValue("name") != null && !param.attributeValue("name").equals("") 
										&& param.attributeValue("value") != null)
								lookupParams.put(param.attributeValue("name"), param.attributeValue("value"));
						}
					}
				}
			}
		}
		
		if (XMLUtil.countElements(dom, "field/xslOut") == 1)
			this.xslOut 	= XMLUtil.createDocument(dom, "field/xslOut").asXML();
		
		return this;
	}

	/**
	 * aggiunta dei valori dei campi al formAdapter per salvataggio dei 
	 * campi custom 
	 */
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	String fieldprefix = prefix + getFieldPrefixForFormAdapter();
    	for (int i=0; i<instances.size(); i++) {
    		String instanceprefix = fieldprefix;
    		if (instances.size() > 1)
    			instanceprefix = fieldprefix + "[" + i + "]";
    		params.putAll(instances.get(i).asFormAdapterParams(instanceprefix, type));
    	}
    	
		return params;
	}
	
	/**
	 * ricostruisce il prefisso del campo da aggiungere ai parametri del formAdapter
	 * per il salvataggio dei campi custom
	 * @return
	 */
	private String getFieldPrefixForFormAdapter() {
		String prefix = "";
		
		if (xpath != null && xpath.length() > 0) {
			prefix = xpath.replaceAll("/", ".");
			
			// eliminazione di un eventuale riferimento a text()
			if (prefix.contains("text()")) {
				prefix = prefix.replace("text()", "");
				if (prefix.endsWith("."))
					prefix = prefix.substring(0, prefix.length()-1);
			}
			
			if (prefix.length() > 0 && !prefix.startsWith("."))
				prefix = "." + prefix;
		}
		
		return prefix;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isNumeric() {
		return numeric;
	}

	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}
	
	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getXslOut() {
		return xslOut;
	}

	public void setXslOut(String xslOut) {
		this.xslOut = xslOut;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getHelpmessage() {
		return helpmessage;
	}

	public void setHelpmessage(String helpmessage) {
		this.helpmessage = helpmessage;
	}

	public boolean isRepeteable() {
		return repeteable;
	}

	public void setRepeteable(boolean repeteable) {
		this.repeteable = repeteable;
	}
	
	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
	public HashMap<String, String> getLookupParams() {
		return lookupParams;
	}

	public void setLookupParams(HashMap<String, String> lookupParams) {
		this.lookupParams = lookupParams;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public List<FieldInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<FieldInstance> instances) {
		this.instances = instances;
	}
	
	/**
	 * inizializzazione del valore da impostare sul campo
	 * @param context elemento XML relativo al campo
	 */
	public void initFieldValue(Element context) {
		if (context != null) {
			if (type.equals("group")) { 
				// navigazione dei figli del gruppo
				
				List<?> elements = context.selectNodes(xpath);
				if (elements.size() > 0) { 
					// l'elemento contiene piu' istanze del campo/gruppo di campi
					for (int k=0; k<elements.size(); k++) {
						Element element = (Element) elements.get(k);
						if (element != null) {
							FieldInstance instance = new FieldInstance();
							instance.init(XMLUtil.getDOM(xml));
							instance.initFieldValue(context, type, xpath, defaultvalue);
							instances.add(instance);
						}
					}
				}
			}
			else { 
				// modifica di un campo base (text, textarea, ecc.)
				FieldInstance instance = new FieldInstance();
				instance.init(XMLUtil.getDOM(xml));
				instance.initFieldValue(context, type, xpath, defaultvalue);
				instances.add(instance);
			}
		}
		else {
			// nessun elemento, inserimento di un campo base (text, textarea, ecc.)
			FieldInstance instance = new FieldInstance();
			instance.init(XMLUtil.getDOM(xml));
			instance.initFieldValue(null, type, xpath, defaultvalue);
			instances.add(instance);
		}
	}

	/**
	 * eliminazione di un'istanza di sezione
	 */
	public String deleteInstance() {
		deleteInstance((FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldInstance"));
		return null;
	}
	
	/**
	 * eliminazione di un'istanza di sezione
	 */
	public void deleteInstance(FieldInstance fieldInstance) {
		if (fieldInstance != null && instances != null) {
			instances.remove(fieldInstance);
			if (instances.isEmpty()) {
				FieldInstance empty = new FieldInstance();
				empty.init(XMLUtil.getDOM(xml));
				empty.initFieldValue(null, type, xpath, defaultvalue);
				instances.add(empty);
			}
		}
	}
	
	/**
	 * aggiunta di un'istanza di sezione
	 */
	public String addInstance() {
		addInstance((FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldInstance"));
		return null;
	}
	
	/**
	 * aggiunta di un'istanza di sezione
	 */
	public void addInstance(FieldInstance fieldInstance) {
		int index = 0;
		if (fieldInstance != null && instances != null)
			index = instances.indexOf(fieldInstance);
		
		if (instances != null) {
			FieldInstance empty = new FieldInstance();
			empty.init(XMLUtil.getDOM(xml));
			empty.initFieldValue(null, type, xpath, defaultvalue);
			
			if (instances.size() > index)
				instances.add(index+1,  empty);
			else
				instances.add(empty);
		}
	}
	
	/**
	 * spostamento in alto di un'istanza di campo/gruppo di campi
	 */
	public String moveUpInstance() {
		moveUpInstance((FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldInstance"));
		return null;
	}
	
	/**
	 * spostamento in alto di un'istanza di campo/gruppo di campi
	 */
	public void moveUpInstance(FieldInstance fieldInstance) {
		if (fieldInstance != null && instances != null) {
			int index = instances.indexOf(fieldInstance);
			if (index > 0) {
				instances.remove(index);
				instances.add(index-1, fieldInstance);
			}
		}
	}

	/**
	 * spostamento in basso di un'istanza di campo/gruppo di campi
	 */
	public String moveDownInstance() {
		moveDownInstance((FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldInstance"));
		return null;
	}
	
	/**
	 * spostamento in basso di un'istanza di campo/gruppo di campi
	 */
	public void moveDownInstance(FieldInstance fieldInstance) {
		if (fieldInstance != null && instances != null) {
			int index = instances.indexOf(fieldInstance);
			if (index < instances.size()-1) {
				instances.remove(index);
				instances.add(index+1, fieldInstance);
			}
		}
	}
	
	/**
	 * controllo su eventuali campi custom obbligatori
	 * 
	 * @param modify true se si devono eseguire i controlli su una modifica, false in caso di inserimento
	 * @param formatoData formato da utilizzare per i campi data
	 * @param pageBean bean della pagina che contiene il form con i campi custom
	 * @param parentFormElementId identificativo del campo padre nel form, utilizzato per la segnalazione dell'errore sull'input
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredFields(boolean isModify, String formatoData, Page pageBean, String parentFormElementId) {
		boolean result = false;
		
		if (instances != null && instances.size() > 0) {
			if (parentFormElementId == null)
				parentFormElementId = "";
			
			for (int i=0; i<instances.size(); i++) {
				FieldInstance instance = (FieldInstance) instances.get(i);
				if (instance != null) {
					
					String currentFormLevelId = parentFormElementId;
					if (parentFormElementId.indexOf("fieldInstance") == -1)
						currentFormLevelId = currentFormLevelId + ":fieldInstance:" + i;
					else
						currentFormLevelId = currentFormLevelId + ":subfieldInstance:" + i;
					
					String idCampo = type + "field";
					if (type.equals("lookup"))
						idCampo = type + "_input";
					
					// TODO attualmente non e' gestito il controllo di obbligatorieta' su checkbox, occorre gestirlo?
					
					if (!type.equals("checkbox")) {
						
						// controllo su campi obbligatori
						if (required && (instance.getValue() == null || instance.getValue().equals(""))) {
							if (pageBean != null)
								pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo, I18N.mrs("acl.requiredfield") + " '" + label + "'");
							result = true;
						}
						
						if (instance.getValue() != null && !instance.getValue().equals("")) {
							// controllo su formato numerico
							if (numeric && !StringUtil.isNumber(instance.getValue())) {
								if (pageBean != null)
									pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo, I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + label + "' ");
								result = true;
							}
						
							// controllo su formato data
							if (type.equals("calendar") && !DateUtil.isValidDate(instance.getValue(), formatoData)) {
								if (pageBean != null)
									pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo, I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + label + "': " + formatoData.toLowerCase());
								result = true;
							}
						}
					}
					
					// controllo di eventuali campi obbligatori presenti nei subfields
					if (instance.getFields() != null && instance.getFields().size() > 0) {
						for (int j=0; j<instance.getFields().size(); j++) {
							Field subfield = (Field) instance.getFields().get(j);
							if (subfield != null) {
								
								String childFormLevelId = currentFormLevelId;
								if (currentFormLevelId.indexOf("field") == -1)
									childFormLevelId = childFormLevelId + ":field:" + j;
								else
									childFormLevelId = childFormLevelId + ":subfield:" + j;
								
								if (subfield.checkRequiredFields(isModify, formatoData, pageBean, childFormLevelId))
									result = true;
							}
						}
					}
					
				}
			}
		}
		
		return result;
	}
	
	/**
	 * visualizzazione di un valore impostato su un campo
	 * @param context elemento relativo al campo
	 * 
	 * @return true se il campo/gruppo di campi contiene almeno un valore non vuoto, false altrimenti
	 */
	public boolean showFieldValue(Element context) {
		boolean valueFound = false;
		
		if (context != null) {
			if (type.equals("group")) { 
				// navigazione dei figli del gruppo
				
				List<?> elements = context.selectNodes(xpath);
				if (elements.size() > 0) { 
					// l'elemento contiene piu' istanze del campo/gruppo di campi
					for (int k=0; k<elements.size(); k++) {
						Element element = (Element) elements.get(k);
						if (element != null) {
							FieldInstance instance = new FieldInstance();
							instance.init(XMLUtil.getDOM(xml));
							if (instance.showFieldValue(element, type, xpath, xslOut))
								valueFound = true;
							instances.add(instance);
						}
					}
				}
			}
			else { 
				// visualizzazione di un campo base (text, textarea, ecc.)
				
				FieldInstance instance = new FieldInstance();
				instance.init(XMLUtil.getDOM(xml));
				if (instance.showFieldValue(context, type, xpath, xslOut))
					valueFound = true;
				instances.add(instance);
			}
		}
		
		return valueFound;
	}
	
	/**
	 * aggiornamento di un valore su un campo custom tramite recupero da lookup.
	 * Formato del campo custom: section[0].field_2[0].field_3[2]
	 * 
	 * @param lookupXpath definizione del campo da aggiornare (formato lookup custom)
	 * @param value valore da assegnare al campo custom
	 * @throws Exception
	 */
	public void setFieldValueFromLookup(String lookupXpath, String value) throws Exception {
		if (lookupXpath != null && !lookupXpath.equals("")) {
			
			int fieldIndex = lookupXpath.indexOf(".");
			if (fieldIndex != -1) { 
				// se esiste almeno un punto nella definizione del campo significa che il campo corrente
				// e' un campo di tipo gruppo (il campo da settare e' contenuto al suo interno)
				
				String propertyName = lookupXpath.substring(0, fieldIndex);
				String currentLookupXpath = lookupXpath.substring(fieldIndex+1);
				
				String index = "0";
				if (propertyName.endsWith("]"))
					index = propertyName.substring(propertyName.indexOf("[")+1, propertyName.length()-1);
				
				this.getInstances().get(new Integer(index)).setFieldValueFromLookup(currentLookupXpath, value);
			}
			else {
				// non e' stato individuato un punto nell'xpath di lookup, quindi il campo da settare e' il 
				// campo corrente
				
				String index = "0";
				if (lookupXpath.endsWith("]"))
					index = lookupXpath.substring(lookupXpath.indexOf("[")+1, lookupXpath.length()-1);
				
				this.getInstances().get(new Integer(index).intValue()).setValue(value);
			}
		}
	}
	
}
