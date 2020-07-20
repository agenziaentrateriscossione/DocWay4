package it.tredi.dw4.docway.model;

import org.dom4j.Element;

import it.tredi.dw4.model.XmlEntity;

public class Signer {
	private String alg;
    private String signatureDate;
    private String name;
    private String issuer;
    private String cf;
    private String country;
    private String organization;
    private String qualified;
    private String until;
    private String serial;
    private Boolean revoked;
    private Boolean valid;
    private Boolean trusted;
    
	public XmlEntity init(Element signer) {
		this.valid = Boolean.parseBoolean(signer.attributeValue("valid", "false"));
		
		this.alg = signer.selectSingleNode("./alg").getText();
		this.signatureDate = signer.selectSingleNode("./signatureDate").getText();
		this.name = signer.selectSingleNode("./name").getText();
		this.issuer = signer.selectSingleNode("./issuer").getText();
		this.cf = signer.selectSingleNode("./cf").getText();
		this.country = signer.selectSingleNode("./country").getText();
		this.organization = signer.selectSingleNode("./organization").getText();
		this.qualified = signer.selectSingleNode("./qualified").getText();
		this.until = signer.selectSingleNode("./until").getText();
		this.serial = signer.selectSingleNode("./serial").getText();
		this.revoked = Boolean.parseBoolean(signer.selectSingleNode("./revoked").getText());
		this.trusted = Boolean.parseBoolean(signer.selectSingleNode("./trusted").getText());
		
		return null;
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getQualified() {
		return qualified;
	}

	public void setQualified(String qualified) {
		this.qualified = qualified;
	}

	public String getUntil() {
		return until;
	}

	public void setUntil(String until) {
		this.until = until;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Boolean getRevoked() {
		return revoked;
	}

	public void setRevoked(Boolean revoked) {
		this.revoked = revoked;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getTrusted() {
		return trusted;
	}

	public void setTrusted(Boolean trusted) {
		this.trusted = trusted;
	}
}
