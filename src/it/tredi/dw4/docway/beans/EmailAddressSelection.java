package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class EmailAddressSelection {

	public static final int TIPO_INVIO_MULTIPLO = 1;
	public static final int TIPO_INVIO_SINGOLO = 0;
	
	private boolean visible = false;
	private ShowdocDoc showdoc;
	private int tipoInvioTelematico = 1; // tipologia di invio telematico selezionata: singolo o multiplo
	private int rifEsternoPos = 0; // posizione del destinatario della mail in caso di invio a singolo destinatario 
	
	private String xml;
	private String selectedEmail = "";
	private List<String> addresses = new ArrayList<String>();
	
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		List<?> nodes = dom.selectNodes("/response/emailpec_inviotelematico/address");
		if (nodes != null && nodes.size() > 0) {
			for (int i=0; i<nodes.size(); i++) {
				Element el = (Element) nodes.get(i);
				if (el != null)
					this.addresses.add(el.getTextTrim());
			}
		}
		
		this.visible = true;
	}
	
	/**
	 * Selezione dell'indirizzo PEC da utilizzare come mittente per l'invio telematico del documento
	 * @return
	 */
	public String confirmEmailAddress() throws Exception {
		this.showdoc.setEmailFromInvioTelematico(this.selectedEmail);
		this.close();
		
		if (this.tipoInvioTelematico == 0)
			return this.showdoc.invioTelematicoEmail(false, rifEsternoPos);
		else
			return this.showdoc.invioTelematicoEmail(true, 0);
	}
	
	public String close() {
		this.visible = false;
		return null;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ShowdocDoc getShowdoc() {
		return showdoc;
	}

	public void setShowdoc(ShowdocDoc showdoc) {
		this.showdoc = showdoc;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	
	public String getSelectedEmail() {
		return selectedEmail;
	}

	public void setSelectedEmail(String selectedEmail) {
		this.selectedEmail = selectedEmail;
	}
	
	public int getTipoInvioTelematico() {
		return tipoInvioTelematico;
	}

	public void setTipoInvioTelematico(int tipoInvioTelematico) {
		this.tipoInvioTelematico = tipoInvioTelematico;
	}

	public int getRifEsternoPos() {
		return rifEsternoPos;
	}

	public void setRifEsternoPos(int rifEsternoPos) {
		this.rifEsternoPos = rifEsternoPos;
	}
	
}