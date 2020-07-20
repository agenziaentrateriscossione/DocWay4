package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class EntityBaseApvInAttesaDecrescenteComparator implements Comparator<EntityBaseApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(EntityBaseApv o1, EntityBaseApv o2) {
		if(o1.getProcess() == null) {
			if(o2.getProcess() == null)
				return 0;
			else
				return 1;
		} else {
			if(o2.getProcess() == null)
				return -1;
			return - o1.getProcess().compareInAttesaTo(o2.getProcess());			
		}
    }
	
	/*
	public String getInAttesaDa() {
		String toRet = "";
		if(this.getProcess() != null) {
			if(this.getProcess().getStato() == StatoFlusso.Finished) {
				toRet = df.format(this.getProcess().getDataConclusione().getTime()); 
			} else {
				//il flusso e' attivo
				if(this.getProcess().getTasks() != null && this.getProcess().getTasks().size() > 0) {
					//Dovrebbe esserci un solo task anche se ho previsto che ne possa essere presente contemporaneamente piu' di uno
					Task task = this.getProcess().getTasks().get(0);
					toRet = task.getInAttesaDa();
				}
			}
		}
		return toRet;
	}
	*/
}
