package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class LineaBeniServizi extends XmlEntity {

	private String descrizione = "";
	private String tipoCessionePrestazione = "";
	private String quantita = "";
	private String unitaMisura = "";
	private String dataInizioPeriodo = "";
	private String dataFinePeriodo = "";
	private String prezzoUnitario = "";
	private String prezzoTotale = "";
	private String aliquotaIva = "";
	private String ritenuta = "";
	private String natura = "";
		
	@Override
	public XmlEntity init(Document dom) {
		this.descrizione 	= XMLUtil.parseStrictElement(dom, "linea");
		this.tipoCessionePrestazione 	= XMLUtil.parseStrictAttribute(dom, "linea/@tipoCessionePrestazione");
		this.quantita 	= XMLUtil.parseStrictAttribute(dom, "linea/@quantita");
		this.unitaMisura 	= XMLUtil.parseStrictAttribute(dom, "linea/@unitaMisura");
		this.dataInizioPeriodo 	= XMLUtil.parseStrictAttribute(dom, "linea/@dataInizioPeriodo");
		this.dataFinePeriodo 	= XMLUtil.parseStrictAttribute(dom, "linea/@dataFinePeriodo");
		this.prezzoUnitario 	= XMLUtil.parseStrictAttribute(dom, "linea/@prezzoUnitario");
		this.prezzoTotale 	= XMLUtil.parseStrictAttribute(dom, "linea/@prezzoTotale");
		this.aliquotaIva 	= XMLUtil.parseStrictAttribute(dom, "linea/@aliquotaIva");
		this.ritenuta 	= XMLUtil.parseStrictAttribute(dom, "linea/@ritenuta");
		this.natura 	= XMLUtil.parseStrictAttribute(dom, "linea/@natura");
		
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	String dateFormat = Const.DEFAULT_DATE_FORMAT; // TODO formato da caricare da file di properties
    	
    	if (this.descrizione != null && this.descrizione.length() > 0)
    		params.put(prefix+"", this.descrizione);
    	
    	if (this.tipoCessionePrestazione != null && this.tipoCessionePrestazione.length() > 0)
    		params.put(prefix+".@tipoCessionePrestazione", this.tipoCessionePrestazione);
    	if (this.quantita != null && this.quantita.length() > 0)
    		params.put(prefix+".@quantita", this.quantita);
    	if (this.unitaMisura != null && this.unitaMisura.length() > 0)
    		params.put(prefix+".@unitaMisura", this.unitaMisura);
    	if (this.dataInizioPeriodo != null && this.dataInizioPeriodo.length() > 0) {
	    	if (this.dataInizioPeriodo.length() == 10)
	    		params.put(prefix+".@dataInizioPeriodo", DateUtil.changeDateFormat(this.dataInizioPeriodo, dateFormat, "yyyyMMdd")); 
	    	else
	    		params.put(prefix+".@dataInizioPeriodo", this.dataInizioPeriodo);
    	}
    	if (this.dataFinePeriodo != null && this.dataFinePeriodo.length() > 0) {
	    	if (this.dataFinePeriodo.length() == 10)
	    		params.put(prefix+".@dataFinePeriodo", DateUtil.changeDateFormat(this.dataFinePeriodo, dateFormat, "yyyyMMdd"));
	    	else
	    		params.put(prefix+".@dataFinePeriodo", this.dataFinePeriodo);
    	}
    	if (this.prezzoUnitario != null && this.prezzoUnitario.length() > 0)
    		params.put(prefix+".@prezzoUnitario", this.prezzoUnitario);
    	if (this.prezzoTotale != null && this.prezzoTotale.length() > 0)
    		params.put(prefix+".@prezzoTotale", this.prezzoTotale);
    	if (this.aliquotaIva != null && this.aliquotaIva.length() > 0)
    		params.put(prefix+".@aliquotaIva", this.aliquotaIva);
    	if (this.ritenuta != null && this.ritenuta.length() > 0)
    		params.put(prefix+".@ritenuta", this.ritenuta);
    	if (this.natura != null && this.natura.length() > 0)
    		params.put(prefix+".@natura", this.natura);
    	
    	return params;
	}
	
	public String getPrezzoTotale() {
		return prezzoTotale;
	}

	public void setPrezzoTotale(String prezzoTotale) {
		this.prezzoTotale = prezzoTotale;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getTipoCessionePrestazione() {
		return tipoCessionePrestazione;
	}

	public void setTipoCessionePrestazione(String tipoCessionePrestazione) {
		this.tipoCessionePrestazione = tipoCessionePrestazione;
	}

	public String getQuantita() {
		return quantita;
	}

	public void setQuantita(String quantita) {
		this.quantita = quantita;
	}

	public String getUnitaMisura() {
		return unitaMisura;
	}

	public void setUnitaMisura(String unitaMisura) {
		this.unitaMisura = unitaMisura;
	}

	public String getDataInizioPeriodo() {
		return dataInizioPeriodo;
	}

	public void setDataInizioPeriodo(String dataInizioPeriodo) {
		this.dataInizioPeriodo = dataInizioPeriodo;
	}

	public String getDataFinePeriodo() {
		return dataFinePeriodo;
	}

	public void setDataFinePeriodo(String dataFinePeriodo) {
		this.dataFinePeriodo = dataFinePeriodo;
	}

	public String getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(String prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public String getAliquotaIva() {
		return aliquotaIva;
	}

	public void setAliquotaIva(String aliquotaIva) {
		this.aliquotaIva = aliquotaIva;
	}

	public String getRitenuta() {
		return ritenuta;
	}

	public void setRitenuta(String ritenuta) {
		this.ritenuta = ritenuta;
	}

	public String getNatura() {
		return natura;
	}

	public void setNatura(String natura) {
		this.natura = natura;
	}

}
