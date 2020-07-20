package it.highwaytech.apps.xdocway.apv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FatturaApv extends EntityBaseApv {
	//Il fornitore che ha inviato la fattura, su DocWay rif_esterni/rif/nome
	private String fornitore;
	//Il numero della fattura, su DocWay extra/fatturaPA/datiFattura/datiGeneraliDocumento/@numero
	private String numero;
	//L'importo della fattura, su DocWay extra/fatturaPA/datiFattura/datiGeneraliDocumento/@importoTotaleDocumento
	private String importo;
	//Viene utilizzato per calcolare i giorni data fattura, su DocWay extra/fatturaPA/@sendDate
	private Date dataInvioEmail;
	//Lista degli RepertoriOrdini e RepertoriDecretazioni degli ordini a cui la fattura e' agganciata
	private List<FatturaRepertorioOrdineRepertorioDecretazioneApv> ordiniRepertori;
	
	public String getFornitore() {
		return fornitore;
	}
	public void setFornitore(String fornitore) {
		this.fornitore = fornitore;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public Date getDataInvioEmail() {
		return dataInvioEmail;
	}
	public void setDataInvioEmail(Date dataInvioEmail) {
		this.dataInvioEmail = dataInvioEmail;
	}
	public List<FatturaRepertorioOrdineRepertorioDecretazioneApv> getOrdiniRepertori() {
		if(ordiniRepertori==null)
			ordiniRepertori = new ArrayList<FatturaRepertorioOrdineRepertorioDecretazioneApv>();
		return ordiniRepertori;
	}
	public void setOrdiniRepertori(
			List<FatturaRepertorioOrdineRepertorioDecretazioneApv> ordiniRepertori) {
		this.ordiniRepertori = ordiniRepertori;
	}
	//Restituisce i giorni trascorsi dall'invio della fattura se non conclusa
	public String getGiorniDataFattura() {
		String toRet = "==";

        //GregorianCalendar curDate = new GregorianCalendar();
        //curDate.add((GregorianCalendar.MONTH), -mesiFiltro);
		if(!(this.getProcess() != null && this.getProcess().getStato() == StatoFlusso.Finished)) {
			GregorianCalendar curDate = new GregorianCalendar();
			long dallaDataMilliSecondi = getDataInvioEmail().getTime();
			long allaDataMilliSecondi = curDate.getTimeInMillis();
			long millisecondiFraDueDate = allaDataMilliSecondi - dallaDataMilliSecondi;
			long giorniFraDueDate = Math.round(millisecondiFraDueDate / 86400000.0) - 1;
			toRet = String.valueOf(giorniFraDueDate);
		}
		return toRet;
	}
	
	public int compareGiorniDataFatturaTo(FatturaApv o2){
		if(o2 == null)
			return 1;

		if(this.getProcess() == null) {
			if(o2.getProcess() == null)
				return 0;
			else
				return -1;
		} else {
			if(o2.getProcess() == null)
				return 1;
			//getProcess non nullo in entrambi
			if(this.getProcess().getStato() != o2.getProcess().getStato()) {
				return this.getProcess().getStato().toString().compareTo(o2.getProcess().getStato().toString());			
			} else {
				//stesso getStato
				if(this.getProcess().getStato() == StatoFlusso.Finished) {
					return 0; 
				} else {
					//il flusso e' attivo
					if(this.getDataInvioEmail() == null) {
						if(o2.getDataInvioEmail() == null)
							return 0;
						else
							return -1;
					} else {
						if(o2.getDataInvioEmail() == null)
							return 1;
						else {
							//Entrambi hanno getDataInvioEmail
							return - this.getDataInvioEmail().compareTo(o2.getDataInvioEmail());
						}
					}
				}
			}
		}
	}
	
	public int compareRepertorioOrdine(FatturaApv o2){
		if(o2 == null)
			return 1;

		if(this.getOrdiniRepertori() == null || this.getOrdiniRepertori().size() == 0) {
			if(o2.getOrdiniRepertori() == null || o2.getOrdiniRepertori().size() == 0)
				return 0;
			else
				return -1;
		} else {
			if(o2.getOrdiniRepertori() == null || o2.getOrdiniRepertori().size() == 0)
				return 1;
			//getOrdiniRepertori non nullo in entrambi e con almeno un oggetto
			//this.getOrdiniRepertori().get(0).getRepertorioOrdine()
			String o1RepOrd = this.getOrdiniRepertori().get(0).getRepertorioOrdine();
			String o2RepOrd = o2.getOrdiniRepertori().get(0).getRepertorioOrdine();
			if(o1RepOrd == null) {
				if(o2RepOrd == null)
					return 0;
				else
					return -1;
			} else {
				if(o2RepOrd == null)
					return 1;
				return o1RepOrd.compareTo(o2RepOrd);			
			}
		}
	}
	
	public int compareRepertorioDecretazione(FatturaApv o2){
		if(o2 == null)
			return 1;

		if(this.getOrdiniRepertori() == null || this.getOrdiniRepertori().size() == 0) {
			if(o2.getOrdiniRepertori() == null || o2.getOrdiniRepertori().size() == 0)
				return 0;
			else
				return -1;
		} else {
			if(o2.getOrdiniRepertori() == null || o2.getOrdiniRepertori().size() == 0)
				return 1;
			//getOrdiniRepertori non nullo in entrambi e con almeno un oggetto
			String o1RepDecr = this.getOrdiniRepertori().get(0).getRepertorioDecretazione();
			String o2RepDecr = o2.getOrdiniRepertori().get(0).getRepertorioDecretazione();
			if(o1RepDecr == null) {
				if(o2RepDecr == null)
					return 0;
				else
					return -1;
			} else {
				if(o2RepDecr == null)
					return 1;
				return o1RepDecr.compareTo(o2RepDecr);			
			}
		}
	}

	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	@Override
	public String toString() {
		String toRet = "FatturaApv - fornitore: " + (fornitore==null?"":fornitore) + "; numero: " + (numero==null?"":numero) + "; importo: " + (importo==null?"":importo) + "; dataInvioEmail: " + (dataInvioEmail==null?"":df.format(dataInvioEmail));
		int i = 1;
		for(FatturaRepertorioOrdineRepertorioDecretazioneApv ordine : ordiniRepertori) {
			toRet += i++ + ") " + ordine.toString();
		}
		toRet += super.toString();
		return toRet;
	}
}
