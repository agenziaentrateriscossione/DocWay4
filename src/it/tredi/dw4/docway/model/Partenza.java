package it.tredi.dw4.docway.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

public class Partenza extends Doc {

	private boolean hideDestinatari = false;

	@Override
	public XmlEntity init(Document dom) {
		super.init(dom, Const.DOCWAY_TIPOLOGIA_PARTENZA);

		// repertori partenza senza destinatari
		String codiceRep;
		if (nrecord.equals(".")) {
			// caso inserimento
			codiceRep = XMLUtil.parseStrictAttribute(dom, "/response/@codice_rep", "");
		} else {
			// caso modifica
			codiceRep = repertorio.getCod();
		}
		if (!codiceRep.isEmpty()) {
			List<String> repCodsWithoutDest = Arrays.asList(DocWayProperties.readProperty("repertoriPartenzaSenzaDestinatari", "").split(","));
			hideDestinatari = repCodsWithoutDest.stream().anyMatch(cod -> cod.equalsIgnoreCase(codiceRep));
		}

		return null;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, modify, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify,  boolean isRepertorio) {
		return this.asFormAdapterParams(prefix, modify, true, isRepertorio);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean modifyRifEsterni, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);
    	
    	// Invio dei parametri specifici del doc in partenza
    	if (!modify) {
    		params.put(prefix+".@tipo", Const.DOCWAY_TIPOLOGIA_PARTENZA);
    	}
    	
    	if (modifyRifEsterni) {
			for (int i = 0; i < rif_esterni.size(); i++) {
	    		RifEsterno rif = (RifEsterno) rif_esterni.get(i);
	    		params.putAll(rif.asFormAdapterParams(".rif_esterni.rif["+String.valueOf(i)+"]"));
			}
    	}
    	params.putAll(prot_differito.asFormAdapterParams(".prot_differito"));
    	
    	return params;
	}

	public boolean isHideDestinatari() {
		return hideDestinatari;
	}

	public void setHideDestinatari(boolean hideDestinatari) {
		this.hideDestinatari = hideDestinatari;
	}
}
