package it.tredi.dw4.docway.model;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DestinatariDoc extends XmlEntity {
	
	private List<RifEsterno> rif_esterni;
	
	public DestinatariDoc() { }

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.setRif_esterni(XMLUtil.parseSetOfElement(dom, "/destinatari/rif", new RifEsterno()));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public List<RifEsterno> getRif_esterni() {
		return rif_esterni;
	}

	public void setRif_esterni(List<RifEsterno> rif_esterni) {
		this.rif_esterni = rif_esterni;
	}

}
