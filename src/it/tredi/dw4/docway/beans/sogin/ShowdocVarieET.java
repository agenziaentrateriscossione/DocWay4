package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.docway.beans.ShowdocVarie;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class ShowdocVarieET extends ShowdocVarie {

	public ShowdocVarieET() throws Exception {
		super();
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/sogin/showdoc@varie@ET");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
}
