package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class Fascicolo_speciale extends XmlEntity {
	
	// campi specifici del fascicolo "personale"
	private String matricola = "";
	private String codice_fiscale = "";
	private String categoria = "";
	private String categoria_tipo = "";
	private String data_nascita = "";
	private String luogo_nascita = "";
	private String data_assunzione = "";
	private String data_cessazione = "";
	private String data_immatricolazione = "";
	
	@Override
	public XmlEntity init(Document dom) {
		matricola 		= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@matricola", "");
		codice_fiscale 	= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@cod_fis", "");
		categoria 		= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@categoria", "");
		categoria_tipo 	= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@categoria_tipo", "");
		data_nascita 	= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@data_nascita", "");
		luogo_nascita 	= XMLUtil.parseAttribute(dom, "fascicolo_speciale/@luogo_nascita", "");
		data_assunzione = XMLUtil.parseAttribute(dom, "fascicolo_speciale/@data_assunzione", "");
		data_cessazione = XMLUtil.parseAttribute(dom, "fascicolo_speciale/@data_cessazione", "");
		data_immatricolazione = XMLUtil.parseAttribute(dom, "fascicolo_speciale/@data_immatricolazione", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (matricola != null && matricola.length() > 0)
    		params.put(prefix+".@matricola", matricola);
    	if (codice_fiscale != null && codice_fiscale.length() > 0)
    		params.put(prefix+".@cod_fis", codice_fiscale);
    	if (categoria != null && categoria.length() > 0)
    		params.put(prefix+".@categoria", categoria);
    	if (categoria_tipo != null && categoria_tipo.length() > 0)
    		params.put(prefix+".@categoria_tipo", categoria_tipo);
    	if (data_nascita != null && data_nascita.length() > 0)
    		params.put(prefix+".@data_nascita", data_nascita);
    	if (luogo_nascita != null && luogo_nascita.length() > 0)
    		params.put(prefix+".@luogo_nascita", luogo_nascita);
    	if (data_assunzione != null && data_assunzione.length() > 0)
    		params.put(prefix+".@data_assunzione", data_assunzione);
    	if (data_cessazione != null && data_cessazione.length() > 0)
    		params.put(prefix+".@data_cessazione", data_cessazione);
    	if (data_immatricolazione != null && data_immatricolazione.length() > 0)
    		params.put(prefix+".@data_immatricolazione", data_immatricolazione);
    	
    	return params;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public String getCategoria_tipo() {
		return categoria_tipo;
	}

	public void setCategoria_tipo(String categoria_tipo) {
		this.categoria_tipo = categoria_tipo;
	}

	public String getData_nascita() {
		return data_nascita;
	}

	public void setData_nascita(String data_nascita) {
		this.data_nascita = data_nascita;
	}

	public String getLuogo_nascita() {
		return luogo_nascita;
	}

	public void setLuogo_nascita(String luogo_nascita) {
		this.luogo_nascita = luogo_nascita;
	}

	public String getData_assunzione() {
		return data_assunzione;
	}

	public void setData_assunzione(String data_assunzione) {
		this.data_assunzione = data_assunzione;
	}

	public String getData_cessazione() {
		return data_cessazione;
	}

	public void setData_cessazione(String data_cessazione) {
		this.data_cessazione = data_cessazione;
	}
	
	public String getData_immatricolazione() {
		return data_immatricolazione;
	}

	public void setData_immatricolazione(String data_immatricolazione) {
		this.data_immatricolazione = data_immatricolazione;
	}
	
}
