package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class OrdineApvRepertorioDecretazioneDecrescenteComparator implements Comparator<OrdineApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(OrdineApv o1, OrdineApv o2) {
		if(o1.getRepertorioDecretazione() == null) {
			if(o2.getRepertorioDecretazione() == null)
				return 0;
			else
				return 1;
		} else {
			if(o2.getRepertorioDecretazione() == null)
				return -1;
			return - o1.getRepertorioDecretazione().compareTo(o2.getRepertorioDecretazione());
		}
    }
}
