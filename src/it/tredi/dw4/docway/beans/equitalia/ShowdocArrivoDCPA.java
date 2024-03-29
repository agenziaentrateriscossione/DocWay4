package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.docway.beans.ShowdocArrivo;
import it.tredi.dw4.docway.model.equitalia.ExtraDCP;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class ShowdocArrivoDCPA extends ShowdocArrivo {

	// campi specifici del repertori documento del ciclo passivo di equitalia
	private ExtraDCP extraDCP = new ExtraDCP();
	
	public ShowdocArrivoDCPA() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraDCP.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/equitalia/showdoc@arrivo@DCPA");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	/**
	 * invio del doc a NAV
	 * @return
	 * @throws Exception
	 */
	public String sendToDynamicsNav() throws Exception {
		try {
			
			formsAdapter.sendToDynamicsNav("repertoriDcw2Nav");
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				// si tratta di un errore nella comunicazione con NAV? in questo caso occorre personalizzare il messaggio di errore
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				Errormsg errormsg = (Errormsg) session.getAttribute("errormsg");
				if (errormsg != null && errormsg.getErrore() != null) {
					Errore errore = errormsg.getErrore();
					if (errore != null) {
						if (errore.getErrtype() != null && errore.getErrtype().contains(Const.RITORNO_ESITO_INVIO_MICROSOFT_DYNAMIC_NAV_CON_ERRORE))
							errore.setLevel(ErrormsgFormsAdapter.ERROR);
						errormsg.setErrore(errore);
						session.setAttribute("errormsg", errormsg);
					}
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public ExtraDCP getExtraDCP() {
		return extraDCP;
	}

	public void setExtraDCP(ExtraDCP extraDCP) {
		this.extraDCP = extraDCP;
	}
	
}
