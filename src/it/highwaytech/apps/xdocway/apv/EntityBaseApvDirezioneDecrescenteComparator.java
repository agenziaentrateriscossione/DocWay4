package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class EntityBaseApvDirezioneDecrescenteComparator implements Comparator<EntityBaseApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(EntityBaseApv o1, EntityBaseApv o2) {
		if(o1.getDirezione() == null) {
			if(o2.getDirezione() == null)
				return 0;
			else
				return 1;
		} else {
			if(o2.getDirezione() == null)
				return -1;
			return - o1.getDirezione().compareTo(o2.getDirezione());			
		}
    }
}
