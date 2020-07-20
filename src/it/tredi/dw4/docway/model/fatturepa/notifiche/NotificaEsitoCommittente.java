package it.tredi.dw4.docway.model.fatturepa.notifiche;

import java.util.ArrayList;
import java.util.List;

public class NotificaEsitoCommittente {

	// accettazione/rifiuto dell'intero documento (singola fattura o lotto di fatture)
	private boolean accettazioneInteroDocumentoEnabled = false;
	private String esitoInteroDocumento = "";
	
	private List<EsitoCommittenteFattura> esitiFatture = new ArrayList<EsitoCommittenteFattura>();
	
	private String motivazioneRifiuto = null; // eventuale motivazione di rifiuto di una fattura/lotto di fatture
	
	public NotificaEsitoCommittente() {
		accettazioneInteroDocumentoEnabled = false;
		esitoInteroDocumento = "";
		esitiFatture = new ArrayList<EsitoCommittenteFattura>();
		motivazioneRifiuto = "";
	}
	
	public boolean isAccettazioneInteroDocumentoEnabled() {
		return accettazioneInteroDocumentoEnabled;
	}

	public void setAccettazioneInteroDocumentoEnabled(
			boolean accettazioneInteroDocumentoEnabled) {
		this.accettazioneInteroDocumentoEnabled = accettazioneInteroDocumentoEnabled;
	}

	public String getEsitoInteroDocumento() {
		return esitoInteroDocumento;
	}

	public void setEsitoInteroDocumento(String esitoInteroDocumento) {
		this.esitoInteroDocumento = esitoInteroDocumento;
	}

	public List<EsitoCommittenteFattura> getEsitiFatture() {
		return esitiFatture;
	}

	public void setEsitiFatture(List<EsitoCommittenteFattura> esitiFatture) {
		this.esitiFatture = esitiFatture;
	}
	
	public String getMotivazioneRifiuto() {
		return motivazioneRifiuto;
	}

	public void setMotivazioneRifiuto(String motivazioneRifiuto) {
		this.motivazioneRifiuto = motivazioneRifiuto;
	}
	
	/**
	 * aggiunta di una notifica su specifica fattura del documento (lotto di fatture)
	 * @param esitoFattura
	 */
	public void addEsitoFattura(EsitoCommittenteFattura esitoFattura) {
		if (esitoFattura != null) {
			if (esitiFatture == null) 
				esitiFatture = new ArrayList<EsitoCommittenteFattura>();
			
			esitiFatture.add(esitoFattura);
		}
	}

}
