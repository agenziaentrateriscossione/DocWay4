package it.tredi.dw4.model.customfields;

import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class FieldInstance extends XmlEntity {

	private String value = "";
	private List<Field> fields = new ArrayList<Field>(); // il campo di tipo gruppo puo' contenere altri campi
	private List<Item> items = new ArrayList<Item>(); // utilizzato per campi checkbox, radio o select
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.fields 	= XMLUtil.parseSetOfElement(dom, "field/field", new Field());
		this.items 		= XMLUtil.parseSetOfElement(dom, "field/input/item", new Item());
		
		return this;
	}

	/**
	 * aggiunta dei valori dei campi al formAdapter per salvataggio dei 
	 * campi custom
	 * NB. da non utilizzare perche' non viene considerata la tipologia del campo (non vengono eseguite elaborazioni specifiche sui campi)
	 */
	@Override
	@Deprecated 
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, "");
	}
	
	/**
	 * aggiunta dei valori dei campi al formAdapter per salvataggio dei 
	 * campi custom 
	 * 
	 * @param prefix prefisso del parametro per la request
	 * @param type tipologia di campo alla quale fa riferimento l'istanza corrente
	 */
	public Map<String, String> asFormAdapterParams(String prefix, String type) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (type.equals("group")) {
    		// gestione di un gruppo di campi (ciclo su tutti i campi figli)
    		
    		for (int i=0; i<fields.size(); i++) {
    			Field field = (Field) fields.get(i);
    			if (field != null) 
    				params.putAll(field.asFormAdapterParams(prefix));
    		}
    	}
    	else {
    		// gestione dei campi base
    		
	    	if (type.equals("checkbox")) {
	    		// campo checkbox: occorre verificare quali item sono stati selezionati dall'utente
	    		if (items != null && items.size() > 0) {
	    			int index = 0;
		    		for (int i=0; i<items.size(); i++) {
		    			Item item = (Item) items.get(i);
		    			if (item != null && item.isSelected()) {
		    				params.put(prefix + ".value[" + index + "]", item.getValue());
		    				index++;
		    			}
		    		}
	    		}
	    	}
	    	else if (type.equals("calendar")) {
	    		// in caso di campo calendar occorre formattare la data in formato xway
	    		if (value != null && value.trim().length() > 0)
	    			params.put(prefix, DateUtil.formatDate2XW(value, null));
	    	}
	    	else {
	    		// il valore da salvare e' contenuto nel campo value
	    		if (value != null && value.trim().length() > 0)
	    			params.put(prefix, value);
	    	}
    	}
    	
		return params;
	}
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	/**
	 * inizializzazione del valore da impostare sul campo
	 * @param context elemento xml relativo al campo
	 * @param type tipologia di campo che si sta gestendo
	 * @param xpath xpath da utilizzare per il recupero del valore
	 * @param defaultvalue  valore di default da assegnare al campo in caso di mancato recupero tramite xpath 
	 */
	public void initFieldValue(Element context, String type, String xpath, String defaultvalue) {
		if (value.equals("")) { // se il campo non ha alcun valore tento di valorizzarlo ...
			if (xpath == null)
				xpath = "";
			if (defaultvalue == null)
				defaultvalue = "";
			
			// prima in base al valore recuperato tramite xpath (modifica di un documento)
			if (!xpath.equals("") && context != null) { // ... in base a quanto recuperato tramite un xpath ...
				// recupero tramite xpath
				Node node = context.selectSingleNode(xpath);
				if (node != null)
					value = node.getText();
			}
			
			// in seconda istanza tramite un eventuale valore di default specificato nell'xml 
			// di definizione dei campi custom
			if (value.equals("") && !defaultvalue.equals(""))
				value = defaultvalue;
			
			// se il campo e' di tipo checkbox occorre gestire la selezione degli items
			if (context != null && type.equals("checkbox") && items.size() > 0) {
				for (int i=0; i<items.size(); i++) {
					setCheckboxItemSelected((Item) items.get(i), context, xpath);
				}
			}
		}
		
		// inizializzazione di eventuali sottocampi (campo di tipo gruppo)
		if (fields != null && fields.size() > 0) {
			for (int i=0; i<fields.size(); i++)
				fields.get(i).initFieldValue(context);
		}
	}
	
	/**
	 * in caso di campo checkbox verifica se l'item deve essere selezionato (tramite applicazione dell'xPath
	 * all'elemento passato) e in caso affermato lo imposta come selected.
	 * 
	 * @param item item da verificare (da settare come checked o meno)
	 * @param context elemento relativo al campo
	 * @param xpath xpath da utilizzare per il recupero del valore del campo
	 */
	private void setCheckboxItemSelected(Item item, Element context, String xpath) {
		if (item != null && xpath.length() > 0) {
			if (!xpath.endsWith("/"))
				xpath = xpath + "/";
			
			Node node = context.selectSingleNode(xpath + "/value[text()='" + item.getValue() + "']"); // recupero dal dom di response il valore corrispondente all'item
			if (node != null)
				item.setSelected(true);
		}
	}
	
	/**
	 * visualizzazione di un valore impostato su un campo
	 * @param context elemento relativo al campo
	 * @param type tipologia di campo che si sta gestendo
	 * @param xpath xpath da utilizzare per il recupero del valore
	 * @param xslOut eventuale xsl da utilizzare per la formattazione del campo (in alternativa a xpath)
	 * 
	 * @return true se il campo/gruppo di campi contiene almeno un valore non vuoto, false altrimenti
	 */
	public boolean showFieldValue(Element context, String type, String xpath, String xslOut) {
		boolean valueFound = false;
		
		if (!type.equals("group")) {
			if (value.equals("")) { 
				// prima in base al valore recuperato tramite xpath
				if (!xpath.equals("")) { // ... in base a quanto recuperato tramite un xpath ...
					
					// recupero tramite xpath
					if (type.equals("checkbox")) {
						// campo di tipo checkbox
						List<?> nodes = context.selectNodes(xpath + "/value");
						if (nodes != null && nodes.size() > 0) {
							String values = "";
							for (int k=0; k<nodes.size(); k++) {
								Node node = (Node) nodes.get(k);
								if (node != null && node.getText() != null && !node.getText().equals("")) {
									values = values + node.getText() + ", ";
								}
							}
							if (values.length() > 0) {
								value = values.substring(0, values.length()-2);
								valueFound = true;
							}
						}
					}
					else {
						// altra tipologia di campo
						
						Node node = context.selectSingleNode(xpath);
						if (node != null && node.getText() != null && !node.getText().equals("")) {
							value = node.getText();
							valueFound = true;
						}
					}
													
				}
				else if (!xslOut.equals("")) { // ... oppure in base ad un XSL specificato nel file di definizione dei campi custom
					
					// recupero del valore da mostrare per il campo tramite 
					// l'applicazione del modello XSL al contenuto del documento
					try {
						String xslvalue = getHtmlFieldValue(context, xslOut);
						if (xslvalue != null && !xslvalue.equals("")) {
							value = xslvalue;
							valueFound = true;
						}
					}
					catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					
				}
			}
		}
		else {
			// visualizzazione di eventuali sottocampi (campo di tipo gruppo)
			if (fields != null && fields.size() > 0) {
				for (int i=0; i<fields.size(); i++) {
					if (fields.get(i).showFieldValue(context))
						valueFound = true;
				}
			}
		}
		
		return valueFound;
	}
	
	/**
	 * applicazione dell'xslt 'xslOut' per la gestione di visualizzazioni personalizzate
	 * 
	 * @param context elemento XML sul quale applicare l'XSL
	 * @param xslt modello XSL da applicare al documento per la formattazione dell'output
	 * @return
	 * @throws Exception
	 */
	private String getHtmlFieldValue(Element context, String xslt) throws Exception {
		int index = xslt.indexOf("<xslOut>");
		xslt = xslt.substring(index+8);
		
		// TODO da verificare... dovrebbe svolgere la stessa attivita'
		xslt = StringUtil.replace(xslt, "</xslOut>", "");
		//xslt = XmlReplacer.replaceString(xslt, "</xslOut>", "");
		
		String stylesheet = "<?xml version=\"1.0\" encoding=\"" + FormAdapter.ENCODING_UFT_8 + "\"?>" +
								"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">" +
									"<xsl:output method=\"html\" encoding=\"UTF-8\"/>" +
									"<xsl:template match=\"/\">" +
										xslt +
									"</xsl:template>" +
									"<xsl:template name=\"dateformatter\">" +
										"<xsl:param name=\"datas\" select=\"''\" />" +
										"<xsl:value-of select=\"substring($datas,7,2)\" />" +
										"<xsl:text>/</xsl:text>" +
										"<xsl:value-of select=\"substring($datas,5,2)\" />" +
										"<xsl:text>/</xsl:text>" +
										"<xsl:value-of select=\"substring($datas,1,4)\" />" +
									"</xsl:template>" +
								"</xsl:stylesheet>";

		StringWriter writer = new StringWriter();
		Result htmlResult = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer engine = tf.newTransformer(new StreamSource(new StringReader(stylesheet)));
		engine.transform(new StreamSource(new StringReader(context.asXML())), htmlResult);
		String html = new String(writer.getBuffer());
		
		return html.trim();
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

}
