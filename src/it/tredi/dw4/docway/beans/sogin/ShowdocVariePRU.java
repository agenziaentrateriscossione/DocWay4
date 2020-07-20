package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.docway.beans.ShowdocVarie;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class ShowdocVariePRU extends ShowdocVarie {

	public ShowdocVariePRU() throws Exception {
		super();
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/sogin/showdoc@varie@PRU");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
}
