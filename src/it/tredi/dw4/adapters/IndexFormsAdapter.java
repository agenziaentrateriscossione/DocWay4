package it.tredi.dw4.adapters;

import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class IndexFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public IndexFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("startkey", "");
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("xMode", root.attributeValue("xMode", ""));
		defaultForm.addParam("keypath", root.attributeValue("keypath", ""));
		defaultForm.addParam("fillField", root.attributeValue("fillField", "")); //campo da riempire
		defaultForm.addParam("cPath", root.attributeValue("cPath", "")); //parte comune
		defaultForm.addParam("keylower", ""); //se leggere verso minore (pagina precedente)
		defaultForm.addParam("minFreq", root.attributeValue("minFreq", "")); //frequenza minima
		defaultForm.addParam("maxFreq", root.attributeValue("maxFreq", "")); //frequenza massima
		defaultForm.addParam("minRes", root.attributeValue("minRes", "")); //risoluzione minima
		
		//specifiche idx
		defaultForm.addParam("doubleKey", root.attributeValue("doubleKey", "")); //chiave a doppia indicizzazione
		defaultForm.addParam("threl", root.attributeValue("threl", ""));
		defaultForm.addParam("shwMode", root.attributeValue("shwMode", ""));
		
		//specifiche x ths, usate per passare da una vista all'altra
		defaultForm.addParam("xRels", root.attributeValue("xRels", "")); //relazioni espandibili
		defaultForm.addParam("vRels", root.attributeValue("vRels", "")); //relazioni visualizzabili
		defaultForm.addParam("lRel", root.attributeValue("lRel", "")); //ultima relazione
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			String chiavePosizionamento = this.lastResponse.getAttributeValue("/response/vocabolario/@chiave"); //vocabolario[0]/@chiave
			defaultForm.addParam("startkey", chiavePosizionamento);
			defaultForm.addParam("keylower", "1");
			
			//FIXME
			//idxSubmit();
						
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPaginaPrecedenteEnabled() {
		return this.getDefaultForm().getParamAsInt("pos") > 0;
	}	
	
	@SuppressWarnings("unchecked")
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			List<Element> l = this.lastResponse.selectNodes("/response/vocabolario");
			String chiavePosizionamento = l.get(l.size() - 1).attributeValue("chiave"); 
			defaultForm.addParam("startkey", chiavePosizionamento);
			defaultForm.addParam("keylower", "");
			
			//FIXME
			//idxSubmit();
						
			return true;
		}
		else {
			return false;
		}		
	}

	public boolean isPaginaSuccessivaEnabled() {
		int count = this.getDefaultForm().getParamAsInt("count");
		int pos = this.getDefaultForm().getParamAsInt("pos");
		int vocabolarioCount = this.lastResponse.selectNodes("/response/vocabolario").size();
		return count > pos + vocabolarioCount;
	}	
	
	/**
	 * Imposta l'IndexFormsAdapter per un'operazione di posizionamento all'interno del 
	 * vocabolario
	 * 
	 * @param seekText Testo specificato per il posizionamento
	 */
	public void posiziona(String seekText) {
		/*
		if(getForm('hxpForm').shwMode.value!='') // yyyymmdd
			chiavePosizionamento=usr2XwKey(getForm('hxpForm').shwMode.value,chiavePosizionamento);
		*/
		
		String chiavePosizionamento = this.getDefaultForm().getParam("cPath") + seekText;
		
		this.getDefaultForm().addParam("startkey", chiavePosizionamento.toUpperCase());
		this.getDefaultForm().addParam("keylower", "");
		
		String nomeChiave = this.getDefaultForm().getParam("keypath");
		this.getDefaultForm().addParam("keypath", nomeChiave);
		
		/*
		if(typeof(getForm('seek').minFreq)!='undefined')
			getForm('hxpForm').minFreq.value=getForm('seek').minFreq.value;
		if(typeof(getForm('seek').maxFreq)!='undefined')
			getForm('hxpForm').maxFreq.value=getForm('seek').maxFreq.value;
		if(typeof(getForm('seek').minRes)!='undefined') {
			if(getForm('seek').minRes.type=="text")
				getForm('hxpForm').minRes.value=getForm('seek').minRes.value;
			else if(getForm('seek').minRes.type=="select-one")
				getForm('hxpForm').minRes.value=getForm('seek').minRes.options[getForm('seek').minRes.selectedIndex].value;
		}
		*/
	}
	
}
