package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;

public class AttachFile {

	private String filename = "";
	private byte[] content = null;
	
	private XMLDocumento xmlDocumento = null;
	
	public AttachFile(XMLDocumento xmlDocumento) {
		this.setXmlDocumento(xmlDocumento);
	}
	
	public AttachFile(String filename, byte[] content) {
		this.filename = filename;
		this.content = content;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}

	public XMLDocumento getXmlDocumento() {
		return xmlDocumento;
	}

	public void setXmlDocumento(XMLDocumento xmlDocumento) {
		this.xmlDocumento = xmlDocumento;
	}

}
