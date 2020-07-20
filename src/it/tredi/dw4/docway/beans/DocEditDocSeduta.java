package it.tredi.dw4.docway.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.delibere.SedutaDocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.delibere.DataOra;
import it.tredi.dw4.docway.model.delibere.Seduta;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public abstract class DocEditDocSeduta extends DocWayDocedit {
	
	protected SedutaDocDocWayDocEditFormsAdapter formsAdapter;
	protected String xml;
	protected Seduta doc = new Seduta();
	protected String docEditTitle = "";
	
	public abstract Seduta getDoc();
	
	// identifica se si sta eseguendo un inserimento (docEdit) o una modifica (docEditModify) di
	// un doc di DocWay
	public abstract boolean isDocEditModify();
	
	public DocEditDocSeduta() throws Exception {
		this.formsAdapter = new SedutaDocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		setErrorFieldIds(""); // Azzero i campi di error per la scheda corrente
		
		this.setXml(dom.asXML());
		this.setDoc((Seduta) XMLUtil.parseElement(dom, "/response/seduta" , new Seduta()));
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		if(chkSaveSeduta()){
			try{
				formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", isDocEditModify()));
				XMLDocumento response = super._saveDocument("seduta", "list_of_doc");
				
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				buildSpecificShowdocPageAndReturnNavigationRule("seduta",response);
				return "showdoc@seduta@reload";
			}
			catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;			
			}
		}
		return null;
	}
	
	private boolean chkSaveSeduta(){
		try{
			String pattern = "dd/MM/yyyy";
			Date oggi = new Date();
			oggi = new SimpleDateFormat(pattern).parse( new SimpleDateFormat(pattern).format(oggi));
			
			if(doc.getData_convocazione() != null && !doc.getData_convocazione().equals(""))
			{
				Date data_convocazione = new SimpleDateFormat(pattern).parse(doc.getData_convocazione());
				if(data_convocazione.compareTo(oggi) < 0){
					setErrorMessage("templateForm:dataConvocazione", I18N.mrs("dw4.errore_data_convocazione"));
					return false;
				}
				
				if(doc.getLimite_presentazione_proposte() != null && !doc.getLimite_presentazione_proposte().getData().equals(""))
				{
					Date data_presentazione = new SimpleDateFormat(pattern).parse(doc.getLimite_presentazione_proposte().getData());
					if(data_presentazione.compareTo(data_convocazione) > 0){
						setErrorMessage("templateForm:dataPresentazione_entro", I18N.mrs("dw4.errore_data_presentazione_entro"));
						return false;
					}
					
					if(data_presentazione.compareTo(oggi) < 0){
						setErrorMessage("templateForm:dataPresentazione_entro", I18N.mrs("dw4.errore_data_presentazione"));
						return false;
					}
				}
			}else{
				setErrorMessage("templateForm:dataConvocazione", I18N.mrs("dw4.errore_manca_data_convocazione"));
				return false;
			}
			
			if(doc.getLimite_presentazione_proposte() != null && !doc.getLimite_presentazione_proposte().getOra().equals("")){
				if(StringUtil.timeValue(doc.getLimite_presentazione_proposte().getOra()).equals("")){
					setErrorMessage("templateForm:oraPresentazione_entro", I18N.mrs("dw4.ora_non_corretta"));
					return false;
				}else
				{
					doc.getLimite_presentazione_proposte().setOra(StringUtil.timeValue(doc.getLimite_presentazione_proposte().getOra()));
					checkHour(doc.getLimite_presentazione_proposte().getOra(),"templateForm:oraPresentazione_entro");
				}
			}
			
//			if(doc.getLimite_presentazione_proposte() != null && !doc.getLimite_presentazione_proposte().getOra().equals("")){
//				if(StringUtil.timeValue(doc.getLimite_presentazione_proposte().getOra()).equals("")){
//					setErrorMessage("templateForm:oraPresentazione_entro", I18N.mrs("dw4.ora_non_corretta"));
//					return false;
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean checkHour(String time, String id){
		try{
			int hh = Integer.parseInt(time.substring(0,2));
			if(hh >= 0 && hh < 24){
				int mm = Integer.parseInt(time.substring(3,5));
				if(mm >= 0 && mm < 60){
					int ss = Integer.parseInt(time.substring(6));
					if(ss >= 0 && ss < 60){
						return true;
					}else
					{
						setErrorMessage(id, I18N.mrs("dw4.ora_ss_non_corretta"));
						return false;
					}
				}else{
					setErrorMessage(id, I18N.mrs("dw4.ora_mm_non_corretta"));
					return false;
				}
			}else{
				setErrorMessage(id, I18N.mrs("dw4.ora_hh_non_corretta"));
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String clearDocument() throws Exception {
		if(!isDocEditModify()){
			//inserimento
			doc.setStraordinaria(false);
			doc.setData_convocazione("");
			doc.setLimite_presentazione_proposte(new DataOra());
			doc.setPubblicazione_odg_dal(new DataOra());
			doc.setPubblicazione_odg_al(new DataOra());
			doc.setNote("");
		}else{
			try{
				XMLDocumento response = super._clearDocument();
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				buildSpecificShowdocPageAndReturnNavigationRule("seduta",response);
				return "showdoc@seduta@reload";
			}
			catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;			
			}
		}
		return null;
	}

	/*
	 * getter / setter
	 * */
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getDocEditTitle() {
		return docEditTitle;
	}

	public void setDocEditTitle(String docEditTitle) {
		this.docEditTitle = docEditTitle;
	}

	public void setFormsAdapter(SedutaDocDocWayDocEditFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}

	public void setDoc(Seduta doc) {
		this.doc = doc;
	}
}
