package it.tredi.dw4.docway.model.fatturepa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiFattura extends XmlEntity {

	// campi extra relativi ad una singola fattura contenuta in una fattura elettronica fatturaPA
	private DatiGeneraliDocumento datiGeneraliDocumento = new DatiGeneraliDocumento();
	private List<DatiOrdineAcquisto> datiOrdineAcquisto = new ArrayList<DatiOrdineAcquisto>();
	private List<DatiContratto> datiContratto = new ArrayList<DatiContratto>();
	private List<DatiConvenzione> datiConvenzione = new ArrayList<DatiConvenzione>();
	private List<DatiRicezione> datiRicezione = new ArrayList<DatiRicezione>();
	private List<DatiFattureCollegate> datiFattureCollegate = new ArrayList<DatiFattureCollegate>();
	private List<DatiSAL> datiSAL = new ArrayList<DatiSAL>();
	private List<DatiDDT> datiDDT = new ArrayList<DatiDDT>();
	private DatiBeniServizi datiBeniServizi = new DatiBeniServizi();
	private List<DatiPagamento> datiPagamento = new ArrayList<DatiPagamento>();
	
	private DatiRegistroFatture datiRegistroFatture = new DatiRegistroFatture();
	
	private boolean showDettagli = false;
	private boolean showRiferimenti = false;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.datiGeneraliDocumento.init(XMLUtil.createDocument(dom, "datiFattura/datiGeneraliDocumento"));
		this.datiOrdineAcquisto = XMLUtil.parseSetOfElement(dom, "datiFattura/datiOrdineAcquisto", new DatiOrdineAcquisto());
		this.datiContratto = XMLUtil.parseSetOfElement(dom, "datiFattura/datiContratto", new DatiContratto());
		this.datiConvenzione = XMLUtil.parseSetOfElement(dom, "datiFattura/datiConvenzione", new DatiConvenzione());
		this.datiRicezione = XMLUtil.parseSetOfElement(dom, "datiFattura/datiRicezione", new DatiRicezione());
		this.datiFattureCollegate = XMLUtil.parseSetOfElement(dom, "datiFattura/datiFattureCollegate", new DatiFattureCollegate());
		this.datiSAL = XMLUtil.parseSetOfElement(dom, "datiFattura/datiSAL", new DatiSAL());
		this.datiDDT = XMLUtil.parseSetOfElement(dom, "datiFattura/datiDDT", new DatiDDT());
		this.datiBeniServizi.init(XMLUtil.createDocument(dom, "datiFattura/datiBeniServizi"));
		this.datiPagamento = XMLUtil.parseSetOfElement(dom, "datiFattura/datiPagamento", new DatiPagamento());
		
		this.datiRegistroFatture.init(XMLUtil.createDocument(dom, "datiFattura/datiRegistroFatture"));
		
		// utilizzati per mostrare/nascondere i dettagli della fattura in visualizzazione
		this.showDettagli = false;
		this.showRiferimenti = false;
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.putAll(datiGeneraliDocumento.asFormAdapterParams(prefix+".datiGeneraliDocumento"));
    	
    	for (int i = 0; i < datiOrdineAcquisto.size(); i++) {
    		DatiOrdineAcquisto dati = (DatiOrdineAcquisto) datiOrdineAcquisto.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiOrdineAcquisto["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiContratto.size(); i++) {
    		DatiContratto dati = (DatiContratto) datiContratto.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiContratto["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiConvenzione.size(); i++) {
    		DatiConvenzione dati = (DatiConvenzione) datiConvenzione.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiConvenzione["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiRicezione.size(); i++) {
    		DatiRicezione dati = (DatiRicezione) datiRicezione.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiRicezione["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiFattureCollegate.size(); i++) {
    		DatiFattureCollegate dati = (DatiFattureCollegate) datiFattureCollegate.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiFattureCollegate["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiSAL.size(); i++) {
    		DatiSAL dati = (DatiSAL) datiSAL.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiSAL["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < datiDDT.size(); i++) {
    		DatiDDT dati = (DatiDDT) datiDDT.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiDDT["+String.valueOf(i)+"]"));
		}
    	params.putAll(datiBeniServizi.asFormAdapterParams(prefix+".datiBeniServizi"));
    	for (int i = 0; i < datiPagamento.size(); i++) {
    		DatiPagamento dati = (DatiPagamento) datiPagamento.get(i);
    		params.putAll(dati.asFormAdapterParams(prefix+".datiPagamento["+String.valueOf(i)+"]"));
		}
    	
    	params.putAll(datiRegistroFatture.asFormAdapterParams(prefix+".datiRegistroFatture"));
    	
    	return params;
	}
	
	public DatiGeneraliDocumento getDatiGeneraliDocumento() {
		return datiGeneraliDocumento;
	}

	public void setDatiGeneraliDocumento(DatiGeneraliDocumento datiGeneraliDocumento) {
		this.datiGeneraliDocumento = datiGeneraliDocumento;
	}

	public List<DatiOrdineAcquisto> getDatiOrdineAcquisto() {
		return datiOrdineAcquisto;
	}

	public void setDatiOrdineAcquisto(List<DatiOrdineAcquisto> datiOrdineAcquisto) {
		this.datiOrdineAcquisto = datiOrdineAcquisto;
	}

	public List<DatiContratto> getDatiContratto() {
		return datiContratto;
	}

	public void setDatiContratto(List<DatiContratto> datiContratto) {
		this.datiContratto = datiContratto;
	}

	public List<DatiConvenzione> getDatiConvenzione() {
		return datiConvenzione;
	}

	public void setDatiConvenzione(List<DatiConvenzione> datiConvenzione) {
		this.datiConvenzione = datiConvenzione;
	}

	public List<DatiRicezione> getDatiRicezione() {
		return datiRicezione;
	}

	public void setDatiRicezione(List<DatiRicezione> datiRicezione) {
		this.datiRicezione = datiRicezione;
	}

	public List<DatiFattureCollegate> getDatiFattureCollegate() {
		return datiFattureCollegate;
	}

	public void setDatiFattureCollegate(
			List<DatiFattureCollegate> datiFattureCollegate) {
		this.datiFattureCollegate = datiFattureCollegate;
	}

	public List<DatiSAL> getDatiSAL() {
		return datiSAL;
	}

	public void setDatiSAL(List<DatiSAL> datiSAL) {
		this.datiSAL = datiSAL;
	}

	public List<DatiDDT> getDatiDDT() {
		return datiDDT;
	}

	public void setDatiDDT(List<DatiDDT> datiDDT) {
		this.datiDDT = datiDDT;
	}

	public DatiBeniServizi getDatiBeniServizi() {
		return datiBeniServizi;
	}

	public void setDatiBeniServizi(DatiBeniServizi datiBeniServizi) {
		this.datiBeniServizi = datiBeniServizi;
	}
	
	public boolean isShowDettagli() {
		return showDettagli;
	}

	public void setShowDettagli(boolean showDettagli) {
		this.showDettagli = showDettagli;
	}

	public boolean isShowRiferimenti() {
		return showRiferimenti;
	}

	public void setShowRiferimenti(boolean showRiferimenti) {
		this.showRiferimenti = showRiferimenti;
	}
	
	public List<DatiPagamento> getDatiPagamento() {
		return datiPagamento;
	}

	public void setDatiPagamento(List<DatiPagamento> datiPagamento) {
		this.datiPagamento = datiPagamento;
	}
		
	public DatiRegistroFatture getDatiRegistroFatture() {
		return datiRegistroFatture;
	}

	public void setDatiRegistroFatture(DatiRegistroFatture datiRegistroFatture) {
		this.datiRegistroFatture = datiRegistroFatture;
	}
	
}
