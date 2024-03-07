package it.tredi.dw4.model.customfields;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.model.customfields.specialized_fields.NumeroAnnoQueryField;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CustomQueryFields extends XmlEntity {

	private HashMap<String, List<QueryField>> querysections = new HashMap<String, List<QueryField>>();
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		List<?> pages = dom.selectNodes("response/customqueryfields/page");
		if (pages != null && pages.size() > 0) {
			for (int i=0; i<pages.size(); i++) {
				Element page = (Element) pages.get(i);
				if (page != null) { 
					String dbTable = page.attributeValue("dbTable");
					String codRep = page.attributeValue("codRep");
					if (dbTable != null && dbTable.length() > 0) {
						String sectionName = dbTable;
						if (codRep != null && codRep.length() > 0)
							sectionName = sectionName + "_" + codRep;
						
						Document doc = DocumentHelper.createDocument();
			            doc.setRootElement(page.createCopy());

			            // workaround volante per gestione custom campi specializzati
						QueryField queryField = new QueryField();
						List<Element> fields = doc.selectNodes("page/field");
						if (fields != null && !fields.isEmpty()) {
							for (Element field : fields) {
								String type = field.attributeValue("type");
								switch (type) {
									case "numero_anno":
										queryField = new NumeroAnnoQueryField();
										break;
								}
							}
						}
						List<QueryField> queryFields = XMLUtil.parseSetOfElement(doc, "page/field", queryField);
						if (queryFields != null && queryFields.size() > 0)
							querysections.put(sectionName, queryFields);
					}
				}
			}
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public HashMap<String, List<QueryField>> getQuerysections() {
		return querysections;
	}

	public void setQuerysections(HashMap<String, List<QueryField>> querySections) {
		this.querysections = querySections;
	}
	
	/**
	 * costruzione del filtro di ricerca per i campi custom
	 * 
	 * @param sectionName nome della sezione attivata (sezione per la quale eseguire la ricerca)
	 * @return porzione di query sui campi custom
	 */
	public String createQuery(String sectionName) {
		String query =  "";
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO andrebbe caricato da file di properties dell'applicazione
		
		// scorro tutti i campi custom definiti e costruisco la query di ricerca
		if (sectionName != null && sectionName.length() > 0 && querysections.containsKey(sectionName)) {
			List<QueryField> fields = querysections.get(sectionName);
			if (fields != null && fields.size() > 0) {
				for (int i=0; i<fields.size(); i++) {
					QueryField field = (QueryField) fields.get(i);
					if (field != null && field.getXpath() != null && field.getXpath().length() > 0) {
						
						if (field.getType().equals("calendar")) {
							
							// filtro su date
							String from = field.getValuefrom();
							String to = field.getValueto();
							
							if (from != null && from.length() > 0 && DateUtil.isValidDate(from, formatoData)) {
								String query1 = "";
								if (to != null && to.length() > 0 && DateUtil.isValidDate(to, formatoData))
									query1 = "{" + DateUtil.formatDate2XW(from, null) + "|" + DateUtil.formatDate2XW(to, null) + "}";
								else
									query1 = DateUtil.formatDate2XW(from, null);
								
								query += "([xml," + field.getXpath() + "]=" + query1 + ") AND ";
							}
							
						}
						else if (field.getType().equals("checkbox")) {
							
							// occorre controllare quali item sono stati selezionati
							// dall'utente
							if (field.getItems() != null && field.getItems().size() > 0) {
								String query1 = "";
								String path = field.getXpath();
								if (!path.endsWith("/"))
									path += "/";
								
				    			for (int j=0; j<field.getItems().size(); j++) {
					    			Item item = (Item) field.getItems().get(j);
					    			if (item != null && item.isSelected())
					    				query1 += "[xml," + path + "value]=\"" + item.getValue() + "\" OR ";
					    		}
				    			
				    			if (query1.endsWith(" OR "))
				    				query += "(" + query1.substring(0, query1.length()-3) + ") AND ";
				    		}
							
						}
						else if (field.getType().equals("radio") || field.getType().equals("select")) {
							
							// viene ricercato l'esatto valore espresso in value
							if (field.getValue() != null && field.getValue().length() > 0)
								query += "([xml," + field.getXpath() + "]=\"" + field.getValue() + "\") AND ";
							
						}
						else {
							
							// text, textarea
							if (field.getValue() != null && field.getValue().length() > 0)
								query += "([xml," + field.getXpath() + "]=" + field.getValue() + ") AND ";
							
						}
					}
				}
				
				if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
					query = query.substring(0, query.length()-4);
			}
		}
		
		return query;
	}
	
	/**
	 * pulizia di eventuali valori settati sui campi custom (reset del form di ricerca)
	 */
	public void cleanCustomFields() {
		if (querysections != null) {
			Iterator<Map.Entry<String, List<QueryField>>> iterator = querysections.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, List<QueryField>> section = (Map.Entry<String, List<QueryField>>) iterator.next();
				if (section != null && section.getValue() != null) {
					List<QueryField> fields = section.getValue();
					if (fields.size() > 0) {
						for (int i=0; i<fields.size(); i++) {
							if (fields.get(i) != null)
								fields.get(i).cleanValue();
						}
					}
				}
			}
		}
	}
	
}
