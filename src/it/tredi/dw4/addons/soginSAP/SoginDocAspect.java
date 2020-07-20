package it.tredi.dw4.addons.soginSAP;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.soginSAP.model.SAPDoc;

public class SoginDocAspect extends BaseAddOn {

	private SAPDoc doc;

	public SAPDoc getDoc() {
		return doc;
	}

	public void setDoc(SAPDoc doc) {
		this.doc = doc;
	}

	public SoginDocAspect(String template, Page host) {
		super(template, host);
	}

	@Override
	public void init(Document dom) {
		doc = new SAPDoc();
		doc.init(dom);
	}

	@Override
	public Map<String, String> asFormAdapterParams() {
		return null;
	}
	
	
	@Override
	public void clear() {
	}

	@Override
	public String asQuery() {
		return null;
	}
	
}
