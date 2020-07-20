/**
 * Creata il 19/06/2012
 */
package it.tredi.dw4.utils;

import it.tredi.dw4.model.XmlEntity;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author czappavigna
 *
 */
public class XMLUtil {
	protected static Logger log = Logger.getRootLogger();
    
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * N.B.: All'xPath specificato viene anteposto '//'
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseElement(Document dom, String xpath) {
        return parseElement(dom, xpath, true);
    }
	
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseStrictElement(Document dom, String xpath) {
        return parseStrictElement(dom, xpath, true);
    }
	
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * N.B.: All'xPath specificato viene anteposto '//'
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @param textTrim true se occorre eseguire il trim del testo contenuto, false altrimenti (caso di testo con ritorni a capo o spaziature - es. textarea)
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseElement(Document dom, String xpath, boolean textTrim) {
		String retValue = "";
		if (dom != null && xpath != null && !xpath.equals(""))
			retValue = parseStrictElement(dom, "//" + xpath, textTrim);
		
		return retValue;
    }
	
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @param textTrim true se occorre eseguire il trim del testo contenuto, false altrimenti (caso di testo con ritorni a capo o spaziature - es. textarea)
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseStrictElement(Document dom, String xpath, boolean textTrim) {
        String retValue = "";
        try {
        	if (dom != null && xpath != null) {
        		List<?> nodes = dom.selectNodes(xpath);
        		if (nodes != null && nodes.size() > 0) {
		            Element element = (Element) nodes.get(0);
		            if ( null != element ) {
		            	if (textTrim)
		            		retValue = element.getTextTrim();
		            	else
		            		retValue = element.getText();
		            }
        		}
        	}
        }
        catch(Exception e) {
            log.error(e, e);
        }
        return retValue;
    }
	
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseElementNode(Document dom, String xpath) {
        return parseElementNode(dom, xpath, false);
    }
	
	/**
	 * Estrae il testo contenuto in un elemento del DOM. In caso di errore o di elemento
	 * non trovato viene restituita la stringa vuota.
	 * 
	 * @param dom oggetto DOM dal quale recuperare il testo dell'elemento
	 * @param xpath xPath attraverso il quale identificare l'elemento 
	 * @param textTrim true se occorre eseguire il trim del testo contenuto, false altrimenti (caso di testo con ritorni a capo o spaziature - es. textarea)
	 * @return contenuto dell'elemento specificato. Stringa vuota in caso di errore.
	 */
	public static String parseElementNode(Document dom, String xpath, boolean textTrim) {
        String retValue = "";
        try {
        	if (dom != null && xpath != null) {
        		List<?> nodes = dom.selectNodes(xpath);
        		if (nodes != null && nodes.size() > 0) {
		            Element element = (Element) nodes.get(0);
		            if ( null != element ) {
		            	if (textTrim)
		            		retValue = element.getTextTrim();
		            	else
		            		retValue = element.getText();
		            }
        		}
        	}
        }
        catch(Exception e) {
            log.error(e, e);
        }
        return retValue;
    }
	
	/**
	 * Conta le occorrenze di elementi trovati sul documento in base all'xpath
	 * specificato
	 * 
	 * @param dom oggetto DOM sul quale effettuare la ricerca
	 * @param xpath xPath da applicare
	 * @return numero di elementi restituiti in base all'xPath specificato
	 */
	public static int countElements(Document dom, String xpath) {
		int count = 0;
		if (dom != null && xpath != null && xpath.length() > 0) {
			try {
				List<?> nodes = dom.selectNodes(xpath);
				if (nodes != null)
					count = nodes.size();
			}
			catch (Exception e) {
				log.error(e, e);
			}
		}
		return count;
	}

    public static Element loadElement(Document dom, String xpath) {
        try {
        	if (dom != null && xpath != null) {
        		List<?> nodes = dom.selectNodes(xpath);
        		if (nodes != null && nodes.size() > 0) {
		            Element element = (Element) nodes.get(0);
		            return element;
        		}
        	}
        }
        catch(Exception e) {
            log.error(e, e);
        }
        return null;
    }
    
