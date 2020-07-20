package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

public class DocWayPostitFormsAdapter extends DocEditFormsAdapter {

	public DocWayPostitFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		Element root = response.getRootElement();
		
		//visualizzazione di contenuto di fascicolo
		defaultForm.addParam("postitPos", root.attributeValue("postitPos", ""));
		defaultForm.addParam("postitText", root.attributeValue("postitText", ""));
	}
	
	

	public void delPostit(int postitPos){
//	        setGlobalFormRestore('verbo','xverb','postitPos');
		 this.defaultForm.addParam("verbo", "postit_response");
		 this.defaultForm.addParam("xverb", "remove");
		 this.defaultForm.addParam("postitPos", postitPos);
	}
	
	public void modPostit(int pos, String text){
		addPostit(pos, text);
	}
	
	public void addPostit(int pos, String text)	{
		addPostit(String.valueOf(pos), text);
	}
	
	public void addPostit(String pos, String text)	{
		this.defaultForm.addParam("verbo", "postit_response");
		this.defaultForm.addParam("xverb", "@addPage");

		this.defaultForm.addParam("postitPos", pos);
		this.defaultForm.addParam("postitText", text);
	}

	public void confirmPostit(String postitText)	{
//		il controllo sulla textarea lo deve fare il metodo chiamante
//		if ( getForm("theForm").postitTextArea.value.length == 0 )
//			postitMSG(noValField);
//		else {
		this.defaultForm.addParam("verbo", "postit_response");
		this.defaultForm.addParam("xverb", "@add");
		this.defaultForm.addParam("postitText", postitText);		
	}
	
}
