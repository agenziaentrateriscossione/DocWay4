package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.DocWayProperties;

public class DocWayLoadingbarFolderExport extends DocWayLoadingbar {

	public DocWayLoadingbarFolderExport() throws Exception {
		super();
	}

	
	public boolean isFileTooBig() {
		// dipende se sotto e stato mandato email
		return getEmailResult().toLowerCase().equals("ok");
	}
	
	public boolean isSharedFile() {
		return getSharedFileName().length() > 0;
	}
	
	public String getSharedFolderName() {
		if (getDocument() == null)
			return "";
		else
			return getDocument().getAttributeValue("//exportfile/@sharedFolder", "");
	}
	
	public String getSharedFileName() {
		if (getDocument() == null)
			return "";
		else
			return getDocument().getAttributeValue("//exportfile/@sharedFile", "");
	}
	
	public Long getSharedFileSize() {
		if (getDocument() == null){
			return 0L;
		}else{
			return Long.parseLong( getDocument().getAttributeValue("//exportfile/@fileSize", "0"));
		}
	}
	
	public String getEmailResult() {
		if (getDocument() == null){
			return "No";
		}else{
			return getDocument().getAttributeValue("//exportfile/@emailResult", "");
		}
	}
	
	@Override 
	public String getStatus() {
		String st = super.getStatus();
		if(st.length() == 0) {
			return "pfolders.start";
		}else{
			return st;
		}
	}
}
