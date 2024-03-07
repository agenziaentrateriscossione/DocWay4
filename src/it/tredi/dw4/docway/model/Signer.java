package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Signer extends XmlEntity {

	private String firmatario = "";
	private String codiceFiscale = "";
	private String organizzazione = "";
	private String stato = "";
	private String enteCertificatore = "";
	private String algoritmoDigest = "";
	private String signatureDate = "";
	private String certFrom = "";
	private String certTo = "";
	private boolean qualified = false;
	private boolean revoked = false;
	private boolean trusted = false;
	private boolean valid = false;
	private String serial = "";

	// nuovi campi validazione DSS
	private boolean dssValidation = false;
	private String signatureId = "";
	private String certificateId = "";
	private String validOverall = "";
	private String locality = "";
	private String state = "";
	private String email = "";
	private String digestValid = "";
	private String signatureIntact = "";
	private String validTimeTest = "";
	private String certificateRevoked = "";
	private String revocationTest = "";
	private String revocationReason = "";
	private String revocationDate = "";

	@Override
	public XmlEntity init(Document dom) {
		this.firmatario = XMLUtil.parseStrictElement(dom, "signer/name");
		this.codiceFiscale = XMLUtil.parseStrictElement(dom, "signer/cf");
		this.organizzazione = XMLUtil.parseStrictElement(dom, "signer/organization");
		this.stato = XMLUtil.parseStrictElement(dom, "signer/country");
		this.enteCertificatore = XMLUtil.parseStrictElement(dom, "signer/issuer");
		this.certFrom = XMLUtil.parseStrictElement(dom, "signer/certFrom");
		this.certTo = XMLUtil.parseStrictElement(dom, "signer/certTo");
		this.signatureDate = XMLUtil.parseStrictElement(dom, "signer/signatureDate");
		this.algoritmoDigest = XMLUtil.parseStrictElement(dom, "signer/digestAlg");
		this.dssValidation = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "signer/@dssValidation"));
		if (this.dssValidation) {
			this.signatureId = XMLUtil.parseStrictElement(dom, "signer/signatureId");
			this.certificateId = XMLUtil.parseStrictElement(dom, "signer/certificateId");
			this.validOverall = XMLUtil.parseStrictAttribute(dom, "signer/@valid");
			this.locality = XMLUtil.parseStrictElement(dom, "signer/locality");
			this.state = XMLUtil.parseStrictElement(dom, "signer/state");
			this.email = XMLUtil.parseStrictElement(dom, "signer/email");
			this.digestValid = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@digestValid");
			this.signatureIntact = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@intactSignature");
			this.validTimeTest = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@validTimeTest");
			this.certificateRevoked = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@certificateRevoked");
			this.revocationTest = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@revocationTest");
			this.revocationReason = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@revocationReason");
			this.revocationDate = XMLUtil.parseStrictAttribute(dom, "signer/validationDetails/@revocationDate");
		} else {
			this.valid = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "signer/@valid"));
			this.qualified = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "signer/@qualified"));
			this.revoked = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "signer/@revoked"));
			this.trusted = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "signer/@trusted"));
		}

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getFirmatario() {
		return firmatario;
	}

	public void setFirmatario(String firmatario) {
		this.firmatario = firmatario;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getOrganizzazione() {
		return organizzazione;
	}

	public void setOrganizzazione(String organizzazione) {
		this.organizzazione = organizzazione;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getEnteCertificatore() {
		return enteCertificatore;
	}

	public void setEnteCertificatore(String enteCertificatore) {
		this.enteCertificatore = enteCertificatore;
	}

	public String getAlgoritmoDigest() {
		return algoritmoDigest;
	}

	public void setAlgoritmoDigest(String algoritmoDigest) {
		this.algoritmoDigest = algoritmoDigest;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	public String getCertFrom() {
		return certFrom;
	}

	public void setCertFrom(String certFrom) {
		this.certFrom = certFrom;
	}

	public String getCertTo() {
		return certTo;
	}

	public void setCertTo(String certTo) {
		this.certTo = certTo;
	}

	public boolean isQualified() {
		return qualified;
	}

	public void setQualified(boolean qualified) {
		this.qualified = qualified;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public boolean isDssValidation() {
		return dssValidation;
	}

	public void setDssValidation(boolean dssValidation) {
		this.dssValidation = dssValidation;
	}

	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureId = signatureId;
	}

	public String getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}

	public String getValidOverall() {
		return validOverall;
	}

	public void setValidOverall(String validOverall) {
		this.validOverall = validOverall;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDigestValid() {
		return digestValid;
	}

	public void setDigestValid(String digestValid) {
		this.digestValid = digestValid;
	}

	public String getSignatureIntact() {
		return signatureIntact;
	}

	public void setSignatureIntact(String signatureIntact) {
		this.signatureIntact = signatureIntact;
	}

	public String getValidTimeTest() {
		return validTimeTest;
	}

	public void setValidTimeTest(String validTimeTest) {
		this.validTimeTest = validTimeTest;
	}

	public String getCertificateRevoked() {
		return certificateRevoked;
	}

	public void setCertificateRevoked(String certificateRevoked) {
		this.certificateRevoked = certificateRevoked;
	}

	public String getRevocationTest() {
		return revocationTest;
	}

	public void setRevocationTest(String revocationTest) {
		this.revocationTest = revocationTest;
	}

	public String getRevocationReason() {
		return revocationReason;
	}

	public void setRevocationReason(String revocationReason) {
		this.revocationReason = revocationReason;
	}

	public String getRevocationDate() {
		return revocationDate;
	}

	public void setRevocationDate(String revocationDate) {
		this.revocationDate = revocationDate;
	}
}
