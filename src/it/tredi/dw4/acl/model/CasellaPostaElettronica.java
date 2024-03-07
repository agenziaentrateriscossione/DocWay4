package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class CasellaPostaElettronica extends XmlEntity {

	private String nrecord = ".";
	private String nome;
	private String interop;
	private String tag;
	private String cod_amm;
	private String cod_aoo;
	private String cod_amm_segnatura;
	private String cod_aoo_segnatura;
	private String db;
	private String documentModel;
	private boolean protocollaFattura = false;
	private boolean smistamentoFatturePA = false;
	private boolean splitByAttachments = false;
	private boolean casellaImport = false;
	private boolean archiviazioneTags = false;
	private boolean smistamentoByMittente = false;
	private String oper;
	private String uff_oper;
	private Mailbox_in mailbox_in = new Mailbox_in();
	private Mailbox_out mailbox_out = new Mailbox_out();
	private Responsabile responsabile = new Responsabile();
	private List<AssegnatarioMailBox> assegnazioneCC = new ArrayList<AssegnatarioMailBox>();
	private Notify notify = new Notify();

	// definizione di persone che possono operare sulla casella (modifica dati, cambio password, ecc.)
	private List<GestoreMailbox> gestoriMailbox = new ArrayList<GestoreMailbox>();
	private boolean updateMailbox = false; // indica se l'utente corrente possiede i diritti di aggiornamento dati della mailbox
	private boolean changePassword = false; // indica se l'utente corrente possiede i diritti di aggiornamento della password di scaricamento posta

	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();

	public CasellaPostaElettronica() {
		// aggiunta di una istanza vuota di gestore mailbox
		this.gestoriMailbox.add(new GestoreMailbox());
	}

    @SuppressWarnings("unchecked")
	public CasellaPostaElettronica init(Document dom) {
    	this.nrecord = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@nrecord", ".");
    	this.nome = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@nome");
    	this.interop = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@interop");
    	this.tag = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/tag/@value");

    	this.cod_amm = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@cod_amm");
    	this.cod_aoo = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@cod_aoo");
    	this.cod_amm_segnatura = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@cod_amm_segnatura");
    	this.cod_aoo_segnatura = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@cod_aoo_segnatura");

    	this.db = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@db");
    	this.documentModel = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@documentModel");
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@protocollaFattura").toLowerCase().equals("true"))
    		this.protocollaFattura = true;
    	else
    		this.protocollaFattura = false;
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@smistamentoFatturePA").toLowerCase().equals("true"))
    		this.smistamentoFatturePA = true;
    	else
    		this.smistamentoFatturePA = false;
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@splitByAttachments").toLowerCase().equals("true"))
    		this.splitByAttachments = true;
    	else
    		this.splitByAttachments = false;
    	
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@casellaImport").toLowerCase().equals("true"))
    		this.casellaImport = true;
    	else
    		this.casellaImport = false;
    	
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@archiviazioneTags").toLowerCase().equals("true"))
    		this.archiviazioneTags = true;
    	else
    		this.archiviazioneTags = false;
    	
    	if (XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@smistamentoByMittente").toLowerCase().equals("true"))
    		this.smistamentoByMittente = true;
    	else
    		this.smistamentoByMittente = false;
    	
    	this.oper = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@oper");
    	this.uff_oper = XMLUtil.parseAttribute(dom, "casellaPostaElettronica/@uff_oper");

    	mailbox_in.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/mailbox_in"));
    	mailbox_out.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/mailbox_out"));
    	responsabile.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/responsabile"));

    	// tiommi 03/07/2017 : definizione dei CC sulle caselle di posta elettronica
    	this.assegnazioneCC =  XMLUtil.parseSetOfElement(dom, "//casellaPostaElettronica/assegnazione_cc/assegnatario", new AssegnatarioMailBox());
    	if (this.assegnazioneCC.size() == 0) this.assegnazioneCC.add(new AssegnatarioMailBox());

    	notify.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/notify"));

    	// mbernardini 15/10/2015 : gestori della mailbox
    	this.gestoriMailbox = XMLUtil.parseSetOfElement(dom, "//casellaPostaElettronica/gestori_mailbox/gestore", new GestoreMailbox());
    	if (this.gestoriMailbox.size() == 0) this.gestoriMailbox.add(new GestoreMailbox());

    	this.modifiche = XMLUtil.parseSetOfElement(dom, "//casellaPostaElettronica/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(dom, "//casellaPostaElettronica/storia/ultima_modifica"));

        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();

    	if (null != this.nrecord ) params.put(prefix+".@nrecord", this.nrecord);
    	if (null != this.nome ) params.put(prefix+".@nome", this.nome.trim());
    	if (null != this.interop ) params.put(prefix+".@interop", this.interop.trim());
    	if (null != this.tag ) params.put(prefix+".tag.@value", this.tag.trim());
    	if (null != this.cod_amm ) params.put(prefix+".@cod_amm", this.cod_amm.trim());
    	if (null != this.cod_aoo ) params.put(prefix+".@cod_aoo", this.cod_aoo.trim());

    	// i cod_amm e cod_aoo di segnatura devono essere inviati solo se casella di interoperabilita'
    	if (this.isInteroperabilita()) {
	    	params.put(prefix+".@cod_amm_segnatura", this.cod_amm_segnatura.trim());
	    	params.put(prefix+".@cod_aoo_segnatura", this.cod_aoo_segnatura.trim());
    	}
    	else {
    		params.put(prefix+".@cod_amm_segnatura", "");
    		params.put(prefix+".@cod_aoo_segnatura", "");
    	}

    	if (null != this.db ) params.put(prefix+".@db", this.db.trim());
    	if (null != this.documentModel) params.put(prefix+".@documentModel", this.documentModel);
    	if (null != this.oper ) params.put(prefix+".@oper", this.oper.trim());
    	if (null != this.uff_oper ) params.put(prefix+".@uff_oper", this.uff_oper.trim());
    	if (this.protocollaFattura)
    		params.put(prefix+".@protocollaFattura", "true");
    	else
    		params.put(prefix+".@protocollaFattura", "false");
    	if (this.smistamentoFatturePA)
    		params.put(prefix+".@smistamentoFatturePA", "true");
    	else
    		params.put(prefix+".@smistamentoFatturePA", "false");
    	if (this.splitByAttachments)
    		params.put(prefix+".@splitByAttachments", "true");
    	else
    		params.put(prefix+".@splitByAttachments", "false");
    	
    	if (this.casellaImport)
    		params.put(prefix+".@casellaImport", "true");
    	else
    		params.put(prefix+".@casellaImport", "false");
    	
    	if (this.archiviazioneTags)
    		params.put(prefix+".@archiviazioneTags", "true");
    	else
    		params.put(prefix+".@archiviazioneTags", "false");
    	
    	if (this.smistamentoByMittente)
    		params.put(prefix+".@smistamentoByMittente", "true");
    	else
    		params.put(prefix+".@smistamentoByMittente", "false");

    	params.putAll(mailbox_in.asFormAdapterParams(prefix+".mailbox_in"));
    	params.putAll(mailbox_out.asFormAdapterParams(prefix+".mailbox_out"));
    	params.putAll(responsabile.asFormAdapterParams(prefix+".responsabile"));

    	// tiommi 03/07/2017 : definizione dei CC sulle caselle di posta elettronica
    	int counter = 0;
    	for (int i = 0; i < this.assegnazioneCC.size(); i++) {
    		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) this.assegnazioneCC.get(i);
    		if (assegnatario != null) {
    			params.putAll(assegnatario.asFormAdapterParams(prefix+".assegnazione_cc.assegnatario["+String.valueOf(counter)+"]"));
    			counter++;
    		}
		}

    	params.putAll(notify.asFormAdapterParams(prefix+".notify"));

    	// mbernardini 15/10/2015 : gestori della mailbox
    	int index = 0;
    	for (int i = 0; i < this.gestoriMailbox.size(); i++) {
    		GestoreMailbox gestoreMail = (GestoreMailbox) this.gestoriMailbox.get(i);

    		// mbernardini 04/07/2016 : impossibile svuotare la lista dei gestori di mailbox
    		if (gestoreMail != null/* && gestoreMail.getMatricola() != null && gestoreMail.getMatricola().length() > 0*/) {
    			params.putAll(gestoreMail.asFormAdapterParams(prefix+".gestori_mailbox.gestore["+String.valueOf(index)+"]"));
    			index++;
    		}
		}

    	return params;
    }

    public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getInterop() {
		return interop;
	}

	public void setInterop(String interop) {
		this.interop = interop;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

    public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_amm(String spec) {
		this.cod_amm = spec;
	}

	public void setCod_aoo(String bban) {
		this.cod_aoo = bban;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public String getCod_amm_segnatura() {
		return cod_amm_segnatura;
	}

	public void setCod_amm_segnatura(String cod_amm_segnatura) {
		this.cod_amm_segnatura = cod_amm_segnatura;
	}

	public String getCod_aoo_segnatura() {
		return cod_aoo_segnatura;
	}

	public void setCod_aoo_segnatura(String cod_aoo_segnatura) {
		this.cod_aoo_segnatura = cod_aoo_segnatura;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getDb() {
		return db;
	}

	public void setDocumentModel(String documentModel) {
		this.documentModel = documentModel;
	}

	public String getDocumentModel() {
		return documentModel;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getOper() {
		return oper;
	}

	public void setUff_oper(String uff_oper) {
		this.uff_oper = uff_oper;
	}

	public String getUff_oper() {
		return uff_oper;
	}

	public void setMailbox_in(Mailbox_in mailbox_in) {
		this.mailbox_in = mailbox_in;
	}

	public Mailbox_in getMailbox_in() {
		return mailbox_in;
	}

	public void setMailbox_out(Mailbox_out mailbox_out) {
		this.mailbox_out = mailbox_out;
	}

	public Mailbox_out getMailbox_out() {
		return mailbox_out;
	}

	public void setResponsabile(Responsabile responsabile) {
		this.responsabile = responsabile;
	}

	public Responsabile getResponsabile() {
		return responsabile;
	}

	public List<AssegnatarioMailBox> getAssegnazioneCC() {
		return assegnazioneCC;
	}

	public void setAssegnazioneCC(List<AssegnatarioMailBox> assegnazioneCC) {
		this.assegnazioneCC = assegnazioneCC;
	}

	public void setNotify(Notify notify) {
		this.notify = notify;
	}

	public Notify getNotify() {
		return notify;
	}

	public boolean isProtocollaFattura() {
		return protocollaFattura;
	}

	public void setProtocollaFattura(boolean protocollaFattura) {
		this.protocollaFattura = protocollaFattura;
	}
	
	public boolean isSmistamentoFatturePA() {
		return smistamentoFatturePA;
	}

	public void setSmistamentoFatturePA(boolean smistamentoFatturePA) {
		this.smistamentoFatturePA = smistamentoFatturePA;
	}

	public boolean isSplitByAttachments() {
		return splitByAttachments;
	}

	public void setSplitByAttachments(boolean splitByAttachments) {
		this.splitByAttachments = splitByAttachments;
	}

	public List<GestoreMailbox> getGestoriMailbox() {
		return gestoriMailbox;
	}

	public void setGestoriMailbox(List<GestoreMailbox> gestoriMailbox) {
		this.gestoriMailbox = gestoriMailbox;
	}

	public boolean isUpdateMailbox() {
		return updateMailbox;
	}

	public void setUpdateMailbox(boolean updateMailbox) {
		this.updateMailbox = updateMailbox;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}
	
	public boolean isCasellaImport() {
		return casellaImport;
	}

	public void setCasellaImport(boolean casellaImport) {
		this.casellaImport = casellaImport;
	}
	
	public boolean isArchiviazioneTags() {
		return archiviazioneTags;
	}

	public void setArchiviazioneTags(boolean archiviazioneTags) {
		this.archiviazioneTags = archiviazioneTags;
	}
	
	public boolean isSmistamentoByMittente() {
		return smistamentoByMittente;
	}

	public void setSmistamentoByMittente(boolean smistamentoByMittente) {
		this.smistamentoByMittente = smistamentoByMittente;
	}

	/**
	 * Eliminazione di un gestore della mailbox
	 */
	public String removeGestore() {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		if (gestore != null) {
			gestoriMailbox.remove(gestore);
			if (gestoriMailbox.isEmpty())
				gestoriMailbox.add(new GestoreMailbox());
		}
		return null;
	}

	/**
	 * Aggiunta di un gestore della mailbox
	 */
	public String addGestore() {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int index = 0;
		if (gestore != null)
			index = gestoriMailbox.indexOf(gestore);

		if (gestoriMailbox != null) {
			if (gestoriMailbox.size() > index)
				gestoriMailbox.add(index+1,  new GestoreMailbox());
			else
				gestoriMailbox.add(new GestoreMailbox());
		}
		return null;
	}

	/**
	 * Eliminazione di un assegnatario della mailbox
	 */
	public String deleteAssegnatario() {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		if (assegnatario != null) {
			assegnazioneCC.remove(assegnatario);
			if (assegnazioneCC.isEmpty())
				assegnazioneCC.add(new AssegnatarioMailBox());
		}
		return null;
	}

	/**
	 * Aggiunta di un assegnatario della mailbox
	 */
	public String addAssegnatario() {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int index = 0;
		if (assegnatario != null)
			index = assegnazioneCC.indexOf(assegnatario);

		if (assegnazioneCC != null) {
			if (assegnazioneCC.size() > index)
				assegnazioneCC.add(index+1,  new AssegnatarioMailBox());
			else
				assegnazioneCC.add(new AssegnatarioMailBox());
		}
		return null;
	}


	/**
	 * Ritorna true se si tratta di una casella di interoperabilità, false in caso di casella di archiviazione
	 */
	public boolean isInteroperabilita() {
		if (this.interop != null && this.interop.equals("si"))
			return true;
		else
			return false;
	}
}
