package it.tredi.dw4.docway.beans;

import it.tredi.consoledecretazioniordinifattureapv.DecretazioneApv;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvAreaCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvAreaDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvDirezioneCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvDirezioneDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvFormattedRepertorioCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvFormattedRepertorioDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvInAttesaCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvInAttesaDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvOggettoCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvOggettoDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvStatoCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApvStatoDecrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.OrdineApv;
import it.tredi.consoledecretazioniordinifattureapv.OrdineApvRepertorioDecretazioneCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.OrdineApvRepertorioDecretazioneDecrescenteComparator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocWayConsoleOrdiniApv extends DocWayConsoleApv implements DocWayConsoleApvPaginatorInterface {
	private List<OrdineApv> ordiniNotOrdered = new ArrayList<OrdineApv>();
	private List<OrdineApv> ordiniOrdered = new ArrayList<OrdineApv>();
	private List<OrdineApv> ordiniOnPage = new ArrayList<OrdineApv>();
	
	public DocWayConsoleOrdiniApv() throws Exception {
		super();
		//this.formsAdapter = new DocWayConsoleApvFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public List<OrdineApv> getOrdiniOnPage() {
		return ordiniOnPage;
	}

	public List<OrdineApv> getOrdiniOrdered() {
		return ordiniOrdered;
	}

	public int getTotRecords() {
		if(ordiniNotOrdered == null)
			return 0;
		return ordiniNotOrdered.size();
	}

	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		colSort = "rep_ord";
    	
    	try {
            List<?> list = dom.selectNodes("/response/consolelistobj/java");
            if ( (null != list) && (list.size() == 1) ) {
                Element element = (Element)list.get(0);
                log.debug(element.asXML());
                
            	XMLDecoder decoder=null;
    			decoder = new XMLDecoder(new ByteArrayInputStream(element.asXML().getBytes()));
        		ordiniNotOrdered = (ArrayList<OrdineApv>)decoder.readObject();
        		decoder.close();
        		this.paginator = new DocWayConsoleApvPaginator(this, ordiniNotOrdered.size(), recordsForPage);
            } else {
            	//nessun dato trovato
            	ordiniNotOrdered = null;
        		this.paginator = new DocWayConsoleApvPaginator(this, 0, recordsForPage);
            }
            order();
        }
        catch(Exception e) {
            log.error(e, e);
        }
    }
	
	/**
	 * Filtro ordini
	 * 
	 * @return
	 * @throws Exception
	 */
	public String order() throws Exception {
		try {
			if(ordiniNotOrdered == null) {
				ordiniOrdered = new ArrayList<OrdineApv>();
            	return null;
			}
			//Effettuo la ricerca
			//if (checkSearchFilter()) return null;
			ordiniOrdered = new ArrayList<OrdineApv>(ordiniNotOrdered);
			if("direzione".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvDirezioneCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvDirezioneDecrescenteComparator());
			} else if("area".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvAreaCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvAreaDecrescenteComparator());
			
			} else if("rep_decr".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new OrdineApvRepertorioDecretazioneCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new OrdineApvRepertorioDecretazioneDecrescenteComparator());
			} else if("rep_ord".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvFormattedRepertorioCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvFormattedRepertorioDecrescenteComparator());
			} else if("oggetto".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvOggettoCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvOggettoDecrescenteComparator());
			} else if("stato".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvStatoCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvStatoDecrescenteComparator());
			} else if("in_attesa".equals(colSort)) {
				if(ascSort)
					Collections.sort(ordiniOrdered, new EntityBaseApvInAttesaCrescenteComparator());
				else
					Collections.sort(ordiniOrdered, new EntityBaseApvInAttesaDecrescenteComparator());
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
		if(ordiniNotOrdered == null) {
        	ordiniOnPage = new ArrayList<OrdineApv>();
        	return false;
		}
		ordiniOnPage = new ArrayList<OrdineApv>();
		if(ordiniOrdered.size() < lastRecordOnPage - 1)
			return false;
		for(int i = firstRecordOnPage - 1; i < lastRecordOnPage; i++)
			ordiniOnPage.add(ordiniOrdered.get(i));
		
		return true;
	}
}
