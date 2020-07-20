package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

public class DocWayChangeClassifFormsAdapter extends DocEditFormsAdapter {

	public DocWayChangeClassifFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);

		// Element root = response.getRootElement();
	}

	public void newClassifForSel(String operation) {
		if (operation == null || operation.equals(""))
			operation = "@CAMBIA_CLASSIF_FASC";

		// String physDoc = getParameterFromCustomTupleValue(this.defaultForm.getParam(customTupleName), "physDocToUse");
		String op = getParameterFromCustomTupleValue(
				this.defaultForm.getParam(customTupleName), "operationToDo");
		if (op == null)
			op = operation;

		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", op);
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
	
}
