package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

/**
 * FormsAdapter specifico per la gestione dei raccoglitori di tipo indice
 * @author mbernardini
 */
public class DocWayRaccIndiceFormsAdapter extends DocEditFormsAdapter {

	public DocWayRaccIndiceFormsAdapter(AdapterConfig config) {
		super(config);
	}

	/**
	 * Salvataggio di un nuovo documento all'interno di un raccoglitore di tipo indice
	 */
	public void addNewDocInRaccoglitore() {
		defaultForm.addParam("verbo", "raccindice_response");
		defaultForm.addParam("xverb", "newDoc");
	}

	/**
	 * Salvataggio di più documenti all'interno di un raccoglitore di tipo indice
	 */
	public void addAllNewDocsInRaccoglitore(int numDocsToInsert) {
		defaultForm.addParam("verbo", "raccindice_response");
		defaultForm.addParam("xverb", "multipleNewDocs");
		defaultForm.addParam("numDocsToInsert", numDocsToInsert);
	}

	/**
	 * Apertura della pagina di ricerca documenti da interfaccia del raccoglitore di tipo Indice
	 */
	public void searchDocumento() {
		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("dbTable", "@globale");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("selid", ""); // senza questo parametro non funzionerebbero i vocabolari sul form di ricerca
		this.defaultForm.addParam("personalView", "");
		this.defaultForm.addParam("view", "linkDocRaccIndice");
	}

}
