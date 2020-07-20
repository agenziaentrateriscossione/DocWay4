package it.highwaytech.apps.xdocway.apv;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ProcessInstance implements Serializable {
	private static final long serialVersionUID = -6712606523348654204L;

	private String instanceId;
	private StatoFlusso stato;
	private GregorianCalendar dataAvvio;
	private GregorianCalendar dataConclusione;
	private List<Task> tasks;
	
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public StatoFlusso getStato() {
		return stato;
	}
	public void setStato(StatoFlusso stato) {
		this.stato = stato;
	}
	public GregorianCalendar getDataAvvio() {
		return dataAvvio;
	}
	public void setDataAvvio(GregorianCalendar dataAvvio) {
		this.dataAvvio = dataAvvio;
	}
	public GregorianCalendar getDataConclusione() {
		return dataConclusione;
	}
	public void setDataConclusione(GregorianCalendar dataConclusione) {
		this.dataConclusione = dataConclusione;
	}
	public List<Task> getTasks() {
		if(tasks==null)
			tasks = new ArrayList<Task>();
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
    public int compareInAttesaTo(ProcessInstance o2){
		if(o2 == null)
			return 1;

		if(this.getStato() == null) {
			if(o2.getStato() == null)
				return 0;
			else
				return -1;
		} else {
			if(o2.getStato() == null)
				return 1;
			//getStato non nullo in entrambi
			if(this.getStato() != o2.getStato()) {
				return this.getStato().toString().compareTo(o2.getStato().toString());			
			} else {
				//stesso getStato
				if(this.getStato() == StatoFlusso.Finished) {
					return this.getDataConclusione().compareTo(o2.getDataConclusione()); 
				} else {
					//il flusso e' attivo
					if(this.getTasks() == null || this.getTasks().size() == 0) {
						if(o2.getTasks() == null || o2.getTasks().size() == 0)
							return 0;
						else
							return -1;
					} else {
						if(o2.getTasks() == null || o2.getTasks().size() == 0)
							return 1;
						else {
							//Entrambi hanno un task
							return this.getTasks().get(0).getInAttesaDa().compareToIgnoreCase(o2.getTasks().get(0).getInAttesaDa());
						}
					}
				}
			}
		}
	}
	
	DateFormat df = new SimpleDateFormat("yyyyMMdd-HH.mm.ss");
	@Override
	public String toString() {
		String toRet = "ProcessInstance - instanceId: " + (instanceId==null?"":instanceId) + "; stato: " + (stato==null?"":stato) + "; dataAvvio: " + (dataAvvio==null?"":df.format(dataAvvio.getTime())) + "; dataConclusione: " + (dataConclusione==null?"":df.format(dataConclusione.getTime())) + "; tasks: ";
		int i = 1;
		for(Task task : getTasks()) {
			toRet += i++ + ") " + task.toString();
		}
		return toRet;
	}

}
