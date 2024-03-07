package it.tredi.dw4.model.customfields;

/**
 * Eventuale azione da svolgere in caso di selezione di un item di una tendina custom
 */
public class ItemAction {

	private String item = "";
	private String type = "";
	private String value = "";
	
	/**
	 * Costruttore
	 * @param item
	 * @param type
	 * @param value
	 */
	public ItemAction(String item, String type, String value) {
		this.item = item;
		this.type = type;
		this.value = value;
	}
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
