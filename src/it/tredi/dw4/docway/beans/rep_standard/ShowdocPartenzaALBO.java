package it.tredi.dw4.docway.beans.rep_standard;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocPartenza;
import it.tredi.dw4.docway.model.rep_standard.Richiedente;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Pagina di visualizzazione del repertorio ALBO
 */
public class ShowdocPartenzaALBO extends ShowdocPartenza {

	// campi specifici del repertorio di richiesta pubblicazione albo online
	private Richiedente richiedente = new Richiedente();
	
	public ShowdocPartenzaALBO() throws Exception {
		super();
	}

	@Override
	public void init(Document dom) {
		super.init(dom);
		
		richiedente.init(XMLUtil.createDocument(dom, "/response/doc/extra/richiedente")); // richiedente interno
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/rep_standard/showdoc@partenza@ALBO");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	public Richiedente getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}
	
	/**
	 * Ritiro del bando
	 * @return
	 * @throws Exception
	 */
	public String ritiraBando() throws Exception {
		try {
			formsAdapter.ritiraBando();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			_reloadWithoutNavigationRule();
			return null;
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

}
