package it.tredi.dw4.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.docway.beans.DocWayTitles;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Titolo extends XmlEntity {
	private String testo = "";
	private String indice = "";
	private String dbTable = "";
	private String icon = "";
	private String frequenza = "";
	private String fascicolato = "";
	private String fascicolato_minuta = "";
	private String diritto = "";
	private String visto = "";
	private String immagini = "";
	private String db = "";
	private String type = "";
	private String files = "";
	private String visible = "";
	private String extra = "";
	private String subtitle = "";
	private String is_linked = "";
	private boolean overrideCC = true; // Utilizzato su lookup di tipo 'Voce di Indice' per gestione responsabili
	private boolean select = false; 
	private boolean selected = false; // indica se il titolo e' selezionato
	private List<Campo> campi = new ArrayList<Campo>();
	private Map<String, String> campiTitolo = new HashMap<String, String>();
	private List<String> split = new ArrayList<String>();
	private Map<String, String> hashSplit = new HashMap<String, String>(); // utilizzato per la gestione di colonne aggiuntive nella lista risultati da ricerca // TODO verifica la correttezza
    private DocWayTitles subtitles; 
    private boolean openSubTitles;
    
    //TitoloSeduta
    private boolean showSeduta;
	private boolean notShowSeduta;
	private boolean sedutaSospesa;
	private boolean straordinaria;
    
	public Titolo() {}
    
	public Titolo(String xmlTitolo) throws Exception {
        this.init(XMLUtil.getDOM(xmlTitolo));
    }
    
    @SuppressWarnings("unchecked")
	public Titolo init(Document domTitolo) {
    	this.testo = 				XMLUtil.parseAttribute(domTitolo, "titolo/@testo", "");
    	this.indice = 				XMLUtil.parseAttribute(domTitolo, "titolo/@indice");
    	this.dbTable = 				XMLUtil.parseAttribute(domTitolo, "titolo/@dbTable");
    	this.icon = 				XMLUtil.parseAttribute(domTitolo, "titolo/@icon");
    	this.fascicolato = 			XMLUtil.parseAttribute(domTitolo, "titolo/@fascicolato");
    	this.fascicolato_minuta = 	XMLUtil.parseAttribute(domTitolo, "titolo/@fascicolato_m");
    	this.files = 				XMLUtil.parseAttribute(domTitolo, "titolo/@files");
    	this.diritto = 				XMLUtil.parseAttribute(domTitolo, "titolo/@diritto");
    	this.visto = 				XMLUtil.parseAttribute(domTitolo, "titolo/@visto");
    	this.immagini =				XMLUtil.parseAttribute(domTitolo, "titolo/@immagini");
    	this.db = 					XMLUtil.parseAttribute(domTitolo, "titolo/@db");
    	this.type = 				XMLUtil.parseAttribute(domTitolo, "titolo/@type");
    	this.frequenza= 			XMLUtil.parseAttribute(domTitolo, "titolo/@frequenza");
    	this.visible =	 			XMLUtil.parseAttribute(domTitolo, "titolo/@visible");
    	this.extra =				XMLUtil.parseAttribute(domTitolo, "titolo/@extra");
    	this.is_linked =			XMLUtil.parseAttribute(domTitolo, "titolo/@is_linked");
    	this.campi = 				XMLUtil.parseSetOfElement(domTitolo, "//titolo/campo", new Campo());
    	String valore_campo = 		XMLUtil.parseAttribute(domTitolo, "titolo/@campo");
    	
    	if (XMLUtil.parseAttribute(domTitolo, "titolo/@selected", "").equals("true"))
    		this.selected =	true;
    	else
    		this.selected =	false;
    	
    	if ( null != valore_campo && !"".equals(valore_campo.trim())){
    		Campo campo = new Campo();
    		campo.setNome(valore_campo);
    		campo.setText(testo);
    		campi.add(campo);
    	}
    	
    	if ( testo.trim().length()>0 && testo.contains("|")){
    		String[] parti = testo.split("\\|");
    		for (int i = 0; i < parti.length; i++) {
				String stringa = parti[i];
				split.add(stringa);
				
				// TODO e' corretto gestire in questo modo questa tipologia di campi?
				int index = stringa.indexOf("!");
				if (index != -1) {
					String key = stringa.substring(0, index);
					String value = stringa.substring(index+1);
					if (key.trim().length() > 0)
						hashSplit.put(key, value);
				}
			}
    	}
    	
    	// il blocco seguente (riempimento hashMap) e' necessario alla gestione di lookup su
    	// voce d'indice (riempimento dei responsabili del doc dopo selezione voce d'indice)
    	if (campi != null && campi.size() > 0) {
    		for (int i=0; i<campi.size(); i++) {
    			Campo campo = (Campo) campi.get(i);
    			if (campo != null) {
		    		// TODO verificare se è possibile convertire direttamente la lista campi in hashmap
		    		campiTitolo.put(campo.getNome(), campo.getText());
    			}
    		}
    	}
    		
    	//TitoloSeduta
    	this.setShowSeduta(Boolean.valueOf(XMLUtil.parseAttribute(domTitolo, "titolo/@showSeduta", "")));
		this.setNotShowSeduta(Boolean.valueOf(XMLUtil.parseAttribute(domTitolo, "titolo/@notShowSeduta", "")));
		
		//se è una seduta...
		if(this.isNotShowSeduta() || this.isShowSeduta()){
			//in xsl fa così per capire che è una seduta...io lo mappo in 'visible' per gestirlo negli xhtml
			this.setVisible(String.valueOf(!(this.getVisible().equals("false")) && this.getDb().startsWith("xdocwaydoc") && this.getDbTable().isEmpty() && this.getTesto().contains("U!seduta")));
		
			if(this.getSplit().get(15).equals("12/12/1999"))
			{
				this.setSedutaSospesa(true);
			}else
			{
				this.setSedutaSospesa(false);
			}
			
			this.setStraordinaria(StringUtil.booleanValue(this.getSplit().get(16)));
		}
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	return null;
    }
    
    public void setTesto(String text) {
		this.testo = text;
	}

	public String getTesto() {
		return testo;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}

	public String getIndice() {
		return indice;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getDbTable() {
		return dbTable;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getDb() {
		return db;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setCampi(List<Campo> campi) {
		this.campi = campi;
	}

	public List<Campo> getCampi() {
		return campi;
	}
	
	public Map<String, String> getCampiTitolo() {
		return campiTitolo;
	}

	public void setCampiTitolo(Map<String, String> campiTitolo) {
		this.campiTitolo = campiTitolo;
	}

	public void setFrequenza(String frequenza) {
		this.frequenza = frequenza;
	}

	public String getFrequenza() {
		return frequenza;
	}

	public void setSplit(List<String> split) {
		this.split = split;
	}

	public List<String> getSplit() {
		return split;
	}
	
	public Map<String, String> getHashSplit() {
		return hashSplit;
	}

	public void setHashSplit(Map<String, String> hashSplit) {
		this.hashSplit = hashSplit;
	}

	public void setFascicolato(String fascicolato) {
		this.fascicolato = fascicolato;
	}

	public String getFascicolato() {
		return fascicolato;
	}

	public String getFascicolato_minuta() {
		return fascicolato_minuta;
	}

	public void setFascicolato_minuta(String fascicolato_minuta) {
		this.fascicolato_minuta = fascicolato_minuta;
	}
	
	public void setFiles(String files) {
		this.files = files;
	}

	public String getFiles() {
		return files;
	}

	public void setDiritto(String diritto) {
		this.diritto = diritto;
	}

	public String getDiritto() {
		return diritto;
	}

	public void setVisto(String visto) {
		this.visto = visto;
	}

	public String getVisto() {
		return visto;
	}

	public void setImmagini(String immagini) {
		this.immagini = immagini;
	}

	public String getImmagini() {
		return immagini;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getVisible() {
		return visible;
	}
	
	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		if (extra == null)
			extra = "";
		return extra;
	}
	
	public void setSubtitle(String text) {
		this.subtitle = text;
	}
	
	public String getSubtitle() {
		if (subtitle == null)
			subtitle = "";
		return subtitle;
	}

	public boolean isOverrideCC() {
		return overrideCC;
	}

	public void setOverrideCC(boolean overrideCC) {
		this.overrideCC = overrideCC;
	}

	public void setIs_linked(String is_linked) {
		this.is_linked = is_linked;
	}

	public String getIs_linked() {
		return is_linked;
	}

	public void setSelect(boolean selected) {
		this.select = selected;
	}

	public boolean isSelect() {
		return select;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSubtitles(DocWayTitles titles) {
		this.subtitles = titles;
	}

	public DocWayTitles getSubtitles() {
		return subtitles;
	}

	public void setOpenSubTitles(boolean openSubTitles) {
		this.openSubTitles = openSubTitles;
	}

	public boolean isOpenSubTitles() {
		return openSubTitles;
	}
	
	public int getFolderLevel(){
		String id = split.get(7);
		if (id.contains("/")) id = id.substring(id.indexOf("/")+1);
		else if (id.contains("-")) id = id.substring(id.indexOf("-")+1);
		String[] level = id.split("\\.");
		return level.length-1;
	}
	
	//Titolo Seduta
		public boolean isShowSeduta() {
			return showSeduta;
		}

		public void setShowSeduta(boolean showSeduta) {
			this.showSeduta = showSeduta;
		}

		public boolean isNotShowSeduta() {
			return notShowSeduta;
		}

		public void setNotShowSeduta(boolean notShowSeduta) {
			this.notShowSeduta = notShowSeduta;
		}

		public boolean isSedutaSospesa() {
			return sedutaSospesa;
		}

		public void setSedutaSospesa(boolean sedutaSospesa) {
			this.sedutaSospesa = sedutaSospesa;
		}

		public boolean isStraordinaria() {
			return straordinaria;
		}

		public void setStraordinaria(boolean straordinaria) {
			this.straordinaria = straordinaria;
		}
}