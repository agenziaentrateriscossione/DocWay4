package it.tredi.dw4.docwayproc.model;

public class NodoTitolario {

	private String text = "";
	
	public NodoTitolario() { }
	
	public NodoTitolario(String val) {
		this.text = val;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
