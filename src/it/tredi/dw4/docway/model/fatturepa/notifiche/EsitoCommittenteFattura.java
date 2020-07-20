package it.tredi.dw4.docway.model.fatturepa.notifiche;


public class EsitoCommittenteFattura {

	private int posizione = -1;
	private String esito = "";
	private String tipoDocumento = "";
	private String numero = "";
	private String data = "";
	
	public EsitoCommittenteFattura(String tipo, String numero, String data, int posizone) {
		this.tipoDocumento = tipo;
		this.numero = numero;
		this.data = data;
		this.posizione = posizone;
		this.esito = "";
	}
	
	public int getPosizione() {
		return posizione;
	}

	public void setPosizione(int posizione) {
		this.posizione = posizione;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
