package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.docway.beans.ShowdocPartenza;
import it.tredi.dw4.docway.model.equitalia.ExtraFTRA;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class ShowdocPartenzaFTRA extends ShowdocPartenza {

	// campi specifici del repertori fatture di equitalia
	private ExtraFTRA extraFTRA = new ExtraFTRA();
	
	public ShowdocPartenzaFTRA() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraFTRA.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/equitalia/showdoc@partenza@FTRA");
	}
	
	/**
	 * invio del doc a NAV
	 * @return
	 * @throws Exception
	 */
	public String sendToDynamicsNav() throws Exception {
		try {
			
			formsAdapter.sendToDynamicsNav("salseDcw2Nav");
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
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	public ExtraFTRA getExtraFTRA() {
		return extraFTRA;
	}

	public void setExtraFTRA(ExtraFTRA extraFTR) {
		this.extraFTRA = extraFTR;
	}
	
}
