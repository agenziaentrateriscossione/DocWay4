package it.tredi.dw4.model.azionimassive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Definzione delle azioni massive (tramite stored procedure LUA) disponibili su una determinata lista titoli o pagina di visualizzazione
 * di uno specifico documento
 * @author mbernardini
 */
public class AzioniMassive extends XmlEntity {

	private String db;
	private List<Azione> azioni = new ArrayList<Azione>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.db = XMLUtil.parseStrictAttribute(dom, "//azioni_massive/@db");
		this.azioni = XMLUtil.parseSetOfElement(dom, "//azioni_massive/azione", new Azione());
		
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}
	
	public List<Azione> getAzioni() {
		return azioni;
	}

	public void setAzioni(List<Azione> azioni) {
		this.azioni = azioni;
	}

}
