package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.dom4j.Document;

import it.tredi.dw4.docway.model.Delega;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class PersonaInterna extends XmlEntity {

	private Document response;
	private String nrecord= ".";
	private String nome;
	private String cognome;
	private String cod_uff;
	private String nome_uff;
	private String nomeufficio;
	private String cod_amm;
	private String cod_aoo;
	private String matricola = ".";
	private String sesso = "M";
	private String titolo;
	private String titolo_deferenza;
	private String secondo_nome;
	private String soprannome;
	private String profile_name = "";
	private String profile_cod = "";
	private boolean profile_changed = false;
	private String multisocieta = "";
	private String physDoc;

	private Profile profilo = new Profile();
	private Recapito recapito = new Recapito();
	private RecapitoPersonale recapito_personale = new RecapitoPersonale();
	private Qualifica qualifica = new Qualifica();
	private Competenze competenze = new Competenze();
	private Note note = new Note();

	private List<Mansione> mansione;
	private List<Login> login;
	private List<GruppoAppartenenza> gruppo_appartenenza;
	private List<Responsabilita> responsabilita;
	private List<Profile> profili;
	private List<Right> personal_rights;

	private List<Mailbox> mailboxes; // parametri necessari alla configurazione della mailbox

	private List<Listof_rights> listof_rights;
	private HashMap<String, Right> rights = new HashMap<String, Right>();

	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();

	//tiommi 25/09/2017 : gestione deleghe
	private List<Delega> deleghe = new ArrayList<Delega>();
	private int maxDeleghe = 5;
	
	//dpranteda 23/09/2019 - credenziali per firma remota
	private List<CredenzialeFirmaRemota> firmeRemote = new ArrayList<CredenzialeFirmaRemota>();

	public PersonaInterna() {
		this.mansione = new ArrayList<Mansione>();
		this.login = new ArrayList<Login>();
		this.gruppo_appartenenza = new ArrayList<GruppoAppartenenza>();
		this.responsabilita = new ArrayList<Responsabilita>();
		this.mailboxes = new ArrayList<Mailbox>();
	}

    public PersonaInterna(String xmlPersonaInterna) throws Exception {
        this.init(XMLUtil.getDOM(xmlPersonaInterna));
    }

    @SuppressWarnings("unchecked")
	public PersonaInterna init(Document domPersonaInterna) {
    	this.response = domPersonaInterna;
    	this.nrecord = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@nrecord", ".");
    	this.nome = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@nome");
    	this.cognome = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@cognome");
    	this.cod_uff = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@cod_uff");
    	this.nome_uff = 		XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@nome_uff");
    	this.nomeufficio = 		XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@nome_uff");
    	this.cod_amm = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@cod_amm");
    	this.cod_aoo = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@cod_aoo");
    	this.matricola = 		XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@matricola", "");
    	this.sesso = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@sesso", "M");
    	this.titolo = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@titolo");
    	this.titolo_deferenza = XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@titolo_deferenza");
    	this.secondo_nome = 	XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@secondo_nome");
    	this.soprannome = 		XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@soprannome");
    	this.multisocieta = 	XMLUtil.parseElementNode(domPersonaInterna, "//persona_interna/multisocieta", false);
    	this.physDoc = 			XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/@physDoc");
    	this.login = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/login", new Login());
    	this.responsabilita = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/responsabilita", new Responsabilita());
    	this.mansione = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/mansione", new Mansione());
    	this.mailboxes = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/mailbox", new Mailbox());
    	this.gruppo_appartenenza = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/gruppo_appartenenza", new GruppoAppartenenza());
    	this.recapito.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/recapito"));
    	this.recapito_personale.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/recapito_personale"));
    	this.qualifica.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/qualifica"));
    	this.competenze.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/competenze"));
    	this.note.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/note"));
    	this.modifiche = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/storia/modifica", new Modifica());
    	this.personal_rights = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/personal_rights/right", new Right());
    	this.creazione.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domPersonaInterna, "//persona_interna/storia/ultima_modifica"));

    	this.profile_name = 	XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/profile/@name");
    	this.profile_cod =		XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/profile/@cod");
    	this.profilo.init(XMLUtil.createDocument(domPersonaInterna, "//profilo[@selected='true']"));
    	this.profile_changed =	StringUtil.booleanValue(XMLUtil.parseAttribute(domPersonaInterna, "persona_interna/profile/@changed", ""));
    	this.deleghe = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/deleghe/delega", new Delega());
    	this.maxDeleghe = Integer.parseInt(XMLUtil.parseStrictAttribute(domPersonaInterna, "/response/@maxDeleghe", "5"));
    	
    	this.firmeRemote = XMLUtil.parseSetOfElement(domPersonaInterna, "//persona_interna/firmeRemote/credenzialeFirmaRemota", new CredenzialeFirmaRemota());
    	
    	initListOfRights();

        this.profili = XMLUtil.parseSetOfElement(domPersonaInterna, "/response/profilo", new Profile());
        Profile defaultProfile = new Profile();
        defaultProfile.setNome(Const.PROFILE_DEFAULT_NAME);
        defaultProfile.setCodice(Const.PROFILE_DEFAULT_CODE);
        this.profili.add(0, new Profile());
        this.profili.add(1, defaultProfile);

        // Da decommentare se si decide di differenziare il codice del profilo di default rispetto a quello
        // custom (attualmente sono entrambi impostati a stringa vuota)
        if (this.profilo == null || (this.profilo.getCodice() == null || this.profilo.getCodice().length() == 0)) {
        	// Caso di modifica con nessun profilo assegnato (caso custom o di default)
        	if (this.profile_cod != null
        			&& this.profile_cod.length() > 0
        			&& this.profile_cod.equals(Const.PROFILE_DEFAULT_CODE))
        		this.profilo = (Profile) this.profili.get(1); // Profilo di default
        }

        if (personal_rights.size() > 0) fillPersonalRights();
        if (this.login.size() == 0) this.login.add(new Login());
        if (this.mansione.size() == 0) this.mansione.add(new Mansione());
        if (this.mailboxes.size() == 0) this.mailboxes.add(new Mailbox());
        if (this.gruppo_appartenenza.size() == 0) this.gruppo_appartenenza.add(new GruppoAppartenenza());

        // mbernardini 03/04/2015 : spostata la valutazione dei disabled perche' non ancora caricati i diritti dell'utente
        evaluateDisabled();

        return this;
    }

    public List<CredenzialeFirmaRemota> getFirmeRemote() {
		return firmeRemote;
	}

	public void setFirmeRemote(List<CredenzialeFirmaRemota> firmeRemote) {
		this.firmeRemote = firmeRemote;
	}

	@SuppressWarnings("unchecked")
	private void initListOfRights() {
    	this.listof_rights = XMLUtil.parseSetOfElement(response, "//listof_rights", new Listof_rights());
    	resetRights();
	}

	private void resetRights(){
    	List<Right> group_rights = new ArrayList<Right>();
    	this.rights.clear();
    	boolean openedFirst = true;
    	for (Iterator<Listof_rights> iter = listof_rights.iterator(); iter.hasNext();) {
			Listof_rights liste = (Listof_rights) iter.next();
			List<Group> gruppi = liste.getGroups();
			for (Iterator<Group> iterator = gruppi.iterator(); iterator.hasNext();) {
				Group gruppo = (Group) iterator.next();
				gruppo.setOpened(openedFirst);
				openedFirst = false;
				group_rights.addAll(gruppo.getRights());
				List<Group> sottogruppi = gruppo.getGroups();
				for (Iterator<Group> iterator2 = sottogruppi.iterator(); iterator2.hasNext();) {
					Group sottogruppo = (Group) iterator2.next();
					group_rights.addAll(sottogruppo.getRights());
				}
			}
			List<Db> dbs = liste.getDbs();
			for (Iterator<Db> iterator = dbs.iterator(); iterator.hasNext();) {
				Db db = (Db) iterator.next();
				List<Group> gruppi_db = db.getGroups();
				for (Iterator<Group> iterator2 = gruppi_db.iterator(); iterator2.hasNext();) {
					Group gruppo = (Group) iterator2.next();
					gruppo.setOpened(openedFirst);
					openedFirst = false;
					group_rights.addAll(gruppo.getRights());
					List<Group> sottogruppi = gruppo.getGroups();
					for (Iterator<Group> iterator3 = sottogruppi.iterator(); iterator3.hasNext();) {
						Group sottogruppo = (Group) iterator3.next();
						group_rights.addAll(sottogruppo.getRights());
					}
				}
			}
		}
    	for (Iterator<Right> iterator = group_rights.iterator(); iterator.hasNext();) {
			Right right = (Right) iterator.next();
			this.rights.put(right.getCod(), right);
		}

    	// mbernardini 03/04/2015 : spostata la valutazione dei disabled perche' non ancora caricati i diritti dell'utente
    	//evaluateDisabled();
    }

	public void changeOpenedGroup(){
		Group group = (Group) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("group");
		Db dbFather = (Db) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("db");
    	for (Iterator<Listof_rights> iter = listof_rights.iterator(); iter.hasNext();) {
			Listof_rights liste = (Listof_rights) iter.next();
			List<Group> gruppi = liste.getGroups();
			for (Iterator<Group> iterator = gruppi.iterator(); iterator.hasNext();) {
				Group gruppo = (Group) iterator.next();
				if (gruppo.getLabel().equals(group.getLabel()))
					gruppo.setOpened(true);
				else
					gruppo.setOpened(false);
			}
			List<Db> dbs = liste.getDbs();
			for (Iterator<Db> iterator = dbs.iterator(); iterator.hasNext();) {
				Db db = (Db) iterator.next();
				List<Group> gruppi_db = db.getGroups();
				for (Iterator<Group> iterator2 = gruppi_db.iterator(); iterator2.hasNext();) {
					Group gruppo = (Group) iterator2.next();
					if (gruppo.getLabel().equals(group.getLabel())
							&& dbFather != null
							&& dbFather.getCod().equals(db.getCod()))
						gruppo.setOpened(true);
					else
						gruppo.setOpened(false);
				}
			}
		}
    }

	public void profilesValueChange(ValueChangeEvent vce) {
        profile_cod = (String)vce.getNewValue();
        profilesChange(profile_cod);
    }

    public void fillPersonalRights(){
    	for (Iterator<Right> iterator = personal_rights.iterator(); iterator.hasNext();) {
			Right right = (Right) iterator.next();
			if (null != this.rights.get(right.getCod()))
				this.rights.get(right.getCod()).setValue(right.getValue());
		}
    }

    public void profilesChange(String codice_profilo) {
    	if (null == codice_profilo || codice_profilo.trim().length() == 0) {
    		//profilo = new Profile();
    		profilo = (Profile) this.profili.get(0); // Assegno il profilo vuoto (sempre in posizione 0)
    		return;
    	}
    	else {
    		// disattivazione di tutti selezionati per modifica del profilo assegnato alla persona
    		disableAllRepertories();

    		if ( codice_profilo.trim().equals(Const.PROFILE_DEFAULT_CODE)) {
	    		profilo = (Profile) this.profili.get(1); // Assegno il profilo di default (sempre in posizione 1)
	    		initListOfRights();
	    	}
	        else{
	        	for (Iterator<Profile> iterator = this.profili.iterator(); iterator.hasNext();) {
					Profile profile = (Profile) iterator.next();
					if (codice_profilo.equals(profile.getCodice())){
						profilo = profile;
						for (Iterator<Diritto> iterator2 = profile.getDiritti().iterator(); iterator2.hasNext();) {
							Diritto diritto = (Diritto) iterator2.next();
							if (null != this.rights.get(diritto.getCod()))
								this.rights.get(diritto.getCod()).setValue(diritto.getValue());
						}
					}
				}
	        }
    	}

        evaluateDisabled();
    }

    /**
     * disattiva tutti i diritti relativi a repertori abilitati
     */
    private void disableAllRepertories() {
    	for (Map.Entry<String, Right> entry : rights.entrySet()) {
    		if (entry.getKey() != null
    				&& (entry.getKey().endsWith("-InsRep") || entry.getKey().endsWith("-VisRep") || entry.getKey().endsWith("-CompRep"))) {
    			Right right = entry.getValue();
    			if (right != null) {
    				right.setValue("");
    				entry.setValue(right);
    			}
    		}
    	}
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@nrecord", this.nrecord);
    	params.put(prefix + ".@nome", this.nome);
    	params.put(prefix + ".@cognome", this.cognome);
    	params.put(prefix + ".@cod_uff", (this.cod_uff != null) ? this.cod_uff.trim() : null);
    	params.put(prefix + ".@cod_amm", (this.cod_amm != null) ? this.cod_amm.trim() : null);
    	params.put(prefix + ".@cod_aoo", (this.cod_aoo != null) ? this.cod_aoo.trim() : null);
    	params.put(prefix + ".@matricola", (this.matricola.trim().length()> 0) ? this.matricola.trim() : "." );
    	params.put(prefix + ".@sesso", this.sesso);
    	params.put(prefix + ".@titolo", this.titolo);
    	params.put(prefix + ".@titolo_deferenza", this.titolo_deferenza);
    	params.put(prefix + ".@secondo_nome", this.secondo_nome);
    	params.put(prefix + ".@soprannome", this.soprannome);
    	params.put(prefix + ".multisocieta", this.multisocieta);
    	for (int i = 0; i < login.size(); i++) {
    		Login account = (Login) login.get(i);
    		params.putAll(account.asFormAdapterParams(".login["+String.valueOf(i)+"]"));
		}
//    	for (int i = 0; i < responsabilita.size(); i++) {
//    		Responsabilita resp = (Responsabilita) responsabilita.get(i);
//    		params.putAll(resp.asFormAdapterParams(".responsabilita["+String.valueOf(i)+"]"));
//		}
    	for (int i = 0; i < mansione.size(); i++) {
    		Mansione mans = (Mansione) mansione.get(i);
    		params.putAll(mans.asFormAdapterParams(".mansione["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < mailboxes.size(); i++) {
    		Mailbox mail = (Mailbox) mailboxes.get(i);
    		if (mail.getLogin() != null && !mail.getLogin().equals(""))
    			params.putAll(mail.asFormAdapterParams(".mailbox["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < gruppo_appartenenza.size(); i++) {
    		GruppoAppartenenza gruppoAppartenenza = (GruppoAppartenenza) gruppo_appartenenza.get(i);
    		params.putAll(gruppoAppartenenza.asFormAdapterParams(".gruppo_appartenenza["+String.valueOf(i)+"]"));
		}
    	if (null != profilo) params.putAll(profilo.asFormAdapterParams(".profile"));
    	params.putAll(recapito.asFormAdapterParams(".recapito"));
    	params.putAll(recapito_personale.asFormAdapterParams(".recapito_personale"));
    	params.putAll(qualifica.asFormAdapterParams(".qualifica"));
    	params.putAll(competenze.asFormAdapterParams(".competenze"));
    	params.putAll(note.asFormAdapterParams(".note"));
    	for ( Iterator<?> i = this.rights.entrySet().iterator() ; i.hasNext() ; ) {
    	    @SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) i.next();
    	    Right right = (Right) e.getValue();
			if(right.getType().equals("alfa"))
				params.put("*tRight_"+right.getCod(), right.getValue());
			else if(right.getSelected())
				params.put("*right_"+right.getCod(), "1");
		}

    	//tiommi 28/09/2017 - gestione deleghe in ACL
    	cleanBlankDeleghe();
    	if(deleghe.isEmpty()) {
    		deleghe.add(new Delega());
    	}
    	for (int i = 0; i < deleghe.size(); i++) {
    		Delega delega = (Delega) deleghe.get(i);
    		params.putAll(delega.asFormAdapterParams(".deleghe.delega["+i+"]"));
		}
    	
    	//dpranteda 23/09/2019 - gestione deleghe in ACL
    	cleanBlankFirmeRemote();
    	if(firmeRemote.isEmpty()) {
    		firmeRemote.add(new CredenzialeFirmaRemota());
    	}
    	for (int i = 0; i < firmeRemote.size(); i++) {
    		CredenzialeFirmaRemota credenzialeFirmaRemota = (CredenzialeFirmaRemota) firmeRemote.get(i);
    		params.putAll(credenzialeFirmaRemota.asFormAdapterParams(".firmeRemote.credenzialeFirmaRemota["+i+"]"));
		}

    	return params;
    }

    // toglie le deleghe non valorizzate prima di salvare
	private void cleanBlankDeleghe() {
		deleghe.removeIf(delega -> delega.isBlank());
	}

	// toglie le firme remote non valorizzate prima di salvare
	private void cleanBlankFirmeRemote() {
		firmeRemote.removeIf(credenzialeFirmaRemota -> credenzialeFirmaRemota.getUsername().isEmpty());
	}
		
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getMatricola() {
		// In caso di matricola pari al punto restituisco la stringa vuota in modo
		// da non visualizzare il punto all'interno della scheda di inserimento della persona
		if (matricola.equals("."))
			return "";
		else
			return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getTitolo_deferenza() {
		return titolo_deferenza;
	}

	public void setTitolo_deferenza(String titolo_deferenza) {
		this.titolo_deferenza = titolo_deferenza;
	}

	public String getSecondo_nome() {
		return secondo_nome;
	}

	public void setSecondo_nome(String secondo_nome) {
		this.secondo_nome = secondo_nome;
	}

	public String getSoprannome() {
		return soprannome;
	}

	public void setSoprannome(String soprannome) {
		this.soprannome = soprannome;
	}

	public Profile getProfilo() {
		return profilo;
	}

	public void setProfilo(Profile profilo) {
		this.profilo = profilo;
	}

	public List<Login> getLogin() {
		return login;
	}

	public void setLogin(List<Login> login) {
		this.login = login;
	}

	public Recapito getRecapito() {
		return recapito;
	}

	public void setRecapito(Recapito recapito) {
		this.recapito = recapito;
	}

	public RecapitoPersonale getRecapito_personale() {
		return recapito_personale;
	}

	public void setRecapito_personale(RecapitoPersonale recapito_personale) {
		this.recapito_personale = recapito_personale;
	}

	public List<Mansione> getMansione() {
		return mansione;
	}

	public void setMansione(List<Mansione> mansione) {
		this.mansione = mansione;
	}

	public List<Mailbox> getMailboxs() {
		return mailboxes;
	}

	public void setMailboxs(List<Mailbox> mailbox) {
		this.mailboxes = mailbox;
	}

	public Qualifica getQualifica() {
		return qualifica;
	}

	public void setQualifica(Qualifica qualifica) {
		this.qualifica = qualifica;
	}

	public Competenze getCompetenze() {
		return competenze;
	}

	public void setCompetenze(Competenze competenze) {
		this.competenze = competenze;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setGruppo_appartenenza(List<GruppoAppartenenza> gruppi_apparntenenza) {
		this.gruppo_appartenenza = gruppi_apparntenenza;
	}

	public List<GruppoAppartenenza> getGruppo_appartenenza() {
		return gruppo_appartenenza;
	}

	public void setResponsabilita(List<Responsabilita> responsabilita) {
		this.responsabilita = responsabilita;
	}

	public List<Responsabilita> getResponsabilita() {
		return responsabilita;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
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

	public void setNomeufficio(String nomeufficio) {
		this.nomeufficio = nomeufficio;
	}

	public String getNomeufficio() {
		return nomeufficio;
	}

	public void clearMansione() {
		if (mansione != null) {
			mansione.clear();
			if (mansione.isEmpty())
				this.addMansione();
		}
	}

	public String deleteMansione(){
		Mansione mans = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		this.mansione.remove(mans);
		if (mansione.size() == 0) mansione.add(new Mansione());
		return null;
	}

	public String addMansione(){
		Mansione mans = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		int index = mansione.indexOf(mans);
		if (index == mansione.size()-1)
			mansione.add(index+1, new Mansione());
		else
			mansione.add(new Mansione());
		return null;
	}

	public void clearMailbox() {
		if (mailboxes != null) {
			mailboxes.clear();
			if (mailboxes.isEmpty())
				this.addMailbox();
		}
	}

	public String deleteMailbox(){
		Mailbox mail = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		this.mailboxes.remove(mail);
		if (mailboxes.size() == 0) mailboxes.add(new Mailbox());
		return null;
	}

	public String addMailbox(){
		Mailbox mail = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mail);
		if (index == mailboxes.size()-1)
			mailboxes.add(index+1, new Mailbox());
		else
			mailboxes.add(new Mailbox());
		return null;
	}

	public void clearGruppoAppartenenza() {
		if (gruppo_appartenenza != null) {
			gruppo_appartenenza.clear();
			if (gruppo_appartenenza.isEmpty())
				this.addGruppoAppartenenza();
		}
	}

	public String deleteGruppoAppartenenza(){
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		this.gruppo_appartenenza.remove(gruppo);
		if (gruppo_appartenenza.size() == 0 ) gruppo_appartenenza.add(new GruppoAppartenenza());
		return null;
	}

	public String addGruppoAppartenenza(){
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		int index = gruppo_appartenenza.indexOf(gruppo);
		if (index == gruppo_appartenenza.size()-1)
			gruppo_appartenenza.add(index+1, new GruppoAppartenenza());
		else
			gruppo_appartenenza.add(new GruppoAppartenenza());
		return null;
	}


	public String deleteLogin(){
		Login log = (Login) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("login");
		this.login.remove(log);
		if (login.size() == 0) login.add(new Login());
		return null;
	}

	public String addLogin(){
		Login loginname = (Login) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("login");
		int index = login.indexOf(loginname);
		if (login.size() > index) 	login.add(index+1, new Login());
		else						login.add(new Login());
		return null;
	}

	public void setListof_rights(List<Listof_rights> listof_rights) {
		this.listof_rights = listof_rights;
	}

	public List<Listof_rights> getListof_rights() {
		return listof_rights;
	}

	public void setProfili(List<Profile> profili) {
		this.profili = profili;
	}

	public List<Profile> getProfili() {
		return profili;
	}

	/**
	 * Modifica di un diritto dalla scheda di inserimento/modifica di
	 * una persona interna
	 * @return
	 */
	public String modifyRight(){
		Right right = (Right) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("right");
		boolean newVal = !right.getSelected();

		// mbernardini 04/09/2017 : migliorata la modifica di diritti di tipo 'alfa'
		if (right.getType() != null && right.getType().equals("alfa")) {
			if (right.getValue().equals("") || right.getValue().equals("*NHL*"))
				newVal = false;
			else
				newVal = true;
		}
		else
			right.setValue(String.valueOf(newVal));

		if (newVal){
			List<Oncheck> operation = right.getOnchecks();
			for (Iterator<Oncheck> iterator = operation.iterator(); iterator.hasNext();) {
				Oncheck oncheck = (Oncheck) iterator.next();
				Right changed = this.rights.get(oncheck.getWhat());
				if( oncheck.getAction().equals("check") )
					changed.setValue("true");
				else if( oncheck.getAction().equals("uncheck") )
					changed.setValue("false");
			}
		}
		else{
			List<Onuncheck> operation = right.getOnunchecks();
			for (Iterator<Onuncheck> iterator = operation.iterator(); iterator.hasNext();) {
				Onuncheck onuncheck = (Onuncheck) iterator.next();
				Right changed = this.rights.get(onuncheck.getWhat());
				if( onuncheck.getAction().equals("check") )
					changed.setValue("true");
				else if( onuncheck.getAction().equals("uncheck") )
					changed.setValue("false");
			}
		}


		// mbernardini 26/06/2015 : in caso di modifica di un diritto non deve essere sganciato il profilo
		// Nel caso di modifica di un diritto di una persona interna occorre azzerare
		// l'eventuale profilo agganciato alla persona stessa
		//profilesChange("");

    	evaluateDisabled();

		return null;
	}

	public String evaluateDisabled(){
		for ( Iterator<?> i = this.rights.entrySet().iterator() ; i.hasNext() ; ) {
    	    @SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) i.next();
    	    Right right = (Right) e.getValue();
    	    String disabled = right.getDisabled().getIfVar();
    	    if (null != disabled && disabled.trim().length() > 0){
		        JexlEngine jexl = new JexlEngine();
		        // Create an expression object
		        Expression ex = jexl.createExpression( disabled );

		        // Create a context and add data
		        JexlContext jc = new MapContext();
		        jc.set("rights", this.rights);
		        jc.set("this", this);

		        // Now evaluate the expression, getting the result
		        boolean enabled = (Boolean) ex.evaluate(jc);
	        	right.setDisable(enabled);
	        	if ( enabled ) right.setValue("false");
    	    }
		}
        return null;
	}

	public void setPersonal_rights(List<Right> personal_rights) {
		this.personal_rights = personal_rights;
	}

	public List<Right> getPersonal_rights() {
		return personal_rights;
	}

	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}

	public String getProfile_name() {
		return profile_name;
	}

	public void setProfile_cod(String profile_cod) {
		this.profile_cod = profile_cod;
	}

	public String getProfile_cod() {
		return profile_cod;
	}

	public boolean isProfile_changed() {
		return profile_changed;
	}

	public void setProfile_changed(boolean profile_changed) {
		this.profile_changed = profile_changed;
	}

	public void setMultisocieta(String multisocieta) {
		this.multisocieta = multisocieta;
	}

	public String getMultisocieta() {
		return multisocieta;
	}

	public void setPhysDoc(String physDoc) {
		this.physDoc = physDoc;
	}

	public String getPhysDoc() {
		return physDoc;
	}

	public List<Delega> getDeleghe() {
		return deleghe;
	}

	public void setDeleghe(List<Delega> deleghe) {
		this.deleghe = deleghe;
	}

	public int getMaxDeleghe() {
		return maxDeleghe;
	}

	public void setMaxDeleghe(int maxDeleghe) {
		this.maxDeleghe = maxDeleghe;
	}

	public String moveUpMansione(){
		Mansione mans = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		int index = mansione.indexOf(mans);
		if (index > 0 ) {
			mansione.remove(index);
			this.mansione.add(index-1, mans);
		}
		return null;
	}

	public String moveDownMansione(){
		Mansione mans = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		int index = mansione.indexOf(mans);
		if (index < mansione.size()-1 ) {
			mansione.remove(index);
			this.mansione.add(index+1, mans);
		}
		return null;
	}

	public String moveUpMailbox(){
		Mailbox mail = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mail);
		if (index > 0 ) {
			mailboxes.remove(index);
			this.mailboxes.add(index-1, mail);
		}
		return null;
	}

	public String moveDownMailbox(){
		Mailbox mail = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mail);
		if (index < mailboxes.size()-1 ) {
			mailboxes.remove(index);
			this.mailboxes.add(index+1, mail);
		}
		return null;
	}

	public String moveUpGruppo(){
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		int index = gruppo_appartenenza.indexOf(gruppo);
		if (index > 0 ) {
			gruppo_appartenenza.remove(index);
			this.gruppo_appartenenza.add(index-1, gruppo);
		}
		return null;
	}
	public String moveDownGruppo(){
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		int index = gruppo_appartenenza.indexOf(gruppo);
		if (index < gruppo_appartenenza.size()-1 ) {
			gruppo_appartenenza.remove(index);
			this.gruppo_appartenenza.add(index+1, gruppo);
		}
		return null;
	}

	public String moveUpLogin(){
		Login loginname = (Login) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("login");
		int index = login.indexOf(loginname);
		if (index > 0 ) {
			login.remove(index);
			this.login.add(index-1, loginname);
		}
		return null;
	}
	public String moveDownLogin(){
		Login loginname = (Login) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("login");
		int index = login.indexOf(loginname);
		if (index < login.size()-1 ) {
			login.remove(index);
			this.login.add(index+1, loginname);
		}
		return null;
	}

	public String removeDelegato() {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		deleghe.remove(delega);
		if(deleghe.isEmpty())
			deleghe.add(new Delega(true));
		return null;
	}

	public String addDelegato() {
		if(this.deleghe.size() >= maxDeleghe) {
			return null;
		}
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int index = deleghe.indexOf(delega);
		deleghe.add(index+1, new Delega(true));
		return null;
	}

	public String copyDelegato() {
		if(this.deleghe.size() >= maxDeleghe) {
			return null;
		}
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int index = deleghe.indexOf(delega);
		deleghe.add(index+1, new Delega(delega.getDataInizio(), delega.getDataFine(), delega.isPermanente(), delega.isAttiva(), delega.isSostituto()));
		return null;
	}
	
	//dpranteda 23/09/2019 - credenziali per firma remota
	public String removeCredenzialeFirmaRemota() {
		CredenzialeFirmaRemota credenzialeFirmaRemota = (CredenzialeFirmaRemota) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("credenzialeFirmaRemota");
		firmeRemote.remove(credenzialeFirmaRemota);
		if(firmeRemote.isEmpty())
			firmeRemote.add(new CredenzialeFirmaRemota());
		return null;
	}

	public String addCredenzialeFirmaRemota() {
		CredenzialeFirmaRemota credenzialeFirmaRemota = (CredenzialeFirmaRemota) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("credenzialeFirmaRemota");
		int index = firmeRemote.indexOf(credenzialeFirmaRemota);
		firmeRemote.add(index+1, new CredenzialeFirmaRemota());
		return null;
	}

}
