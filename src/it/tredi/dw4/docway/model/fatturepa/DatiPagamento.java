package it.tredi.dw4.docway.model.fatturepa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiPagamento extends XmlEntity {

	private String condizioniPagamento = "";
	private List<DettaglioPagamento> dettaglioPagamento = new ArrayList<DettaglioPagamento>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.condizioniPagamento = XMLUtil.parseStrictElement(dom, "datiPagamento/condizioniPagamento");
		this.dettaglioPagamento = XMLUtil.parseSetOfElement(dom, "datiPagamento/dettaglioPagamento", new DettaglioPagamento());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.condizioniPagamento != null && this.condizioniPagamento.length() > 0)
    		params.put(prefix+".condizioniPagamento", this.condizioniPagamento);
    	for (int i = 0; i < dettaglioPagamento.size(); i++) {
    		DettaglioPagamento dati = (DettaglioPagamento) dettaglioPagamento.get(i);
    		if (dati.getModalitaPagamento() != null && dati.getModalitaPagamento().length() > 0)
    			params.putAll(dati.asFormAdapterParams(prefix+".dettaglioPagamento["+String.valueOf(i)+"]"));
		}
    	
		return params;
	}
	
	/**
	 * aggiunta di un dettaglio di pagamento alla fattura
	 */
	public String addDettaglioPagamento() {
		DettaglioPagamento pagamento = (DettaglioPagamento) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("pagamento");
		int index = 0;
		if (pagamento != null)
			index = dettaglioPagamento.indexOf(pagamento);
		
		if (dettaglioPagamento != null) {
			if (dettaglioPagamento.size() > index)
				dettaglioPagamento.add(index+1,  new DettaglioPagamento());
			else
				dettaglioPagamento.add(new DettaglioPagamento());
		}
		return null;
	}
	
	/**
	 * eliminazione di un dettaglio di pagamento dalla fattura
	 */
	public String deleteDettaglioPagamento() {
		DettaglioPagamento pagamento = (DettaglioPagamento) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("pagamento");
		if (pagamento != null) {
			dettaglioPagamento.remove(pagamento);
			if (dettaglioPagamento.isEmpty()) 
				dettaglioPagamento.add(new DettaglioPagamento());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di un dettaglio di pagamento della fattura
	 */
	public String moveUpDettaglioPagamento() {
		DettaglioPagamento pagamento = (DettaglioPagamento) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("pagamento");
		if (pagamento != null && dettaglioPagamento != null) {
			int index = dettaglioPagamento.indexOf(pagamento);
			if (index > 0 ) {
				dettaglioPagamento.remove(index);
				dettaglioPagamento.add(index-1, pagamento);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di un dettaglio di pagamento della fattura
	 */
	public String moveDownDettaglioPagamento() {
		DettaglioPagamento pagamento = (DettaglioPagamento) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("pagamento");
		if (pagamento != null && dettaglioPagamento != null) {
			int index = dettaglioPagamento.indexOf(pagamento);
			if (index < dettaglioPagamento.size()-1 ) {
				dettaglioPagamento.remove(index);
				dettaglioPagamento.add(index+1, pagamento);
			}
		}
		return null;
	}
	
	public String getCondizioniPagamento() {
		return condizioniPagamento;
	}

	public void setCondizioniPagamento(String condizioniPagamento) {
		this.condizioniPagamento = condizioniPagamento;
	}

	public List<DettaglioPagamento> getDettaglioPagamento() {
		return dettaglioPagamento;
	}

	public void setDettaglioPagamento(List<DettaglioPagamento> dettaglioPagamento) {
		this.dettaglioPagamento = dettaglioPagamento;
	}

}
