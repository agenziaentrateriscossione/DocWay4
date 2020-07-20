package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Classif;
import it.tredi.dw4.docway.model.Voce_indice;
import it.tredi.dw4.docway.model.delibere.Componente;
import it.tredi.dw4.docway.model.delibere.Proposta;
import it.tredi.dw4.docway.model.delibere.Categoria;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Organo extends XmlEntity {
	private String fullcod = "";
	private String cod = "";
	private String nrecord = "";
	private String cod_amm_aoo = "";
	private List<Modello> modelli = new ArrayList<Modello>();
	private String nome = "";
	private Classif classif = new Classif();
	private List<Categoria> categorie = new ArrayList<Categoria>();
	private List<Componente> componenti = new ArrayList<Componente>();
	private String lista_distribuzione_odg = "";
	private String lista_distribuzione_risultati = "";
	private String lista_distribuzione_verbali = "";
	private String lista_distribuzione_seduta = "";
	private List<Modifica> modifiche;
	private Creazione creazione;
	private UltimaModifica ultima_modifica;
	private List<PropostaOrgano> proposte = new ArrayList<PropostaOrgano>();
	
	private String cod_amm = "";
	private String cod_aoo = "";
	
	/* usati dal meccanismo di Titolario di Classificazione di Docway
	 * per non riscrivere tutto il meccanismo e quindi sfruttare bean + template esistenti 
	 * ./acl/classifFields.xhtml*/
	private Voce_indice voce_indice = new Voce_indice();
	private String scarto = "";

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.cod_amm								=	XMLUtil.parseAttribute(dom, "organo/@cod_amm");
		this.cod_aoo								=	XMLUtil.parseAttribute(dom, "organo/@cod_aoo");
		
		this.fullcod									=	XMLUtil.parseAttribute(dom, "organo/@cod");
		if(!this.fullcod.isEmpty())
			this.cod = StringUtil.substringAfter(this.fullcod, "-");
		
		this.nrecord								=	XMLUtil.parseAttribute(dom, "organo/@nrecord");
		this.cod_amm_aoo							=	XMLUtil.parseAttribute(dom, "organo/@cod_amm_aoo");
		this.modelli								=	XMLUtil.parseSetOfElement(dom, "//organo/modelli/node()", new Modello());
		this.nome									=	XMLUtil.parseElement(dom, "organo/nome");
		this.classif								= 	(Classif) XMLUtil.parseElement(dom, "//organo/classif_verbale",new Classif());
		this.categorie								= 	XMLUtil.parseSetOfElement(dom, "//organo/categorie/categoria", new Categoria());
		this.componenti								=	XMLUtil.parseSetOfElement(dom, "//organo/componenti/componente", new Componente());
		this.lista_distribuzione_odg				=	XMLUtil.parseAttribute(dom, "organo/lista_distribuzione_odg/@email");
		this.lista_distribuzione_risultati			=	XMLUtil.parseAttribute(dom, "organo/lista_distribuzione_risultati/@email");
		this.lista_distribuzione_verbali			=	XMLUtil.parseAttribute(dom, "organo/lista_distribuzione_verbali/@email");
		this.lista_distribuzione_seduta				=	XMLUtil.parseAttribute(dom, "organo/lista_distribuzione_seduta/@email");
		
		this.modifiche = XMLUtil.parseSetOfElement(dom, "//organo/storia/modifica", new Modifica());
		this.setCreazione((Creazione)XMLUtil.parseElement(dom, "//organo/storia/creazione",new Creazione()));
		this.setUltima_modifica((UltimaModifica)XMLUtil.parseElement(dom, "//organo/storia/ultima_modifica",new UltimaModifica()));
		
		this.proposte								=	XMLUtil.parseSetOfElement(dom, "//organo/proposte/proposta", new PropostaOrgano());
		
		if(classif == null)
			classif = new Classif();
		
		if(this.categorie == null || this.categorie.size() == 0){
			this.categorie = new ArrayList<Categoria>();
			this.categorie.add(new Categoria());
			}
		
		if(this.componenti == null || this.componenti.size() == 0){
			this.componenti = new ArrayList<Componente>();
			this.componenti.add(new Componente());
			}
		
		if(this.proposte == null || this.proposte.size() == 0){
			this.proposte = new ArrayList<PropostaOrgano>();
			this.proposte.add(new PropostaOrgano());
			}
		
		
		//i modelli non presenti vanno inizializzati a vuoto per essere visibili nel template
		this.modelli	= 	initModelli();
		
		return this;
	}
	
	private List<Modello> initModelli(){
		
		if(this.modelli == null || this.modelli.size() == 0){
			this.modelli = new ArrayList<Modello>();
			this.modelli.add(new Modello("proposta"));
			this.modelli.add(new Modello("odg"));
			this.modelli.add(new Modello("risultati"));
			this.modelli.add(new Modello("delibera"));
			this.modelli.add(new Modello("verbale"));
		}
			
		
		List<Modello> tempModelli = new ArrayList<Modello>();
		
		if(getModelloByNome("proposta") == null) 
			tempModelli.add(new Modello("proposta")); 
		else
			tempModelli.add(getModelloByNome("proposta"));
		
		if(getModelloByNome("odg") == null) 
			tempModelli.add(new Modello("odg")); 
		else
			tempModelli.add(getModelloByNome("odg"));
		
		if(getModelloByNome("risultati") == null) 
			tempModelli.add(new Modello("risultati")); 
		else
			tempModelli.add(getModelloByNome("risultati"));
		
		if(getModelloByNome("delibera") == null) 
			tempModelli.add(new Modello("delibera")); 
		else
			tempModelli.add(getModelloByNome("delibera"));
		
		if(getModelloByNome("verbale") == null) 
			tempModelli.add(new Modello("verbale")); 
		else
			tempModelli.add(getModelloByNome("verbale"));
		
		return tempModelli;
	}
	
	private Modello getModelloByNome(String nome){
		for(Modello modello : this.modelli)
			if(modello.getNome().equals(nome))
				return modello;
		return null;
	}
	
	public String addCategoria() {
		Categoria categoria = (Categoria) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("categoria");
		int index = 0;
		if (categoria != null)
			index = this.getCategorie().indexOf(categoria);
		
		if (this.getCategorie() != null) {
			if (this.getCategorie().size() > index)
				this.getCategorie().add(index+1,  new Categoria());
			else
				this.getCategorie().add(new Categoria());
		}
		return null;
	}
	
	public String deleteCategoria() {
		Categoria categoria = (Categoria) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("categoria");
		if (categoria != null) {
			this.getCategorie().remove(categoria);
			if (this.getCategorie().isEmpty()) 
				this.getCategorie().add(new Categoria());
		}
		return null;
	}
	
	public String moveUpCategoria(){
		Categoria categoria = (Categoria) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("categoria");
		int index = this.getCategorie().indexOf(categoria);
		if (index > 0 ) {
			this.getCategorie().remove(index);
			this.getCategorie().add(index-1, categoria);
		}
		
		return null;
	}

	public String moveDownCategoria(){
		Categoria categoria = (Categoria) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("categoria");
		int index = this.getCategorie().indexOf(categoria);
		if (index < this.getCategorie().size()-1 ) {
			this.getCategorie().remove(index);
			this.getCategorie().add(index+1, categoria);
		}
		return null;
	}
	
	public String addComponente() {
		Componente componente = (Componente) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("componente");
		int index = 0;
		if (componente != null)
			index = this.getComponenti().indexOf(componente);
		
		if (this.getComponenti() != null) {
			if (this.getComponenti().size() > index)
				this.getComponenti().add(index+1,  new Componente());
			else
				this.getComponenti().add(new Componente());
		}
		return null;
	}
	
	public String deleteComponente() {
		Componente componente = (Componente) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("componente");
		if (componente != null) {
			this.getComponenti().remove(componente);
			if (this.getComponenti().isEmpty()) 
				this.getComponenti().add(new Componente());
		}
		return null;
	}
	
	public String addProposta() {
		PropostaOrgano proposta = (PropostaOrgano) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		int index = 0;
		if (proposta != null)
			index = this.getProposte().indexOf(proposta);
		
		if (this.getProposte() != null) {
			if (this.getProposte().size() > index)
				this.getProposte().add(index+1,  new PropostaOrgano());
			else
				this.getProposte().add(new PropostaOrgano());
		}
		return null;
	}
	
	public String deleteProposta() {
		PropostaOrgano proposta = (PropostaOrgano) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		if (proposta != null) {
			this.getProposte().remove(proposta);
			if (this.getProposte().isEmpty()) 
				this.getProposte().add(new PropostaOrgano());
		}
		return null;
	}
	
	public String removeFileModello(String index) {
		int indice = Integer.parseInt(index);
		this.modelli.get(indice).clear();
		return null;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify){
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if(!modify)
    		params.put(prefix+".@nrecord", ".");
    	
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".nome", this.nome);
    	
    	params.putAll(classif.asFormAdapterParams(prefix+".classif_verbale"));
    	for(int i=0;i<categorie.size();i++){
    		Categoria categoria = categorie.get(i);
    		params.putAll(categoria.asFormAdapterParams(prefix+".categorie.categoria[" + i + "]"));
    		params.put(prefix+".categorie.categoria[" + i + "]" + ".@tipo", categoria.getTipo());
    	}
    	
    	for(int i=0;i<componenti.size();i++){
    		Componente componente = componenti.get(i);
    		params.putAll(componente.asFormAdapterParams(prefix+".componenti.componente[" + i + "]"));
    	}
    	
    	params.put(prefix+".lista_distribuzione_odg.@email", this.lista_distribuzione_odg);
    	params.put(prefix+".lista_distribuzione_risultati.@email", this.lista_distribuzione_risultati);
    	params.put(prefix+".lista_distribuzione_verbali.@email", this.lista_distribuzione_verbali);
    	params.put(prefix+".lista_distribuzione_seduta.@email", this.lista_distribuzione_seduta);
    	
    	for(int i=0;i<proposte.size();i++){
    		PropostaOrgano proposta = proposte.get(i);
    		params.putAll(proposta.asFormAdapterParamsForOrgano(prefix+".proposte.proposta[" + i + "]"));
    		params.putAll(proposta.getWorkflow().asFormAdapterParams(prefix+".proposte.proposta[" + i + "].workflow"));
    	}
    	
    	for(int i=0;i<modelli.size();i++){
    		Modello modello = modelli.get(i);
    		//if(!modello.getFile().getTitle().isEmpty() && !modello.getFile().getName().isEmpty())
    			if(!modello.getFile().getName().isEmpty())
    				modello.getFile().setTitle(modello.getNome());
    			params.putAll(modello.asFormAdapterParams(prefix+"*x_files.modelli." + modello.getNome() + ".xw:file"));
    	}
    	
		// TODO Auto-generated method stub
		return params;
	}
	
	/*
	 * getter / setter
	 * */
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}
	
	public List<Modello> getModelli() {
		return modelli;
	}

	public void setModelli(List<Modello> modelli) {
		this.modelli = modelli;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getLista_distribuzione_odg() {
		return lista_distribuzione_odg;
	}

	public void setLista_distribuzione_odg(
			String lista_distribuzione_odg) {
		this.lista_distribuzione_odg = lista_distribuzione_odg;
	}

	public String getLista_distribuzione_risultati() {
		return lista_distribuzione_risultati;
	}

	public void setLista_distribuzione_risultati(
			String lista_distribuzione_risultati) {
		this.lista_distribuzione_risultati = lista_distribuzione_risultati;
	}

	public String getLista_distribuzione_verbali() {
		return lista_distribuzione_verbali;
	}

	public void setLista_distribuzione_verbali(
			String lista_distribuzione_verbali) {
		this.lista_distribuzione_verbali = lista_distribuzione_verbali;
	}

	public String getLista_distribuzione_seduta() {
		return lista_distribuzione_seduta;
	}

	public void setLista_distribuzione_seduta(
			String lista_distribuzione_seduta) {
		this.lista_distribuzione_seduta = lista_distribuzione_seduta;
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

	public List<PropostaOrgano> getProposte() {
		return proposte;
	}

	public void setProposte(List<PropostaOrgano> proposte) {
		this.proposte = proposte;
	}

	public Classif getClassif() {
		return classif;
	}

	public void setClassif(Classif classif_verbale) {
		this.classif = classif_verbale;
	}

	public Voce_indice getVoce_indice() {
		return voce_indice;
	}

	public void setVoce_indice(Voce_indice voce_indice) {
		this.voce_indice = voce_indice;
	}

	public String getScarto() {
		return scarto;
	}

	public void setScarto(String scarto) {
		this.scarto = scarto;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getFullcod() {
		return fullcod;
	}

	public void setFullcod(String fullcod) {
		this.fullcod = fullcod;
	}
}
