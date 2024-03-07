package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;

/**
 * Modale di registrazione dell'esito della verifica di virus fatta sugli allegati del documento in caso di individuazione di file
 * infetti (documento in quarantena)
 */
public class DocWayEsitoVerificaVirus extends Page {

	private ShowdocDoc showdocDoc;
	
	/**
	 * Indentifica se la sezione di inserimento dei dati dei virus rilevati deve essere attiva (documento infetto) o meno (nessun
	 * virus rilevato)
	 */
	private boolean active = false;
	
	/**
	 * Eventuali note da agganciare al documento con indicazioni in merito al virus trovato
	 */
	private String note;
	
	/**
	 * Lista di Identificativi di file caricati sul documento sui quali e' stato rilevato il virus
	 */
	public String[] fileIdsInfetti;
	
	/**
	 * Elenco di tutti gli xw:file contenuti nel documento (ad eccezione di eventuali file derivati)
	 */
	public List<XwFile> xwfilesInDoc;

	/**
	 * Costruttore
	 * @param showdocDoc Page bean di show del documento
	 */
	public DocWayEsitoVerificaVirus(ShowdocDoc showdocDoc) {
		this.showdocDoc = showdocDoc;
		
		this.xwfilesInDoc = new ArrayList<>();
		Doc doc = this.showdocDoc.getDoc();
		if (doc != null) {
			if (doc.getFiles() != null) {
				for (XwFile file : doc.getFiles()) {
					if (file != null && file.getName() != null && !file.getName().isEmpty() && (file.getDer_from() == null || file.getDer_from().isEmpty()))
						xwfilesInDoc.add(file);
				}
			}
			if (doc.getImmagini() != null) {
				for (XwFile file : doc.getImmagini()) {
					if (file != null && file.getName() != null && !file.getName().isEmpty() && (file.getDer_from() == null || file.getDer_from().isEmpty()))
						xwfilesInDoc.add(file);
				}
			}
		}
	}
	
	@Override
	public FormsAdapter getFormsAdapter() {
		return showdocDoc.getFormsAdapter();
	}
	
	/**
	 * Salvataggio dell'esito di virus rilevati sul documento corrente
	 * @return
	 */
	public String save() throws Exception {
		if (fileIdsInfetti == null || fileIdsInfetti.length == 0 || fileIdsInfetti[0].isEmpty()) {
			this.setErrorMessage("templateForm:fileInfettiCheck", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.file_infetti") + "'");
			return null;
		}
		this.active = false;
		return showdocDoc.esitoVirusQuarantena(this.note, this.fileIdsInfetti);
	}
	
	/**
	 * Chiusura del modale di inserimento dei dati di esito in quarantena 
	 * @return
	 */
	public String close() {
		this.active = false;
		return null;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String testo) {
		this.note = testo;
	}

	public String[] getFileIdsInfetti() {
		return fileIdsInfetti;
	}

	public void setFileIdsInfetti(String[] fileIdsInfetti) {
		this.fileIdsInfetti = fileIdsInfetti;
	}
	
	public List<XwFile> getXwfilesInDoc() {
		return xwfilesInDoc;
	}

	public void setXwfilesInDoc(List<XwFile> xwfilesInDoc) {
		this.xwfilesInDoc = xwfilesInDoc;
	}
	
	/**
	 * Documento visualizzato sulla pagina di show
	 * @return
	 */
	public Doc getDoc() {
		return (showdocDoc != null) ? showdocDoc.getDoc() : null;
	}
	
}
