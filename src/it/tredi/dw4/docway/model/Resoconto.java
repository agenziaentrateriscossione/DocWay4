package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Resoconto extends XmlEntity {
	private String locked;
	private String restored;
	private String updated;
	private String created;
	private String failed;
	private String deleted;
	private String present;
	private String skipped;
	private String transferred;
	private String trasf_non_nec;
	private String update_non_nec;
	private String processed;
	// tiommi : aggiunto nrecord da ritornare al bean showdoc a loadingbar ultimata
	private String nrecordToPreview;
	private List<HistoryLoadingbar> historyLoadingbar;
	
	public Resoconto() {}
    
	public Resoconto(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    @SuppressWarnings("unchecked")
	public Resoconto init(Document dom) {
    	this.locked = XMLUtil.parseAttribute(dom, "resoconto/@locked");
    	this.updated = XMLUtil.parseAttribute(dom, "resoconto/@updated");
    	this.restored = XMLUtil.parseAttribute(dom, "resoconto/@restored");
    	this.created = XMLUtil.parseAttribute(dom, "resoconto/@created");
    	this.failed = XMLUtil.parseAttribute(dom, "resoconto/@failed");
		this.deleted = XMLUtil.parseAttribute(dom, "resoconto/@deleted");
		this.present = XMLUtil.parseAttribute(dom, "resoconto/@present");
    	this.skipped = XMLUtil.parseAttribute(dom, "resoconto/@skipped");
    	this.processed = XMLUtil.parseAttribute(dom, "resoconto/@processed");
    	this.transferred= XMLUtil.parseAttribute(dom, "resoconto/@transferred");
    	this.trasf_non_nec = XMLUtil.parseAttribute(dom, "resoconto/@trasf_non_nec");
    	this.update_non_nec = XMLUtil.parseAttribute(dom, "resoconto/@update_non_nec");
    	this.nrecordToPreview = XMLUtil.parseAttribute(dom, "resoconto/@nrecordToPreview");
    	this.historyLoadingbar = XMLUtil.parseSetOfElement(dom, "//item", new HistoryLoadingbar());
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
    public String getLocked() {
		return locked;
	}

	public void setLocked(String cod) {
		this.locked = cod;
	}
	
	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String value) {
		this.updated = value;
	}

	public void setFailed(String failed) {
		this.failed = failed;
	}

	public String getFailed() {
		return failed;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public void setSkipped(String skipped) {
		this.skipped = skipped;
	}

	public String getSkipped() {
		return skipped;
	}

	public void setHistoryLoadingbar(List<HistoryLoadingbar> historyLoadingbar) {
		this.historyLoadingbar = historyLoadingbar;
	}

	public List<HistoryLoadingbar> getHistoryLoadingbar() {
		return historyLoadingbar;
	}

	public void setRestored(String restored) {
		this.restored = restored;
	}

	public String getRestored() {
		return restored;
	}

	public void setTransferred(String transferred) {
		this.transferred = transferred;
	}

	public String getTransferred() {
		return transferred;
	}

	public void setTrasf_non_nec(String trasf_non_nec) {
		this.trasf_non_nec = trasf_non_nec;
	}

	public String getTrasf_non_nec() {
		return trasf_non_nec;
	}

	public void setUpdate_non_nec(String update_non_nec) {
		this.update_non_nec = update_non_nec;
	}

	public String getUpdate_non_nec() {
		return update_non_nec;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
	}

	public String getProcessed() {
		return processed;
	}
	
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getPresent() {
		return present;
	}

	public void setPresent(String present) {
		this.present = present;
	}

	public String getNrecordToPreview() {
		return nrecordToPreview;
	}

	public void setNrecordToPreview(String nrecordToPreview) {
		this.nrecordToPreview = nrecordToPreview;
	}
}

