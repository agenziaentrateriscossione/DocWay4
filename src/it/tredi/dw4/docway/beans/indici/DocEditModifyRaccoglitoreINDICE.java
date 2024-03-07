package it.tredi.dw4.docway.beans.indici;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyRaccoglitore;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditModifyRaccoglitoreINDICE extends DocEditModifyRaccoglitore {

	public DocEditModifyRaccoglitoreINDICE() throws Exception {
		super();
	}

	/**
	 * Aggiornamento dei dati base del raccoglitori di tipo indice
	 */
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;

			getFormsAdapter().getDefaultForm().addParams(getRaccoglitore().asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, "list_of_doc");

			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			ShowdocRaccoglitoreINDICE showdocRaccoglitoreINDICE = new ShowdocRaccoglitoreINDICE();
			showdocRaccoglitoreINDICE.getFormsAdapter().fillFormsFromResponse(response);
			showdocRaccoglitoreINDICE.init(response.getDocument());
			setSessionAttribute("showdocRaccoglitoreINDICE", showdocRaccoglitoreINDICE);

			return "showdoc@raccoglitore@INDICE@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Annullamento della modifica del raccoglitore di tipo Indice (reload della pagina di showdoc)
	 */
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();

			ShowdocRaccoglitoreINDICE showdocRaccoglitoreINDICE = new ShowdocRaccoglitoreINDICE();
			showdocRaccoglitoreINDICE.getFormsAdapter().fillFormsFromResponse(response);
			showdocRaccoglitoreINDICE.init(response.getDocument());
			setSessionAttribute("showdocRaccoglitoreINDICE", showdocRaccoglitoreINDICE);

			return "showdoc@raccoglitore@INDICE@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

}
