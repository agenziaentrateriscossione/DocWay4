package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class DettaglioPagamento extends XmlEntity {

	private String beneficiario = "";
	private String modalitaPagamento = "";
	private String dataRiferimentoTerminiPagamento = "";
	private String giorniTerminiPagamento = "";
	private String dataScadenzaPagamento = "";
	private String importoPagamento = "";
	private String iban = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.beneficiario 						= XMLUtil.parseStrictElement(dom, "dettaglioPagamento/beneficiario");
		this.modalitaPagamento 					= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@modalitaPagamento");
		this.dataRiferimentoTerminiPagamento 	= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@dataRiferimentoTerminiPagamento");
		this.giorniTerminiPagamento 			= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@giorniTerminiPagamento");
		this.dataScadenzaPagamento 				= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@dataScadenzaPagamento");
		this.importoPagamento 					= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@importoPagamento");
		this.iban 								= XMLUtil.parseStrictAttribute(dom, "dettaglioPagamento/@iban");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.beneficiario != null && this.beneficiario.length() > 0)
    		params.put(prefix+".beneficiario", this.beneficiario);
    	if (this.modalitaPagamento != null && this.modalitaPagamento.length() > 0)
    		params.put(prefix+".@modalitaPagamento", this.modalitaPagamento);
    	if (this.dataRiferimentoTerminiPagamento != null && this.dataRiferimentoTerminiPagamento.length() > 0) {
	    	if (this.dataRiferimentoTerminiPagamento.length() == 10)
	    		params.put(prefix+".@dataRiferimentoTerminiPagamento", DateUtil.changeDateFormat(this.dataRiferimentoTerminiPagamento, Const.DEFAULT_DATE_FORMAT, "yyyyMMdd")); // TODO formato da caricare da file di properties
	    	else
	    		params.put(prefix+".@dataRiferimentoTerminiPagamento", this.dataRiferimentoTerminiPagamento);
    	}
    	if (this.giorniTerminiPagamento != null && this.giorniTerminiPagamento.length() > 0)
    		params.put(prefix+".@giorniTerminiPagamento", this.giorniTerminiPagamento);
    	if (this.dataScadenzaPagamento != null && this.dataScadenzaPagamento.length() > 0) {
	    	if (this.dataScadenzaPagamento.length() == 10)
	    		params.put(prefix+".@dataScadenzaPagamento", DateUtil.changeDateFormat(this.dataScadenzaPagamento, Const.DEFAULT_DATE_FORMAT, "yyyyMMdd")); // TODO formato da caricare da file di properties
	    	else
	    		params.put(prefix+".@dataScadenzaPagamento", this.dataScadenzaPagamento);
    	}
    	if (this.importoPagamento != null && this.importoPagamento.length() > 0)
    		params.put(prefix+".@importoPagamento", this.importoPagamento);
    	if (this.iban != null && this.iban.length() > 0)
    		params.put(prefix+".@iban", this.iban);
    	
		return params;
	}
	
	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getModalitaPagamento() {
		return modalitaPagamento;
	}

	public void setModalitaPagamento(String modalitaPagamento) {
		this.modalitaPagamento = modalitaPagamento;
	}

	public String getDataRiferimentoTerminiPagamento() {
		return dataRiferimentoTerminiPagamento;
	}

	public void setDataRiferimentoTerminiPagamento(
			String dataRiferimentoTerminiPagamento) {
		this.dataRiferimentoTerminiPagamento = dataRiferimentoTerminiPagamento;
	}

	public String getGiorniTerminiPagamento() {
		return giorniTerminiPagamento;
	}

	public void setGiorniTerminiPagamento(String giorniTerminiPagamento) {
		this.giorniTerminiPagamento = giorniTerminiPagamento;
	}

	public String getDataScadenzaPagamento() {
		return dataScadenzaPagamento;
	}

	public void setDataScadenzaPagamento(String dataScadenzaPagamento) {
		this.dataScadenzaPagamento = dataScadenzaPagamento;
	}

	public String getImportoPagamento() {
		return importoPagamento;
	}

	public void setImportoPagamento(String importoPagamento) {
		this.importoPagamento = importoPagamento;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

}
