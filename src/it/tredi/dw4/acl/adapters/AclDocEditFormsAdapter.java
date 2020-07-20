package it.tredi.dw4.acl.adapters;

import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

public class AclDocEditFormsAdapter extends DocEditFormsAdapter {

	public AclDocEditFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		Element root = response.getRootElement();
		/* inserimento di una persona interna in una struttura interna */
		defaultForm.addParam("physDoc_struint", root.attributeValue("physDoc_struint", ""));
		defaultForm.addParam("selid_struint", root.attributeValue("selid_struint", ""));
		defaultForm.addParam("pos_struint", root.attributeValue("pos_struint", ""));
		
		/* per editing della gerarchia */
		defaultForm.addParam("physDocOrigine", root.attributeValue("physDocOrigine", ""));
		defaultForm.addParam("relType", root.attributeValue("relType", ""));
//defaultForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
		
		/* lista dei diritti editabili nella pagina */
		defaultForm.addParam("rT", root.attributeValue("rT", ""));
	}
	
	// TODO Metodo presente anche in AclDocumentFormsAdapter (potrebbe essere creato un metodo di utility comune)
	public void insPersona(String cod_uff, String cod_amm, String cod_aoo, String additionalCodUff)
	{

		this.defaultForm.addParam("verbo", "docEdit");

	    // Federico 16/07/07: aggiunta trasmissione di un eventuale id addizionale per l'ufficio [RW 0045515]
		this.defaultForm.addParam("xverb", "@" + cod_uff + "|" + cod_amm + "|" + cod_aoo +
	                                        ((null!= additionalCodUff && (additionalCodUff.trim().length()>0) )
	                                            ? "|" + additionalCodUff
	                                            : ""));

		if(this.defaultForm.getParam("dbTable").indexOf("interna") != -1 )
			this.defaultForm.addParam("dbTable", "@persona_interna");
		else
			this.defaultForm.addParam("dbTable", "@persona_esterna");

		this.defaultForm.addParam("physDoc_struint", this.defaultForm.getParam("physDoc"));
		this.defaultForm.addParam("pos_struint", this.defaultForm.getParam("pos"));
		this.defaultForm.addParam("selid_struint", this.defaultForm.getParam("selid"));
		this.defaultForm.addParam("physDoc", "0");
		this.defaultForm.addParam("pos", "0");
		this.defaultForm.addParam("selid", "");
	}
	
	public void saveOrgano(Map<String, String> organoAsFormAdapterParams, boolean enableIW){
		defaultForm.addParams(organoAsFormAdapterParams);
		defaultForm.addParam("enableIW", enableIW);
	}
	
}
