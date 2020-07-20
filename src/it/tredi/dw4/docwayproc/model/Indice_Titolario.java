package it.tredi.dw4.docwayproc.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class Indice_Titolario extends XmlEntity {

	private String nrecord = "";
	private String voce = "";
	private String cod_amm_aoo = "";
	private Compilazione_Automatica compilazione_automatica = new Compilazione_Automatica();
	private List<Validita> validita = new ArrayList<Validita>();
	
	private String[] arrValidita = new String[4]; // utilizzato in inserimento per selezione delle tipologie di documento
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.nrecord = XMLUtil.parseStrictAttribute(dom, "/response/indice_titolario/@nrecord", ".");
		this.voce = XMLUtil.parseStrictAttribute(dom, "/response/indice_titolario/@voce", "");
		this.cod_amm_aoo = XMLUtil.parseStrictAttribute(dom, "/response/indice_titolario/@cod_amm_aoo", "");
		this.compilazione_automatica.init(XMLUtil.createDocument(dom, "/response/indice_titolario/compilazione_automatica"));
		this.validita = XMLUtil.parseSetOfElement(dom, "/response/indice_titolario/validita", new Validita());
		for (int i=0; i<this.validita.size(); i++) {
			Validita val = (Validita) this.validita.get(i);
			if (val != null && val.getTipodoc() != null && !val.getTipodoc().equals(""))
				this.arrValidita[i] = val.getTipodoc();
		}
		if (this.validita.size() == 0) this.validita.add(new Validita());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".@voce", this.voce);
    	params.put(prefix+".@cod_amm_aoo", this.cod_amm_aoo);    	
    	params.putAll(compilazione_automatica.asFormAdapterParams(prefix+".compilazione_automatica"));
    	for (int i = 0; i < arrValidita.length; i++) {
    		if (arrValidita[i] != null && !arrValidita[i].equals(""))
    			params.put(prefix+".validita[" + i + "].@tipodoc", arrValidita[i]);
		}
    	
    	return params;
	}
	
	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getVoce() {
		return voce;
	}

	public void setVoce(String voce) {
		this.voce = voce;
	}

	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}

	public Compilazione_Automatica getCompilazione_automatica() {
		return compilazione_automatica;
	}

	public void setCompilazione_automatica(
			Compilazione_Automatica compilazione_automatica) {
		this.compilazione_automatica = compilazione_automatica;
	}

	public List<Validita> getValidita() {
		return validita;
	}

	public void setValidita(List<Validita> validita) {
		this.validita = validita;
	}
	
	public String[] getArrValidita() {
		return arrValidita;
	}

	public void setArrValidita(String[] arrValidita) {
		this.arrValidita = arrValidita;
	}

}
