package it.tredi.dw4.docway.beans;

import it.tredi.consoledecretazioniordinifattureapv.DecretazioneApv;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvAreaCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvAreaDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvDirezioneCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvDirezioneDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvInAttesaCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvInAttesaDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvOggettoCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvOggettoDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvStatoCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvStatoDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApv;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvFornitoreCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvFornitoreDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvGiorniDataFatturaCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvGiorniDataFatturaDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvImportoCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvImportoDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvNumeroCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvNumeroDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvRepertorioDecretazioneCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvRepertorioDecretazioneDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvRepertorioOrdineCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.FatturaApvRepertorioOrdineDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.OrdineApv;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocWayConsoleFattureApv extends DocWayConsoleApv implements DocWayConsoleApvPaginatorInterface {
	private List<FatturaApv> fattureOrdered = new ArrayList<FatturaApv>();
	private List<FatturaApv> fattureNotOrdered = new ArrayList<FatturaApv>();
	private List<FatturaApv> fattureOnPage = new ArrayList<FatturaApv>();
	
	public DocWayConsoleFattureApv() throws Exception {
		super();
		//this.formsAdapter = new DocWayConsoleApvFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public List<FatturaApv> getFattureOnPage() {
		return fattureOnPage;
	}

	public List<FatturaApv> getFattureOrdered() {
		return fattureOrdered;
	}

	public int getTotRecords() {
		if(fattureNotOrdered == null)
			return 0;
		return fattureNotOrdered.size();
	}

	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		colSort = "giorni_df";
		
    	try {
            List<?> list = dom.selectNodes("/response/consolelistobj/java");
            if ( (null != list) && (list.size() == 1) ) {
                Element element = (Element)list.get(0);
                log.debug(element.asXML());
                
            	XMLDecoder decoder=null;
    			decoder = new XMLDecoder(new ByteArrayInputStream(element.asXML().getBytes()));
        		fattureNotOrdered = (ArrayList<FatturaApv>)decoder.readObject();
        		decoder.close();
        		this.paginator = new DocWayConsoleApvPaginator(this, fattureNotOrdered.size(), recordsForPage);
            } else {
            	//nessun dato trovato
            	fattureNotOrdered = null;
        		this.paginator = new DocWayConsoleApvPaginator(this, 0, recordsForPage);
            }
            order();
        }
        catch(Exception e) {
            log.error(e, e);
        }
    	
    }
	
	/**
	 * Filtro fatture
	 * 
	 * @return
	 * @throws Exception
	 */
	public String order() throws Exception {
		try {
			if(fattureNotOrdered == null) {
				fattureOrdered = new ArrayList<FatturaApv>();
            	return null;
			}
			//Effettuo la ricerca
			//if (checkSearchFilter()) return null;
			fattureOrdered = new ArrayList<FatturaApv>(fattureNotOrdered);
			if("direzione".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new EntityBaseApvDirezioneCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new EntityBaseApvDirezioneDecrescenteComparator());
			} else if("area".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new EntityBaseApvAreaCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new EntityBaseApvAreaDecrescenteComparator());
			} else if("fornitore".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvFornitoreCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvFornitoreDecrescenteComparator());
			} else if("numero".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvNumeroCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvNumeroDecrescenteComparator());
			} else if("importo".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvImportoCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvImportoDecrescenteComparator());
			} else if("giorni_df".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvGiorniDataFatturaCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvGiorniDataFatturaDecrescenteComparator());
			} else if("oggetto".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new EntityBaseApvOggettoCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new EntityBaseApvOggettoDecrescenteComparator());
			} else if("stato".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new EntityBaseApvStatoCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new EntityBaseApvStatoDecrescenteComparator());
			} else if("in_attesa".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new EntityBaseApvInAttesaCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new EntityBaseApvInAttesaDecrescenteComparator());
			} else if("rep_ord".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvRepertorioOrdineCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvRepertorioOrdineDecrescenteComparator());
			} else if("rep_decr".equals(colSort)) {
				if(ascSort)
					Collections.sort(fattureOrdered, new FatturaApvRepertorioDecretazioneCrescenteComparator());
				else
					Collections.sort(fattureOrdered, new FatturaApvRepertorioDecretazioneDecrescenteComparator());
			}
			
			paginator.gotoPage(1);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public boolean gotoPage(int pageNumber, int firstRecordOnPage, int lastRecordOnPage) {
		if(fattureNotOrdered == null) {
        	fattureOnPage = new ArrayList<FatturaApv>();
        	return false;
		}
		fattureOnPage = new ArrayList<FatturaApv>();
		if(fattureOrdered.size() < lastRecordOnPage - 1)
			return false;
		for(int i = firstRecordOnPage - 1; i < lastRecordOnPage; i++)
			fattureOnPage.add(fattureOrdered.get(i));
		
		return true;
	}
}
