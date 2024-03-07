package it.tredi.dw4.docway.doc.adapters;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.docway.model.Rif;

/**
 *  FormsAdapter specifico per la comunicazione intra-aoo
 */
public class IntraAooDocumentFormsAdapter extends DocDocWayDocumentFormsAdapter {

	public IntraAooDocumentFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	/**
	 * Protocollazione di un documento su AOO differente (comunicazione intra-aoo tramite chiamata a 3diWS)
	 * @param codAmmAoo
	 * @param assegnazioneRPA
	 */
	public void saveIntraAoo(String codAmmAoo, Rif assegnazioneRPA) {
		defaultForm.addParam("verbo", "intraaoo");
		defaultForm.addParam("xverb", "@saveIntraAoo");
		defaultForm.addParam("newCodAmmAoo", codAmmAoo);
		defaultForm.addParams(assegnazioneRPA.asFormAdapterParams("*assegnazioneRPA"));
	}
		
	/**
	 * Lookup su ufficio in comunicazione intra-aoo (chiamata a 3diWS)
	 * @param codAmmAoo
	 * @param searchKeys
	 */
	public void lookupUfficio(String codAmmAoo, String searchTerms) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@lookupStruInt");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
		indexForm.addParam("searchTerms", searchTerms);
	}
	
	/**
	 * Lookup su persona in comunicazione intra-aoo (chiamata a 3diWS)
	 * @param codAmmAoo
	 * @param searchKeys
	 */
	public void lookupPersona(String codAmmAoo, String searchTerms) {
		lookupPersona(codAmmAoo, searchTerms, null);
	}
	
	/**
	 * Lookup su persona in comunicazione intra-aoo (chiamata a 3diWS)
	 * @param codAmmAoo
	 * @param searchKeys
	 * @param codUff codice dell'ufficio di appartenenza della persona
	 */
	public void lookupPersona(String codAmmAoo, String searchTerms, String codUff) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@lookupPersInt");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
		indexForm.addParam("searchTerms", searchTerms);
		indexForm.addParam("codUffIntraAoo", codUff);
	}
	
	/**
	 * Lookup su ruolo in comunicazione intra-aoo (chiamata a 3diWS)
	 * @param codAmmAoo
	 * @param searchKeys
	 */
	public void lookupRuolo(String codAmmAoo, String searchTerms) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@lookupRuolo");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
		indexForm.addParam("searchTerms", searchTerms);
	}
	
	/**
	 * Caricamento della prima pagina dei risultati di lookup
	 * @param codAmmAoo
	 */
	public void primaPagina(String codAmmAoo) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@firstPage");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
	}
	
	/**
	 * Caricamento della pagina precedente dei risultati di lookup
	 * @param codAmmAoo
	 */
	public void paginaPrecedente(String codAmmAoo) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@prevPage");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
	}
	
	/**
	 * Caricamento della pagina successiva dei risultati di lookup
	 * @param codAmmAoo
	 */
	public void paginaSuccessiva(String codAmmAoo) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@nextPage");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
	}
	
	/**
	 * Caricamento dell'ultima pagina  dei risultati di lookup
	 * @param codAmmAoo
	 */
	public void ultimaPagina(String codAmmAoo) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@lastPage");
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
	}
	
	/**
	 * Caricamento di un documento in base al suo idIUnit
	 * @param idIUnit
	 * @param codAmmAoo
	 */
	public void loadByIdIUnit(String idIUnit, String codAmmAoo) {
		indexForm.addParam("verbo", "intraaoo");
		indexForm.addParam("xverb", "@loadByIdIUnit");
		indexForm.addParam("idIUnit", idIUnit);
		indexForm.addParam("newCodAmmAoo", codAmmAoo);
	}

}
