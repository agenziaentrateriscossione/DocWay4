package it.tredi.dw4.docway.model.delibere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.docway.model.History;
import it.tredi.dw4.docway.model.Non_disponibile;
import it.tredi.dw4.docway.model.Protocollazione;
import it.tredi.dw4.docway.model.Storia;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Seduta extends XmlEntity {
	
	private String tipo = "";
	private boolean straordinaria;
	private String nrecord;
	private String stato = "";
	private String data_convocazione = "";
	private String anno = "";
	private String cod_amm_aoo = "";
	private String organo_cod = "";
	private String organo = "";
	private DataOra limite_presentazione_proposte;
	private DataOra pubblicazione_odg_dal;
	private DataOra pubblicazione_odg_al;
	private String note = "";
	private List<Storia> storia;
	private List<History> history;
	private Creazione creazione;
	private UltimaModifica ultima_modifica;
	private Protocollazione protocollazione;
	private List<Categoria> categorie;// = new ArrayList<Categoria>();
	private List<Componente> componenti;// = new ArrayList<Componente>();
	private String nrecord_verbale = "";
	private List<Cat_container> odg;// = new ArrayList<Cat_container>();
	private List<PropostaOdg> proposte_da_deliberare;
	private List<PropostaOdg> elencoProposte;
	private Non_disponibile non_disponibile;
	
	public Seduta() {
		limite_presentazione_proposte = new DataOra();
		pubblicazione_odg_dal = new DataOra();
		pubblicazione_odg_al = new DataOra();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.setTipo("seduta");
		this.setStraordinaria(StringUtil.booleanValue(XMLUtil.parseAttribute(dom, "seduta/@straordinaria")));
		this.setNrecord(XMLUtil.parseAttribute(dom, "seduta/@nrecord","."));
		this.setStato(XMLUtil.parseAttribute(dom, "seduta/@stato"));
		this.setData_convocazione(XMLUtil.parseAttribute(dom, "seduta/@data_convocazione"));
		this.setAnno(XMLUtil.parseAttribute(dom, "seduta/@anno"));
		this.setCod_amm_aoo(XMLUtil.parseAttribute(dom, "seduta/@cod_amm_aoo"));
		this.setOrgano_cod(XMLUtil.parseAttribute(dom, "seduta/organo/@cod"));
		this.setOrgano(XMLUtil.parseElement(dom, "seduta/organo"));
		this.setLimite_presentazione_proposte((DataOra)XMLUtil.parseElement(dom, "//seduta/limite_presentazione_proposte" , new DataOra()));
		this.setPubblicazione_odg_dal((DataOra)XMLUtil.parseElement(dom, "//seduta/pubblicazione_odg/dal" , new DataOra()));
		this.setPubblicazione_odg_al((DataOra)XMLUtil.parseElement(dom, "//seduta/pubblicazione_odg/al" , new DataOra()));
		this.setNote(XMLUtil.parseElement(dom, "seduta/note"));
		this.setStoria((List<Storia>)XMLUtil.parseSetOfElement(dom, "//seduta/storia",new Storia()));
		this.setCreazione((Creazione)XMLUtil.parseElement(dom, "//seduta/storia/creazione",new Creazione()));
		this.setProtocollazione((Protocollazione)XMLUtil.parseElement(dom, "//seduta/storia/protocollazione",new Protocollazione()));
		this.setUltima_modifica((UltimaModifica)XMLUtil.parseElement(dom, "//seduta/storia/ultima_modifica",new UltimaModifica()));
		this.setCategorie((ArrayList<Categoria>)XMLUtil.parseSetOfElement(dom, "//seduta/categorie/categoria", new Categoria()));
		this.setComponenti((ArrayList<Componente>)XMLUtil.parseSetOfElement(dom, "//seduta/componenti/componente", new Componente()));
		this.setNrecord_verbale(XMLUtil.parseAttribute(dom, "seduta/verbale/@nrecord_verbale"));
		this.setOdg((List<Cat_container>)XMLUtil.parseSetOfElement(dom, "//seduta/odg/cat_container" , new Cat_container()));
		this.setProposte_da_deliberare((List<PropostaOdg>)XMLUtil.parseSetOfElement(dom, "//seduta/proposta_da_deliberare" , new PropostaOdg()));
		this.setElencoProposte((List<PropostaOdg>)XMLUtil.parseSetOfElement(dom, "//seduta/odg" , new PropostaOdg()));
		this.non_disponibile = ((Non_disponibile)XMLUtil.parseElement(dom, "//response/non_disponibile", new Non_disponibile()));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void initHistory(Document dom){
		this.history = XMLUtil.parseSetOfElement(dom, "//item", new History());
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, modify, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if(!modify){
    		params.put(".@nrecord", getNrecord());
    	}
    	
    	params.put(".@straordinaria", isStraordinaria() ? "si" : "no");
    	
    	params.put(".@stato", "aperta");
    	params.put(".organo.@cod", getOrgano_cod());
    	params.put(".organo", getOrgano());
    	
    	params.put(".@data_convocazione", getData_convocazione());
    	
    	params.put(".limite_presentazione_proposte.@data", getLimite_presentazione_proposte().getData());
    	params.put(".limite_presentazione_proposte.@ora", getLimite_presentazione_proposte().getOra());
    	
    	params.put(".pubblicazione_odg.dal.@data", getPubblicazione_odg_dal().getData());
    	params.put(".pubblicazione_odg.dal.@ora", getPubblicazione_odg_dal().getOra());
    	params.put(".pubblicazione_odg.al.@data", getPubblicazione_odg_al().getData());
    	params.put(".pubblicazione_odg.al.@ora", getPubblicazione_odg_al().getOra());
    	
    	params.put(".note", getNote());

    	return params;
	}
	
//	public Cat_container getCat_containerByName(String cat_container){
//		for(Cat_container cat : odg){
//			if(cat.getCat_container().equals(cat_container))
//				return cat;
//		}
//		return null;
//	}

	/*
	 * getter / setter
	 * */
	public boolean isStraordinaria() {
		return straordinaria;
	}

	public void setStraordinaria(boolean straordinaria) {
		this.straordinaria = straordinaria;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getData_convocazione() {
		return data_convocazione;
	}

	public void setData_convocazione(String data_convocazione) {
		this.data_convocazione = data_convocazione;
	}

	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}

	public String getOrgano_cod() {
		return organo_cod;
	}

	public void setOrgano_cod(String organo_cod) {
		this.organo_cod = organo_cod;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	public DataOra getLimite_presentazione_proposte() {
		return limite_presentazione_proposte;
	}

	public void setLimite_presentazione_proposte(DataOra limite_presentazione_proposte) {
		if(limite_presentazione_proposte == null)
			this.limite_presentazione_proposte = new DataOra();
		else
			this.limite_presentazione_proposte = limite_presentazione_proposte;
	}

	public DataOra getPubblicazione_odg_dal() {
		return pubblicazione_odg_dal;
	}

	public void setPubblicazione_odg_dal(DataOra pubblicazione_odg_dal) {
		if(pubblicazione_odg_dal == null)
			this.pubblicazione_odg_dal = new DataOra();
		else
			this.pubblicazione_odg_dal = pubblicazione_odg_dal;
	}

	public DataOra getPubblicazione_odg_al() {
		return pubblicazione_odg_al;
	}

	public void setPubblicazione_odg_al(DataOra pubblicazione_odg_al) {
		if(pubblicazione_odg_al == null)
			this.pubblicazione_odg_al = new DataOra();
		else
			this.pubblicazione_odg_al = pubblicazione_odg_al;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<Categoria> getCategorie() {
		return categorie;
	}

	public void setCategorie(List<Categoria> categorie) {
		this.categorie = categorie;
	}

	public List<Componente> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<Componente> componenti) {
		this.componenti = componenti;
	}

	public String getNrecord_verbale() {
		return nrecord_verbale;
	}

	public void setNrecord_verbale(String nrecord_verbale) {
		this.nrecord_verbale = nrecord_verbale;
	}

	public Non_disponibile getNon_disponibile() {
		return non_disponibile;
	}

	public void setNon_disponibile(Non_disponibile non_disponibile) {
		this.non_disponibile = non_disponibile;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<Cat_container> getOdg() {
		return odg;
	}

	public void setOdg(List<Cat_container> odg) {
		this.odg = odg;
	}

	public List<Storia> getStoria() {
		return storia;
	}

	public void setStoria(List<Storia> storia) {
		this.storia = storia;
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

	public Protocollazione getProtocollazione() {
		return protocollazione;
	}

	public void setProtocollazione(Protocollazione protocollazione) {
		this.protocollazione = protocollazione;
	}

	public List<History> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<History> arrayList) {
		this.history = arrayList;
	}

	public List<PropostaOdg> getProposte_da_deliberare() {
		return proposte_da_deliberare;
	}

	public void setProposte_da_deliberare(List<PropostaOdg> proposte_da_deliberare) {
		this.proposte_da_deliberare = proposte_da_deliberare;
	}

	public List<PropostaOdg> getElencoProposte() {
		return elencoProposte;
	}

	public void setElencoProposte(List<PropostaOdg> elencoProposte) {
		this.elencoProposte = elencoProposte;
	}
}
