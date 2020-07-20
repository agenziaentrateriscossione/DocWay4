package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Minuta extends XmlEntity {
   	private Classif classif = new Classif();
    private MittenteMinuta mittente = new MittenteMinuta();
    private Voce_indice voce_indice = new Voce_indice();
    private String scarto;
    private String motiv_ogg_div = "";
    
    private int countCcFascicoloMinuta;
    private List<Rif> cc_fasc_minuta_list = new ArrayList<Rif>();
    private HashMap<String, List<Rif>> cc_fasc_minuta_ufficio = new HashMap<String, List<Rif>>();
    
	public Minuta() {}
    
	public Minuta(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
	public Minuta init(Document dom) {
    	this.scarto = XMLUtil.parseStrictAttribute(dom, "/minuta/@scarto");
    	this.classif.init(XMLUtil.createDocument(dom, "//minuta/classif"));
		this.mittente.init(XMLUtil.createDocument(dom, "//minuta/mittente"));
		this.voce_indice.init(XMLUtil.createDocument(dom, "//minuta/voce_indice"));
		this.motiv_ogg_div = XMLUtil.parseStrictElement(dom, "//minuta/motivazione_oggetti_diversi");
		
		List<Rif> rif_minuta = new ArrayList<Rif>();
        try {
        	if (dom != null) {
	            List<?> list = dom.selectNodes("//minuta/rifInt_minuta/rif_minuta"); 
	            if ( (null != list) && (list.size() > 0) ) {
	                for (int index = 0; index < list.size(); index++) {
	                	if (list.get(index) != null && list.get(index) instanceof Element) { // TODO da verificare, ma dovrebbe essere corretto
		                    Element element = (Element)list.get(index);
		                    Rif entity = new Rif();
		                    rif_minuta.add(entity.init(DocumentHelper.createDocument(element.createCopy()), true));
	                	}
	                }
	            }
        	}
        }
        catch(Exception e) {
        	Logger.error(e.getMessage(), e);
        }
		
		// Gestione dei CC ereditati dal fascicolo in minuta (solo doc tra uffici)
		if (rif_minuta != null && rif_minuta.size() > 0) {
			for (int i=0; i<rif_minuta.size(); i++) {
				Rif tmpRifMinuta = (Rif) rif_minuta.get(i);
				if (tmpRifMinuta != null && tmpRifMinuta.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_CC)) {
					cc_fasc_minuta_list.add(tmpRifMinuta);
					if (tmpRifMinuta.getCod_uff() != null && !tmpRifMinuta.getCod_uff().equals("")) {
						if (cc_fasc_minuta_ufficio.containsKey(tmpRifMinuta.getCod_uff())) { // ufficio gia' presente nell'hashmap
							cc_fasc_minuta_ufficio.get(tmpRifMinuta.getCod_uff()).add(tmpRifMinuta);
						}
						else { // nuovo ufficio nell'hashmap
							ArrayList<Rif> new_list_ufficio = new ArrayList<Rif>();
							new_list_ufficio.add(tmpRifMinuta);
							cc_fasc_minuta_ufficio.put(tmpRifMinuta.getCod_uff(), new_list_ufficio);
						}
					}
				}
			}
			
			this.countCcFascicoloMinuta = dom.selectNodes("//minuta/rifInt_minuta/rif_minuta[@diritto='CC'][@cc_from_fasc!='']").size();
		}
		
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@scarto", this.scarto);
    	params.putAll(classif.asFormAdapterParams(prefix+".classif")); // TODO Da verificare - aggiunto prefix perche' altrimenti sarebbero stati azzerati i parametri del doc e non della sottoparte minuta
    	params.putAll(mittente.asFormAdapterParams(prefix+".mittente"));
    	params.putAll(voce_indice.asFormAdapterParams(prefix+".voce_indice"));
    	params.put("motiv_ogg_div_minuta", this.motiv_ogg_div);
    	return params;
    }
    
	public void setClassif(Classif classif) {
		this.classif = classif;
	}

	public Classif getClassif() {
		return classif;
	}

	public void setMittente(MittenteMinuta mittente) {
		this.mittente = mittente;
	}

	public MittenteMinuta getMittente() {
		return mittente;
	}

	public void setVoce_indice(Voce_indice voce_indice) {
		this.voce_indice = voce_indice;
	}

	public Voce_indice getVoce_indice() {
		return voce_indice;
	}

	public void setScarto(String scarto) {
		this.scarto = scarto;
	}

	public String getScarto() {
		return scarto;
	}
	
	public String getMotiv_ogg_div() {
		return motiv_ogg_div;
	}

	public void setMotiv_ogg_div(String motiv_ogg_div_minuta) {
		this.motiv_ogg_div = motiv_ogg_div_minuta;
	}
	
	public int getCountCcFascicoloMinuta() {
		return countCcFascicoloMinuta;
	}

	public void setCountCcFascicoloMinuta(int countCcFascicoloMinuta) {
		this.countCcFascicoloMinuta = countCcFascicoloMinuta;
	}

	public List<Rif> getCc_fasc_minuta_list() {
		return cc_fasc_minuta_list;
	}

	public void setCc_fasc_minuta_list(List<Rif> cc_fasc_minuta_list) {
		this.cc_fasc_minuta_list = cc_fasc_minuta_list;
	}

	public HashMap<String, List<Rif>> getCc_fasc_minuta_ufficio() {
		return cc_fasc_minuta_ufficio;
	}

	public void setCc_fasc_minuta_ufficio(
			HashMap<String, List<Rif>> cc_fasc_minuta_ufficio) {
		this.cc_fasc_minuta_ufficio = cc_fasc_minuta_ufficio;
	}
	
}

