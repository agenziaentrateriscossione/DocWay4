package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.utils.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void confirmPostit(String postitText, List<XwFile> files)	{
//		il controllo sulla textarea lo deve fare il metodo chiamante
//		if ( getForm("theForm").postitTextArea.value.length == 0 )
//			postitMSG(noValField);
//		else {
		this.defaultForm.addParam("verbo", "postit_response");
		this.defaultForm.addParam("xverb", "@add");
		this.defaultForm.addParam("postitText", postitText);
		if (files != null && !files.isEmpty()) {
			// rimozione file vuoto
			files.remove(files.size() - 1);
			this.defaultForm.addParams(filesToParams(files));
		}
	}

	/**
	 * Setting delle informazioni relative ai file come params di un formAdapter
	 * @param files ArrayList di XwFile da convertire in params per il formAdapter
	 */
	private HashMap<String, String> filesToParams(List<XwFile> files) {
		// files
		HashMap<String, String> params = new HashMap<>();
		for (int i = 0; i < files.size(); i++) {
			XwFile file = files.get(i);
			params.put("xwfile.title." + i, file.getTitle());
			params.put("xwfile.xwayId." + i, file.getXwayId());
		}
		params.put("xwfiles.size", String.valueOf(files.size()));
		return params;
	}

	/**
	 * Ritorna il valore impostato come sizeMaxFile (dimensione massima dei file da uploadare)
	 * @return
	 */
	public int getSizeMaxFile() {
		int size = 0;
		String sizeMaxFile = defaultForm.getParam("sizeMaxFile");
		try {
			if (sizeMaxFile == null || sizeMaxFile.isEmpty())
				sizeMaxFile = "0";
			size = Integer.parseInt(sizeMaxFile);
		}
		catch (Exception e) {
			Logger.warn("DocDocWayDocEditFormsAdapter.getSizeMaxFile(): unable to parse sizeMaxFile -> " + sizeMaxFile);
			size = 0;
		}
		return size;
	}
	
}
