package it.tredi.dw4.model.customfields;

/**
 * Tipologia di azione scatenata in caso di relazione fra campi custom
 * @author mbernardini
 *
 */
public enum RelationshipAction {

	REQUIRED("required"),
	VISIBLE("visible");
	
	private String text;

	RelationshipAction(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
	
	public static RelationshipAction fromString(String text) {
		if (text != null && !text.isEmpty()) {
			for (RelationshipAction type : RelationshipAction.values()) {
				if (type.text.equalsIgnoreCase(text)) {
					return type;
				}
			}
		}
		return null;
	}
	
}
