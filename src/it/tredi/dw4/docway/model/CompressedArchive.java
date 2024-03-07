package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;

public class CompressedArchive extends XmlEntity {
	
	private boolean convertPdf = false;
	private boolean convertXml = false;
	private boolean da_firmare = false;
	private String title;
	
	public boolean isConvertPdf() {
		return convertPdf;
	}

	public void setConvertPdf(boolean convertPdf) {
		this.convertPdf = convertPdf;
	}

	public boolean isConvertXml() {
		return convertXml;
	}

	public void setConvertXml(boolean convertXml) {
		this.convertXml = convertXml;
	}

	public boolean isDa_firmare() {
		return da_firmare;
	}

	public void setDa_firmare(boolean da_firmare) {
		this.da_firmare = da_firmare;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public XmlEntity init(Document dom) {
		return null;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	

}
