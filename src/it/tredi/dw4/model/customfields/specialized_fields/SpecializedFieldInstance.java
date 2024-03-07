package it.tredi.dw4.model.customfields.specialized_fields;

import it.tredi.dw4.beans.Page;
import it.tredi.dw4.model.customfields.Validator;

public interface SpecializedFieldInstance {

	boolean hasSpecificValidators();

	boolean doSpecificValidation(Page pageBean, Validator validator, String currentFormLevelId, String idCampo, String label);

}
