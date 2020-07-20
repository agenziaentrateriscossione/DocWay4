package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.i18n.I18N;

import org.dom4j.Document;

public class QueryTrasferimento extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String vuor = "";
	private String vrpa = "";
	private String nuor = "";
	private String nrpa = "";
	private boolean lvrpa = true; // verifica la presenza del vecchio UOR/RPA
	private boolean cc = false; // anche CC, CDS, OP e OPM
	private boolean bozz = false; // anche bozze di documenti
	private boolean ann = false; // anche documenti annullati
		
	public QueryTrasferimento() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getVuor() {
		return vuor;
	}

	public void setVuor(String vuor) {
		this.vuor = vuor;
	}

	public String getVrpa() {
		return vrpa;
	}

	public void setVrpa(String vrpa) {
		this.vrpa = vrpa;
	}

	public String getNuor() {
		return nuor;
	}

	public void setNuor(String nuor) {
		this.nuor = nuor;
	}

	public String getNrpa() {
		return nrpa;
	}

	public void setNrpa(String nrpa) {
		this.nrpa = nrpa;
	}
	
	public boolean isLvrpa() {
		return lvrpa;
	}

	public void setLvrpa(boolean lvrpa) {
		this.lvrpa = lvrpa;
	}

	public boolean isCc() {
		return cc;
	}

	public void setCc(boolean cc) {
		this.cc = cc;
	}

	public boolean isBozz() {
		return bozz;
	}

	public void setBozz(boolean bozz) {
		this.bozz = bozz;
	}

	public boolean isAnn() {
		return ann;
	}

	public void setAnn(boolean ann) {
		this.ann = ann;
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Azzeramento dei campo del form di trasferimento massivo di
	 * responsabilita' di fascicoli e documenti
	 */
	private void clearFields() {
		vuor = "";
		nuor = "";
		vrpa = "";
		nrpa = "";
		lvrpa = true;
		cc = false;
		ann = false;
		bozz = false;
	}
	
	/**
	 * Trasferimento massivo di responsabilita' di fascicoli e documenti
	 * @return
	 * @throws Exception
	 */
	public String trasferimentoMassivo() throws Exception {
		try {
			if (checkRequiredFields()) return null;
			
		    String enc_string = "";
		    enc_string += lvrpa ? "1" : "0";
		    enc_string += bozz ? "1" : "0";
		    enc_string += cc ? "1" : "0";
		    enc_string += ann ? "1" : "0";
		    enc_string += vuor + "|" + vrpa + "§" + nuor + "|" + nrpa;
		    formsAdapter.trasferimentoMassivo(enc_string);
		    
		    XMLDocumento response = this.formsAdapter.getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
			}
			
			// azzeramento dei campi del form
			clearFields();
		    
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkRequiredFields() {
		boolean result = false;
		
		if (vuor == null || vuor.length() == 0) {
			this.setErrorMessage("templateForm:vecchioUor", I18N.mrs("dw4.occorre_valorizzare_il_campo") + " '" + I18N.mrs("dw4.vecchio_uor") + "'");
			result = true;
		}
		if (nuor == null || nuor.length() == 0) {
			this.setErrorMessage("templateForm:nuovoUor", I18N.mrs("dw4.occorre_valorizzare_il_campo") + " '" + I18N.mrs("dw4.nuovo_uor") + "'");
			result = true;
		}
		if ((vrpa == null || vrpa.length() == 0) && (nrpa != null && nrpa.length() > 0)) {
			this.setErrorMessage("templateForm:vecchioRpa", I18N.mrs("dw4.occorre_valorizzare_il_campo") + " '" + I18N.mrs("dw4.vecchio_rpa") + "'");
			result = true;
		}
		if ((nrpa == null || nrpa.length() == 0) && (vrpa != null && vrpa.length() > 0)) {
			this.setErrorMessage("templateForm:nuovoRpa", I18N.mrs("dw4.occorre_valorizzare_il_campo") + " '" + I18N.mrs("dw4.nuovo_rpa") + "'");
			result = true;
		}
		
		return result;
	}

}
