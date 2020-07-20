package it.highwaytech.apps.xdocway.apv;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FatturaRepertorioOrdineRepertorioDecretazioneApv {
	//Il repertorio della relativa lettera d'ordine, su db postgres apv
	private String repertorioOrdine;
	//Il repertorio della relativa decretazione, su db postgres apv
	private String repertorioDecretazione;
	
	private String matricolaResponsabile;
	private String matricolaIncaricato;
	private String codiceDirezione;
	
	public String getRepertorioOrdine() {
		return repertorioOrdine;
	}
	public void setRepertorioOrdine(String repertorioOrdine) {
		this.repertorioOrdine = repertorioOrdine;
	}
	public String getRepertorioDecretazione() {
		return repertorioDecretazione;
	}
	public void setRepertorioDecretazione(String repertorioDecretazione) {
		this.repertorioDecretazione = repertorioDecretazione;
	}
	public String getMatricolaResponsabile() {
		return matricolaResponsabile;
	}
	public void setMatricolaResponsabile(String matricolaResponsabile) {
		this.matricolaResponsabile = matricolaResponsabile;
	}
	public String getMatricolaIncaricato() {
		return matricolaIncaricato;
	}
	public void setMatricolaIncaricato(String matricolaIncaricato) {
		this.matricolaIncaricato = matricolaIncaricato;
	}

	public String getCodiceDirezione() {
		return codiceDirezione;
	}
	public void setCodiceDirezione(String codiceDirezione) {
		this.codiceDirezione = codiceDirezione;
	}

	
	public String getFullRepertorioDecretazione() {
		if(repertorioDecretazione != null)
			return "DECRETAZIONI^APVEAPV-" + repertorioDecretazione.replace(".", "");
		return "";
	}
	
	public String getFullRepertorioOrdine() {
		if(repertorioDecretazione != null)
			return "LETORD^APVEAPV-" + repertorioOrdine.replace(".", "");
		return "";
	}

	@Override
	public String toString() {
		return "FatturaRepertorioOrdineRepertorioDecretazioneApv - repertorio_ordine: " + (repertorioOrdine==null?"":repertorioOrdine) + "; repertorio_decretazione: " + (repertorioDecretazione==null?"":repertorioDecretazione);
	}
}
