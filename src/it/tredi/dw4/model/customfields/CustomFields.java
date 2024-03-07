package it.tredi.dw4.model.customfields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.beans.Page;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.model.customfields.specialized_fields.NumeroAnnoInstance;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class CustomFields extends XmlEntity {
	
	private String pageType = "";
	private HashMap<String, Boolean> hiddenSections = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> hiddenFields = new HashMap<String, Boolean>();
	private HashMap<String, String> hiddenFieldsVariables = new HashMap<String, String>();
	private List<Section> sections = new ArrayList<Section>();
	
	private List<Relationship> relationships = new ArrayList<Relationship>();
	
	@Override
	public XmlEntity init(Document dom) {
		return init(dom, null);
	}
	
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom, String pne) {
		this.pageType 			= XMLUtil.parseStrictAttribute(dom, "response/customfields/page/@type");
		this.sections 			= XMLUtil.parseSetOfElement(dom, "response/customfields/page/section", new Section());
		this.hiddenSections 	= initHiddenSection(dom, "response/customfields/page/hide/section");
		this.hiddenFields 		= initHiddenSection(dom, "response/customfields/page/hide/field");
		
		this.relationships 		= XMLUtil.parseSetOfElement(dom, "response/customfields/page/relationships/relationship", new Relationship());
		
		List<?> hidevariables = dom.selectNodes("response/customfields/page/hide/variable");
		if (hidevariables != null && hidevariables.size() > 0) {
			for (int i=0; i<hidevariables.size(); i++) {
				Element hide = (Element) hidevariables.get(i);
				if (hide != null && hide.attributeValue("name") != null) { 
					String hidevalue = "";
					if (hide.attributeValue("value") != null)
						hidevalue = hide.attributeValue("value");
					
					this.hiddenFieldsVariables.put(hide.attributeValue("name"), hidevalue);
				}
			}
		}
		
		// riempimento dei valori dei campi custom definiti in base al contenuto della response (contenuto
		// del doc)
		if (pageType.equals("docedit"))
			fillDocEditCustomFieldsValues(dom, pne);
		else if (pageType.equals("showdoc"))
			fillShowdocCustomFieldsValues(dom, pne);
		
		return this;
	}
	
	/**
	 * inizializzazioni delle sezioni/dei campi base del documento da nascondere
	 * @param dom
	 * @param xpath
	 * @return
	 */
	private HashMap<String, Boolean> initHiddenSection(Document dom, String xpath) {
		HashMap<String, Boolean> hiddenMap = new HashMap<String, Boolean>();
		
		List<?> hidesections = dom.selectNodes(xpath);
		if (hidesections != null && hidesections.size() > 0) {
			for (int i=0; i<hidesections.size(); i++) {
				Element hide = (Element) hidesections.get(i);
				if (hide != null && hide.attributeValue("name") != null) { 
					boolean hidevalue = false;
					if (hide.attributeValue("hide") != null)
						hidevalue = StringUtil.booleanValue(hide.attributeValue("hide"));
					
					hiddenMap.put(hide.attributeValue("name"), new Boolean(hidevalue));
				}
			}
		}
		
		return hiddenMap;
	}
		
	/**
	 * aggiunta dei valori dei campi al formAdapter per salvataggio dei 
	 * campi custom 
	 */
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
		for (int i=0; i<sections.size(); i++) {
    		params.putAll(sections.get(i).asFormAdapterParams(prefix));
    	}
		
		return params;
	}
	
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public HashMap<String, Boolean> getHiddenSections() {
		return hiddenSections;
	}

	public void setHiddenSections(HashMap<String, Boolean> hiddenSections) {
		this.hiddenSections = hiddenSections;
	}

	public HashMap<String, Boolean> getHiddenFields() {
		return hiddenFields;
	}

	public void setHiddenFields(HashMap<String, Boolean> hiddenFields) {
		this.hiddenFields = hiddenFields;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	public HashMap<String, String> getHiddenFieldsVariables() {
		return hiddenFieldsVariables;
	}

	public void setHiddenFieldsVariables(
			HashMap<String, String> hiddenFieldsVariables) {
		this.hiddenFieldsVariables = hiddenFieldsVariables;
	}
	
	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}
	
	/**
	 * riempimento dei valori dei campi custom definiti in base al contenuto della response (contenuto del doc)
	 * per la pagina di docEdit (inserimento/modifica)
	 * 
	 * @param dom response del service
	 * @param pne nome dell'elemento radice dell'oggetto da gestire (es. doc per i documenti)
	 */
	private void fillDocEditCustomFieldsValues(Document dom, String pne) {
		
		// occorre valorizzare ogni campo custom in base all'xpath definito e al dom caricato...
		if (this.sections.size() > 0) {
			
			// recupero il nome dell'elemento radice (dovrebbe comunque essere sempre 'response')
			String baseXPath = "//";
			if (pne != null && pne.length() > 0)
				baseXPath = dom.getRootElement().getName() + "/" + pne;
			
			for (int i=0; i<this.sections.size(); i++) {
				Section section = this.sections.get(i);
				if (section != null && section.getFields().size() > 0) {
					section.setDaVisualizzare(true);  // in inserimento/modifica occorre visualizzare sempre le sezioni
					
					Element sectionelement = null;
					if (dom.selectSingleNode(baseXPath) != null)
						sectionelement = ((Element) dom.selectSingleNode(baseXPath)).createCopy();
					
					for (int j=0; j<section.getFields().size(); j++) {
						Field field = section.getFields().get(j);
						if (field != null) {
							if (field.getType().equals("group") && !field.getXpath().equals("")) { 
								// campo o gruppo di campi ripetibile
								// sezione ripetibile
								List<?> elements = null;
								if (sectionelement != null)
									elements = sectionelement.selectNodes(field.getXpath());
								
								if (elements != null && elements.size() > 0) { // il dom contiene istanze della sezione valorizzate
									for (int k=0; k<elements.size(); k++) {
										Element element = (Element) elements.get(k);
										if (element != null) {
											FieldInstance fieldInstance = new FieldInstance();
											fieldInstance.init(XMLUtil.getDOM(field.getXml()));
											fieldInstance.initFieldValue(element, field.getType(), field.getXpath(), field.getDefaultvalue());
											field.getInstances().add(fieldInstance);
										}
									}
								}
								else { // il dom non contiene istanze della sezione valorizzate
									FieldInstance fieldInstance = new FieldInstance();
									fieldInstance.init(XMLUtil.getDOM(field.getXml()));
									fieldInstance.initFieldValue(sectionelement, field.getType(), field.getXpath(), field.getDefaultvalue());
									field.getInstances().add(fieldInstance);
								}
							}
							else {
								// campo base (singola istanza)
								FieldInstance fieldInstance;
								// Gestione possibili instanze specializzate
								switch (field.getType()) {
									case "numero_anno":
										fieldInstance = new NumeroAnnoInstance();
										break;
									default:
										fieldInstance = new FieldInstance();
								}
								fieldInstance.init(XMLUtil.getDOM(field.getXml()));
								fieldInstance.initFieldValue(sectionelement, field.getType(), field.getXpath(), field.getDefaultvalue());
								field.getInstances().add(fieldInstance);
							}
						}
					}
				}
			}
			
			// Verifica delle relazioni fra campi in fase di costruzione del form dei campi custom
			this.evaluateRelationships();
		}
	}
	
	/**
	 * Verifica di eventuali relazioni fra campi con attivazione di azioni (es. obbligatorieta'/visibilita' 
	 * di campi in base al valore di una tendina)
	 */
	public String evaluateRelationships() {
		// TODO al momento le relazioni fra campi custom sono supportate solo al di fuori di gruppi/campi multi-istanza, nel caso deve essere prevista un'implementazione specifica
		if (this.relationships != null && !this.relationships.isEmpty()) {
			List<String> filledXPaths = new ArrayList<String>();
			for (Relationship relationship : relationships) {
				processRelationshipAction(relationship, filledXPaths);
			}
		}
		return null;
	}
	
	/**
	 * Esecuzione dell'azione prevista su una specifica relazione fra campi custom
	 * @param relationship
	 * @param filledXPaths
	 */
	private void processRelationshipAction(Relationship relationship, List<String> filledXPaths) {
		if (relationship != null) {
			if (filledXPaths == null)
				filledXPaths = new ArrayList<String>();
			String value = getValueFromPath(relationship.getFieldFrom());
			boolean todo = false;
			boolean boolValue = (value != null && value.equals(relationship.getValueFrom()));
			if (boolValue) {
				if (!filledXPaths.contains(relationship.getFieldTo()))
					filledXPaths.add(relationship.getFieldTo());
				todo = true;
			}
			else {
				// Se il valore corrente non corriponde a quello definito nella relazione applico la regola solo se
				// il path di destinazione non e' gia' stato settato 
				if (!filledXPaths.contains(relationship.getFieldTo()))
					todo = true;
			}
			
			if (todo)
				processActionOnField(relationship.getFieldTo(), relationship.getAction(), boolValue);
		}
	}
	
	/**
	 * Recupera il valore associato ad un campo identificato da un path.
	 * Formato del campo custom: section[0].field_2[0].field_3[2]
	 * 
	 * @param fieldPath definizione del campo per il quale recuperare il valore (es. section[0].field_2[0].field_3[2])
	 * @return Valore associato al campo
	 */
	private String getValueFromPath(String fieldPath) {
		String value = null;
		if (fieldPath != null && !fieldPath.equals("")) {
			
			// recupero della sezione
			int sectionIndex = fieldPath.indexOf(".");
			if (sectionIndex != -1) {
				String propertyName = fieldPath.substring(0, sectionIndex); // la prima property corrisponde sempre ad una sezione
				String currentPath = fieldPath.substring(sectionIndex+1);
				
				String index = "0";
				if (propertyName.indexOf("_") != -1) {
					index = propertyName.substring(propertyName.indexOf("_") + 1);
					
					value = this.getSections().get(new Integer(index).intValue()).getValueFromPath(currentPath);
				}
			}
		}
		return value;
	}
	
	/**
	 * Esecuzione di una azione su un campo identificato da un path.
	 * Formato del campo custom: section[0].field_2[0].field_3[2]
	 * 
	 * @param fieldPath definizione del campo da aggiornare (es. section[0].field_2[0].field_3[2])
	 * @param action azione da svolgere sul campo
	 * @param value Valore da settare in base all'azione
	 */
	private void processActionOnField(String fieldPath, RelationshipAction action, Object value) {
		if (fieldPath != null && !fieldPath.equals("") && action != null) {
			
			// recupero della sezione
			int sectionIndex = fieldPath.indexOf(".");
			if (sectionIndex != -1) {
				String propertyName = fieldPath.substring(0, sectionIndex); // la prima property corrisponde sempre ad una sezione
				String currentPath = fieldPath.substring(sectionIndex+1);
				
				String index = "0";
				if (propertyName.indexOf("_") != -1) {
					index = propertyName.substring(propertyName.indexOf("_") + 1);
					
					this.getSections().get(new Integer(index).intValue()).processActionOnField(currentPath, action, value);
				}
			}
		}
	}
	
	/**
	 * Verifica di eventuali relazioni relative ad uno specifico campo
	 * @param fieldFrom
	 */
	public void checkRelationships(String fieldFrom) {
		if (fieldFrom != null && !fieldFrom.isEmpty() && this.relationships != null && !this.relationships.isEmpty()) {
			for (Relationship relationship : relationships) {
				if (relationship.getFieldFrom().equalsIgnoreCase(fieldFrom)) {
					processRelationshipAction(relationship, null);
				}
			}
		}
	}
	
	/**
	 * riempimento dei valori dei campi custom definiti in base al contenuto della response (contenuto del doc)
	 * per la pagina di visualizzazione (showdoc)
	 * 
	 * @param dom response del service
	 * @param pne nome dell'elemento radice dell'oggetto da gestire (es. doc per i documenti)
	 */
	private void fillShowdocCustomFieldsValues(Document dom, String pne) {
		
		// occorre valorizzare ogni campo custom in base all'xpath definito e al dom caricato...
		if (this.sections.size() > 0) {
			
			// recupero il nome dell'elemento radice (dovrebbe comunque essere sempre 'response')
			String baseXPath = "//";
			if (pne != null && pne.length() > 0)
				baseXPath = dom.getRootElement().getName() + "/" + pne;
			
			for (int i=0; i<this.sections.size(); i++) {
				Section section = this.sections.get(i);
				if (section != null && section.getFields().size() > 0) {
					
					// variabile utilizzata per controllare se occorre visualizzare o meno la sezione (almeno un 
					// campo valorizzato)
					boolean campiValorizzati = false;
					
					Element sectioncontext = (Element) dom.selectSingleNode(baseXPath);
					for (int j=0; j<section.getFields().size(); j++) {
						Field field = section.getFields().get(j);
						if (field != null) {
							if (field.getType().equals("group") && !field.getXpath().equals("")) { 
								// campo o gruppo di campi ripetibile
								// sezione ripetibile
								List<?> elements = sectioncontext.selectNodes(field.getXpath());
								if (elements != null && elements.size() > 0) { // il dom contiene istanze della sezione valorizzate
									for (int k=0; k<elements.size(); k++) {
										Element element = (Element) elements.get(k);
										if (element != null) {
											FieldInstance fieldInstance = new FieldInstance();
											fieldInstance.init(XMLUtil.getDOM(field.getXml()));
											if (fieldInstance.showFieldValue(element, field.getType(), field.getXpath(), field.getXslOut()))
												campiValorizzati = true;
											field.getInstances().add(fieldInstance);
										}
									}
								}
							}
							else {
								// campo base (singola istanza)
								FieldInstance fieldInstance;
								// Gestione possibili instanze specializzate
								switch (field.getType()) {
									case "numero_anno":
										fieldInstance = new NumeroAnnoInstance();
										break;
									default:
										fieldInstance = new FieldInstance();
								}
								fieldInstance.init(XMLUtil.getDOM(field.getXml()));
								if (fieldInstance.showFieldValue(sectioncontext, field.getType(), field.getXpath(), field.getXslOut()))
									campiValorizzati = true;
								field.getInstances().add(fieldInstance);
							}
						}
					}
					
					section.setDaVisualizzare(campiValorizzati);
				}
			}
		}
	}
	
	/**
	 * controllo su eventuali campi custom obbligatori
	 * 
	 * @param isModify true se si devono eseguire i controlli su una modifica, false in caso di inserimento
	 * @param formatoData formato da utilizzare per i campi data
	 * @param pageBean bean della pagina che contiene il form con i campi custom
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredFields(boolean isModify, String formatoData, Page pageBean) {
		boolean result = false;
		
		// controllo dei campi obbligatori di tutte le sezioni
		if (this.getSections().size() > 0) {
			for (int i=0; i<this.getSections().size(); i++) {
				Section section = this.getSections().get(i);
				if (section != null && section.getFields().size() > 0) {
					for (int j=0; j<section.getFields().size(); j++) {
						Field field = section.getFields().get(j);
						if (field != null) {
							String parentFormElementId = "templateForm:section:" + i + ":field:" + j;  // necessario all'identificazione del campo per la segnalazione dell'errore
							
							if (field.checkRequiredFields(isModify, formatoData, pageBean, parentFormElementId))
								result = true; 
						}
					}
				}
			}
		}
		
		return result;
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
			
			if (value == null)
				value = "";
			
			// recupero della sezione
			int sectionIndex = lookupXpath.indexOf(".");
			if (sectionIndex != -1) {
				String propertyName = lookupXpath.substring(0, sectionIndex); // la prima property corrisponde sempre ad una sezione
				String currentLookupXpath = lookupXpath.substring(sectionIndex+1);
				
				String index = "0";
				if (propertyName.indexOf("_") != -1) {
					index = propertyName.substring(propertyName.indexOf("_") + 1);
					
					this.getSections().get(new Integer(index).intValue()).setFieldValueFromLookup(currentLookupXpath, value);
				}
			}
		}
	}
	
}
