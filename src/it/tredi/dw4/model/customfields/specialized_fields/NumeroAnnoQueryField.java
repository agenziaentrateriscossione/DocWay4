package it.tredi.dw4.model.customfields.specialized_fields;

import it.tredi.dw4.model.customfields.QueryField;

public class NumeroAnnoQueryField extends QueryField {

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
	public void setValue(String value) {
		super.setValue(value);
		if (value != null && !value.isEmpty()) {
			int separator = value.indexOf("/");
			if (separator > -1) {
				numero = value.substring(0, separator);
				anno = value.substring(separator + 1);
			}
		}
	}
}
