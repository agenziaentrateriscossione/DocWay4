package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLUtil;

public class RifPresaInCarico extends Rif {

	List<String> cod_ruoli_soddisfatti = new ArrayList<String>();
	List<String> cod_uffici_soddisfatti = new ArrayList<String>();
	List<String> nomi_ruoli_soddisfatti = new ArrayList<String>();
	List<String> nomi_uffici_soddisfatti = new ArrayList<String>();
	
	public List<String> getCod_ruoli_soddisfatti() {
		return cod_ruoli_soddisfatti;
	}

	public void setCod_ruoli_soddisfatti(List<String> cod_ruoli_soddisfatti) {
		this.cod_ruoli_soddisfatti = cod_ruoli_soddisfatti;
	}

	public List<String> getCod_uffici_soddisfatti() {
		return cod_uffici_soddisfatti;
	}

	public void setCod_uffici_soddisfatti(List<String> cod_uffici_soddisfatti) {
		this.cod_uffici_soddisfatti = cod_uffici_soddisfatti;
	}

	public List<String> getNomi_ruoli_soddisfatti() {
		return nomi_ruoli_soddisfatti;
	}

	public void setNomi_ruoli_soddisfatti(List<String> nomi_ruoli_soddisfatti) {
		this.nomi_ruoli_soddisfatti = nomi_ruoli_soddisfatti;
	}

	public List<String> getNomi_uffici_soddisfatti() {
		return nomi_uffici_soddisfatti;
	}

	public void setNomi_uffici_soddisfatti(List<String> nomi_uffici_soddisfatti) {
		this.nomi_uffici_soddisfatti = nomi_uffici_soddisfatti;
	}
	
	public RifPresaInCarico() {}

	public RifPresaInCarico(String xmlDiritto) {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }

	public RifPresaInCarico init(Document domDiritto) {
		return init(domDiritto, false);
	}
	
	public RifPresaInCarico init(Document domDiritto, boolean isMinuta) {
		super.init(domDiritto, isMinuta);
		String nodeName = "rif";
    	if (isMinuta)
    		nodeName = "rif_minuta";
		// gestione della parte relativa alla presa in carico
		this.cod_ruoli_soddisfatti = parseNameCodGroup(XMLUtil.parseAttribute(domDiritto, nodeName + "/@ruoli_soddisfatti"), 0);
    	this.cod_uffici_soddisfatti = parseNameCodGroup(XMLUtil.parseAttribute(domDiritto, nodeName + "/@uff_soddisfatti"), 0);
    	this.nomi_ruoli_soddisfatti = parseNameCodGroup(XMLUtil.parseAttribute(domDiritto, nodeName + "/@ruoli_soddisfatti"), 1);
    	this.nomi_uffici_soddisfatti = parseNameCodGroup(XMLUtil.parseAttribute(domDiritto, nodeName + "/@uff_soddisfatti"), 1);
		return this;
	}

	/**
	 * Estrae dalla string passata come parametro la lista dei nomi relativi agli uffici/ruoli soddifatti con la presa in carico
	 * VEDI MODALITÀ PRESA IN CARICO "GRUPPO"
	 * @param stingList - lista da parsare
	 * @param index - cosa prendere come risultato: 0 = codice, 1 = nome
	 * @return
	 */
	private List<String> parseNameCodGroup(String stringList, int index) {
		List<String> result = new ArrayList<String>();
		if (stringList != null && !stringList.isEmpty()) {
			List<String> arrayCodName = Arrays.asList(stringList.split("\\|"));
			for (String couple : arrayCodName) {
				if (!couple.isEmpty()) {
					String name = couple.trim().split("\\$\\#\\$")[index];
					result.add(name);
				}
			}
		}
		return result;
	}
	
	public String getUfficiRuoliSoddifatti() {
		String result = "";
		for (String nomeUff : nomi_uffici_soddisfatti) {
			result += nomeUff + ", ";
		}
		for (String nomeRuolo : nomi_ruoli_soddisfatti) {
			result += nomeRuolo + ", ";
		}
		if (!result.isEmpty()) {
			// rimuove l'ultima virgola
			int index = result.lastIndexOf(",");
			if (index > 0)
				result = result.substring(0, index);
		}
		return result;
	}
	
	
}
