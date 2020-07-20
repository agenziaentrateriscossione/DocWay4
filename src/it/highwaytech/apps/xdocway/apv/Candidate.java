package it.highwaytech.apps.xdocway.apv;

import java.io.Serializable;

public class Candidate implements Serializable {
	private static final long serialVersionUID = -7107520158000239051L;
	
	private String code;
	private String nome;
	private String cognome;
	private boolean trovatoInAnagrafica = false;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public boolean isTrovatoInAnagrafica() {
		return trovatoInAnagrafica;
	}
	public void setTrovatoInAnagrafica(boolean trovatoInAnagrafica) {
		this.trovatoInAnagrafica = trovatoInAnagrafica;
	}
	
	@Override
	public String toString() {
		return "Candidate - code " + (code==null?"":code) + "; nome: " + (nome==null?"":nome) + "; cognome: " + (cognome==null?"":cognome);
	}
	
}
