package it.tredi.dw4.acl.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Delega;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

public class TitoloDelega extends Titolo {
	
	String testo;
	String cod_delegante;
	String cogn_delegante;
	String nome_delegante;
	List<Delega> deleghe = new ArrayList<Delega>();
	
	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
	public String getCod_delegante() {
		return cod_delegante;
	}
	public void setCod_delegante(String cod_delegante) {
		this.cod_delegante = cod_delegante;
	}
	public String getCogn_delegante() {
		return cogn_delegante;
	}
	public void setCogn_delegante(String cogn_delegante) {
		this.cogn_delegante = cogn_delegante;
	}
	public String getNome_delegante() {
		return nome_delegante;
	}
	public void setNome_delegante(String nome_delegante) {
		this.nome_delegante = nome_delegante;
	}
	public List<Delega> getDeleghe() {
		return deleghe;
	}
	public void setDeleghe(List<Delega> deleghe) {
		this.deleghe = deleghe;
	}

	@Override
	public Titolo init(Document domTitolo) {
		super.init(domTitolo);
		this.testo = XMLUtil.parseAttribute(domTitolo, "titolo/@testo", "");
		if (testo != null && !testo.isEmpty())
			parseTitle(testo);
		
		return this;
	}
	
	// parsa il testo del title nel formato
	// cod_delegante|cogn_delegante|nome_delegante|cod_delegato;*|cogn_nome_delegato;*|attiva;*|permanente;*|data_inizio;*|data_fine;*|uneditable;*|notifiche;*
	// dove ;* vuol dire ripetibile in base al numero dei delegati
	// ATTENZIONE, ORRORE: posizionale in base alla definizione dei titoli (ripropz)!! 
	private void parseTitle(String testo) {
		// formatter per le date di tipo "yyyy-MM-dd"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DATABASE_DATE_FORMAT);
		String[] fields = testo.split("\\|");
		this.cod_delegante = fields[0];
		this.cogn_delegante = fields[1];
		this.nome_delegante = fields[2];
		// controllo che i dati relativi alle deleghe siano valorizzati
		if (fields.length > 3) {
			// conto il numero dei divisori per ottenere il numero dei delegati
			int numDeleghe = (int) fields[3].chars().filter(ch -> ch == ';').count() + 1;
			for (int i = 0; i < numDeleghe; i++) {
				String cod_delegato = fields[3].split(";")[i];
				String cogn_nome_delegato = fields[4].split(";")[i];
				boolean attiva = Boolean.parseBoolean(fields[5].split(";")[i]);
				boolean permanente = Boolean.parseBoolean(fields[6].split(";")[i]);
				LocalDate dataInizio = null;
				LocalDate dataFine = null;
				if (!permanente) {
					dataInizio = LocalDate.parse(fields[7].split(";")[i], formatter);
					dataFine = LocalDate.parse(fields[8].split(";")[i], formatter);
				}
				boolean uneditable = Boolean.parseBoolean(fields[9].split(";")[i]);
				boolean sostituto = Boolean.parseBoolean(fields[10].split(";")[i]);
				this.deleghe.add(new Delega(cod_delegato, cogn_nome_delegato, dataInizio, dataFine, permanente, attiva, sostituto, uneditable));
			}
		}
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
