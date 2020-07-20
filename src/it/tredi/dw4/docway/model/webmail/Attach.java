package it.tredi.dw4.docway.model.webmail;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;
import it.tredi.utils.string.Text;

public class Attach extends XmlEntity {

	private String name = "";
	private long size = 0;
	private String cid = "";
	private String id = "";
	
	@Override
	public XmlEntity init(Document dom) {
		// TODO da verificare, deriva dalla riga 160 di XMLDocumento (xmlString = xmlString.replaceAll("&#", "&amp;#");)
		this.name 		= Text.htmlToText(XMLUtil.parseAttribute(dom, "attach/@name"));
		this.cid 		= XMLUtil.parseAttribute(dom, "attach/@cid");
		this.id 		= XMLUtil.parseAttribute(dom, "attach/@id");
		String strsize 	= XMLUtil.parseAttribute(dom, "attach/@size");
		if (strsize != null && !strsize.equals(""))
			size 		= new Long(strsize).longValue();
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
