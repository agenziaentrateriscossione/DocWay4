package it.tredi.dw4.docway.beans.rep_standard;

import it.tredi.dw4.docway.beans.ShowdocVarie;
import it.tredi.dw4.docway.model.rep_standard.Richiedente;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocVarieRAOL extends ShowdocVarie {

	// campi specifici del repertorio di richiesta pubblicazione albo online
	private Richiedente richiedente = new Richiedente();
	
	public ShowdocVarieRAOL() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		richiedente.init(XMLUtil.createDocument(dom, "/response/doc/extra/richiedente")); // richiedente interno
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/rep_standard/showdoc@varie@RAOL");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	public Richiedente getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}
	
}
