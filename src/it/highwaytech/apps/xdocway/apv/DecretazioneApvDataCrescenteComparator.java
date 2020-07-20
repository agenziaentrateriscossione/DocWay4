package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class DecretazioneApvDataCrescenteComparator implements Comparator<DecretazioneApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(DecretazioneApv o1, DecretazioneApv o2) {
		if(o1.getData() == null) {
			if(o2.getData() == null)
				return 0;
			else
				return -1;
		} else {
			if(o2.getData() == null)
				return 1;
			return o1.getData().compareTo(o2.getData());
		}
    }
}
