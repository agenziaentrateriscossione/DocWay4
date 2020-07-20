package it.tredi.dw4.docway.model.fatturepa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiBeniServizi extends XmlEntity {

	private List<LineaBeniServizi> lineaBeniServizi = new ArrayList<LineaBeniServizi>();
	private List<RiepilogoBeniServizi> riepilogoBeniServizi = new ArrayList<RiepilogoBeniServizi>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.lineaBeniServizi 		= XMLUtil.parseSetOfElement(dom, "datiBeniServizi/linea", new LineaBeniServizi());
		this.riepilogoBeniServizi 	= XMLUtil.parseSetOfElement(dom, "datiBeniServizi/riepilogo", new RiepilogoBeniServizi());
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	for (int i = 0; i < lineaBeniServizi.size(); i++) {
    		LineaBeniServizi dati = (LineaBeniServizi) lineaBeniServizi.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".linea["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < riepilogoBeniServizi.size(); i++) {
    		RiepilogoBeniServizi dati = (RiepilogoBeniServizi) riepilogoBeniServizi.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".riepilogo["+String.valueOf(i)+"]"));
		}
    	
    	return params;
	}
	
	public List<LineaBeniServizi> getLineaBeniServizi() {
		return lineaBeniServizi;
	}

	public void setLineaBeniServizi(List<LineaBeniServizi> lineaBeniServizi) {
		this.lineaBeniServizi = lineaBeniServizi;
	}

	public List<RiepilogoBeniServizi> getRiepilogoBeniServizi() {
		return riepilogoBeniServizi;
	}

	public void setRiepilogoBeniServizi(List<RiepilogoBeniServizi> riepilogoBeniServizi) {
		this.riepilogoBeniServizi = riepilogoBeniServizi;
	}
	
	/**
	 * aggiunta di una linea alla fattura
	 */
	public String addLineaBeniServizi() {
		LineaBeniServizi linea = (LineaBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("linea");
		int index = 0;
		if (linea != null)
			index = lineaBeniServizi.indexOf(linea);
		
		if (lineaBeniServizi != null) {
			if (lineaBeniServizi.size() > index)
				lineaBeniServizi.add(index+1,  new LineaBeniServizi());
			else
				lineaBeniServizi.add(new LineaBeniServizi());
		}
		return null;
	}
	
	/**
	 * eliminazione di una linea dalla fattura
	 */
	public String deleteLineaBeniServizi() {
		LineaBeniServizi linea = (LineaBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("linea");
		if (linea != null) {
			lineaBeniServizi.remove(linea);
			if (lineaBeniServizi.isEmpty()) 
				lineaBeniServizi.add(new LineaBeniServizi());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di una linea della fattura
	 */
	public String moveUpLineaBeniServizi() {
		LineaBeniServizi linea = (LineaBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("linea");
		if (linea != null && lineaBeniServizi != null) {
			int index = lineaBeniServizi.indexOf(linea);
			if (index > 0 ) {
				lineaBeniServizi.remove(index);
				lineaBeniServizi.add(index-1, linea);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di una linea della fattura
	 */
	public String moveDownLineaBeniServizi() {
		LineaBeniServizi linea = (LineaBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("linea");
		if (linea != null && lineaBeniServizi != null) {
			int index = lineaBeniServizi.indexOf(linea);
			if (index < lineaBeniServizi.size()-1 ) {
				lineaBeniServizi.remove(index);
				lineaBeniServizi.add(index+1, linea);
			}
		}
		return null;
	}
	
	/**
	 * aggiunta di un riepilogo alla fattura
	 */
	public String addRiepilogoBeniServizi() {
		RiepilogoBeniServizi riepilogo = (RiepilogoBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("riepilogo");
		int index = 0;
		if (riepilogo != null)
			index = riepilogoBeniServizi.indexOf(riepilogo);
		
		if (riepilogoBeniServizi != null) {
			if (riepilogoBeniServizi.size() > index)
				riepilogoBeniServizi.add(index+1,  new RiepilogoBeniServizi());
			else
				riepilogoBeniServizi.add(new RiepilogoBeniServizi());
		}
		return null;
	}
	
	/**
	 * eliminazione di un riepilogo dalla fattura
	 */
	public String deleteRiepilogoBeniServizi() {
		RiepilogoBeniServizi riepilogo = (RiepilogoBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("riepilogo");
		if (riepilogo != null) {
			riepilogoBeniServizi.remove(riepilogo);
			if (riepilogoBeniServizi.isEmpty()) 
				riepilogoBeniServizi.add(new RiepilogoBeniServizi());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di un riepilogo della fattura
	 */
	public String moveUpRiepilogoBeniServizi() {
		RiepilogoBeniServizi riepilogo = (RiepilogoBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("riepilogo");
		if (riepilogo != null && riepilogoBeniServizi != null) {
			int index = riepilogoBeniServizi.indexOf(riepilogo);
			if (index > 0 ) {
				riepilogoBeniServizi.remove(index);
				riepilogoBeniServizi.add(index-1, riepilogo);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di un riepilogo della fattura
	 */
	public String moveDownRiepilogoBeniServizi() {
		RiepilogoBeniServizi riepilogo = (RiepilogoBeniServizi) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("riepilogo");
		if (riepilogo != null && riepilogoBeniServizi != null) {
			int index = riepilogoBeniServizi.indexOf(riepilogo);
			if (index < riepilogoBeniServizi.size()-1 ) {
				riepilogoBeniServizi.remove(index);
				riepilogoBeniServizi.add(index+1, riepilogo);
			}
		}
		return null;
	}

}
