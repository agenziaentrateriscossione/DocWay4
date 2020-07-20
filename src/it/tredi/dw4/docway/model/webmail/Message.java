package it.tredi.dw4.docway.model.webmail;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;
import it.tredi.utils.string.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class Message extends XmlEntity {
	
	private String messageNo = "";
	private String messageId = "";
	
	private String sentDate = null;
	private String receivedDate = null;
	
	private Recipient from = new Recipient();
	private List<Recipient> to = new ArrayList<Recipient>();
	private List<Recipient> cc = new ArrayList<Recipient>();
	private List<Recipient> bcc = new ArrayList<Recipient>();
	
	private String subject = "";
	private String body = "";
	private boolean html;
	
	private List<Attach> attachments = new ArrayList<Attach>();
	
	private boolean converted = false;

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.messageNo 			= XMLUtil.parseStrictAttribute(dom, "message/@messageNo");
		this.messageId			= XMLUtil.parseStrictAttribute(dom, "message/@messageId");
		this.sentDate 			= XMLUtil.parseStrictAttribute(dom, "message/@sentDate");
		this.receivedDate 		= XMLUtil.parseStrictAttribute(dom, "message/@receivedDate");
		
		// TODO da verificare, deriva dalla riga 160 di XMLDocumento (xmlString = xmlString.replaceAll("&#", "&amp;#");)
		this.subject 			= Text.htmlToText(XMLUtil.parseStrictElement(dom, "message/subject"));
		this.body	 			= Text.htmlToText(XMLUtil.parseStrictElement(dom, "message/body", false));
		this.html	 			= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "message/@html"));
		
		from.init(XMLUtil.createDocument(dom, "message/from"));
		this.to = XMLUtil.parseSetOfElement(dom, "message/recipients/to", new Recipient());
		this.cc = XMLUtil.parseSetOfElement(dom, "message/recipients/cc", new Recipient());
		this.bcc = XMLUtil.parseSetOfElement(dom, "message/recipients/bcc", new Recipient());
		
		this.attachments = XMLUtil.parseSetOfElement(dom, "message/attachments/attach", new Attach());
		
		this.converted 			= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "message/@converted"));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO probabilmente questo metodo deve essere implementato per la funzione di trasformazione mail in documento di docway
		return null;
	}

	public String getMessageNo() {
		return messageNo;
	}

	public void setMessageNo(String messageNo) {
		this.messageNo = messageNo;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Recipient getFrom() {
		return from;
	}

	public void setFrom(Recipient from) {
		this.from = from;
	}

	public List<Recipient> getTo() {
		return to;
	}

	public void setTo(List<Recipient> to) {
		this.to = to;
	}

	public List<Recipient> getCc() {
		return cc;
	}

	public void setCc(List<Recipient> cc) {
		this.cc = cc;
	}

	public List<Recipient> getBcc() {
		return bcc;
	}

	public void setBcc(List<Recipient> bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public List<Attach> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attach> attachments) {
		this.attachments = attachments;
	}

	public boolean isConverted() {
		return converted;
	}

	public void setConverted(boolean converted) {
		this.converted = converted;
	}

}
