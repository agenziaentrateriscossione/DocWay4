package it.tredi.dw4.docway.beans.folderTree;

import org.dom4j.Element;

import it.highwaytech.apps.code.CryptoUtils;
import it.tredi.dw4.docway.model.XwFile;

public class XwFileSimple extends XwFile {

	public XwFileSimple(Element elem) {
		initFromSimpleLastVersionElement(elem);
	}

	// ERM015806 
	// ottimizazione di parsing
	// elem deve essere la ultima versione!!!!
	// NON e adatto per uso docway perche qui xw:file e semplificato
	// 
	public XwFile initFromSimpleLastVersionElement(Element elem){
		String name = elem.attributeValue("name");
		// nome va criptato!!
		int pos = name.lastIndexOf(".");
		if (pos != -1) {
			name = CryptoUtils.encrypt(name.substring(0, pos)) + name.substring(pos);
		 }	      
		
		this.setName(name);
    	this.setDer_from(elem.attributeValue("der_from"));
    	this.setDer_to(elem.attributeValue("der_to"));
    	this.setTitle(elem.attributeValue("title"));
    	this.setIndex(elem.attributeValue("index"));
    	this.setForce_sign(elem.attributeValue("force_sign"));
    	this.setAgent_meta(elem.attributeValue("agent.meta"));
    	this.setAgent_pdf(elem.attributeValue("agent.pdf"));
    	this.setAgent_xml(elem.attributeValue("agent.xml"));
    	this.setSize(elem.attributeValue("size"));
    	
    	/* RIMOSSO PERCHE NON SERVE PER USO CHE SE NE FA
    	// der_from con ';' : conversione in pdf di vecchie scansioni fatte con IW con pagine in files separati
    	if (this.der_from != null && this.der_from.contains(";")) {
    		// il file convertito viene associato solo alla prima immagine
    		this.der_from = this.der_from.substring(0, this.der_from.indexOf(";"));
    	}
    	
    	
    	
    	// gestione libri firma
    	this.da_firmare =	StringUtil.booleanValue(elem.attributeValue("da_firmare"));
    	
    	// mbernardini 17/03/2015 : identificazione di allegati non modificabili (es. eml relativi a mail scaricate dall'archiviatore)
    	this.readonly =		StringUtil.booleanValue(elem.attributeValue("readonly"));
    	
    	// checkin / checkout del file corrente
    	this.chkin.init(DocumentHelper.createDocument((Element)elem.selectSingleNode("chkin")));
    	this.chkout.init(DocumentHelper.createDocument((Element)elem.selectSingleNode("chkout")));
    	
    	// metadati del file
    	this.metadata.init(DocumentHelper.createDocument((Element)elem.selectSingleNode("metadata")));
    	
    	String ckin = elem.attributeValue("chkin");
    	if (ckin != null && ckin.equals("true"))
    		this.checkinFile = true;
    	String ckout = elem.attributeValue("chkout");
    	if (ckout != null && ckout.equals("true"))
    		this.checkoutFile = true;
    	String used = elem.attributeValue("used");
    	if (used != null && used.equals("true"))
    		this.usedFile = true;
    	
    	this.impronta = 	elem.attributeValue("impronta");
    	this.tipoImpronta =	elem.attributeValue("tipoImpronta");
    	
    	// mbernardini 07/02/2017 : in caso di deleteImagesAfterPDF inibire l'invio telematico fino a conversione completata
    	this.agent_delete =	StringUtil.booleanValue(elem.attributeValue("agent.delete", "false"));
    	*/
    	
        return this;
	}

}
