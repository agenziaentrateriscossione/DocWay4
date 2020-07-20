package it.tredi.dw4.docway.model.webmail;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;
import it.tredi.utils.string.Text;

import java.util.Map;

import org.dom4j.Document;

public class Recipient extends XmlEntity {

	private String address = "";
	private String name = "";
	
	@Override
	public XmlEntity init(Document dom) {
		// TODO da verificare, deriva dalla riga 160 di XMLDocumento (xmlString = xmlString.replaceAll("&#", "&amp;#");)
		this.name 		= Text.htmlToText(XMLUtil.parseAttribute(dom, "./@name"));
		this.address 	= Text.htmlToText(XMLUtil.parseAttribute(dom, "./@address"));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO probabilmente questo metodo deve essere implementato per la funzione di trasformazione mail in documento di docway
		return null;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
