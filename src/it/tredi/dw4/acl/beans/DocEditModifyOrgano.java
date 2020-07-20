package it.tredi.dw4.acl.beans;

import org.dom4j.Document;

public class DocEditModifyOrgano extends DocEditOrgano {

	public DocEditModifyOrgano() throws Exception {
		super();
	}
	
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		this.setModify(true);
	}	

	
}
