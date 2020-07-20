package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.PrintProfilesFormsAdapter;
import it.tredi.dw4.utils.StringUtil;

import org.dom4j.DocumentException;

public class DocWayPrintProfilesFormsAdapter extends PrintProfilesFormsAdapter {
	
	public DocWayPrintProfilesFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);

		// Element root = response.getRootElement();
	}
	
	/**
	 * Avvio della stampa di profilo
	 * 
	 * @param profileSelection
	 * @return
	 */
	public void startPrint(String profileSelection) {
		
		/* Federico 03/11/05: la tupla 'printTemplate' puo' contenere, oltre al nome del template di stampa da usare,
	     * anche il tipo di report da generare, il tipo di output, una classe java custom da usare per la  stampa e
	     * il data source da usare nelle stampe jasper.
	     * La sua sintassi e' la seguente: nome_template|tipo_report|tipo_output|classe_custom|data_source.
	     * Qualora le ultime quattro informazioni non siano state indicate, si avra': nome_template|||| o nome_template
	     * (se mancano i "|" significa che la tupla e' "vecchio stile").
	     * [RW 0030160]
	     *
	     * Federico 12/01/06: aggiunte tuple per il cambio di selezione iniziale [RW 0029314]
	     *
	     * Federico 29/06/07: aggiunta tupla per l'ordinamento della selezione da stampare [RW 0045047]
	     */
		String printTemplate = "", profileType = "", outputType = "", customClass = "", dataSource = "", selOrder = "";
		String initDb = "", initQuery = "", initOrd = "";

		if (profileSelection.indexOf("|") != -1) {
			String[] tmp = StringUtil.split(profileSelection, "|");

			printTemplate   = tmp[0];
			profileType     = tmp[1];
			outputType      = tmp[2];
			customClass     = tmp[3];
			dataSource      = tmp[4];
			initDb          = tmp[5];
			initQuery       = tmp[6];
			initOrd         = tmp[7];
			selOrder        = tmp[8];
		}
		else {
			printTemplate = profileSelection;
		}
		
		if (outputType.equals("")) {
			// non e' stato definito il tipo di output da generare per la stampa --> imposto il default (pdf)
			outputType = "pdf";
		}

		String verbo = defaultForm.getParam("verbo");
		int pos = 0; // numero del doc. da stampare (calcolato a partire dalla tupla 'physDoc')

		// Federico 06/04/06: ristrutturato codice per costruire l'url di base mediante 'buildUrlForGet' [RW 0035909]
		//var url = buildUrlForGet(getForm('hxpForm').uri.value, false, true, true);

		if (verbo.equals("showdoc")) {
			// stampa del singolo documento con FOP
			pos = defaultForm.getParamAsInt("physDoc") - 1;
			
			//url += "&verbo=showdoc&pos=" + pos + "&view=" + getForm('hxpForm').view.value + "&printTemplate=" + printTemplate + "&outContent=" + outputType;
			defaultForm.addParam("verbo", "showdoc");
			defaultForm.addParam("pos", pos);
			defaultForm.addParam("printTemplate", printTemplate);
			defaultForm.addParam("outContent", outputType);
		}
		else if (verbo.equals("print_selection")) {
			// Federico 04/11/04: stampa con FOP di una selezione di documenti con avviamento della loadingbar
			// Federico 03/01/06: aggiunta trasmissione tupla 'keylist' [RW 0030948]
			
			//url += "&verbo=printselection_response&xverb=stampe&selid=" + getForm('hxpForm').selid.value +
			//			"&dbTable=@stampe" + "&printTemplate=" + printTemplate + "&outType=" + outputType +
			//			"&keylist=" + getForm('hxpForm').keylist.value;
			defaultForm.addParam("verbo", "printselection_response");
			defaultForm.addParam("xverb", "stampe");
			defaultForm.addParam("dbTable", "@stampe");
			defaultForm.addParam("printTemplate", printTemplate);
			defaultForm.addParam("outContent", outputType);
		}
		else if (verbo.indexOf("genericPrintHandler") == 0 || verbo.indexOf(".") != -1) {
			// Federico 11/05/05: esecuzione della stampa mediante una generica classe figlia di Response
			// Federico 17/10/05: esecuzione della stampa mediante l'handler per stampe fo/jasper [RW 0030160]
			String xverb = "";
			
			if (!defaultForm.getParam("physDoc").equals("")) {
				// in hxpForm e' indicato un numero fisico di documento
				pos = defaultForm.getParamAsInt("physDoc") - 1;
			}

			if (verbo.indexOf("|") != -1) {
				// e' stato specificato anche un xverb
				xverb = verbo.substring(verbo.indexOf("|") + 1);
				verbo = verbo.substring(0, verbo.indexOf("|"));
			}

			// preparazione dell'url da invocare
			// Federico 03/01/06: aggiunta trasmissione tupla 'keylist' [RW 0030948]
			
			/*
			url += "&verbo=" +
	                verbo + "&xverb=" +
	                xverb + "&selid=" +
	                getForm('hxpForm').selid.value + "&pos=" +
	                pos + "&dbTable=@stampe" +
	                "&printTemplate=" + printTemplate +
	                "&outType=" + outputType + "&profileType=" +
	                profileType + "&keylist=" +
	                getForm('hxpForm').keylist.value +
	                // Federico 27/06/06: se si stampa subito dopo un inserimento, non c'e' una selezione -->
	                // occorre trasmettere anche la tupla 'physDoc' [RW 0037621]
	                "&physDoc=" + getForm('hxpForm').physDoc.value;
			 */
			defaultForm.addParam("verbo", verbo);
			defaultForm.addParam("xverb", xverb);
			defaultForm.addParam("pos", pos);
			defaultForm.addParam("dbTable", "@stampe");
			defaultForm.addParam("printTemplate", printTemplate);
			defaultForm.addParam("outType", outputType);
			defaultForm.addParam("profileType", profileType);
			
			if (verbo.equals("genericPrintHandler")) {
				/* Qualora si usi l'handler per stampe fo/jasper e' utile sapere se occorre usare una classe custom
				 * per la stampa.
				 * 'customClass' contiene il valore di verbo da usare per istanziare la classe custom, che deve
				 * appartenere alla gerarchia di Response (viene usato il meccanismo dell'indicazione, nel verbo,
				 * del package della classe da istanziare).
				 * L'xverb passato alla classe custom e' quello presente nella richiesta inviata da questa funzione.
				 */
				//url += "&useCustomClass=" + customClass;
				defaultForm.addParam("useCustomClass", customClass);

				// In caso di stampe jasper occorre aggiungere all'url altre info necessarie per la stampa
				//url += "&dataSource=" + replaceAll(dataSource, '#', '%23');
				defaultForm.addParam("dataSource", dataSource.replaceAll("#", "%23"));

				// posizione del doc. nella selezione (x JRXWDataSource)
				//url += "&selPos=" + getForm('hxpForm').pos.value;
				defaultForm.addParam("selPos", defaultForm.getParam("pos"));

				// Federico 12/01/06: aggiunte tuple per il cambio di selezione iniziale [RW 0029314]
				//url += "&initDb=" + initDb + "&initQuery=" + initQuery + "&initOrd=" + initOrd;
				defaultForm.addParam("initDb", initDb);
				defaultForm.addParam("initQuery", initQuery);
				defaultForm.addParam("initOrd", initOrd);

				// Federico 29/06/07: aggiunta tupla per l'ordinamento della selezione da stampare [RW 0045047]
				//url += "&selOrder=" + selOrder;
				defaultForm.addParam("selOrder", selOrder);
			}
	 	}
	}
	
}
