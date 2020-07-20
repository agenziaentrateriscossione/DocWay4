package it.highwaytech.apps.xdocway.apv;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {
	private static final long serialVersionUID = -8284722894693593665L;

	private String name;
	private String label;
	private String id;
	private String state;
	private String description;
	private List<Candidate> candidates;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Candidate> getCandidates() {
		if(candidates == null)
			candidates = new ArrayList<Candidate>();
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
	
	public String getInAttesaDa() {
		String toRet = "";
		if(this.label != null) {
			int pos = this.label.indexOf(".");
			if(pos > 0)
				toRet = this.label.substring(0, pos);
			else
				toRet = this.label;
		}
		if(this.candidates != null && this.candidates.size() > 0) {
			//Per ora carico solo il primo candidato perche' dovrebbe i flussi sono fatti cosi
			//E' possibile ciclare per ottenere l'elenco dei candidati in tal caso va rivisto anche l'invio sollecito che dovra' essere inviato a tutti i candidati
			toRet += ". ";
			boolean write = false;
			for(Candidate candidate : getCandidates()) {
				if(!candidate.isTrovatoInAnagrafica())
					continue;
				if(write)
					toRet += ", ";
				toRet += candidate.getCognome();
				write = true;
			}
		}
		return toRet;
	}
	
	@Override
	public String toString() {
		String toRet = "Task - name: " + (name==null?"":name) + "; label: " + (label==null?"":label) + "; id: " + (id==null?"":id) + "; state: " + (state==null?"":state) + "; description: " + (description==null?"":description) + "; candidates:";
		int i = 1;
		for(Candidate candidate : getCandidates()) {
			toRet += " " + i++ + ") " + candidate.toString();
		}
		return toRet;
	}
}
