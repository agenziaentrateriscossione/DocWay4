package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class PersonaInRuolo extends XmlEntity {

	private String nome = ""; // cognome e nome della persona
	private String matricola = "";
	private String codUff = "";
	private String codAmm = "";
	private String codAoo = "";
	private String email = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.nome 		= XMLUtil.parseStrictElement(dom, "persona/nome");
		this.matricola 	= XMLUtil.parseStrictAttribute(dom, "persona/@matricola", "");
		this.codUff 	= XMLUtil.parseStrictAttribute(dom, "persona/@codUff", "");
		this.codAmm 	= XMLUtil.parseStrictAttribute(dom, "persona/@codAmm", "");
		this.codAoo 	= XMLUtil.parseStrictAttribute(dom, "persona/@codAoo", "");
		this.email 		= XMLUtil.parseStrictAttribute(dom, "persona/@email", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
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

	public String getCodAmm() {
		return codAmm;
	}

	public void setCodAmm(String codAmm) {
		this.codAmm = codAmm;
	}

	public String getCodAoo() {
		return codAoo;
	}

	public void setCodAoo(String codAoo) {
		this.codAoo = codAoo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
