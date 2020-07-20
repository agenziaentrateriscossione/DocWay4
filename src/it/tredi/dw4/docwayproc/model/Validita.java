package it.tredi.dw4.docwayproc.model;

import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;

public class Validita extends XmlEntity {

	private String tipodoc = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.tipodoc = XMLUtil.parseStrictAttribute(dom, "validita/@tipodoc", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getTipodoc() {
		return tipodoc;
	}

	public void setTipodoc(String tipodoc) {
		this.tipodoc = tipodoc;
	}
	
	public String getLabelTipodoc() {
		try {
			if (tipodoc != null && !tipodoc.equals(""))
				return I18N.mrs("dw4." + tipodoc.toUpperCase());
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return tipodoc;
	}

}
