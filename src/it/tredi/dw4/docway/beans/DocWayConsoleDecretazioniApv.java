package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.consoledecretazioniordinifattureapv.DecretazioneApv;
import it.tredi.consoledecretazioniordinifattureapv.DecretazioneApvDataCrescenteComparator;
import it.tredi.consoledecretazioniordinifattureapv.DecretazioneApvDataDecrescenteComparator;
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
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocWayConsoleDecretazioniApv extends DocWayConsoleApv implements DocWayConsoleApvPaginatorInterface {
	private List<DecretazioneApv> decretazioniNotOrdered;
	private List<DecretazioneApv> decretazioniOrdered;
	private List<DecretazioneApv> decretazioniOnPage;

	private boolean visibleAtto = false;
	
	public DocWayConsoleDecretazioniApv() throws Exception {
		super();
		//this.formsAdapter = new DocWayConsoleApvFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public List<DecretazioneApv> getDecretazioniOnPage() {
		return decretazioniOnPage;
	}

	public List<DecretazioneApv> getDecretazioniOrdered() {
		return decretazioniOrdered;
	}
	
	public int getTotRecords() {
		if(decretazioniNotOrdered == null)
			return 0;
		return decretazioniNotOrdered.size();
	}

	public boolean isVisibleAtto() {
		return visibleAtto;
	}

	public void setVisibleAtto(boolean visibleAtto) {
		this.visibleAtto = visibleAtto;
	}

	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		colSort = "rep_decr";
    	
    	try {
            List<?> list = dom.selectNodes("/response/consolelistobj/java");
            if ( (null != list) && (list.size() == 1) ) {
                Element element = (Element)list.get(0);
                log.debug(element.asXML());
                
            	XMLDecoder decoder=null;
    			decoder = new XMLDecoder(new ByteArrayInputStream(element.asXML().getBytes()));
    			decretazioniNotOrdered = (ArrayList<DecretazioneApv>)decoder.readObject();
        		decoder.close();
        		this.paginator = new DocWayConsoleApvPaginator(this, decretazioniNotOrdered.size(), recordsForPage);
            } else {
            	//nessun dato trovato
            	decretazioniNotOrdered = null;
        		this.paginator = new DocWayConsoleApvPaginator(this, 0, recordsForPage);
            }
    		order();
        }
        catch(Exception e) {
            log.error(e, e);
        }
    }
	
	public String hideAtto() throws Exception {
		visibleAtto = false;
		return null;
	}
	
	public String showAtto() throws Exception {
		try {
			if(current != null && current.getProcess() != null) {
				DecretazioneApv decretazione = (DecretazioneApv)current;
				formsAdapter.loadContenutoAtto(current.getProcess().getInstanceId());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
		    	try {
		    		//Element attodecretazione = DocumentHelper.createElement("atto");
		            List<?> list = response.getDocument().selectNodes("/response/atto");
		            if ( (null != list) && (list.size() == 1) ) {
		                Element element = (Element)list.get(0);
		                decretazione.setContenutoAtto(element.getTextTrim());
						visibleAtto = true;
						if(decretazione.getContenutoAtto() == null) {
							//carico i dati
						}
		            } else {
		            	//nessun dato trovato
		            }
		        }
		        catch(Exception e) {
		            log.error(e, e);
		        }
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}
	
	/**
	 * Filtro decretazioni
	 * 
	 * @return
	 * @throws Exception
	 */
	public String order() throws Exception {
		try {
			if(decretazioniNotOrdered == null) {
            	decretazioniOrdered = new ArrayList<DecretazioneApv>();
            	return null;
			}
			//Effettuo la ricerca
			//if (checkSearchFilter()) return null;
			decretazioniOrdered = new ArrayList<DecretazioneApv>(decretazioniNotOrdered);
			if("direzione".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvDirezioneCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvDirezioneDecrescenteComparator());
			} else if("area".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvAreaCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvAreaDecrescenteComparator());
			
			} else if("rep_decr".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvFormattedRepertorioCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvFormattedRepertorioDecrescenteComparator());
			} else if("data".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new DecretazioneApvDataCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new DecretazioneApvDataDecrescenteComparator());
			} else if("oggetto".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvOggettoCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvOggettoDecrescenteComparator());
			} else if("stato".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvStatoCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvStatoDecrescenteComparator());
			} else if("in_attesa".equals(colSort)) {
				if(ascSort)
					Collections.sort(decretazioniOrdered, new EntityBaseApvInAttesaCrescenteComparator());
				else
					Collections.sort(decretazioniOrdered, new EntityBaseApvInAttesaDecrescenteComparator());
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
		if(decretazioniNotOrdered == null) {
        	decretazioniOnPage = new ArrayList<DecretazioneApv>();
        	return false;
		}
		decretazioniOnPage = new ArrayList<DecretazioneApv>();
		if(decretazioniOrdered.size() < lastRecordOnPage - 1)
			return false;
		for(int i = firstRecordOnPage - 1; i < lastRecordOnPage; i++)
			decretazioniOnPage.add(decretazioniOrdered.get(i));
		
		return true;
	}
}
