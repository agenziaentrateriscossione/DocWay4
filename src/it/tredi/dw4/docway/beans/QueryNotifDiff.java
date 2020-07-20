package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Dest;
import it.tredi.dw4.utils.XMLUtil;

import java.util.List;

import org.dom4j.Document;

public class QueryNotifDiff extends DocWayQuery {
	
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private List<Dest> dest = null;
	
	public QueryNotifDiff() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml 		= dom.asXML();
				
		this.setDest(XMLUtil.parseSetOfElement(dom, "response/dest", new Dest()));
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}
	
	public List<Dest> getDest() {
		return dest;
	}

	public void setDest(List<Dest> dest) {
		this.dest = dest;
	}
	
	@Override
	public String queryPlain() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
