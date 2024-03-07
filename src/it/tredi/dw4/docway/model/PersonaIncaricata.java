package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class PersonaIncaricata extends XmlEntity {

	private String nome = ""; // cognome e nome della persona
	private String matricola = "";
	private String codUff = "";
	private String nomeUff = "";
	private String emails = "";
	private String motivazioni = "";
	private boolean incaricoDone = false;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getCodUff() {
		return codUff;
	}

	public void setCodUff(String codUff) {
		this.codUff = codUff;
	}

	public String getNomeUff() {
		return nomeUff;
	}

	public void setNomeUff(String nomeUff) {
		this.nomeUff = nomeUff;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}
	
	public String getMotivazioni() {
		return motivazioni;
	}

	public void setMotivazioni(String motivazioni) {
		this.motivazioni = motivazioni;
	}

	public boolean isIncaricoDone() {
		return incaricoDone;
	}

	public void setIncaricoDone(boolean incaricoDone) {
		this.incaricoDone = incaricoDone;
	}
	
	@Override
	public XmlEntity init(Document dom) {
		this.nome = XMLUtil.parseStrictAttribute(dom, "persona/@nome_persona", "");
		this.matricola = XMLUtil.parseStrictAttribute(dom, "persona/@cod_persona", "");
		this.codUff = XMLUtil.parseStrictAttribute(dom, "persona/@cod_uff", "");
		this.nomeUff = XMLUtil.parseStrictAttribute(dom, "persona/@nome_uff", "");
		this.emails = XMLUtil.parseStrictAttribute(dom, "persona/@emails", "");
		this.motivazioni = XMLUtil.parseStrictAttribute(dom, "persona/@motivazioni");
		this.incaricoDone = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, "persona/@done", ""));

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
}
