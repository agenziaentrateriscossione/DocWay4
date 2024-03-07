package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.Showdoc;

public class DocWayMotivazioneRifiuto extends DocEdit {

	private Showdoc showdoc;
	private DocEditFormsAdapter formsAdapter;
	private String motivazione;
	private boolean visible = false;
	
	public Showdoc getShowdoc() {
		return showdoc;
	}
	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}
	public String getMotivazione() {
		return motivazione;
	}
	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public DocWayMotivazioneRifiuto() throws Exception {
		this.formsAdapter = new DocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		return;
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;
	}

	@Override
	public String clearDocument() throws Exception {
		visible = false;
		motivazione = null;
		showdoc = null;
		setSessionAttribute("docwayMotivazioniRifiuto", null);
		return null;
	}
	
	public String confirmRifiuto() throws Exception {
		String _motivazione = motivazione;
		ShowdocDoc _showdoc = (ShowdocDoc) showdoc;
		clearDocument();
		_showdoc.handleMotivazioneRifiutoBozzaArrivo(_motivazione);
		return null;
	}

}
