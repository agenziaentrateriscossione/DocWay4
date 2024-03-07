package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import org.dom4j.DocumentException;

public class DocWayCheckinFormsAdapter extends DocEditFormsAdapter {

	public DocWayCheckinFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		//Element root = response.getRootElement();
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'toDo'.
	 * @return Valore impostato sull'attributo toDo
	 */
	public String getToDo() {
		return defaultForm.getParam("toDo");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'db'.
	 * @return Valore impostato sull'attributo db
	 */
	public String getDb() {
		return defaultForm.getParam("db");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'aclDb'.
	 * @return Valore impostato sull'attributo aclDb
	 */
	public String getAclDb() {
		return defaultForm.getParam("aclDb");
	}
	
	/**
	 * Ritorna il parametro _cd del formsAdapter (necessario all'upload di files in presenza 
	 * di multisocieta') 
	 * @return
	 */
	public String getCustomTupleName() {
		String _cd = defaultForm.getParam("_cd");
		if (_cd == null)
			_cd = "";
		
		return _cd;
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
	
	/**
	 * Ritorna il valore impostato come sizeMaxImg (dimensione massima delle immagini da uploadare)
	 * @return
	 */
	public int getSizeMaxImg() {
		int size = 0;
		String sizeMaxImg = defaultForm.getParam("sizeMaxImg");
		try {
			if (sizeMaxImg == null || sizeMaxImg.isEmpty())
				sizeMaxImg = "0";
			size = Integer.parseInt(sizeMaxImg);
		}
		catch (Exception e) { 
			Logger.warn("DocDocWayDocEditFormsAdapter.getSizeMaxImg(): unable to parse sizeMaxImg -> " + sizeMaxImg);
			size = 0;
		}
		return size;
	}
	
	/**
	 * Conferma di checkin di un file
	 * 
	 * @param fileName identificativo del file su docway
	 * @param fileXwayId identificativo restituito da xway da upload del file
	 * @param fileTitle titolo da assegnare al file
	 * @param convReq eventuali conversioni da eseguire sul file
	 */
	public void commitChkin(String fileName, String fileXwayId, String fileTitle, String convReq) {
		this.defaultForm.addParam("verbo", "attach_response");
		this.defaultForm.addParam("xverb", "@commitChkin");
		this.defaultForm.addParam("name", fileTitle);
		this.defaultForm.addParam("fileToLoad.name", fileXwayId);
		this.defaultForm.addParam("attach", fileName);
		this.defaultForm.addParam("convReq", convReq);
		
		// in ogni caso = true visto che il file e' stato caricato in ogni caso (IWX o SWFUpload)
		this.defaultForm.addParam("enableIW", "true"); 
	}
	
	/**
	 * Download del file da checkin (dopo preleva)
	 * 
	 * @param id nome del file (id extraway)
	 * @param name titolo del file
	 * @throws Exception
	 */
	public void downloadFile(String id, String name) {
		this.defaultForm.addParam("verbo", "attach");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("id", id);
		this.defaultForm.addParam("name", name);
	}
	
}
