package it.tredi.dw4.docwayproc.model;

import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class Compilazione_Automatica extends XmlEntity {

	private Classif classif = new Classif();
	private Scarto scarto = new Scarto();
	private List<Oggetto> oggetto = new ArrayList<Oggetto>();
	private List<Rif> rif_list = new ArrayList<Rif>();
	private Rif rpa = new Rif();
	private List<Rif> cds = new ArrayList<Rif>();
	private List<Rif> cc = new ArrayList<Rif>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.scarto.init(XMLUtil.createDocument(dom, "compilazione_automatica/scarto"));
		this.classif.init(XMLUtil.createDocument(dom, "compilazione_automatica/classif"));
		this.oggetto = XMLUtil.parseSetOfElement(dom, "compilazione_automatica/oggetto", new Oggetto());
		
		if (this.oggetto.size() == 0) this.oggetto.add(new Oggetto());
		
		rif_list = XMLUtil.parseSetOfElement(dom, "compilazione_automatica/rif", new Rif());
		if (rif_list != null && rif_list.size() > 0) {
			for (int i=0; i<rif_list.size(); i++) {
				Rif tmpRif = (Rif) rif_list.get(i);
				if (tmpRif != null) {
					if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPA)) //diritto = RPA
						this.rpa = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_CDS)) //diritto = CDS
						this.cds.add(tmpRif);
					else
						this.cc.add(tmpRif); // diritto = CC
				}
			}
		}
		
		if (this.cds.size() == 0) this.cds.add(new Rif());
		if (this.cc.size() == 0) this.cc.add(new Rif());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.putAll(scarto.asFormAdapterParams(prefix+".scarto"));
    	params.putAll(classif.asFormAdapterParams(prefix+".classif"));
    	for (int i = 0; i < oggetto.size(); i++) {
    		Oggetto ogg = (Oggetto) oggetto.get(i);
    		params.putAll(ogg.asFormAdapterParams(prefix+".oggetto["+String.valueOf(i)+"]"));
		}
    	if (!params.containsKey(prefix+".oggetto[0]"))
    		params.put(prefix+".oggetto[0]", "");
    		
    	params.putAll(getAssegnazioneRPAParam());
    	params.putAll(getAssegnazioneCDSParam());
    	params.putAll(getAssegnazioneCCParam());
    	
    	return params;
	}
	
	/**
	 * aggiunta parametri di rpa al formAdapter
	 * @return
	 */
	public Map<String, String> getAssegnazioneRPAParam(){
		Map<String, String> params = new HashMap<String, String>();
		if (rpa != null && rpa.getNome_uff() != null && rpa.getNome_uff().length() > 0) {
			params.putAll(rpa.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
		}
		return params;
	}
	
	/**
	 * aggiunta parametri di cds al formAdapter
	 * @return
	 */
	public Map<String, String> getAssegnazioneCDSParam(){
		Map<String, String> params = new HashMap<String, String>();
		if (cds != null && cds.size() > 0) {
			for (int i=0; i<cds.size(); i++) {
				Rif rif = (Rif) cds.get(i);
				if (rif != null && rif.getNome_uff() != null && rif.getNome_uff().length() > 0)
					params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCDS["+String.valueOf(i)+"]"));
			}
		}
		return params;
	}
	
	/**
	 * aggiunta parametri di cc al formAdapter
	 * @return
	 */
	public Map<String, String> getAssegnazioneCCParam(){
		Map<String, String> params = new HashMap<String, String>();
		if (cc != null && cc.size() > 0) {
			for (int i=0; i<cc.size(); i++) {
				Rif rif = (Rif) cc.get(i);
				if (rif != null && rif.getNome_uff() != null && rif.getNome_uff().length() > 0)
					params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCC["+String.valueOf(i)+"]"));
			}
		}
		return params;
	}
	
	public Classif getClassif() {
		return classif;
	}

	public void setClassif(Classif classif) {
		this.classif = classif;
	}

	public Scarto getScarto() {
		return scarto;
	}

	public void setScarto(Scarto scarto) {
		this.scarto = scarto;
	}

	public List<Oggetto> getOggetto() {
		return oggetto;
	}

	public void setOggetto(List<Oggetto> oggetto) {
		this.oggetto = oggetto;
	}
	
	public List<Rif> getRif_list() {
		return rif_list;
	}

	public void setRif_list(List<Rif> rif_list) {
		this.rif_list = rif_list;
	}
	
	public Rif getRpa() {
		return rpa;
	}

	public void setRpa(Rif rpa) {
		this.rpa = rpa;
	}

	public List<Rif> getCds() {
		return cds;
	}

	public void setCds(List<Rif> cds) {
		this.cds = cds;
	}

	public List<Rif> getCc() {
		return cc;
	}

	public void setCc(List<Rif> cc) {
		this.cc = cc;
	}
	
	/**
	 * eliminazione di un oggetto
	 */
	public String deleteOggetto() {
		Oggetto testoOggetto = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("oggetto");
		if (testoOggetto != null) {
			oggetto.remove(testoOggetto);
			if (oggetto.isEmpty()) 
				oggetto.add(new Oggetto());
		}
		return null;
	}
	
	/**
	 * aggiunta di un oggetto
	 */
	public String addOggetto() {
		Oggetto testoOggetto = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("oggetto");
		int index = 0;
		if (testoOggetto != null)
			index = oggetto.indexOf(testoOggetto);
		
		if (oggetto != null) {
			if (oggetto.size() > index)
				oggetto.add(index+1,  new Oggetto());
			else
				oggetto.add(new Oggetto());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di un oggetto
	 */
	public String moveUpOggetto() {
		Oggetto testoOggetto = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("oggetto");
		if (testoOggetto != null && oggetto != null) {
			int index = oggetto.indexOf(testoOggetto);
			if (index > 0 ) {
				oggetto.remove(index);
				oggetto.add(index-1, testoOggetto);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di un oggetto
	 */
	public String moveDownOggetto() {
		Oggetto testoOggetto = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("oggetto");
		if (testoOggetto != null && oggetto != null) {
			int index = oggetto.indexOf(testoOggetto);
			if (index < oggetto.size()-1 ) {
				oggetto.remove(index);
				oggetto.add(index+1, testoOggetto);
			}
		}
		return null;
	}
	
	/**
	 * aggiunta di un nuovo Rif int in CC
	 */
	public void addRifintCC(Rif rif) {
		int index = 0;
		if (rif != null)
			index = cc.indexOf(rif);
		
		if (cc != null) {
			Rif rifToAdd = new Rif();
			if (rif != null && rif.getTipo_uff() != null && rif.getTipo_uff().equals("ruolo"))
				rifToAdd.setTipo_uff("ruolo");
			
			if (cc.size() > index)
				cc.add(index+1, rifToAdd);
			else
				cc.add(rifToAdd);
		}
	}

	
	/**
	 * aggiunta di un nuovo Rif int in CDS
	 */
	public void addRifintCDS(Rif rif) {
		int index = 0;
		if (rif != null)
			index = cds.indexOf(rif);
		
		if (cds != null) {
			Rif rifToAdd = new Rif();
			if (rif != null && rif.getTipo_uff() != null && rif.getTipo_uff().equals("ruolo"))
				rifToAdd.setTipo_uff("ruolo");
			
			if (cds.size() > index)
				cds.add(index+1, rifToAdd);
			else
				cds.add(rifToAdd);
		}
	}

	/**
	 * eliminazione di un Rif int in CC
	 */
	public void deleteRifintCC(Rif rif){
		if (rif != null) {
			cc.remove(rif);
			if (cc.isEmpty()) 
				cc.add(new Rif());
		}
	}
	
	/**
	 * eliminazione di un Rif int in CDS
	 */
	public void deleteRifintCDS(Rif rif){
		if (rif != null) {
			cds.remove(rif);
			if (cds.isEmpty()) 
				cds.add(new Rif());
		}
	}

	/**
	 * spostamento in alto di un Rif int in CC
	 */
	public void moveUpRifintCC(Rif rif){
		if (rif != null && cc != null) {
			int index = cc.indexOf(rif);
			if (index > 0 ) {
				cc.remove(index);
				cc.add(index-1, rif);
			}
		}
	}

	/**
	 * spostamento in basso di un Rif int in CC
	 */
	public void moveDownRifintCC(Rif rif){
		if (rif != null && cc != null) {
			int index = cc.indexOf(rif);
			if (index < cc.size()-1 ) {
				cc.remove(index);
				cc.add(index+1, rif);
			}
		}
	}	
	
	/**
	 * spostamento in alto di un Rif int in CDS
	 */
	public void moveUpRifintCDS(Rif rif){
		if (rif != null && cds != null) {
			int index = cds.indexOf(rif);
			if (index > 0 ) {
				cds.remove(index);
				cds.add(index-1, rif);
			}
		}
	}

	/**
	 * spostamento in basso di un Rif int in CC
	 */
	public void moveDownRifintCDS(Rif rif){
		if (rif != null && cds != null) {
			int index = cds.indexOf(rif);
			if (index < cds.size()-1 ) {
				cds.remove(index);
				cds.add(index+1, rif);
			}
		}
	}
	
	/**
	 * aggiunta di un nuovo Rif int vuoto in CC in ultima posizione
	 */
	public void appendEmpty_cc() {
		if (cc != null && cc.size() > 0) {
			Rif previous = cc.get(cc.size()-1);
			if (!previous.isEmpty()) {
				Rif rifToAdd = new Rif();
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				cc.add(rifToAdd);
			}
		}
	}
	
	/**
	 * aggiunta di un nuovo Rif int vuoto in CDS in ultima posizione
	 */
	public void appendEmpty_cds() {
		if (cds != null && cds.size() > 0) {
			Rif previous = cds.get(cds.size()-1);
			if (!previous.isEmpty()) {
				Rif rifToAdd = new Rif();
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				cds.add(rifToAdd);
			}
		}
	}

}
