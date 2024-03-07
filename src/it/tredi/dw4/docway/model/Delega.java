package it.tredi.dw4.docway.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Classe delega, per controllo scadenza e gestione attivazione.
 *
 * @author tiommi
 *
 */

public class Delega extends XmlEntity {

	private String codPersona;
	private String nomePersona;
	private String codUfficio;
	private String nomeUfficio;
	private String inizio;
	private String fine;
	private LocalDate dataInizio;
	private LocalDate dataFine;
	private boolean permanente;
	private boolean uneditable;
	private boolean sostituto;
	private boolean attiva = true;
	private String type = "to";

	//lista Rif per inserimento nuove deleghe
	private List<Rif> rifDelegati = new ArrayList<Rif>();

	public String getCodPersona() {
		return codPersona;
	}

	public void setCodPersona(String codPersona) {
		this.codPersona = codPersona;
	}

	public String getNomePersona() {
		return nomePersona;
	}

	public void setNomePersona(String nomePersona) {
		this.nomePersona = nomePersona;
	}

	public String getCodUfficio() {
		return codUfficio;
	}

	public void setCodUfficio(String codUfficio) {
		this.codUfficio = codUfficio;
	}

	public String getNomeUfficio() {
		return nomeUfficio;
	}

	public void setNomeUfficio(String nomeUfficio) {
		this.nomeUfficio = nomeUfficio;
	}

	public String getInizio() {
		return inizio;
	}

	public void setInizio(String inizio) {
		this.inizio = inizio;
	}

	public String getFine() {
		return fine;
	}

	public void setFine(String fine) {
		this.fine = fine;
	}

