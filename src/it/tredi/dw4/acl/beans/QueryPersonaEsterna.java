package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import org.dom4j.Document;

public class QueryPersonaEsterna extends AclQuery {
	
	private String xml = "";
	
	private String persest_appartenenza = "";
	private String persest_appartenenzacoduff;
	private String persest_matricola;
	private String persest_nomcogn;
	private String persest_soprannome;
	private String persest_codfisc;
	private String persest_piva;
	private String persest_appartenenzaqualifica;
	private String persest_competenze;
	private String persest_operatore;
	private String persest_uffoperatore;
	private String focusElement = "persest_matricola";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryPersonaEsterna() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document dom) {
    	this.xml = dom.asXML();
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	//TEMP
	public String queryPlain() throws Exception {
		
		if (!getCodUffByAppartenenza()) // caricamento dei codici delle strutture di appartenenza in base alla denominazione indicata
			return null;
		
		String query =  "";
		query +=  addQueryField("persest_appartenenzacoduff", persest_appartenenzacoduff);
		query +=  addQueryField("persest_matricola", persest_matricola);
		query +=  addQueryField("persest_cognome", persest_nomcogn);
		query +=  addQueryField("persest_codfisc", persest_codfisc);
		query +=  addQueryField("xml,/persona_esterna/@partita_iva", persest_piva);
		query +=  addQueryField("persest_soprannome", persest_soprannome);
		query +=  addQueryField("persest_appartenenzaqualifica", persest_appartenenzaqualifica);
		query +=  addQueryField("persest_competenze", persest_competenze);
		query +=  addQueryField("persest_operatore", persest_operatore);
		query +=  addQueryField("persest_uffoperatore", persest_uffoperatore);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=persona_esterna";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_EXT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [persest_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [persest_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/persona_esterna/@cognome),xml(xpart:/persona_esterna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	/**
	 * nel caso siano state indicate una o piu' denominazioni di strutture esterne, vengono caricati i codici relativi
	 * alla/e struttura/e individuata/e
	 * 
	 * @return
	 */
	private boolean getCodUffByAppartenenza() throws Exception {
		try {
			if (persest_appartenenza != null && persest_appartenenza.length() > 0) {
				String query = addQueryField("struest_nome", persest_appartenenza);
				if (query.endsWith(" AND "))
					query = query.substring(0, query.length()-4);
				
				// TODO da implementare una chiamata che produca un elenco completo (non paginato) di codici di strutture esterne
				
				XMLDocumento response = this._queryPlain(query, "", "");
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
					return false;
				}
				
				if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
					persest_appartenenzacoduff = response.getRootElement().attributeValue("cod_uff");
				} 
				else {
					// TODO da completare
					
				}
			}
			
			return true;
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return false;
		}
	}
	
	public void setPersest_matricola(String cod) {
		this.persest_matricola = cod;
	}

	public String getPersest_matricola() {
		return persest_matricola;
	}

	public void setPersest_nomcogn(String descr) {
		this.persest_nomcogn = descr;
	}

	public String getPersest_nomcogn() {
		return persest_nomcogn;
	}

	public void setPersest_codfisc(String tipo) {
		this.persest_codfisc = tipo;
	}

	public String getPersest_codfisc() {
		return persest_codfisc;
	}

	public void setPersest_appartenenzaqualifica(String comune) {
		this.persest_appartenenzaqualifica = comune;
	}

	public String getPersest_appartenenzaqualifica() {
		return persest_appartenenzaqualifica;
	}

	public void setPersest_competenze(String competenze) {
		this.persest_competenze = competenze;
	}

	public String getPersest_competenze() {
		return persest_competenze;
	}

	public void setPersest_operatore(String operatore) {
		this.persest_operatore = operatore;
	}

	public String getPersest_operatore() {
		return persest_operatore;
	}

	public void setPersest_uffoperatore(String uff_operatore) {
		this.persest_uffoperatore = uff_operatore;
	}

	public String getPersest_uffoperatore() {
		return persest_uffoperatore;
	}
	
	public String getPersest_appartenenza() {
		return persest_appartenenza;
	}

	public void setPersest_appartenenza(String persest_appartenenza) {
		this.persest_appartenenza = persest_appartenenza;
	}
	
	public String getPersest_appartenenzacoduff() {
		return persest_appartenenzacoduff;
	}

	public void setPersest_appartenenzacoduff(String persest_appartenenza_codunita) {
		this.persest_appartenenzacoduff = persest_appartenenza_codunita;
	}
	
	public String getPersest_piva() {
		return persest_piva;
	}

	public void setPersest_piva(String persest_piva) {
		this.persest_piva = persest_piva;
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.persest_appartenenza = "";
		this.persest_appartenenzacoduff = null;
		this.persest_matricola = null;
		this.persest_nomcogn = null;
		this.persest_codfisc = null;
		this.persest_piva = null;
		this.persest_appartenenzaqualifica = null;
		this.persest_competenze = null;
		this.persest_operatore = null;
		this.persest_uffoperatore = null;
		this.persest_soprannome = null;
		return null;
	}

	public void setPersest_soprannome(String nickname) {
		this.persest_soprannome = nickname;
	}

	public String getPersest_soprannome() {
		return persest_soprannome;
	}
	
	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexAppartenenza() throws Exception {
		this.openIndex("persest_appartenenza", "struest_nome", this.persest_appartenenza, "0", null);
		return null;
	}
	
	public String openIndexAppartenenzaCodUff() throws Exception {
		this.focusElement = "persest_appartenenzacoduff";
		this.openIndex("persest_appartenenzacoduff", this.persest_appartenenzacoduff, "0", null);
		return null;
	}
	
	public String openIndexCodiceUnita() throws Exception {
		this.focusElement = "persest_matricola";
		this.openIndex("persest_matricola", this.persest_matricola, "0", null);
		return null;
	}
	
	public String openIndexCognome() throws Exception {
		this.focusElement = "persest_nomcogn";
		this.openIndex("persest_nomcogn", this.persest_nomcogn, "0", null);
		return null;
	}
	
	public String openIndexNickname() throws Exception {
		this.focusElement = "persest_soprannome";
		this.openIndex("persest_soprannome", this.persest_soprannome, "0", null);
		return null;
	}
	
	public String openIndexCodFisc() throws Exception {
		this.focusElement = "persest_codfisc";
		this.openIndex("persest_codfisc", this.persest_codfisc, "0", null);
		return null;
	}
	
	public String openIndexPiva() throws Exception {
		this.openIndex("persest_piva", "xml,/persona_esterna/@partita_iva", this.persest_piva, "0", "");
		return null;
	}
	
	public String openIndexRuolo() throws Exception {
		this.focusElement = "persest_appartenenzaqualifica";
		this.openIndex("persest_appartenenzaqualifica", this.persest_appartenenzaqualifica, "0", " ");
		return null;
	}
	
	public String openIndexCompetenze() throws Exception {
		this.focusElement = "persest_competenze";
		this.openIndex("persest_competenze", this.persest_competenze, "0", null);
		return null;
	}
	
	public String openIndexOperatore() throws Exception {
		this.focusElement = "persest_operatore";
		this.openIndex("persest_operatore", this.persest_operatore, "0", null);
		return null;
	}
	
	public String openIndexUffOperatore() throws Exception {
		this.focusElement = "persest_uffoperatore";
		this.openIndex("persest_uffoperatore", this.persest_uffoperatore, "0", null);
		return null;
	}
}
