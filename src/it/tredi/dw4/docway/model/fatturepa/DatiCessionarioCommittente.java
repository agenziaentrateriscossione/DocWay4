package it.tredi.dw4.docway.model.fatturepa;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class DatiCessionarioCommittente extends XmlEntity {

	// TODO andrebbero recuperati da ACL su strutture/persone interne
	private String idPaese = "";
	private String idCodice = "";
	
	private String denominazione = "";
	private String nome = "";
	private String cognome = "";
	private String titolo = "";
	private String codiceORI = "";
	
	private String indirizzo = "";
	private String numeroCivico = "";
	private String cap = "";
	private String comune = "";
	private String provincia = "";
	private String nazione = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.idPaese 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/@idPaese");
		this.idCodice 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/@idCodice");
		
		this.denominazione 	= XMLUtil.parseStrictElement(dom, "datiCessionarioCommittente/denominazione");
		this.nome 			= XMLUtil.parseStrictElement(dom, "datiCessionarioCommittente/nome");
		this.cognome 		= XMLUtil.parseStrictElement(dom, "datiCessionarioCommittente/cognome");
		this.titolo 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/@titolo");
		this.codiceORI 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/@codiceORI");
		
		this.indirizzo 		= XMLUtil.parseStrictElement(dom, "datiCessionarioCommittente/indirizzo");
		this.numeroCivico 	= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/indirizzo/@numeroCivico");
		this.comune 		= XMLUtil.parseStrictElement(dom, "datiCessionarioCommittente/comune");
		this.cap 			= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/comune/@cap");
		this.provincia 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/comune/@provincia");
		this.nazione 		= XMLUtil.parseStrictAttribute(dom, "datiCessionarioCommittente/comune/@nazione");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.idPaese != null && this.idPaese.length() > 0)
    		params.put(prefix+".@idPaese", this.idPaese);
    	if (this.idCodice != null && this.idCodice.length() > 0)
    		params.put(prefix+".@idCodice", this.idCodice);
    	
    	if (this.denominazione != null && this.denominazione.length() > 0)
    		params.put(prefix+".denominazione", this.denominazione);
    	if (this.nome != null && this.nome.length() > 0)
    		params.put(prefix+".nome", this.nome);
    	if (this.cognome != null && this.cognome.length() > 0)
    		params.put(prefix+".cognome", this.cognome);
    	if (this.titolo != null && this.titolo.length() > 0)
    		params.put(prefix+".@titolo", this.titolo);
    	if (this.codiceORI != null && this.codiceORI.length() > 0)
    		params.put(prefix+".@codiceORI", this.codiceORI);
    	
    	if (this.indirizzo != null && this.indirizzo.length() > 0)
    		params.put(prefix+".indirizzo", this.indirizzo);
    	if (this.numeroCivico != null && this.numeroCivico.length() > 0)
    		params.put(prefix+".indirizzo.@numeroCivico", this.numeroCivico);
    	if (this.comune != null && this.comune.length() > 0)
    		params.put(prefix+".comune", this.comune);
    	if (this.cap != null && this.cap.length() > 0)
    		params.put(prefix+".comune.@cap", this.cap);
    	if (this.provincia != null && this.provincia.length() > 0)
    		params.put(prefix+".comune.@provincia", this.provincia);
    	if (this.nazione != null && this.nazione.length() > 0)
    		params.put(prefix+".comune.@nazione", this.nazione);
    	
    	return params;
	}
	
	public String getIdPaese() {
		return idPaese;
	}

	public void setIdPaese(String idPaese) {
		this.idPaese = idPaese;
	}

	public String getIdCodice() {
		return idCodice;
	}

	public void setIdCodice(String idCodice) {
		this.idCodice = idCodice;
	}

	public String getCodiceORI() {
		return codiceORI;
	}

	public void setCodiceORI(String codiceORI) {
		this.codiceORI = codiceORI;
	}
	
	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getNumeroCivico() {
		return numeroCivico;
	}

	public void setNumeroCivico(String numeroCivico) {
		this.numeroCivico = numeroCivico;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

}
