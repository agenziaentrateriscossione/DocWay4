package it.tredi.dw4.docway.adapters;

import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

public class DocWayRifIntFormsAdapter extends DocEditFormsAdapter {

	public DocWayRifIntFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);

		Element root = response.getRootElement();

		// visualizzazione di contenuto di fascicolo
		defaultForm.addParam("rifInt", root.attributeValue("rifInt", ""));
	}

	/**
	 * Aggiunta di rif interni ad un documento/fascicolo
	 * 
	 * @param sendMailRifInterni
	 * @param checkNomi
	 * @param map
	 */
	public void confirmRifInt(boolean sendMailRifInterni, boolean checkNomi, Map<String, String> map) {
		confirmRifInt(sendMailRifInterni, checkNomi, map, false);
	}

	/**
	 * Aggiunta di rif interni ad un documento/fascicolo
	 * 
	 * @param sendMailRifInterni
	 * @param checkNomi
	 * @param map
	 * @param isCCPage vale true se si stanno aggiungendo dei CC al doc, false altrimenti
	 */
	public void confirmRifInt(boolean sendMailRifInterni, boolean checkNomi, Map<String, String> map, boolean isCCPage) {
		confirmRifInt(sendMailRifInterni, checkNomi, map, isCCPage, false);
	}

	/**
	 * Aggiunta di rif interni ad un documento/fascicolo
	 * 
	 * @param sendMailRifInterni
	 * @param checkNomi
	 * @param map
	 * @param isCCPage 
	 * 			  vale true se si stanno aggiungendo dei CC al doc, false 
	 * 			  altrimenti
	 * @param trasferisciMinuta
	 *            utilizzata in caso di trasferimento RPA da lista documenti.
	 *            Vale true se in caso di doc tra uffici occorre modificare
	 *            l'RPAM, false atrimenti
	 */
	public void confirmRifInt(boolean sendMailRifInterni, boolean checkNomi, Map<String, String> map, boolean isCCPage, boolean trasferisciMinuta) {
		String rifIntString = checkAndExtractRifIntValues(map);
		
		if (rifIntString != null && rifIntString.length() > 0) {
			String xv = "";
			if (trasferisciMinuta)
				xv = "RPAM";

			if (rifIntString.endsWith("|")) 
				rifIntString = rifIntString.substring(0, rifIntString.length() - 1);
			this.defaultForm.addParam("rifInt", rifIntString);

			this.defaultForm.addParam("verbo", "rifint_response");

			if (sendMailRifInterni)
				this.defaultForm.addParam("sendMail", true);
			else
				this.defaultForm.addParam("sendMail", false);

			if (this.defaultForm.getParam("toDo").equals("from_titles")) {
				if (!isCCPage) {
					this.defaultForm.addParam("verbo", "rifint_response");
					this.defaultForm.addParam("xverb", "@trasferisciSel" + xv);
					return;
				} else {
					this.defaultForm.addParam("verbo", "rifint_response");
					this.defaultForm.addParam("xverb", "@assegnaCCSel");
					return;
				}
			} else if (this.defaultForm.getParam("toDo").equals("from_fasc_titles")) {
				this.defaultForm.addParam("verbo", "rifint_response");
				this.defaultForm.addParam("xverb", "@TRASF_SEL");
				this.defaultForm.addParam("dbTable", "@fascicolo");
				return;
			}

			if (!this.defaultForm.getParam("xverb").equals("@TRASF")) {
				this.defaultForm.addParam("xverb", "@assignRif_" + this.defaultForm.getParam("xverb"));
			}

			// TODO da verificare
			if (checkNomi)
				this.defaultForm.addParam("checkNomi", "si");
			else
				this.defaultForm.addParam("checkNomi", "null");
		} else {
			// tutti campi vuoti (RPA non sensato oppure tutti CC fasulli)
			// rifintMSG(noValField);
		}
	}
	
	/**
	 * aggiunta di rif interni ad un fascicolo
	 * 
	 * @param sendMailRifInterni
	 * @param map
	 */
	public void confirmCConFascicolo(boolean sendMailRifInterni, Map<String, String> map) {
		String rifIntString = checkAndExtractRifIntValues(map);
		
		if (rifIntString != null && rifIntString.length() > 0) {
			if (rifIntString.endsWith("|")) 
				rifIntString = rifIntString.substring(0, rifIntString.length() - 1);
			this.defaultForm.addParam("rifInt", rifIntString);

			this.defaultForm.addParam("verbo", "rifint_response");
			this.defaultForm.addParam("xverb", "@assegnaCCFascicolo");
			
			if (sendMailRifInterni)
				this.defaultForm.addParam("sendMail", true);
			else
				this.defaultForm.addParam("sendMail", false);
		}
	}
	
	/**
	 * controllo sui parametri specificati per un aggiornamento di rif interni su documenti o fascicoli
	 * @param map
	 * @return
	 */
	private String checkAndExtractRifIntValues(Map<String, String> map) {
		boolean errorFound = true;
		String rifIntString = "";
		Set<String> keys = map.keySet();
		for (String key : keys) {
			String value = map.get(key);
			// if (getForm('theForm')[i].type == "checkbox" ) {
			// if (getForm('theForm')[i].checked)
			// rifIntString += getForm('theForm')[i].name + "=" +
			// getForm('theForm')[i].value + "|";
			// }
			// else {
			
			if (key.startsWith("*")) {
				if ((null != value && value.trim().length() > 0) || key.contains("assegnazioneCDS")
						|| key.contains("nome_persona") || key.contains("nome_uff")) { // necessario il controllo su 'nome_persona' e 'nome_uff' (richiesti anche se vuoti)
					
					// in caso di nome_persona vuoto occorre verificare che non si stia
					// gestendo un ruolo -> in questo caso e' necessario indicare 'Tutti'
					if (key.contains("@nome_persona") && value.length() == 0) {
						String tipoRif = map.get(key.replace("@nome_persona", "@tipo_uff"));
						if (tipoRif != null && tipoRif.equals("ruolo"))
							value = "Tutti";
					}
					
					rifIntString += key + "=" + value + "|";
					errorFound = false;
				}

			}

			// }
		}
		
		if (errorFound)
			rifIntString = "";
		
		return rifIntString;
	}

}
