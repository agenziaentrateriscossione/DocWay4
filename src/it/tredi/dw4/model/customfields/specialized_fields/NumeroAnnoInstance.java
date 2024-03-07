package it.tredi.dw4.model.customfields.specialized_fields;

import it.tredi.dw4.beans.Page;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.customfields.FieldInstance;
import it.tredi.dw4.model.customfields.Validator;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumeroAnnoInstance extends FieldInstance implements SpecializedFieldInstance {

	private String numero = "";
	private String anno = "";

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
		updateValue();
	}

	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
		updateValue();
	}

	private void updateValue() {
		if ((numero == null || numero.isEmpty()) && (anno == null || anno.isEmpty()))
			super.setValue("");
		else
			super.setValue(numero + "/" + anno);
	}

	@Override
	public void initFieldValue(Element context, String type, String xpath, String defaultvalue) {
		super.initFieldValue(context, type, xpath, defaultvalue);
		String value = super.getValue();
		if (value != null && !value.isEmpty()) {
			int separator = value.indexOf("/");
			if (separator > -1) {
				numero = value.substring(0, separator);
				anno = value.substring(separator + 1);
			}
		}
	}

	@Override
	public boolean hasSpecificValidators() {
		return true;
	}

	@Override
	public boolean doSpecificValidation(Page pageBean, Validator validator, String currentFormLevelId, String idCampo, String label) {
		boolean hasError = false;
		Integer minAnno = null;
		Integer maxAnno = null;

		// ricerca validators custom per custom field numero_anno
		if (validator != null && validator.getAllAttr() != null && !validator.getAllAttr().isEmpty()) {

			// ricerca degli attributi min-anno e max-anno
			Attribute[] allAttr = validator.getAllAttr().toArray(new Attribute[validator.getAllAttr().size()]);
			List<Attribute> validationRules = Arrays.stream(allAttr).filter(attr -> {
				return (attr.getName().equals("min-anno") || attr.getName().equals("max-anno"));
			}).collect(Collectors.toList());
			if (!validationRules.isEmpty()) {
				for (Attribute validationRule : validationRules) {
					int year;
					if (validationRule.getValue().equals("CURRENT_YEAR")) {
						year = LocalDate.now().getYear();
					}
					else
						year = Integer.parseInt(validationRule.getValue());
					if (validationRule.getName().equals("min-anno"))
						minAnno = year;
					else
						maxAnno = year;
				}
			}
		}

		// numero deve essere un valido intero e deve essere valorizzato se anno è valorizzato
		if (numero != null && !numero.isEmpty()) {
			try {
				Integer.parseInt(numero);
			} catch (NumberFormatException e) {
				Object[] params = {label};
				pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_numero", I18N.mrs("dw4.inserire_un_numero_valido_per_il_campo_X", params));
				hasError = true;
				setNumero("");
			}
			if (anno == null || anno.isEmpty()) {
				Object[] params = {label};
				pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_anno", I18N.mrs("dw4.occorre_valorizzare_anche_l_anno_per_il_campo_X", params));
				hasError = true;
			}
		}
		// anno deve essere un valido intero e deve essere valorizzato se numero è valorizzato
		// inoltre se specicato deve rispettare i vincoli dati da min-anno e max-anno
		if (anno != null && !anno.isEmpty()) {
			try {
				int annoValue = Integer.parseInt(anno);
				if (minAnno != null && annoValue < minAnno) {
					Object[] params = {minAnno.toString(), label};
					pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_anno", I18N.mrs("dw4.inserire_un_anno_non_precedente_al_X_per_il_campo_Y", params));
					hasError = true;
				}
				else if (maxAnno != null && annoValue > maxAnno) {
					Object[] params = {maxAnno.toString(), label};
					pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_anno", I18N.mrs("dw4.inserire_un_anno_non_successivo_al_X_per_il_campo_Y", params));
					hasError = true;
				}
			} catch (NumberFormatException e) {
				Object[] params = {label};
				pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_anno", I18N.mrs("dw4.inserire_un_anno_valido_per_il_campo_X", params));
				hasError = true;
				setAnno("");
			}
			if ((numero == null || numero.isEmpty()) && !hasError) {
				Object[] params = {label};
				pageBean.setErrorMessage(currentFormLevelId + ":" + idCampo + "_numero", I18N.mrs("dw4.occorre_valorizzare_anche_il_numero_per_il_campo_X", params));
				hasError = true;
			}
		}
		return hasError;
	}
}
