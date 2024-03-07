package it.tredi.dw4.docway.beans;

import java.util.List;

import org.dom4j.Element;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.docway.model.Tag;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.NumFascUtils;
import it.tredi.dw4.utils.XMLDocumento;

public abstract class DocEditFascicoloShared extends DocWayDocedit {

	// mbernardini 19/06/2019 : eventuale messaggio di errore riscontrato in fase di validazione dei tag (controllo univocita')
	private String validationTagsErrorMessage;
	
	public String getValidationTagsErrorMessage() {
		return validationTagsErrorMessage;
	}

	public void setValidationTagsErrorMessage(String validationTagsErrorMessage) {
		this.validationTagsErrorMessage = validationTagsErrorMessage;
	}
	
	public abstract Fascicolo getFascicolo();
	
	@Override
	public abstract DocDocWayDocEditFormsAdapter getFormsAdapter();
	
	/**
	 * Inizializzazione dei tags
	 */
	protected void initTags() {
		this.validationTagsErrorMessage = "";
	}
	
	/**
	 * Verifica di univocita' dei tag indicati
	 */
	public String isTagsUnique() throws Exception {
		try {
			String query = "";
			if (getFascicolo().getTags() != null && !getFascicolo().getTags().isEmpty()) {
				for (Tag tag : getFascicolo().getTags()) {
					if (tag != null && tag.getValue() != null && !tag.getValue().isEmpty()) {
						query += "[/fascicolo/tags/tag/@value]=\"" + tag.getValue() + "\" AND ";
					}
				}
				if (query.endsWith(" AND "))
					query = query.substring(0, query.length()-5);
				
				// In caso di modifica di un fascicolo occorre escluderlo in fase di verifica dell'univocita' dei tags
				String nrecord = getFascicolo().getNrecord();
				if (nrecord != null && !nrecord.isEmpty() && !nrecord.equals(".")) { 
					query += " AND NOT([/fascicolo/@nrecord]=\"" + nrecord + "\")";
				}
			}
			if (!query.isEmpty()) {
				
				getFormsAdapter().tagsValidation(query);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				Element validation = response.getRootElement().element("validation");
				if (validation != null) {
					int count = Integer.parseInt(validation.attributeValue("count", "0"));
					if (count > 0) {
						this.validationTagsErrorMessage = I18N.mrs("dw4.trovati_x_fascicoli_con_associati_i_tag_specificati", new Object[]{ count }) + ": ";
						List<?> titles = validation.selectNodes("./titoli/titolo");
						if (titles != null && titles.size() > 0) {
							for (int i=0; i<titles.size(); i++) {
								Element title = (Element) titles.get(i);
								if (title != null && title.getTextTrim() != null)
									this.validationTagsErrorMessage += NumFascUtils.format(title.getTextTrim()) + ", ";
							}
							
							if (this.validationTagsErrorMessage.endsWith(", "))
								this.validationTagsErrorMessage = this.validationTagsErrorMessage.substring(0, this.validationTagsErrorMessage.length()-2);
						}
					}
					else {
						this.validationTagsErrorMessage = "";
					}
				}
				
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		return null;
	}

}
