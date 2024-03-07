package it.tredi.dw4.docway.beans.indici;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditRaccoglitore;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Document;

/**
 * Inserimento (docedit) di raccoglitori custom di tipo indice
 */
public class DocEditRaccoglitoreINDICE extends DocEditRaccoglitore {

	private String action = "";

	private int physDocRacc = 0;
	private String codSede = "";

	private boolean duplicaRicerca = false;

	private final String TODO_PHYSDOC_RACC_PREFIX = "raccIndicePhysDoc=";

	public DocEditRaccoglitoreINDICE() throws Exception {
		super();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getPhysDocRacc() {
		return physDocRacc;
	}

	public void setPhysDocRacc(int physDocRacc) {
		this.physDocRacc = physDocRacc;
	}

	public String getCodSede() {
		return codSede;
	}

	public void setCodSede(String codSede) {
		this.codSede = codSede;
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);

		this.duplicaRicerca = XMLUtil.parseStrictAttribute(domDocumento, "/response/@toDo").equalsIgnoreCase("duplicaRicerca");
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			// in caso di loadingbar attiva sulla pagina non deve essere possibile la richiesta di salvataggio del repertorio (F5 sulla pagina)
			if (isActiveLoadingbar()) return null;

			if (checkRequiredField()) return null;

			getFormsAdapter().getDefaultForm().addParams(getRaccoglitore().asFormAdapterParams(""));
			getFormsAdapter().getDefaultForm().addParam("duplicaRicerca", this.duplicaRicerca);
			XMLDocumento response = super._saveDocument(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, "list_of_doc");

			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			// analisi del ritorno, caricamento della showdoc o della loadingbar di creazione del raccoglitore e di tutti i documenti 
			// definiti nell'indice
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
				action = "aggiungiRaccoglitoreIndice";

				String toDo = response.getAttributeValue("/response/@toDo");
				if (toDo != null && !toDo.isEmpty() && toDo.startsWith(TODO_PHYSDOC_RACC_PREFIX)) {
					try {
						physDocRacc = Integer.parseInt(toDo.substring(TODO_PHYSDOC_RACC_PREFIX.length()));
					}
					catch (Exception e) {
						Logger.error("DocEditRaccoglitoreINDICE.saveDocument(): got exception on physdoc parsing... " + e.getMessage(), e);
					}
				}

				codSede = response.getAttributeValue("/response/@cod_sede");

				return null;
			}
			else {
				buildSpecificShowdocPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, response);
				return "showdoc@raccoglitore@INDICE@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Annullamento dell'inserimento del raccoglitore di tipo Indice (pulizia del form di input)
	 */
	@Override
	public String clearDocument() throws Exception {
		try {
			getFormsAdapter().clearDocumentRaccIndice(getRaccoglitore().getCodiceRaccoglitoreCustom(), getRaccoglitore().getDescrizioneRaccoglitoreCustom());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}

	/**
	 * Ritorna true se la loadingbar e' attiva sulla pagina di registrazione del raccoglitore (richiesta di salvataggio gia' inoltrata
	 * al server), false in caso contrario
	 * @return
	 */
	private boolean isActiveLoadingbar() {
		boolean active = false;
		if (getLoadingbar() != null && getLoadingbar().isActive())
			active = true;
		return active;
	}

	/**
	 * Caricamento della pagina di visualizzazione del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String loadShowdocRaccoglitore() throws Exception {
		try {
			getLoadingbar().setActive(false);
			action = "";

			// TODO DA OTTIMIZZARE... e' possibile simulare il reload della showdoc senza chiamare il queryplain con la ricerca?

			DocWayHome docwayHome = (DocWayHome) getSessionAttribute("docwayHome");
			if (docwayHome != null
					&& physDocRacc > 0) {

				docwayHome.queryPlain("[rac_codammaoo]=\"" + codSede + "\" and [?nDoc]=\"" + physDocRacc + "\"");
				return "showdoc@raccoglitore@INDICE@reload";
			}
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;
		}
	}

}