    public static Document createDocument(Document dom, String xpath) {
        try {
        	if (dom != null && xpath != null) {
        		List<?> nodes = dom.selectNodes(xpath);
        		if (nodes != null && nodes.size() > 0) {
		            Element element = (Element) nodes.get(0);
		            Document doc = DocumentHelper.createDocument();
		            if (null != element) doc.setRootElement(element.createCopy());
		            return doc;
        		}
        	}
        }
        catch(Exception e) {
            log.error(e, e);
        }
        return null;
    }
    
    public static String parseAttribute(Document dom, String xpath) {
    	String retValue = "";
    	if (dom != null && xpath != null && !xpath.equals(""))
    		retValue = parseStrictAttribute(dom, "//"+xpath);
        
    	return retValue;
    }
    
    public static String parseStrictAttribute(Document dom, String xpath) {
        if (null==dom || dom.getRootElement() == null) return "";
    	String retValue = "";
    	List<?> nodes = dom.selectNodes(xpath);
		if (nodes != null && nodes.size() > 0) {
	        Node node = (Node) nodes.get(0);
	        if ( (null == node) || (null == (retValue=node.getText())) || (retValue=retValue.trim()).equals("") ) { 
	            retValue = "";
	        }
		}
        return retValue;
    }
    
    public static String parseAttribute(Document dom, String xpath, String defaultValue) {
        if (null==dom || dom.getRootElement() == null) return defaultValue;
    	String retValue = defaultValue;
    	List<?> nodes = dom.selectNodes("//"+xpath);
		if (nodes != null && nodes.size() > 0) {
	        Node node = (Node) nodes.get(0);
	        if ( (null == node) || (null == (retValue=node.getText())) || (retValue=retValue.trim()).equals("") ) { 
	            retValue = defaultValue;
	        }
		}
        return retValue;
    }

    public static String parseStrictAttribute(Document dom, String xpath, String defaultValue) {
        if (null==dom || dom.getRootElement() == null) return defaultValue;
    	String retValue = defaultValue;
    	List<?> nodes = dom.selectNodes(xpath);
		if (nodes != null && nodes.size() > 0) {
	        Node node = (Node) nodes.get(0);
	        if ( (null == node) || (null == (retValue=node.getText())) || (retValue=retValue.trim()).equals("") ) { 
	            retValue = defaultValue;
	        }
		}
        return retValue;
    }

    
    static public org.dom4j.Document getDOM(String xml) {
        org.dom4j.Document retValue = null;
        try {
            SAXReader saxReader = new SAXReader();
            //saxReader.setValidation(true);
            retValue = saxReader.read(new StringReader(xml));
        }
        catch(Exception e) { log.error(e,e); }
        return retValue;
    } 
    
	public static XmlEntity parseElement(Document dom, String xpath, XmlEntity entity) {
		XmlEntity retValue = null;
		if (null == dom)
			return retValue;
		try {
			List<?> list = dom.selectNodes(xpath);
			if ((null != list) && (list.size() > 0)) {
				Element element = (Element) list.get(0);
				entity = entity.getClass().newInstance();
				retValue = entity.init(DocumentHelper.createDocument(element.createCopy()));
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		return retValue;
	}
    
	@SuppressWarnings("rawtypes")
	public static List parseSetOfElement(Document dom, String xpath, XmlEntity entity) {
    	List<XmlEntity> retValue = new ArrayList<XmlEntity>();
        if (null == dom) return retValue;
    	try {
            List<?> list = dom.selectNodes(xpath); 
            if ( (null != list) && (list.size() > 0) ) {
                for (int index = 0; index < list.size(); index++) {
                	if (list.get(index) != null && list.get(index) instanceof Element) { // TODO da verificare, ma dovrebbe essere corretto
	                    Element element = (Element)list.get(index);
	                    entity = entity.getClass().newInstance();
	                    retValue.add(entity.init(DocumentHelper.createDocument(element.createCopy())));
                	}
                }
            }
        }
        catch(Exception e) {
            log.error(e, e);
        }
        return retValue;
    }
    
    public static Date parseAttributeDate(Document dom, String xpath) {
        String norm = XMLUtil.parseAttribute(dom, xpath);
        return DateUtil.getDateByString(norm);
    }
    
    /*
     * conversione di una stringa (che rappresenta un XML) in DOM
     * @param xml XML in formato stringa da convertire
     * @return oggetto DOM risultante dalla conversione
     */
    
    
    
    public static Document stringToDoc(String xml) {
    	// TODO da completare
    	return null;
    }

}
