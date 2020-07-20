package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.ThEdit;
import it.tredi.dw4.docway.adapters.DocWayThEditFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;

import org.dom4j.Document;

public class DocWayThEdit extends ThEdit {
	private DocWayThEditFormsAdapter formsAdapter;
	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	@Override
	public DocWayThEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public DocWayThEdit() throws Exception {
		this.formsAdapter = new DocWayThEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml 				= dom.asXML();
		
		keypath 			= XMLUtil.parseStrictAttribute(dom, "/response/@keypath");
		name 				= XMLUtil.parseStrictAttribute(dom, "/response/@name");
		values 				= "";
		relDescrOptions 	= new ArrayList<Option>();
		
		loadRelDescr();
		rels2Form();
	}
	
	/**
	 * Salvataggio dei valori per il mezzo di trasmissione o tipologia corrente
	 */
	@Override
	public String salvaNodo() throws Exception {
		try {
			if (keypath != null && !keypath.equals("") 
						&& name != null && !name.equals("")) {
				
				String rels = form2Rels();
				if (rels != null && !rels.equals("")) {
					formsAdapter.salvaNodo(rels, name);
					
					XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
						return null;
					}
					
					formsAdapter.fillFormsFromResponse(response);
					this.init(response.getDocument());
				}
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Reload della maschera per modifica del canale o del tipo
	 */
	@Override
	public String reload() throws Exception {
		try {
			formsAdapter.reload(keypath, name);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

}
