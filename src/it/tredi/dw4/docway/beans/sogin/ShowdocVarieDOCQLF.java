package it.tredi.dw4.docway.beans.sogin;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import it.tredi.dw4.docway.beans.ShowdocVarie;

public class ShowdocVarieDOCQLF extends ShowdocVarie {

	public ShowdocVarieDOCQLF() throws Exception {
		super();
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/sogin/showdoc@varie@DOCQLF");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}

}
