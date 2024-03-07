package it.tredi.dw4.beans;

import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.azionimassive.Azione;
import it.tredi.dw4.model.azionimassive.ParametroInput;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

/**
 * Modale di avvio di una specifica azione massiva tramite stored procedure LUA. Il popup modale viene aperto solo nel caso in cui
 * la stored procedure preveda l'invio di parametri di input
 * @author mbernardini
 */
public class AzioneMassivaInputParams {

	private Azione azione;
	private Page pageBean;
	private boolean active = false;
	
	public AzioneMassivaInputParams(Azione azione, Page pageBean) {
		this.azione = azione;
		this.pageBean = pageBean;
		this.active = true;
	}
	
	/**
	 * Chiusura del modale di avvio dell'azione massiva
	 * @return
	 */
	public String close() {
		this.active = false;
		if (pageBean != null)
			pageBean.removeSessionAttribute("azioneMassivaInputParams");
		return null;
	}
	
	public Azione getAzione() {
		return azione;
	}

	public void setAzione(Azione azione) {
		this.azione = azione;
	}

	public Page getPageBean() {
		return pageBean;
	}

	public void setPageBean(Page pageBean) {
		this.pageBean = pageBean;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String save() throws Exception {
		if (checkRequiredField()) return null;
		
		if (pageBean != null) {
			if (pageBean instanceof Titles)
				return ((Titles) pageBean).startAzioneMassiva(azione);
			else if (pageBean instanceof Showdoc)
				return ((Showdoc) pageBean).startAzioneMassiva(azione);
			else
				throw new Exception("Azioni massive tramite Stored Procedure LUA non supportate per il bean " + pageBean.getClass().getName());
		}
		return null;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		if (azione.getParametriInput() != null && !azione.getParametriInput().isEmpty()) {
			int i=0;
			for (ParametroInput param : azione.getParametriInput()) {
				if (param != null) {
					if (param.isRequired() && (param.getValue() == null || param.getValue().isEmpty())) {
						if (pageBean != null)
							pageBean.setErrorMessage("templateForm:azioneMassivaParams:" + i + ":inputParam", I18N.mrs("acl.requiredfield") + " '" + param.getLabel() + "'");
						result = true;
					}
					else if (param.getTipo().equals("calendar")) {
						// Controllo se e' stato inserito un valore valido nel campo data
						if (!DateUtil.isValidDate(param.getValue(), formatoData)) {
							pageBean.setErrorMessage("templateForm:azioneMassivaParams:" + i + ":inputParam", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + param.getLabel() + "': " + formatoData.toLowerCase());
							result = true;
						}
					}
				}
				i++;
			}
		}
		return result;
	}
	
}
