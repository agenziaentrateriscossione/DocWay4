package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.consoledecretazioniordinifattureapv.Candidate;
import it.tredi.consoledecretazioniordinifattureapv.EntityBaseApv;
import it.tredi.consoledecretazioniordinifattureapv.Task;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayConsoleApvFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.dom4j.Document;

public class DocWayConsoleApv extends Page {
	protected static Logger log = Logger.getRootLogger();

	protected int recordsForPage = 12;
	protected DocWayConsoleApvPaginator paginator;
	protected DocWayConsoleApvFormsAdapter formsAdapter;
	protected String xml = "";
	protected EntityBaseApv current;	
	protected String colSort = "direzione";
	protected boolean ascSort = false;

	private boolean visibleInvioSollecito = false;
	private boolean invioSollecitoSentOK = false;

	public DocWayConsoleApv() throws Exception {
		this.formsAdapter = new DocWayConsoleApvFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public DocWayConsoleApvPaginator getPaginator() {
		return paginator;
	}

	public void setPaginator(DocWayConsoleApvPaginator paginator) {
		this.paginator = paginator;
	}

	public DocWayConsoleApvFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public EntityBaseApv getCurrent() {
		return current;
	}
	
	public void setCurrent(EntityBaseApv current) {
		this.current = current;
	}

	public String getColSort() {
		return colSort;
	}

	public void setColSort(String colSort) {
		this.colSort = colSort;
	}

	public boolean isAscSort() {
		return ascSort;
	}

	public void setAscSort(boolean ascSort) {
		this.ascSort = ascSort;
	}
	
	public boolean isVisibleInvioSollecito() {
		return visibleInvioSollecito;
	}

	public void setVisibleInvioSollecito(boolean visibleInvioSollecito) {
		this.visibleInvioSollecito = visibleInvioSollecito;
	}

	public boolean isInvioSollecitoSentOK() {
		return invioSollecitoSentOK;
	}

	public void setInvioSollecitoSentOK(boolean invioSollecitoSentOK) {
		this.invioSollecitoSentOK = invioSollecitoSentOK;
	}

	public String hideInviaSollecito() throws Exception {
		visibleInvioSollecito = false;
		invioSollecitoSentOK = false;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml 					= dom.asXML();
    	
    	String strRecordsForePage 		= XMLUtil.parseStrictAttribute(dom, "/response/@recordsForPageConsoleAPV", "12");
    	recordsForPage = Integer.parseInt(strRecordsForePage);
    }
	
	/**
	 * lettura dell'attributo colSort passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSortHeader(ActionEvent event){
		String colSelected = (String) event.getComponent().getAttributes().get("colSort");
		if (colSelected == null)
			colSelected = "";
		
		if (colSelected.equals(colSort)) {
			ascSort = !ascSort;
		}
		else {
			colSort = colSelected;
			if (colSort.equals("date"))
				ascSort = false;
			else
				ascSort = true;
		}
	}

	public String inviaSollecito() throws Exception {
		try {
			if(current != null && current.isLinkInviaSollecitoShow()) {
				// sendWithMatricola(String subject, String body, String sourceAddr, String matricolaDestinatario)
				
				//vedi properties
				//customEmailSEnder
				//“Hai ricevuto un sollecito sul documento     ” <link DocWay al documento>    “ che trovi in attesa di essere processato all’interno dei workflow a te assegnati”;
				//l’oggetto è: “Documentale - sollecito”
				String matricole = "";
				boolean write = false;
				for(Task task : current.getProcess().getTasks()) {
					for(Candidate candidate : task.getCandidates()) {
						if(!candidate.isTrovatoInAnagrafica())
							continue;
						if(write)
							matricole += "#|#";
						matricole += candidate.getCode();
						write = true;
					}
				}
				formsAdapter.invioEmailSollecito(matricole, current.getNrecord(), I18N.mrs("apv.email_oggetto"), I18N.mrs("apv.email_testo"));
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
		    	try {
			    	//Element attodecretazione = DocumentHelper.createElement("invionotificaerror");
		            List<?> list = response.getDocument().selectNodes("/response/invionotificaerror");
		            if ( (null != list) && (list.size() == 1) ) {
		                //Element element = (Element)list.get(0);
		            	invioSollecitoSentOK = false;
		            } else {
		            	//nessun errore trovato
		            	invioSollecitoSentOK = true;
		            }
		        }
		        catch(Exception e) {
		            log.error(e, e);
		        }
				
				visibleInvioSollecito = true;
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}

	
}
