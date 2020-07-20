package it.tredi.dw4.docway.model.custom;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.ValutaConversion;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class DatiOrdine extends XmlEntity {

	private List<Tipologia_Ordine> tipologia_ordine = new ArrayList<Tipologia_Ordine>();
	private List<Servizio> servizio = new ArrayList<Servizio>();
	private List<Licenza> licenza = new ArrayList<Licenza>();
	private List<Contratto> contratto = new ArrayList<Contratto>();
	private List<Altro_Ordine> altro = new ArrayList<Altro_Ordine>();
	
	private double totale_servizio = 0.0;
	private double totale_licenza = 0.0;
	private double totale_contratto = 0.0;
	private double totale_altro = 0.0;
	private double totale = 0.0;
	private String str_totale = "";
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.tipologia_ordine = XMLUtil.parseSetOfElement(dom, "ordine/tipologia_ordine", new Tipologia_Ordine());
		this.servizio = XMLUtil.parseSetOfElement(dom, "ordine/servizio", new Servizio());
		this.licenza = XMLUtil.parseSetOfElement(dom, "ordine/licenza", new Licenza());
		this.contratto = XMLUtil.parseSetOfElement(dom, "ordine/contratto", new Contratto());
		this.altro = XMLUtil.parseSetOfElement(dom, "ordine/altro", new Altro_Ordine());
		
		// in caso di liste vuote viene aggiunto un elemento vuoto (per inserimento/modifica)
		if (this.tipologia_ordine.size() == 0) this.tipologia_ordine.add(new Tipologia_Ordine());
		if (this.servizio.size() == 0) this.servizio.add(new Servizio());
		if (this.licenza.size() == 0) this.licenza.add(new Licenza());
		if (this.contratto.size() == 0) this.contratto.add(new Contratto());
		if (this.altro.size() == 0) this.altro.add(new Altro_Ordine());
		
		// calcolo dei totali in caricamento del documento
		calcolaTotali();
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	for (int i = 0; i < tipologia_ordine.size(); i++) {
    		Tipologia_Ordine tipologia = (Tipologia_Ordine) tipologia_ordine.get(i);
    		params.putAll(tipologia.asFormAdapterParams(prefix+".tipologia_ordine["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < servizio.size(); i++) {
    		Servizio serv = (Servizio) servizio.get(i);
    		params.putAll(serv.asFormAdapterParams(prefix+".servizio["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < licenza.size(); i++) {
    		Licenza lic = (Licenza) licenza.get(i);
    		params.putAll(lic.asFormAdapterParams(prefix+".licenza["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < contratto.size(); i++) {
    		Contratto contr = (Contratto) contratto.get(i);
    		params.putAll(contr.asFormAdapterParams(prefix+".contratto["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < altro.size(); i++) {
    		Altro_Ordine altro_ordine = (Altro_Ordine) altro.get(i);
    		params.putAll(altro_ordine.asFormAdapterParams(prefix+".altro["+String.valueOf(i)+"]"));
		}
    	
    	// invio dei totali al service
    	params.put(prefix+".totale_servizi.@costo", ValutaConversion.formatValuta(totale_servizio+""));
    	params.put(prefix+".totale_licenze.@costo", ValutaConversion.formatValuta(totale_licenza+""));
    	params.put(prefix+".totale_contratti.@costo", ValutaConversion.formatValuta(totale_contratto+""));
    	params.put(prefix+".totale_altro.@costo", ValutaConversion.formatValuta(totale_altro+""));
    	params.put(prefix+".totale", str_totale);
    	params.put(prefix+".totale.@costo", ValutaConversion.formatValuta(totale+""));
    	
    	return params;
	}
	
	/**
	 * Aggiunta di un nuova tipologia all'ordine
	 */
	public void addTipologia(Tipologia_Ordine tipologia) {
		int index = 0;
		if (tipologia != null)
			index = tipologia_ordine.indexOf(tipologia);
		
		if (tipologia_ordine != null) {
			Tipologia_Ordine tipologiaToAdd = new Tipologia_Ordine();
			
			if (tipologia_ordine.size() > index)
				tipologia_ordine.add(index+1, tipologiaToAdd);
			else
				tipologia_ordine.add(tipologiaToAdd);
		}
	}
	
	/**
	 * Eliminazione di una tipologia dell'ordine
	 */
	public void deleteTipologia(Tipologia_Ordine tipologia){
		if (tipologia != null) {
			tipologia_ordine.remove(tipologia);
			if (tipologia_ordine.isEmpty()) 
				tipologia_ordine.add(new Tipologia_Ordine());
		}
	}
	
	/**
	 * Aggiunta di un nuovo servizio all'ordine
	 */
	public void addServizio(Servizio serv) {
		int index = 0;
		if (serv != null)
			index = servizio.indexOf(serv);
		
		if (servizio != null) {
			Servizio servToAdd = new Servizio();
			
			if (servizio.size() > index)
				servizio.add(index+1, servToAdd);
			else
				servizio.add(servToAdd);
		}
	}
	
	/**
	 * Eliminazione di un servizio dell'ordine
	 */
	public void deleteServizio(Servizio serv){
		if (serv != null) {
			servizio.remove(serv);
			if (servizio.isEmpty()) 
				servizio.add(new Servizio());
		}
	}
	
	/**
	 * Aggiunta di una nuova licenza all'ordine
	 */
	public void addLicenza(Licenza lic) {
		int index = 0;
		if (lic != null)
			index = licenza.indexOf(lic);
		
		if (licenza != null) {
			Licenza licToAdd = new Licenza();
			
			if (licenza.size() > index)
				licenza.add(index+1, licToAdd);
			else
				licenza.add(licToAdd);
		}
	}
	
	/**
	 * Eliminazione di una licenza dell'ordine
	 */
	public void deleteLicenza(Licenza lic){
		if (lic != null) {
			licenza.remove(lic);
			if (licenza.isEmpty()) 
				licenza.add(new Licenza());
		}
	}
	
	/**
	 * Aggiunta di un nuovo contratto all'ordine
	 */
	public void addContratto(Contratto con) {
		int index = 0;
		if (con != null)
			index = contratto.indexOf(con);
		
		if (contratto != null) {
			Contratto conToAdd = new Contratto();
			
			if (contratto.size() > index)
				contratto.add(index+1, conToAdd);
			else
				contratto.add(conToAdd);
		}
	}
	
	/**
	 * Eliminazione di un contratto dell'ordine
	 */
	public void deleteContratto(Contratto con){
		if (con != null) {
			contratto.remove(con);
			if (contratto.isEmpty()) 
				contratto.add(new Contratto());
		}
	}
	
	/**
	 * Aggiunta di un nuovo 'altro' all'ordine
	 */
	public void addAltro(Altro_Ordine altro_ordine) {
		int index = 0;
		if (altro_ordine != null)
			index = altro.indexOf(altro_ordine);
		
		if (altro != null) {
			Altro_Ordine altroToAdd = new Altro_Ordine();
			
			if (altro.size() > index)
				altro.add(index+1, altroToAdd);
			else
				altro.add(altroToAdd);
		}
	}
	
	/**
	 * Eliminazione di un 'altro' dell'ordine
	 */
	public void deleteAltro(Altro_Ordine altro_ordine){
		if (altro_ordine != null) {
			altro.remove(altro_ordine);
			if (altro.isEmpty()) 
				altro.add(new Altro_Ordine());
		}
	}
	
	/**
	 * Calcolo dei totali dell'ordine
	 * @throws Exception
	 */
	public void calcolaTotali() {
		// totale dei servizi
		totale_servizio = 0.0;
		if (servizio != null && servizio.size() > 0) {
			for (int i=0; i<servizio.size(); i++) {
				if (servizio.get(i) != null && servizio.get(i).getCosto() != null && !servizio.get(i).getCosto().equals(""))
					totale_servizio = totale_servizio + ValutaConversion.getDoubleValuta(servizio.get(i).getCosto());
			}
		}
		
		// totale delle licenze
		totale_licenza = 0.0;
		if (licenza != null && licenza.size() > 0) {
			for (int i=0; i<licenza.size(); i++) {
				if (licenza.get(i) != null && licenza.get(i).getCosto() != null && !licenza.get(i).getCosto().equals(""))
					totale_licenza = totale_licenza + ValutaConversion.getDoubleValuta(licenza.get(i).getCosto());
			}
		}
		
		// totale dei contratti
		totale_contratto = 0.0;
		if (contratto != null && contratto.size() > 0) {
			for (int i=0; i<contratto.size(); i++) {
				if (contratto.get(i) != null && contratto.get(i).getCosto() != null && !contratto.get(i).getCosto().equals(""))
					totale_contratto = totale_contratto + ValutaConversion.getDoubleValuta(contratto.get(i).getCosto());
			}
		}
		
		// totale altro
		totale_altro = 0.0;
		if (altro != null && altro.size() > 0) {
			for (int i=0; i<altro.size(); i++) {
				if (altro.get(i) != null && altro.get(i).getCosto() != null && !altro.get(i).getCosto().equals(""))
					totale_altro = totale_altro + ValutaConversion.getDoubleValuta(altro.get(i).getCosto());
			}
		}
		
		// totale offerta
		totale = totale_servizio + totale_licenza + totale_contratto + totale_altro;
		if (totale > 0)
			str_totale = ValutaConversion.convertInWords(ValutaConversion.formatValuta(totale+""));
	}
	
	public List<Tipologia_Ordine> getTipologia_ordine() {
		return tipologia_ordine;
	}

	public void setTipologia_ordine(List<Tipologia_Ordine> tipologia_ordine) {
		this.tipologia_ordine = tipologia_ordine;
	}

	public List<Servizio> getServizio() {
		return servizio;
	}

	public void setServizio(List<Servizio> servizio) {
		this.servizio = servizio;
	}

	public List<Licenza> getLicenza() {
		return licenza;
	}

	public void setLicenza(List<Licenza> licenza) {
		this.licenza = licenza;
	}

	public List<Contratto> getContratto() {
		return contratto;
	}

	public void setContratto(List<Contratto> contratto) {
		this.contratto = contratto;
	}

	public List<Altro_Ordine> getAltro() {
		return altro;
	}

	public void setAltro(List<Altro_Ordine> altro) {
		this.altro = altro;
	}

	public double getTotale_servizio() {
		return totale_servizio;
	}

	public void setTotale_servizio(double totale_servizio) {
		this.totale_servizio = totale_servizio;
	}

	public double getTotale_licenza() {
		return totale_licenza;
	}

	public void setTotale_licenza(double totale_licenza) {
		this.totale_licenza = totale_licenza;
	}

	public double getTotale_contratto() {
		return totale_contratto;
	}

	public void setTotale_contratto(double totale_contratto) {
		this.totale_contratto = totale_contratto;
	}

	public double getTotale_altro() {
		return totale_altro;
	}

	public void setTotale_altro(double totale_altro) {
		this.totale_altro = totale_altro;
	}

	public double getTotale() {
		return totale;
	}

	public void setTotale(double totale) {
		this.totale = totale;
	}

	public String getStr_totale() {
		if (str_totale != null && !str_totale.equals(""))
			return str_totale;
		else
			return "Zero"; // TODO gestire il multilingua?
	}

	public void setStr_totale(String str_totale) {
		this.str_totale = str_totale;
	}

}
