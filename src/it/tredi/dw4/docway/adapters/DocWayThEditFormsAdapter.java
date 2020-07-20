package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.ThEditFormsAdapter;

public class DocWayThEditFormsAdapter  extends ThEditFormsAdapter {

	public DocWayThEditFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}

	/**
	 * Caricamento della maschera di ThEdit con specifica del canale e 
	 * del tipo sui quali operare
	 * 
	 * @param keypath
	 * @param name
	 */
	public void reload(String keypath, String name) {
		defaultForm.addParam("keypath", keypath);
		defaultForm.addParam("name", name);
	}
	
	/**
	 * Salvataggio dei valori di un thesauro (es. mezzo di trasmissione o tipologia corrente)
	 * 
	 * @param rels
	 * @param name
	 */
	public void salvaNodo(String rels, String name) {
		defaultForm.addParam("rels", rels);
		defaultForm.addParam("name", name);
		defaultForm.addParam("verbo", "thEdit");
		defaultForm.addParam("xverb", "@save");
	}

	/**
	 * caricamento di un nodo dal thesauro
	 * @param name
	 */
	public void cambiaNodo(String name) {
		defaultForm.addParam("verbo", "thEdit");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("rels", "");
		defaultForm.addParam("name", name);
	}
	
}
