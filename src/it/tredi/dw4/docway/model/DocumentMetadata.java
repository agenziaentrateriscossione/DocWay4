package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;

public class DocumentMetadata extends XmlEntity {

	private HashMap<String, String> dati = new HashMap<String, String>();
	
	public DocumentMetadata() {}
    
	public DocumentMetadata(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		if (dom != null) {
			List<Node> metaNodes = dom.selectNodes("node()[name()='documentMetadata']/*");
			if (metaNodes != null && metaNodes.size() > 0) {
				for (int i=0; i<metaNodes.size(); i++) {
					Node metaNode = (Node) metaNodes.get(i);
					if (metaNode != null && metaNode.getNodeType() == Node.ELEMENT_NODE && !metaNode.getName().equals("documentMetadata")) {
						this.dati.put(metaNode.getName().toLowerCase(), metaNode.getText());
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
	
	/**
	 * Ritorna le chiavi dei metadati recuperati
	 */
	public List<String> getDatiKeys() {
		List<String> keys = new ArrayList<String>();
        keys.addAll(getDati().keySet());
        return keys;
    }
	
	public HashMap<String, String> getDati() {
		return dati;
	}

	public void setDati(HashMap<String, String> dati) {
		this.dati = dati;
	}

}
