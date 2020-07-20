package it.tredi.dw4.addons.xDams;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.acl.beans.ShowdocPersonaInterna;
import it.tredi.dw4.acl.model.custom.ArchiveAuthorization;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocPersonaInternaAspect extends BaseAddOn {

	private ArchiveAuthorization archiveAuthorization = new ArchiveAuthorization();
	
	public ShowdocPersonaInternaAspect(String template, Page host) {
		super(template, host);
	}
	
	@Override
	public ShowdocPersonaInterna getHost() {
		return (ShowdocPersonaInterna) super.getHost();
	}
	
	@Override
	public void init(Document dom) {
		this.archiveAuthorization.init(XMLUtil.createDocument(dom, "//persona_interna/extra"));
	}

	@Override
	public Map<String, String> asFormAdapterParams() {
		return null;
	}

	@Override
	public void clear() {
	}
	
	public ArchiveAuthorization getArchiveAuthorization() {
		return archiveAuthorization;
	}

	public void setArchiveAuthorization(ArchiveAuthorization archiveAuthorization) {
		this.archiveAuthorization = archiveAuthorization;
	}
	
	@Override
	public String asQuery() {
		return null;
	}

}
