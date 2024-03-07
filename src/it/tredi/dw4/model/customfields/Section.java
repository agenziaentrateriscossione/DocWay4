package it.tredi.dw4.model.customfields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Section extends XmlEntity {

	private String xml = ""; // xml di definizione della sezione
	private String label = "";
	private String iconclass = "";
	
	private List<Field> fields = new ArrayList<Field>();
	
	// identifica se occorre visualizzare o meno la sezione all'utente (es. in visualizzazione almeno un campo valorizzato)
	private boolean daVisualizzare = false; 
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.label 		= XMLUtil.parseStrictAttribute(dom, "section/@label");
		this.iconclass 	= XMLUtil.parseStrictAttribute(dom, "section/@iconclass");
		this.fields 	= XMLUtil.parseSetOfElement(dom, "section/field", new Field());
		
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
    	
    	for (int i=0; i<fields.size(); i++) {
    		params.putAll(fields.get(i).asFormAdapterParams(prefix));
    	}
		
		return params;
	}
	
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
		
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIconclass() {
		return iconclass;
	}

	public void setIconclass(String iconclass) {
		this.iconclass = iconclass;
	}
	
	public boolean isDaVisualizzare() {
		return daVisualizzare;
	}

	public void setDaVisualizzare(boolean daVisualizzare) {
		this.daVisualizzare = daVisualizzare;
	}
	
	/**
	 * eliminazione di un'istanza di un gruppo di campi
	 */
	public String deleteFieldGroup() {
		deleteFieldGroup((Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldGroup"));
		return null;
	}
	
	/**
	 * eliminazione di un'istanza di un gruppo di campi
	 */
	public void deleteFieldGroup(Field fieldGroup) {
		if (fieldGroup != null)
			fields.remove(fieldGroup);
	}
	
	/**
	 * aggiunta di un'istanza di un gruppo di campi
	 */
	public String addFieldGroup() {
		addFieldGroup((Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldGroup"));
		return null;
	}
	
	/**
	 * aggiunta di un'istanza di un gruppo di campi
	 */
	public void addFieldGroup(Field fieldGroup) {
		int index = 0;
		if (fieldGroup != null && fields != null)
			index = fields.indexOf(fieldGroup);
		
		if (fields != null) {
			Field emptyField = new Field();
			emptyField.init(XMLUtil.getDOM(fieldGroup.getXml()));
			
			if (fields.size() > index)
				fields.add(index+1,  emptyField);
			else
				fields.add(emptyField);
		}
	}
	
	/**
	 * spostamento in alto di un'istanza di un gruppo di campi
	 */
	public String moveUpFieldGroup() {
		moveUpFieldGroup((Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldGroup"));
		return null;
	}
	
	/**
	 * spostamento in alto di un'istanza di un gruppo di campi
	 */
	public void moveUpFieldGroup(Field fieldGroup) {
		if (fieldGroup != null && fields != null) {
			int index = fields.indexOf(fieldGroup);
			if (index > 0) {
				fields.remove(index);
				fields.add(index-1, fieldGroup);
			}
		}
	}

	/**
	 * spostamento in basso di un'istanza di un gruppo di campi
	 */
	public String moveDownFieldGroup() {
		moveDownFieldGroup((Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldGroup"));
		return null;
	}
	
	/**
	 * spostamento in basso di un'istanza di un gruppo di campi
	 */
	public void moveDownFieldGroup(Field fieldGroup) {
		if (fieldGroup != null && fields != null) {
			int index = fields.indexOf(fieldGroup);
			if (index < fields.size()-1) {
				fields.remove(index);
				fields.add(index+1, fieldGroup);
			}
		}
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
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
			String propertyName = lookupXpath;
			
			int fieldIndex = lookupXpath.indexOf(".");
			if (fieldIndex != -1) 
				propertyName = lookupXpath.substring(0, fieldIndex);
			
			if (propertyName.indexOf("[") != -1)
				propertyName = propertyName.substring(0, propertyName.indexOf("["));
			
			String []fieldParams = propertyName.split("_");
			
			this.getFields().get(new Integer(fieldParams[1]).intValue()).setFieldValueFromLookup(lookupXpath, value);
		}
	}
	
	/**
	 * Recupera il valore associato ad un campo identificato da un path.
	 * Formato del campo custom: section[0].field_2[0].field_3[2]
	 * 
	 * @param fieldPath definizione del campo per il quale recuperare il valore (es. section[0].field_2[0].field_3[2])
	 * @return Valore associato al campo
	 */
	public String getValueFromPath(String fieldPath) {
		String value = null;
		if (fieldPath != null && !fieldPath.equals("")) {
			String propertyName = fieldPath;
			
			int fieldIndex = fieldPath.indexOf(".");
			if (fieldIndex != -1) 
				propertyName = fieldPath.substring(0, fieldIndex);
			
			if (propertyName.indexOf("[") != -1)
				propertyName = propertyName.substring(0, propertyName.indexOf("["));
			
			String []fieldParams = propertyName.split("_");
			
			value = this.getFields().get(new Integer(fieldParams[1]).intValue()).getValueFromPath(fieldPath);
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
	public void processActionOnField(String fieldPath, RelationshipAction action, Object value) {
		if (fieldPath != null && !fieldPath.equals("") && action != null) {
			String propertyName = fieldPath;
			
			int fieldIndex = fieldPath.indexOf(".");
			if (fieldIndex != -1) 
				propertyName = fieldPath.substring(0, fieldIndex);
			
			if (propertyName.indexOf("[") != -1)
				propertyName = propertyName.substring(0, propertyName.indexOf("["));
			
			String []fieldParams = propertyName.split("_");
			
			this.getFields().get(new Integer(fieldParams[1]).intValue()).processActionOnField(fieldPath, action, value);
		}
	}

}
