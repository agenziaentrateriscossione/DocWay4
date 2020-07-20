package it.highwaytech.apps.xdocway.apv;

import java.util.Comparator;

public class FatturaApvImportoCrescenteComparator implements Comparator<FatturaApv> {
	//Ritorno -1 se o1 e' minore di o2; 0 se o1 e' uguale a o2, 1 se o1 e' maggiore do o2 
	@Override
    public int compare(FatturaApv o1, FatturaApv o2) {
		if(o1.getImporto() == null) {
			if(o2.getImporto() == null)
				return 0;
			else
				return -1;
		} else {
			if(o2.getImporto() == null)
				return 1;
			try {
				double o1Imp = Double.valueOf(o1.getImporto());
				double o2Imp = Double.valueOf(o2.getImporto());
				double diff = o1Imp - o2Imp;
				if(diff < 0)
					return -1;
				else if(diff == 0)
					return 0;
				else
					return 1;
			} catch (NumberFormatException ex) {
				//impossibile convertire il valore in double comparo come stringa
			}
			return o1.getImporto().compareToIgnoreCase(o2.getImporto());			
		}
    }
}
