package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Rif extends XmlEntity {
	private String cod_persona = "";
	private String cod_uff = "";
	private String nome_persona = "";
	private String nome_uff = "";
	private String cod_fasc = "";
	private String diritto = "";
	private String tipo_uff = "";
	private String cc_from_fasc = "";
	private String cc_from_racc = "";
	private String totale_uff = "";
	private String scartabile = "";
	private String scartato = "";
	private boolean intervento = false;
	private boolean ufficio_completo = false;
	private boolean personale = false;
	private boolean notifica_capillare = true; // ERM012596 - rtirabassi - notifica capillare

	public Rif() {}
	
	public Rif(boolean interventoDefault) {
		if (interventoDefault)
			this.intervento = interventoDefault;
	}

	public Rif(String xmlDiritto) {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }

	public Rif init(Document domDiritto) {
		return init(domDiritto, false);
	}

    public Rif init(Document domDiritto, boolean isMinuta) {
    	String nodeName = "rif";
    	if (isMinuta)
    		nodeName = "rif_minuta";

    	this.cod_persona = XMLUtil.parseAttribute(domDiritto, nodeName + "/@cod_persona");
    	this.cod_uff = XMLUtil.parseAttribute(domDiritto, nodeName + "/@cod_uff");
    	this.nome_persona = XMLUtil.parseAttribute(domDiritto, nodeName + "/@nome_persona");
    	this.nome_uff = XMLUtil.parseAttribute(domDiritto, nodeName + "/@nome_uff");
    	this.cod_fasc = XMLUtil.parseAttribute(domDiritto, nodeName + "/@cod_fasc");
    	this.diritto = XMLUtil.parseAttribute(domDiritto, nodeName + "/@diritto");
    	this.tipo_uff = XMLUtil.parseAttribute(domDiritto, nodeName + "/@tipo_uff");
    	this.cc_from_fasc = XMLUtil.parseAttribute(domDiritto, nodeName + "/@cc_from_fasc");
    	this.cc_from_racc = XMLUtil.parseAttribute(domDiritto, nodeName + "/@cc_from_racc");
    	this.totale_uff= XMLUtil.parseAttribute(domDiritto, nodeName + "/@totale_uff");
    	this.scartabile = XMLUtil.parseAttribute(domDiritto, nodeName + "/@scartabile");
    	this.scartato = XMLUtil.parseAttribute(domDiritto, nodeName + "/@scartato");

    	if (XMLUtil.parseAttribute(domDiritto, nodeName + "/@intervento").toLowerCase().equals("si"))
    		this.intervento = true;
    	if (this.nome_persona.equals("Tutti") && this.cod_persona.startsWith("tutti"))
    		this.ufficio_completo = true;

    	if (XMLUtil.parseAttribute(domDiritto, nodeName + "/@personale").equals("true"))
    		this.personale = true;
    	else
    		this.personale = false;

    	this.notifica_capillare = true;
    	return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod_persona", this.cod_persona);
    	params.put(prefix+".@nome_persona", this.nome_persona);
    	params.put(prefix+".@cod_uff", this.cod_uff);
    	params.put(prefix+".@nome_uff", this.nome_uff);
    	params.put(prefix+".@cod_fasc", this.cod_fasc);
    	params.put(prefix+".@tipo_uff", this.tipo_uff);
    	params.put(prefix+".@cc_from_fasc", this.cc_from_fasc);
    	params.put(prefix+".@cc_from_racc", this.cc_from_racc);
    	params.put(prefix+".@totale_uff", this.totale_uff);
    	params.put(prefix+".@diritto", this.diritto);
    	params.put(prefix+".@scartabile", this.scartabile);
    	params.put(prefix+".@scartato", this.scartato);
    	if (this.intervento)
    		params.put(prefix+".@intervento", "si");
    	else
    		params.put(prefix+".@intervento", "no");

    	return params;
    }

    private void clear() {
    	this.cod_persona = "";
    	this.cod_uff = "";
    	this.nome_persona = "";
    	this.nome_uff = "";
    	this.cod_fasc = "";
    	this.diritto = "";
    	this.tipo_uff = "";
    	this.cc_from_fasc = "";
    	this.cc_from_racc = "";
    	this.totale_uff= "";
    	this.scartabile = "";
    	this.scartato = "";
    	this.intervento = false;
    	this.ufficio_completo = false;
    	this.notifica_capillare = true;
    }

    public String getCod_persona() {
		return cod_persona;
	}

	public void setCod_persona(String cod) {
		this.cod_persona = cod;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setNome_persona(String nome_persona) {
		this.nome_persona = nome_persona;
	}

	public String getNome_persona() {
		return nome_persona;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setDiritto(String diritto) {
		this.diritto = diritto;
	}

	public String getDiritto() {
		return diritto;
	}

	public void setScartabile(String scartabile) {
		this.scartabile = scartabile;
	}

	public String getScartabile() {
		return scartabile;
	}

	public void setIntervento(boolean intervento) {
		this.intervento = intervento;
	}

	public boolean isIntervento() {
		return intervento;
	}

	public void setUfficio_completo(boolean ufficio_completo) {
		this.ufficio_completo = ufficio_completo;
	}

	public boolean isUfficio_completo() {
		return ufficio_completo;
	}

	public void setNotifica_capillare(boolean notifica_capillare) {
		this.notifica_capillare = notifica_capillare;
	}

	public boolean isNotifica_capillare() {
		return notifica_capillare;
	}

	public boolean isPersonale() {
		return personale;
	}

	public void setPersonale(boolean personale) {
		this.personale = personale;
	}

	public void setCod_fasc(String cod_fasc) {
		this.cod_fasc = cod_fasc;
	}

	public String getCod_fasc() {
		return cod_fasc;
	}

	public void setTipo_uff(String tipo_uff) {
		this.tipo_uff = tipo_uff;
	}

	public String getTipo_uff() {
		return tipo_uff;
	}

	public void setCc_from_fasc(String cc_from_fasc) {
		this.cc_from_fasc = cc_from_fasc;
	}

	public String getCc_from_fasc() {
		return cc_from_fasc;
	}

	public String getCc_from_racc() {
		return cc_from_racc;
	}

	public void setCc_from_racc(String cc_from_racc) {
		this.cc_from_racc = cc_from_racc;
	}

	public void setTotale_uff(String totale_uff) {
		this.totale_uff = totale_uff;
	}

	public String getTotale_uff() {
		return totale_uff;
	}

	/**
	 * Modifica della tipologia di rif (da ufficio/persona a ruolo o viceversa)
	 * @param vce
	 */
	public void tipoUffChange(ValueChangeEvent vce) {
		// Visto che e' stato modificata la tipologia di rif devo azzerare tutti
		// i dati dell'istanza
		this.clear();
		String tipo_uff = (String)vce.getNewValue();
		if (tipo_uff == null)
			tipo_uff = "";
		this.setTipo_uff(tipo_uff);
    }

	/**
	 * Imposta il tipo di rif int a Ruolo
	 */
	public String changeToRuolo() {
		this.clear();
		this.setTipo_uff("ruolo");
		return null;
    }

	/**
	 * Imposta il tipo di rif int a Ufficio/Persona
	 */
	public void changeToUfficio() {
		this.clear();
		this.setTipo_uff("");
    }

	/**
	 * Imposta o meno l'ufficio completo per il rif int corrente
	 */
	public void setUfficioCompleto() {
		if (ufficio_completo) {
			this.setNome_persona("Tutti");
			this.setCod_persona("");
		}
		else {
			this.setNome_persona("");
			this.setCod_persona("");
		}
	}

	public void setNotificaCapillare() {
		notifica_capillare = !notifica_capillare;
	}

	public void setScartato(String scartato) {
		this.scartato = scartato;
	}

	public String getScartato() {
		return scartato;
	}

	/**
	 * ritorna true se il rif corrente non e' settato, false altrimenti
	 * @return
	 */
	public boolean isEmpty() {
		if (this.nome_uff == null || this.nome_uff.equals("")) // TODO perche' in caso di ruoli il codice e' vuoto? andrebbe modificato il service?
			return true;
		else
			return false;
	}

}

