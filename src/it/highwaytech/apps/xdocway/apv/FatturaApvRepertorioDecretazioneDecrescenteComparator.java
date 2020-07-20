package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class FatturaApvRepertorioDecretazioneDecrescenteComparator implements Comparator<FatturaApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(FatturaApv o1, FatturaApv o2) {
		return - o1.compareRepertorioDecretazione(o2);
    }
}
