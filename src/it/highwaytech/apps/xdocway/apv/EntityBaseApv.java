package it.highwaytech.apps.xdocway.apv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EntityBaseApv {
	private String nrecord;
	private String direzione;
	private String area;
	private String repertorio;
	private String oggetto;
	private StatoAvanzamentoApv statoAvanzamentoApv = StatoAvanzamentoApv.Altro;
	private boolean contieneFlussiKillati = false; 
	private ProcessInstance process;
	
	public String getNrecord() {
		return nrecord;
	}
	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}
	public String getDirezione() {
		return direzione;
	}
	public void setDirezione(String direzione) {
		this.direzione = direzione;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getRepertorio() {
		return repertorio;
	}
	public void setRepertorio(String repertorio) {
		this.repertorio = repertorio;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public StatoAvanzamentoApv getStatoAvanzamentoApv() {
		return statoAvanzamentoApv;
	}
	public void setStatoAvanzamentoApv(StatoAvanzamentoApv statoAvanzamentoApv) {
		this.statoAvanzamentoApv = statoAvanzamentoApv;
	}
	
	public boolean isContieneFlussiKillati() {
		return contieneFlussiKillati;
	}
	public void setContieneFlussiKillati(boolean contieneFlussiKillati) {
		this.contieneFlussiKillati = contieneFlussiKillati;
	}
	public ProcessInstance getProcess() {
		return process;
	}
	public void setProcess(ProcessInstance process) {
		this.process = process;
	}
	
	public String getFormattedRepertorio() {
		return formatRepertorio(repertorio);
	}
	
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public String getInAttesaDa() {
		String toRet = "";
		if(this.getProcess() != null) {
			if(this.getProcess().getStato() == StatoFlusso.Finished) {
				toRet = df.format(this.getProcess().getDataConclusione().getTime()); 
			} else {
				//il flusso e' attivo
				if(this.getProcess().getTasks() != null && this.getProcess().getTasks().size() > 0) {
					//Dovrebbe esserci un solo task anche se ho previsto che ne possa essere presente contemporaneamente piu' di uno
					boolean write = false;
					for(Task task : this.getProcess().getTasks()) {
						//Task task = this.getProcess().getTasks().get(0);
						if(write)
							toRet += "<br/>";
						toRet += task.getInAttesaDa();
						write = true;
					}
				}
			}
		}
		return toRet;
	}

	protected String formatRepertorio(String repertorio) {
		String toRet = "";
		if(repertorio != null && repertorio.length() >= 11) {
			toRet = repertorio.substring(repertorio.length() - 11);
			toRet = toRet.substring(0, 4) + "." + toRet.substring(4); 
		}		
		return toRet;
	}

	public boolean isLinkInviaSollecitoShow() {
		return getProcess() != null && getProcess().getTasks() != null && getProcess().getTasks().size() > 0 && getProcess().getTasks().get(0).getCandidates() != null && getProcess().getTasks().get(0).getCandidates().size() > 0;
	}

	@Override
	public String toString() {
		return "EntityBaseApv - nrecord: " + nrecord + "; repertorio: " + (repertorio==null?"":repertorio) + "; oggetto: " + (oggetto==null?"":oggetto) + "; process: " + (process==null?"":process.toString());
	}
}
