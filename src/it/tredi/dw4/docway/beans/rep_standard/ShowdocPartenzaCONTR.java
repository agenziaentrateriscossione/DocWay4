package it.tredi.dw4.docway.beans.rep_standard;

import javax.faces.context.FacesContext;

import it.tredi.dw4.docway.beans.ShowdocPartenza;

public class ShowdocPartenzaCONTR extends ShowdocPartenza {

	public ShowdocPartenzaCONTR() throws Exception {
		super();
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/rep_standard/showdoc@partenza@CONTR");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
}
