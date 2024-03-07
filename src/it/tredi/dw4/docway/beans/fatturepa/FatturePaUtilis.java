package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.fatturepa.DatiFattura;
import it.tredi.dw4.docway.model.fatturepa.DatiPagamento;
import it.tredi.dw4.docway.model.fatturepa.DettaglioPagamento;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.docway.model.fatturepa.LineaBeniServizi;
import it.tredi.dw4.docway.model.fatturepa.RiepilogoBeniServizi;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatturePaUtilis {
	
	/**
	 * dato una versione di fatturaPA restituisce il file da utilizzare per la generazione della preview della fattura
	 * in base a quanto definito sul file di properties di DocWay
	 * 
	 * @param versione versione di fatturaPA 
	 * @return
	 */
	public static String getXsltFileForPreview(String versione) {
		String file = "";
		
		if (versione == null || versione.length() == 0)
			versione = "1.0"; // nel caso la versione sia vuota si considera la prima versione emessa (1.0). su docway non gestivamo ancora la versione di fatturaPA
		
		List<String> xslts = DocWayProperties.getPropertyList("fatturepa.xslt");
		if (xslts != null && xslts.size() > 0) {
			if (versione.toLowerCase().equals("latest")) {
				// riscontrato errore in fase di recupero della versione di fatturaPA in fase di analisi dell'XML (archiviatore)
				file = xslts.get(xslts.size()-1).substring(xslts.get(xslts.size()-1).indexOf(",")+1);
			}
			else {
				int i = 0;
				while (i < xslts.size() && file.length() == 0) {
					String xslt = xslts.get(i);
					if (xslt.length() > 0 && xslt.startsWith(versione + ","))
						file = xslt.substring(xslt.indexOf(",")+1);
					i++;
				}
			}
		}
		
		return file;
	}
	
	/**
	 * dato una versione di fatturaPA restituisce il file XSL con il FOP da utilizzare per la generazione della preview della fattura
	 * in base a quanto definito sul file di properties di DocWay
	 * 
	 * @param versione versione di fatturaPA 
	 * @return
	 */
	public static String getXslFopForPreview(String versione) {
		String file = "";
		
		if (versione == null || versione.length() == 0)
			versione = "1.0"; // nel caso la versione sia vuota si considera la prima versione emessa (1.0). su docway non gestivamo ancora la versione di fatturaPA
		
		List<String> fops = DocWayProperties.getPropertyList("fatturepa.fop");
		if (fops != null && fops.size() > 0) {
			if (versione.toLowerCase().equals("latest")) {
				// riscontrato errore in fase di recupero della versione di fatturaPA in fase di analisi dell'XML (archiviatore)
				file = fops.get(fops.size()-1).substring(fops.get(fops.size()-1).indexOf(",")+1);
			}
			else {
				int i = 0;
				while (i < fops.size() && file.length() == 0) {
					String xslt = fops.get(i);
					if (xslt.length() > 0 && xslt.startsWith(versione + ","))
						file = xslt.substring(xslt.indexOf(",")+1);
					i++;
				}
			}
		}
		
		return file;
	}

	/**
	 * ritorna la lista di voci per il regime fiscale delle fatturePA
	 */
	public static List<Option> getListaRegimeFiscale() {
		return getListaRegimeFiscale(false);
	}
	
	/**
	 * ritorna la lista di voci per il regime fiscale delle fatturePA
	 */
	public static List<Option> getListaRegimeFiscale(boolean addFirstEmpty) {
		String[] values = DocWayProperties.readProperty("fatturepa.regime_fiscale", "").split("\\|");
		return getListOptions(values, addFirstEmpty);
	}
	
	/**
	 * ritorna la lista di voci per il tipo documento delle fatturePA
	 */
	public static List<Option> getListaTipoDocumento() {
		return getListaTipoDocumento(false);
	}
	
	/**
	 * ritorna la lista di voci per il tipo documento delle fatturePA
	 */
	public static List<Option> getListaTipoDocumento(boolean addFirstEmpty) {
		String[] values = DocWayProperties.readProperty("fatturepa.tipo_documento", "").split("\\|");
		return getListOptions(values, addFirstEmpty);
	}
	
	/**
	 * ritorna la lista di voci per le condizioni di pagamento delle fatturePA
	 */
	public static List<Option> getListaCondizioniPagamento() {
		return getListaCondizioniPagamento(false);
	}
	
	/**
	 * ritorna la lista di voci per le condizioni di pagamento delle fatturePA
	 */
	public static List<Option> getListaCondizioniPagamento(boolean addFirstEmpty) {
		String[] values = DocWayProperties.readProperty("fatturepa.condizioni_pagamento", "").split("\\|");
		return getListOptions(values, addFirstEmpty);
	}
	
	/**
	 * ritorna la lista di voci per le modalita' di pagamento delle fatturePA
	 */
	public static List<Option> getListaModalitaPagamento() {
		return getListaModalitaPagamento(false);
	}
	
	/**
	 * ritorna la lista di voci per le modalita' di pagamento delle fatturePA
	 */
	public static List<Option> getListaModalitaPagamento(boolean addFirstEmpty) {
		String[] values = DocWayProperties.readProperty("fatturepa.modalita_pagamento", "").split("\\|");
		return getListOptions(values, addFirstEmpty);
	}
	
	/**
	 * dato un array di valori costruisce la lista di opzioni per i campi select dell'interfaccia
	 * di inserimento/modifica di fatturePA
	 * 
	 * @param values
	 * @return
	 */
	private static List<Option> getListOptions(String[] values, boolean addFirstEmpty) {
		List<Option> lista = new ArrayList<Option>();
		
		// aggiunta di una eventuale voce vuota all'inizio della lista
		if (addFirstEmpty) {
			Option empty = new Option();
			empty.setLabel("");
			empty.setValue("");
			lista.add(empty);
		}
		
		if (values != null && values.length > 0) {
			for (int i=0; i<values.length; i++) {
				if (values[i] != null && values[i].length() > 0) {
					
					int index = values[i].indexOf("=");
					if (index != -1) {
						Option opzione = new Option();
						opzione.setLabel(values[i].substring(index+1));
						opzione.setValue(values[i].substring(0, index));
						
						lista.add(opzione);
					}
				}
			}
		}
		
		return lista;
	}
	
	/**
	 * controllo dei campo obbligatori per il registro delle fattur
	 * 
	 * @param doc
	 * @param fatturaPA
	 * @param pageBean
	 * @param enableRegistroFatture true se occorre gestire il registro delle fatture, false altrimenti
	 * @return false se tutti i campi obbligatori del registro fatture sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public static boolean checkRequiredFieldRegistroFatture(Doc doc, FatturaPA fatturaPA, Page pageBean, boolean enableRegistroFatture) {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		for (int i=0; i<fatturaPA.getDatiFattura().size(); i++) { 
			DatiFattura datiFattura = fatturaPA.getDatiFattura().get(i);
			if (datiFattura != null) {
				if (datiFattura.getDatiRegistroFatture().getNumFattura().trim().length() == 0) {
					pageBean.setErrorMessage("templateForm:fattura:" + i + ":numFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numero_fattura") + "'");
					result = true;
				}
				if (datiFattura.getDatiRegistroFatture().getDataEmissioneFattura().trim().length() == 0) {
					pageBean.setErrorMessage("templateForm:fattura:" + i + ":dataFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_fattura") + "'");
					result = true;
				}
				else {
					if (!DateUtil.isValidDate(datiFattura.getDatiRegistroFatture().getDataEmissioneFattura(), formatoData)) {
						pageBean.setErrorMessage("templateForm:fattura:" + i + ":dataFattura", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_fattura") + "': " + formatoData.toLowerCase());
						result = true;
					}
				}
				if (datiFattura.getDatiRegistroFatture().getOggettoFornitura().trim().length() == 0) {
					pageBean.setErrorMessage("templateForm:fattura:" + i + ":oggettoFornitura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.oggetto_fattura") + "'");
					result = true;
				}
				if (datiFattura.getDatiRegistroFatture().getImportoTotale().trim().length() == 0) {
					pageBean.setErrorMessage("templateForm:fattura:" + i + ":importoTotale", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.importo") + "'");
					result = true;
				}
				else {
					if (!StringUtil.isDecimal(datiFattura.getDatiRegistroFatture().getImportoTotale())) {
						pageBean.setErrorMessage("templateForm:fattura:" + i + ":importoTotale", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.importo") + "'");
						result = true;
					}
				}
				if (enableRegistroFatture) {
					if (datiFattura.getDatiRegistroFatture().getEstremiImpegno().trim().length() == 0) {
						pageBean.setErrorMessage("templateForm:fattura:" + i + ":estremiImpegno", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.estremi_impegno") + "'");
						result = true;
					}
					if (datiFattura.getDatiRegistroFatture().getDataScadenzaFattura().trim().length() == 0) {
						pageBean.setErrorMessage("templateForm:fattura:" + i + ":dataScadenza", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_scadenza") + "'");
						result = true;
					}
					else {
						if (!DateUtil.isValidDate(datiFattura.getDatiRegistroFatture().getDataScadenzaFattura(), formatoData)) {
							pageBean.setErrorMessage("templateForm:fattura:" + i + ":dataScadenza", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_scadenza") + "': " + formatoData.toLowerCase());
							result = true;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * controllo dei campo obbligatori per la fattura
	 * 
	 * @param doc
	 * @param fatturaPA
	 * @param pageBean
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public static boolean checkRequiredField(Doc doc, FatturaPA fatturaPA, Page pageBean) {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		// controllo sui campi specifici della fattura

		// destinatario valorizzato
		if (doc.getRif_esterni().get(0).getNome() == null || doc.getRif_esterni().get(0).getNome().length() == 0) {
			pageBean.setErrorMessage("templateForm:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.destinatario") + "'");
			result = true;
		}
		else {
			// controllo su campi obbligatori destinatario
			if ((doc.getRif_esterni().get(0).getCodice_fiscale() == null || doc.getRif_esterni().get(0).getCodice_fiscale().length() == 0)
					&& (doc.getRif_esterni().get(0).getPartita_iva() == null || doc.getRif_esterni().get(0).getPartita_iva().length() == 0)) {
				String[] fieldIds = { "templateForm:cf", "templateForm:piva" };
				pageBean.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_specificare_almeno_un_codice_fiscale_o_una_partita_iva"));
				result = true;
			}
			
			if (doc.getRif_esterni().get(0).getIndirizzo() == null || doc.getRif_esterni().get(0).getIndirizzo().length() == 0) {
				pageBean.setErrorMessage("templateForm:address", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.address") + "'");
				result = true;
			}
		}
		
		// campo codice nazione del cedente valorizzato
		if (fatturaPA.getDatiCedentePrestatore().getIdPaese() == null || fatturaPA.getDatiCedentePrestatore().getIdPaese().length() == 0) {
			pageBean.setErrorMessage("templateForm:idPaeseCedente", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.codice_nazione") + "'");
			result = true;
		}
		else {
			// campo codice nazione con solo caratteri alfabetici
			if (!fatturaPA.getDatiCedentePrestatore().getIdPaese().matches("[a-zA-Z]+")) {
				pageBean.setErrorMessage("templateForm:idPaeseCedente", I18N.mrs("dw4.l_identificativo_del_paese_deve_essere_composto_solo_da_lettere"));
				result = true;
			}
		}
		
		// campo codice nazione della trasmissione valorizzato
		if (fatturaPA.getDatiTrasmissione().getIdPaese() == null || fatturaPA.getDatiTrasmissione().getIdPaese().length() == 0) {
			pageBean.setErrorMessage("templateForm:idPaeseTrasmissione", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.codice_nazione") + "'");
			result = true;
		}
		else {
			// campo codice nazione con solo caratteri alfabetici
			if (!fatturaPA.getDatiTrasmissione().getIdPaese().matches("[a-zA-Z]+")) {
				pageBean.setErrorMessage("templateForm:idPaeseTrasmissione", I18N.mrs("dw4.l_identificativo_del_paese_deve_essere_composto_solo_da_lettere"));
				result = true;
			}
		}
		// campo codice fiscale in trasmissione valorizzato
		if (fatturaPA.getDatiTrasmissione().getIdCodice() == null || fatturaPA.getDatiTrasmissione().getIdCodice().length() == 0) {
			pageBean.setErrorMessage("templateForm:idFiscaleTrasmissione", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.identificativo_fiscale") + "'");
			result = true;
		}
		// campo progressivo invio valorizzato
		if (fatturaPA.getDatiTrasmissione().getProgressivoInvio() == null || fatturaPA.getDatiTrasmissione().getProgressivoInvio().length() == 0) {
			pageBean.setErrorMessage("templateForm:progressivoInvio", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.progressivo_invio") + "'");
			result = true;
		} 
		else {
			// TODO controllo sul valore del progressivo
		}
		// campo codice destinatario in trasmissione valorizzato
		if (fatturaPA.getDatiTrasmissione().getCodiceDestinatario() == null || fatturaPA.getDatiTrasmissione().getCodiceDestinatario().length() == 0) {
			pageBean.setErrorMessage("templateForm:codiceDestinatarioTrasmissione", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.codice_destinatario") + "'");
			result = true;
		}
		
		// campo divisa in dati fattura
		if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getDivisa() == null || fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getDivisa().length() == 0) {
			pageBean.setErrorMessage("templateForm:divisaFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.divisa") + "'");
			result = true;
		}
		else {
			// campo divisa con solo caratteri alfabetici
			if (!fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getDivisa().matches("[a-zA-Z]+")) {
				pageBean.setErrorMessage("templateForm:divisaFattura", I18N.mrs("dw4.la_divisa_deve_essere_composta_solo_da_lettere"));
				result = true;
			}
		}
		// campo numero in dati fattura
		if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getNumero() == null || fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getNumero().length() == 0) {
			pageBean.setErrorMessage("templateForm:numeroFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.num") + "'");
			result = true;
		}
		// campo data fattura
		if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getData() == null || fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getData().length() == 0) {
			pageBean.setErrorMessage("templateForm:dataFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data") + "'");
			result = true;
		}
		else {
			// controllo sul formato della data
			if (!DateUtil.isValidDate(fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getData(), formatoData)) {
				pageBean.setErrorMessage("templateForm:dataFattura", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		// campo causale in dati fattura
		if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getCausale() == null || fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getCausale().length() == 0) {
			pageBean.setErrorMessage("templateForm:causaleFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.causale") + "'");
			result = true;
		}
		// campo importo totale in dati fattura
		if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getImportoTotaleDocumento() != null && fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getImportoTotaleDocumento().length() > 0) {
			if (!StringUtil.isDecimal(fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getImportoTotaleDocumento())) {
				pageBean.setErrorMessage("templateForm:importoTotaleFattura", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.importo_totale") + "'");
				result = true;
			}
		}
		
		// TODO controllo su campi obbligatori del committente
		
		// controllo sulle linee della fattura
		List<LineaBeniServizi> lineeFattura = fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getLineaBeniServizi();
		for (int i=0; i<lineeFattura.size(); i++)
			if (checkLineaBeniServizi(lineeFattura.get(i), i, pageBean))
				result = true;
		
		// controllo sui riepiloghi della fattura
		List<RiepilogoBeniServizi> riepiloghiFattura = fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getRiepilogoBeniServizi();
		for (int i=0; i<riepiloghiFattura.size(); i++)
			if (checkRiepilogoBeniServizi(riepiloghiFattura.get(i), i, pageBean))
				result = true;
		
		// controllo sulle condizioni di pagamento
		List<DatiPagamento> datiPagamento = fatturaPA.getDatiFattura().get(0).getDatiPagamento();
		for (int i=0; i<datiPagamento.size(); i++)
			if (checkDatiPagamento(datiPagamento.get(i), i, pageBean))
				result = true;
				
		return result;
	}
	
	/**
	 * controllo sulle righe della fattura
	 * 
	 * @param lineaBeniServizi
	 * @param index
	 * @param pageBean
	 * @return
	 */
	private static boolean checkLineaBeniServizi(LineaBeniServizi lineaBeniServizi, int index, Page pageBean) {
		boolean result = false;
		
		if (lineaBeniServizi != null) {
			if (lineaBeniServizi.getDescrizione().length() == 0 
					&& lineaBeniServizi.getPrezzoUnitario().length() == 0
					&& lineaBeniServizi.getAliquotaIva().length() == 0
					&& lineaBeniServizi.getPrezzoTotale().length() == 0) {
				String[] fieldIds = { "templateForm:lineaBeniServizi:" + index + ":descrizioneLinea", "templateForm:lineaBeniServizi:" + index + ":prezzoUnitario", "templateForm:lineaBeniServizi:" + index + ":aliquotaIva", "templateForm:lineaBeniServizi:" + index + ":prezzoTotale"  };
				pageBean.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_i_dati_della_linea_della_fattura_beni_servizi"));
				result = true;
			}
			else {
				// controllo sul campo descrizione
				if (lineaBeniServizi.getDescrizione() == null || lineaBeniServizi.getDescrizione().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":descrizioneLinea", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.description") + "'");
					result = true;
				}
				
				// controllo sul campo prezzo unitario
				if (lineaBeniServizi.getPrezzoUnitario() == null || lineaBeniServizi.getPrezzoUnitario().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":prezzoUnitario", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.prezzo_unitario") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo prezzo unitario
					if (!StringUtil.isDecimal(lineaBeniServizi.getPrezzoUnitario())) {
						pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":prezzoUnitario", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.prezzo_unitario") + "'");
						result = true;
					}
				}
				
				// controllo sul campo aliquotaIVA
				if (lineaBeniServizi.getAliquotaIva() == null || lineaBeniServizi.getAliquotaIva().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":aliquotaIva", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.aliquota_iva") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo aliquotaIVA
					if (!StringUtil.isDecimal(lineaBeniServizi.getAliquotaIva())) {
						pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":aliquotaIva", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.aliquota_iva") + "'");
						result = true;
					}
				}
				
				// controllo sul campo prezzo totale
				if (lineaBeniServizi.getPrezzoTotale() == null || lineaBeniServizi.getPrezzoTotale().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":prezzoTotale", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.prezzo_totale") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo prezzo totale
					if (!StringUtil.isDecimal(lineaBeniServizi.getPrezzoTotale())) {
						pageBean.setErrorMessage("templateForm:lineaBeniServizi:" + index + ":prezzoTotale", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.prezzo_totale") + "'");
						result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * controllo sui riepiloghi della fattura
	 * 
	 * @param lineaBeniServizi
	 * @param index
	 * @param pageBean
	 * @return
	 */
	private static boolean checkRiepilogoBeniServizi(RiepilogoBeniServizi riepilogoBeniServizi, int index, Page pageBean) {
		boolean result = false;
		
		if (riepilogoBeniServizi != null) {
			if (riepilogoBeniServizi.getAliquotaIVA().length() == 0 
					&& riepilogoBeniServizi.getImponibileImporto().length() == 0
					&& riepilogoBeniServizi.getImposta().length() == 0) {
				String[] fieldIds = { "templateForm:lineaRiepilogo:" + index + ":aliquotaIva", "templateForm:lineaRiepilogo:" + index + ":imponibile", "templateForm:lineaRiepilogo:" + index + ":imposta" };
				pageBean.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_i_dati_di_riepilogo_della_fattura"));
				result = true;
			}
			else {
				// controllo sul campo aliquotaIVA
				if (riepilogoBeniServizi.getAliquotaIVA() == null || riepilogoBeniServizi.getAliquotaIVA().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":aliquotaIva", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.aliquota_iva") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo aliquotaIVA
					if (!StringUtil.isDecimal(riepilogoBeniServizi.getAliquotaIVA())) {
						pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":aliquotaIva", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.aliquota_iva") + "'");
						result = true;
					}
				}
				
				// controllo sul campo imponibile/importo
				if (riepilogoBeniServizi.getImponibileImporto() == null || riepilogoBeniServizi.getImponibileImporto().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":imponibile", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.imponibile") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo imponibile/importo
					if (!StringUtil.isDecimal(riepilogoBeniServizi.getImponibileImporto())) {
						pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":imponibile", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.imponibile") + "'");
						result = true;
					}
				}
				
				// controllo sul campo imposta
				if (riepilogoBeniServizi.getImposta() == null || riepilogoBeniServizi.getImposta().length() == 0) {
					pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":imposta", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.imposta") + "'");
					result = true;
				}
				else {
					// valore decimale nel campo imposta
					if (!StringUtil.isDecimal(riepilogoBeniServizi.getImposta())) {
						pageBean.setErrorMessage("templateForm:lineaRiepilogo:" + index + ":imposta", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.imposta") + "'");
						result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * controllo sui riepiloghi della fattura
	 * 
	 * @param lineaBeniServizi
	 * @param index
	 * @param pageBean
	 * @return
	 */
	private static boolean checkDatiPagamento(DatiPagamento datiPagamento, int index, Page pageBean) {
		boolean result = false;
		
		if (datiPagamento != null) {
			if (datiPagamento.getCondizioniPagamento() != null && datiPagamento.getCondizioniPagamento().length() > 0) {
				// se le condizioni di pagamento sono valorizzate occorre impostare 
				// anche i dettagli del pagamento
				
				for (int i=0; i<datiPagamento.getDettaglioPagamento().size(); i++) {
					DettaglioPagamento dettaglioPagamento = (DettaglioPagamento) datiPagamento.getDettaglioPagamento().get(i);
					if (dettaglioPagamento != null) {
						
						// controllo sul campo modalita'
						if (dettaglioPagamento.getModalitaPagamento() == null || dettaglioPagamento.getModalitaPagamento().length() == 0) {
							pageBean.setErrorMessage("templateForm:dettaglioPagamento:" + i + ":modalitaPagamento", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.modalita_pagamento") + "'");
							result = true;
						}
						
						// controllo sul campo importo
						if (dettaglioPagamento.getImportoPagamento() == null || dettaglioPagamento.getImportoPagamento().length() == 0) {
							pageBean.setErrorMessage("templateForm:dettaglioPagamento:" + index + ":importoPagamento", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.importo") + "'");
							result = true;
						}
						else {
							// valore decimale nel campo importo
							if (!StringUtil.isDecimal(dettaglioPagamento.getImportoPagamento())) {
								pageBean.setErrorMessage("templateForm:dettaglioPagamento:" + index + ":importoPagamento", I18N.mrs("dw4.inserire_un_valore_decimale_nel_campo") + " '" + I18N.mrs("dw4.importo") + "'");
								result = true;
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
}