	public LocalDate getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDate getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}

	public boolean isPermanente() {
		return permanente;
	}

	public void setPermanente(boolean permanente) {
		this.permanente = permanente;
	}

	public boolean isUneditable() {
		return uneditable;
	}

	public void setUneditable(boolean uneditable) {
		this.uneditable = uneditable;
	}

	public boolean isSostituto() {
		return sostituto;
	}

	public void setSostituto(boolean sostituto) {
		this.sostituto = sostituto;
	}

	public boolean isAttiva() {
		return attiva;
	}

	public void setAttiva(boolean attiva) {
		this.attiva = attiva;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Rif> getRifDelegati() {
		return rifDelegati;
	}

	public void setRifDelegati(List<Rif> rifDelegati) {
		this.rifDelegati = rifDelegati;
	}

	public String dataInizioToString() {
		if (this.dataInizio == null)
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		return this.dataInizio.format(formatter);
	}

	public String dataFineToString() {
		if (this.dataFine == null)
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		return this.dataFine.format(formatter);
	}

	public LocalDate inizioToLocalDate() {
		if (this.inizio == null || this.inizio.isEmpty())
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		return LocalDate.parse(inizio, formatter);
	}

	public LocalDate fineToLocalDate() {
		if (this.fine == null || this.fine.isEmpty())
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		return LocalDate.parse(fine, formatter);
	}

	public boolean isDelegaScaduta() {
		if(this.permanente)
			return false;
		if(this.dataFine != null && this.dataFine.isBefore(LocalDate.now()))
			return true;
		else return false;
	}

	public boolean isDelegaInAnticipo() {
		if(this.permanente)
			return false;
		if(this.dataInizio != null && this.dataInizio.isAfter(LocalDate.now()))
			return true;
		else return false;
	}
	
	public Delega() {}

	public Delega(boolean uneditable) {
		this.uneditable = uneditable;
	}

	public Delega(LocalDate dataInizio, LocalDate dataFine, boolean permanente, boolean attiva, boolean sostituto) {
		this(dataInizio, dataFine, permanente, attiva, sostituto, false);
	}
	
	public Delega(LocalDate dataInizio, LocalDate dataFine, boolean permanente, boolean attiva, boolean sostituto, boolean uneditable) {
		this(null, null, dataInizio, dataFine, permanente, attiva, sostituto, uneditable);
	}
	
	public Delega(String cod_persona, String nome_persona, LocalDate dataInizio, LocalDate dataFine, boolean permanente, boolean attiva, boolean sostituto, boolean uneditable) {
		this.codPersona = cod_persona;
		this.nomePersona = nome_persona;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.permanente = permanente;
		this.attiva = attiva;
		this.sostituto = sostituto;
		this.inizio = dataInizioToString();
		this.fine = dataFineToString();
		this.uneditable = uneditable;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod_persona", this.codPersona);
    	params.put(prefix + ".@nome_persona", this.nomePersona);
    	params.put(prefix + ".@cod_ufficio", this.codUfficio);
    	params.put(prefix + ".@nome_ufficio", this.nomeUfficio);
    	params.put(prefix + ".@data_inizio", this.inizio == null || this.inizio.isEmpty() ? null : inizioToLocalDate().toString());
    	params.put(prefix + ".@data_fine", this.fine == null || this.fine.isEmpty() ? null : fineToLocalDate().toString());
    	params.put(prefix + ".@permanente", this.isBlank() ? null : Boolean.toString(this.permanente));
    	params.put(prefix + ".@uneditable", this.isBlank() ? null : Boolean.toString(this.uneditable));
    	params.put(prefix + ".@sostituto", this.isBlank() ? null : Boolean.toString(this.sostituto));
    	params.put(prefix + ".@attiva", this.isBlank() ? null : Boolean.toString(this.attiva));
		return params;
	}

	@Override
	public XmlEntity init(Document dom) {

		String xpath = "/delega";

		this.codPersona = XMLUtil.parseStrictAttribute(dom, xpath+"/@cod_persona", "");
		this.codUfficio = XMLUtil.parseStrictAttribute(dom, xpath+"/@cod_ufficio", "");
		this.nomePersona = XMLUtil.parseStrictAttribute(dom, xpath+"/@nome_persona", "");
		this.nomeUfficio = XMLUtil.parseStrictAttribute(dom, xpath+"/@nome_ufficio", "");
		this.permanente = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, xpath+"/@permanente", "false"));
		this.uneditable = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, xpath+"/@uneditable", "false"));
		this.sostituto = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, xpath+"/@sostituto", "false"));
		this.attiva = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, xpath+"/@attiva", "true"));
		// nella response sarà sempre in formato ISO-8601 ovvero "yyyy-MM-dd"
		String ISOinizio = XMLUtil.parseStrictAttribute(dom, xpath+"/@data_inizio", "");
		String ISOfine = XMLUtil.parseStrictAttribute(dom, xpath+"/@data_fine", "");
		if (!ISOinizio.isEmpty())
			this.dataInizio = LocalDate.parse(ISOinizio);
		if (!ISOfine.isEmpty())
			this.dataFine = LocalDate.parse(ISOfine);
		//ripristina il formato di dw4 dd/MM/yyyy
		this.inizio = dataInizioToString();
		this.fine = dataFineToString();

		return this;
	}

	/**
	 * Pulisce il form di inserimento nuova delega da DelegheOptions
	 */
	public void clean() {
		this.rifDelegati.clear();
		this.rifDelegati.add(new Rif());
		this.inizio = null;
		this.fine = null;
		this.permanente = false;
		this.sostituto = false;
		this.uneditable = false;
		this.attiva = true;
		this.type = "to";
	}

	/**
	 * Aggiorna le date a seconda del flag permanente
	 */
	public void updateDataInput() {
		if(permanente) {
			dataInizio = null;
			dataFine = null;
			inizio = null;
			fine = null;
		}
	}

	/**
	 * Controlla se la delega sia vuota
	 * Ovvero l'utente non ha valorizzato nemmeno un valore o non ha cambiato i valori di default dei campi principali
	 */
	public boolean isBlank() {
		if((this.nomePersona != null && !this.nomePersona.isEmpty()) ||
				(this.nomeUfficio != null && !this.nomeUfficio.isEmpty()) ||
				(this.inizio != null && !this.inizio.isEmpty()) ||
				(this.fine != null && !this.fine.isEmpty()) ||
				this.permanente)
			return false;

		return true;
	}

}
