package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class RifEsterno extends XmlEntity {
	
	// tipologie di rif esterni
	public static final String TIPORIF_PERSONA_ESTERNA = "persona_esterna";
	public static final String TIPORIF_STRUTTURA_ESTERNA = "struttura_esterna";
	
	private String cod = "";
	private String nome = "";
	private String indirizzo = "";
	private String email = "";
	private String email_certificata = "";
	private String fax = "";
	private String codice_fiscale = "";
	private String partita_iva = "";
	private String cod_SAP = "";
	private String data_prot = "";
	private String n_prot = "";
	// indice del rif all'interno del documento salvato su eXtraWay. in visualizzazione i rif esterni vengono riordinati per posizionare in coda
	// quelli in cc. 
	private String idx = ""; 
	private boolean copia_conoscenza = false;
    private Referente referente = new Referente();
    private List<Interoperabilita> interoperabilita = new ArrayList<Interoperabilita>();
    
    private String statoInvio = ""; // stato dell'invio in caso di "Invio Telematico"
    
    private boolean interop_webservice = false;
    
    private String tipo = ""; // utilizzato per identificare la dbTable di ritorno di un lookup (persona_esterna, struttura_esterna) 
    
	public RifEsterno() {}
    
	public RifEsterno(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    @SuppressWarnings("unchecked")
	public RifEsterno init(Document dom) {
    	this.codice_fiscale = XMLUtil.parseAttribute(dom, "rif/@codice_fiscale");
    	this.partita_iva = XMLUtil.parseAttribute(dom, "rif/@partita_iva");
    	this.cod_SAP = XMLUtil.parseAttribute(dom, "rif/@cod_SAP");
    	this.data_prot = XMLUtil.parseAttribute(dom, "rif/@data_prot");
    	this.n_prot = XMLUtil.parseAttribute(dom, "rif/@n_prot");
    	this.cod = XMLUtil.parseAttribute(dom, "rif/nome/@cod");
    	this.nome = XMLUtil.parseElement(dom, "rif/nome");
    	this.email = XMLUtil.parseAttribute(dom, "rif/indirizzo/@email");
    	this.email_certificata = XMLUtil.parseAttribute(dom, "rif/email_certificata/@addr");
    	this.fax = XMLUtil.parseAttribute(dom, "rif/indirizzo/@fax");
    	this.indirizzo = XMLUtil.parseElement(dom, "rif/indirizzo");
		this.referente.init(XMLUtil.createDocument(dom, "//rif/referente"));
		this.interoperabilita = XMLUtil.parseSetOfElement(dom, "rif/interoperabilita", new Interoperabilita());
		this.idx = XMLUtil.parseAttribute(dom, "rif/@idx");
		
		if (XMLUtil.countElements(dom, "rif/interop_webservice") > 0) // utilizzato per invio telematico tramite web service
			this.interop_webservice = true;
		else
			this.interop_webservice = false;
		
    	if (XMLUtil.parseAttribute(dom, "rif/@copia_conoscenza").toLowerCase().equals("si"))
    		this.copia_conoscenza = true;
    	
    	if (interoperabilita != null && interoperabilita.size() > 0) {
    		for (int i=0; i<interoperabilita.size(); i++) {
    			Interoperabilita interop = interoperabilita.get(i);
    			if (interop != null && interop.getInfo() != null) {
    				String info = interop.getInfo().toLowerCase();
    				if (info.equals("consegna"))
    					statoInvio = "inviato";
    				else if (!statoInvio.equals("inviato") && (info.contains("non accettazione") || info.contains("mancata consegna") || info.contains("errore")))
    					statoInvio = "errore";
    			}
    		}
    	}

        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@codice_fiscale", this.codice_fiscale);
    	params.put(prefix+".@cod_SAP", this.cod_SAP);
    	params.put(prefix+".@data_prot", this.data_prot);
    	params.put(prefix+".@n_prot", this.n_prot);
    	params.put(prefix+".@partita_iva", this.partita_iva);
    	params.put(prefix+".nome.@cod", this.cod);
    	params.put(prefix+".nome", this.nome);
    	params.put(prefix+".indirizzo.@email", this.email);
    	params.put(prefix+".email_certificata.@addr", this.email_certificata); // TODO Da verificare
    	params.put(prefix+".indirizzo.@fax", this.fax);
    	params.put(prefix+".indirizzo", this.indirizzo);
    	params.putAll(referente.asFormAdapterParams(prefix+".referente"));
    	
    	// interoperabilita (senza questa porzione di codice, se si entra in modifica di un documento si perdono le informazioni
    	// di interoperabilita' del rif esterno)
    	/*for (int i = 0; i < interoperabilita.size(); i++) {
    		Interoperabilita interop = (Interoperabilita) interoperabilita.get(i);
    		params.putAll(interop.asFormAdapterParams(prefix+".interoperabilita["+String.valueOf(i)+"]"));
		}*/
    	
    	if (this.copia_conoscenza)
    		params.put(prefix+".@copia_conoscenza", "si");
    	// in caso di copia_conoscenza = false non occorre spedire nulla (compatibilita' con DocWay3)
    	/*else
    		params.put(prefix+".@copia_conoscenza", "no");*/
    	
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setNome(String nome_persona) {
		this.nome = nome_persona;
	}

	public String getNome() {
		return nome;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail_certificata(String email) {
		this.email_certificata = email;
	}

	public String getEmail_certificata() {
		return email_certificata;
	}
	
	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax() {
		return fax;
	}

	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getPartita_iva() {
		return partita_iva;
	}

	public void setCod_SAP(String cod_SAP) {
		this.cod_SAP = cod_SAP;
	}

	public String getCod_SAP() {
		return cod_SAP;
	}
	
	public String getData_prot() {
		return data_prot;
	}

	public void setData_prot(String data) {
		this.data_prot = data;
	}

	public String getN_prot() {
		return n_prot;
	}

	public void setN_prot(String n_prot) {
		this.n_prot = n_prot;
	}

	public void setReferente(Referente referente) {
		this.referente = referente;
	}

	public Referente getReferente() {
		return referente;
	}
	
	public boolean isCopia_conoscenza() {
		return copia_conoscenza;
	}

	public void setCopia_conoscenza(boolean copia_conoscenza) {
		this.copia_conoscenza = copia_conoscenza;
	}

	public List<Interoperabilita> getInteroperabilita() {
		return interoperabilita;
	}

	public void setInteroperabilita(List<Interoperabilita> interoperabilita) {
		this.interoperabilita = interoperabilita;
	}
	
	public boolean isInterop_webservice() {
		return interop_webservice;
	}

	public void setInterop_webservice(boolean interop_webservice) {
		this.interop_webservice = interop_webservice;
	}
	
	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}
	
	public String getStatoInvio() {
		return statoInvio;
	}

	public void setStatoInvio(String statoInvio) {
		this.statoInvio = statoInvio;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
