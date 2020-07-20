package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class FatturaApvFornitoreCrescenteComparator implements Comparator<FatturaApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(FatturaApv o1, FatturaApv o2) {
		if(o1.getFornitore() == null) {
			if(o2.getFornitore() == null)
				return 0;
			else
				return -1;
		} else {
			if(o2.getFornitore() == null)
				return 1;
			return o1.getFornitore().compareToIgnoreCase(o2.getFornitore());			
		}
    }
}
