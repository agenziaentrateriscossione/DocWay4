package it.tredi.dw4.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Raggruppamento di titoli (legati allo stesso oggetto). Utilizzato per il raggruppamento di risultati
 * derivanti da un lookup su mittente in DocWay o su voci di indice all'interno dei quali i titoli risultanti
 * da una ricerca devono essere raggruppati
 */
public class TitoloComposto {

	private List<Titolo> titoli;	
	
	public TitoloComposto() {
		this.titoli = new ArrayList<Titolo>();
	}
    
	public List<Titolo> getTitoli() {
		return titoli;
	}

	public void setTitoli(List<Titolo> titoli) {
		this.titoli = titoli;
	}
	
	public void addTitolo(Titolo titolo) {
		titoli.add(titolo);
	}

}
