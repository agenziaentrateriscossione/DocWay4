package it.tredi.dw4.docway.beans.condotte;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocVarie;
import it.tredi.dw4.docway.model.condotte.Dati_approvazione;
import it.tredi.dw4.docway.model.condotte.Dati_contabili;
import it.tredi.dw4.docway.model.condotte.Dati_fattura;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocVarieFPN extends ShowdocVarie {

	// campi specifici del repertorio
	private Dati_fattura dati_fattura = new Dati_fattura();
	private Dati_contabili dati_contabili = new Dati_contabili();
	private Dati_approvazione dati_approvazione = new Dati_approvazione();
	
	private String showDivs = ""; // definizione di pulsanti da mostrare in visualizzazione (altrimenti nascosti)
	
	public ShowdocVarieFPN() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		dati_fattura.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_fattura"));
		dati_contabili.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_contabili"));
		dati_approvazione.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_approvazione"));
		
		// caricamento di eventuali pulsanti da mostrare in visualizzazione della fattura
		String toDo = XMLUtil.parseStrictAttribute(dom, "/response/@toDo");
		if (toDo.startsWith("showDivs")) {
			showDivs = toDo.substring(9, toDo.length()-1);
			if (showDivs.length() > 0 && !showDivs.endsWith(","))
				showDivs += ",";
		}
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/condotte/showdoc@varie@FPN");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		
		// non e' possibile utilizzare il metodo nuovoRepertorio() perche' su questo ci sono personalizzazioni anche
		// a livello di BusinessLogic
		//return nuovoRepertorio();
		
		try {
			formsAdapter.insTableDocRepFPN(getDoc().getTipo(), getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
			
			XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(responseDoc)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), responseDoc, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * verifica del documento
	 * @return
	 * @throws Exception
	 */
	public String verificaDoc() throws Exception {
		_modifyTableDoc("verifica");
		return "condotte_docEdit@modify@varie_edit-show@FPN";
	}
	
	/**
	 * approvazione del documento
	 * @return
	 * @throws Exception
	 */
	public String approvaDoc() throws Exception {
		_modifyTableDoc("approva");
		return "condotte_docEdit@modify@varie_edit-show@FPN";
	}
	
	/**
	 * personalizzazione della modifica del repertorio FPN (non utilizzato il metodo generico perche' su questo
	 * repertorio e' stata implementata anche una personalizzazione a livello di BusinessLogic)
	 */
	@Override
	public String modifyTableDoc() throws Exception {
		return _modifyTableDoc("");
	}
	
	/**
	 * chiamata di modifica al service in base al pulsante selezionato
	 * 
	 * @param button pulsante selezionato per la modifica (tipologia di modifica)
	 * @return
	 * @throws Exception
	 */
	private String _modifyTableDoc(String button) throws Exception {
		this.formsAdapter.modifyTableDocFPN(getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText(), button);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
	}
		
	public Dati_fattura getDati_fattura() {
		return dati_fattura;
	}

	public void setDati_fattura(Dati_fattura dati_fattura) {
		this.dati_fattura = dati_fattura;
	}

	public Dati_contabili getDati_contabili() {
		return dati_contabili;
	}

	public void setDati_contabili(Dati_contabili dati_contabili) {
		this.dati_contabili = dati_contabili;
	}

	public Dati_approvazione getDati_approvazione() {
		return dati_approvazione;
	}

	public void setDati_approvazione(Dati_approvazione dati_approvazione) {
		this.dati_approvazione = dati_approvazione;
	}
	
	public String getShowDivs() {
		return showDivs;
	}

	public void setShowDivs(String showDivs) {
		this.showDivs = showDivs;
	}

}
