package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.ReloadMsg;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayAlboExtFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class DocWayAlboExt extends DocEdit {
	private DocWayAlboExtFormsAdapter formsAdapter;
	private String dataPubblicazione;
	private String dataDefissione;
	private boolean visible = false;
	private Showdoc showdoc;
	
	public DocWayAlboExt() throws Exception {
		this.formsAdapter = new DocWayAlboExtFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		visible = true;
	}
	
	public DocWayAlboExtFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public void init(Document dom) {
		// TODO Auto-generated method stub
	}

	@Override
	public String saveDocument() throws Exception {
		return null;			
	}

	/**
	 * Annullamento e ritorno sulla showdoc del documento
	 */
	@Override
	public String clearDocument() throws Exception {
		visible = false;
		setSessionAttribute("postit", null);
		setErrorFieldIds("");
		return null;
	}
	
	/**
	 * Conferma di pubblicazione su albo-online esterno del documento
	 * @return
	 * @throws Exception
	 */
	public String pubblicaAlboExt() throws Exception{
		if (!checkRequiredFields()) {
			return null;
		}
		
		formsAdapter.pubblicaAlboExt(dataPubblicazione, dataDefissione);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		visible = false;
		
		// lettura del messaggio di ritorno
		ReloadMsg message = new ReloadMsg();
		message.setActive(true);
		message.init(response.getDocument());
		message.setLevel(Const.MSG_LEVEL_SUCCESS);
		
		if (null != showdoc) {
				showdoc._reloadWithoutNavigationRule();
		}
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("reloadmsg", message);
		return null;
	}
	
	private boolean checkRequiredFields(){
		try{
			String pattern = "dd/MM/yyyy";
			Date oggi = new Date();
			oggi = new SimpleDateFormat(pattern).parse(new SimpleDateFormat(pattern).format(oggi));
			
			if(dataPubblicazione != null && !dataPubblicazione.equals(""))
			{
				Date data_pubblicazione = new SimpleDateFormat(pattern).parse(dataPubblicazione);
				if(data_pubblicazione.compareTo(oggi) < 0){
					setErrorMessage("templateForm:dataPubblicazione", I18N.mrs("dw4.errore_data_pubblicazione"));
					return false;
				}
				
				if(dataDefissione != null && !dataDefissione.equals(""))
				{
					Date data_defissione = new SimpleDateFormat(pattern).parse(dataDefissione);
					if(data_pubblicazione.compareTo(data_defissione) > 0){
						setErrorMessage("templateForm:dataDefissione", I18N.mrs("dw4.errore_data_defissione"));
						return false;
					}
				}
			}else{
				setErrorMessage("templateForm:dataPubblicazione", I18N.mrs("dw4.errore_manca_data_pubblicazione"));
				return false;
			}
		}catch(Exception e){
			
		}
		
		return true;
	}
	
	/*
	 * getter/setter
	 * */
	
	public String getDataPubblicazione() {
		return dataPubblicazione;
	}

	public void setDataPubblicazione(String dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public String getDataDefissione() {
		return dataDefissione;
	}

	public void setDataDefissione(String dataDefissione) {
		this.dataDefissione = dataDefissione;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}

	public Showdoc getShowdoc() {
		return showdoc;
	}

}
