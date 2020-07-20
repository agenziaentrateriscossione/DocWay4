package it.tredi.dw4.dwrep.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.TitoloRepertorio;

import org.dom4j.Document;

public class DwrepQueryGlobale extends QueryGlobale {

	private String repCodes = "";
	
	public DwrepQueryGlobale() throws Exception {
		super();
	}
	
	public String getRepCodes() {
		return repCodes;
	}

	public void setRepCodes(String repCodes) {
		this.repCodes = repCodes;
	}

	@Override
	public void init(Document dom) {
		super.init(dom);
		
		// forzatura della ricerca su repertori (con pulizia dei repertori da mostrare all'utente)
		setRepertori_multipli(true);
		cleanRepertori(repCodes.split(","));
		
		setEstremi_protocollo(false); // nascosta la sezione relativa agli estremi di protocollo
	}
	
	/**
	 * Elimina dalla lista di tutti i repertori configurati su docway che non sono stati richiesti
	 * nella gestione della microapplicazione crud su repertori
	 * 
	 * @param codes array di codici di repertori da gestire su app crud
	 */
	private void cleanRepertori(String[] codes) {
		if (codes != null && codes.length > 0) {
			List<TitoloRepertorio> reps = getListof_rep();
			List<TitoloRepertorio> toRemove = new ArrayList<TitoloRepertorio>();
			for (TitoloRepertorio rep: reps) {
				if (rep.getCodice() != null) {
	        		boolean trovato = false;
	        		for (int j=0; j<codes.length; j++)
	        			if (codes[j] != null && !codes[j].equals("") && codes[j].equals(rep.getCodice()))
	        				trovato = true;
	        		
	        		if (!trovato)
	        			toRemove.add(rep); // se il repertorio corrente non e' fra quelli da gestire deve essere eliminato dalla lista
	        	}
			}
			reps.removeAll(toRemove);
			setListof_rep(reps);
		}
	}
	
	/**
	 * personalizzazione della query globale su repertori gestiti
	 * @return
	 */
	@Override
	public String createQuery() throws Exception {
		String query = super.createQuery();
		
		if (query != null) {
			if (query.length() > 0) // nel caso sia presente una query di base dei documenti viene riaggiunto l'operatore finale
				query += " AND ";

			// nessun repertorio selezionato dall'utente ...
			if (getCustom_repertorio() == null || getCustom_repertorio().equals("")) {
				// ... si forza la ricerca su tutti i repertori disponibili
				
				String repertoriCompleti = "";
				for (int i=1; i < getListof_rep().size(); i++){
		        	TitoloRepertorio rep = getListof_rep().get(i);
		        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
		        		for (int j=0; j < rep.getList_tabelle().size(); j++)
		        			repertoriCompleti += rep.getCodice() + "-" + rep.getList_tabelle().get(j).getTipo() + "%";
		        	}
		        }
				
				String repertorioId[] = repertoriCompleti.split("%");
	            String repertorio = "";
	            String repTable = "";
	            int separator;
	            String query1 = "";
	            for ( int is = 0; is < repertorioId.length; is++ ){
	                repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
	                if ( separator != -1 ){
	                    repTable = repertorioId[is].substring(separator+1);
	                    if ( "A".equals(repTable) ) repTable = "arrivo" ;
	                    else if ( "I".equals(repTable) ) repTable = "interno" ;
	                    else if ( "P".equals(repTable) ) repTable = "partenza" ;
	                    else if ( "V".equals(repTable) ) repTable = "varie" ;
	                }
	                if (query1.equals(""))
	                    query1 = "([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	                else
	                    query1 += " OR ([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	            }
	            
	            if (query1.trim().length() > 0) 
	            	query = query1 + " AND ";
			}
			
			if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query = query.substring(0, query.length()-4);
		}
		
		return query;
	}
	
}
