package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.ValutaConversion;
import it.tredi.dw4.utils.XMLUtil;

public class RiepilogoBeniServizi extends XmlEntity {

	private String aliquotaIVA = "";
	private String imponibileImporto = "";
	private String imposta = "";
	private String natura = "";
	private String speseAccessorie = "";
	private String arrotondamento = "";
	private String eseguibilitaIVA = "";
	private String riferimentoNormativo = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.aliquotaIVA 			= XMLUtil.parseStrictAttribute(dom, "riepilogo/@aliquotaIVA");
		this.imponibileImporto 		= XMLUtil.parseStrictAttribute(dom, "riepilogo/@imponibileImporto");
		this.imposta 				= XMLUtil.parseStrictAttribute(dom, "riepilogo/@imposta");
		this.natura 				= XMLUtil.parseStrictElement(dom, "riepilogo/natura");
		this.speseAccessorie 		= XMLUtil.parseStrictAttribute(dom, "riepilogo/@speseAccessorie");
		this.arrotondamento 		= XMLUtil.parseStrictAttribute(dom, "riepilogo/@arrotondamento");
		this.eseguibilitaIVA 		= XMLUtil.parseStrictElement(dom, "riepilogo/eseguibilitaIVA");
		this.riferimentoNormativo 	= XMLUtil.parseStrictElement(dom, "riepilogo/riferimentoNormativo");
		
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.aliquotaIVA != null && this.aliquotaIVA.length() > 0)
    		params.put(prefix+".@aliquotaIVA", this.aliquotaIVA);
    	if (this.imponibileImporto != null && this.imponibileImporto.length() > 0)
    		params.put(prefix+".@imponibileImporto", this.imponibileImporto);
    	if (this.imposta != null && this.imposta.length() > 0)
    		params.put(prefix+".@imposta", this.imposta);
    	if (this.natura != null && this.natura.length() > 0)
    		params.put(prefix+".natura", this.natura);
    	if (this.speseAccessorie != null && this.speseAccessorie.length() > 0)
    		params.put(prefix+".@speseAccessorie", this.speseAccessorie);
    	if (this.arrotondamento != null && this.arrotondamento.length() > 0)
    		params.put(prefix+".@arrotondamento", this.arrotondamento);
    	if (this.eseguibilitaIVA != null && this.eseguibilitaIVA.length() > 0)
    		params.put(prefix+".eseguibilitaIVA", this.eseguibilitaIVA);
    	if (this.riferimentoNormativo != null && this.riferimentoNormativo.length() > 0)
    		params.put(prefix+".riferimentoNormativo", this.riferimentoNormativo);
    	
    	return params;
	}
	
	/**
	 * restituisce il totale del riepilogo
	 * @return
	 */
	public String getTotaleRiepilogo() {
		String totale = "";
		try {
			totale = ValutaConversion.formatImporto((Float.parseFloat(imponibileImporto) + Float.parseFloat(imposta)));
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			totale = imponibileImporto + " + " + imposta;
		}
		
		return totale;
	}
	
	public String getAliquotaIVA() {
		return aliquotaIVA;
	}

	public void setAliquotaIVA(String aliquotaIVA) {
		this.aliquotaIVA = aliquotaIVA;
	}

	public String getImponibileImporto() {
		return imponibileImporto;
	}

	public void setImponibileImporto(String imponibileImporto) {
		this.imponibileImporto = imponibileImporto;
	}

	public String getImposta() {
		return imposta;
	}

	public void setImposta(String imposta) {
		this.imposta = imposta;
	}
	
	public String getNatura() {
		return natura;
	}

	public void setNatura(String natura) {
		this.natura = natura;
	}

	public String getSpeseAccessorie() {
		return speseAccessorie;
	}

	public void setSpeseAccessorie(String speseAccessorie) {
		this.speseAccessorie = speseAccessorie;
	}

	public String getArrotondamento() {
		return arrotondamento;
	}

	public void setArrotondamento(String arrotondamento) {
		this.arrotondamento = arrotondamento;
	}

	public String getEseguibilitaIVA() {
		return eseguibilitaIVA;
	}

	public void setEseguibilitaIVA(String eseguibilitaIVA) {
		this.eseguibilitaIVA = eseguibilitaIVA;
	}

	public String getRiferimentoNormativo() {
		return riferimentoNormativo;
	}

	public void setRiferimentoNormativo(String riferimentoNormativo) {
		this.riferimentoNormativo = riferimentoNormativo;
	}

}
