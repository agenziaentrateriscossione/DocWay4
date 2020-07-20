package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;

public class Email extends XmlEntity {

	private String mittente = "";
	private List<EmailRecipient> destinatario_int = new ArrayList<EmailRecipient>();
	private List<EmailRecipient> destinatario_est = new ArrayList<EmailRecipient>();
	private String oggetto = "";
	private String corpo = "";
	
	public Email() { }
	
	@Override
	public XmlEntity init(Document dom) {
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
		Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".mittente", this.mittente);
    	for (int i = 0; i < destinatario_int.size(); i++) {
    		EmailRecipient rec = (EmailRecipient) destinatario_int.get(i);
    		params.putAll(rec.asFormAdapterParams(".destinatario_int["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < destinatario_est.size(); i++) {
    		EmailRecipient rec = (EmailRecipient) destinatario_est.get(i);
    		params.putAll(rec.asFormAdapterParams(".destinatario_est["+String.valueOf(i)+"]"));
		}
    	params.put(prefix+".oggetto", this.oggetto);
    	params.put(prefix+".body", this.corpo);
    	
    	return params;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public List<EmailRecipient> getDestinatario_int() {
		return destinatario_int;
	}

	public void setDestinatario_int(List<EmailRecipient> destinatario_int) {
		this.destinatario_int = destinatario_int;
	}

	public List<EmailRecipient> getDestinatario_est() {
		return destinatario_est;
	}

	public void setDestinatario_est(List<EmailRecipient> destinatario_est) {
		this.destinatario_est = destinatario_est;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}
	
}
