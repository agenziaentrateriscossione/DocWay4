package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

public class Thes extends XmlEntity {
	
	private String chiave = "";
	private String nome = "";

	private String indice = "";
	private String codice = "";
	private String titolo = "";
	
	private boolean radice = false; // Identifica se si tratta del nodo radice del thesauro di classificazione
	private int level = 0; // Livello del nodo all'interno dell'albero
	
	private List<Thes> childrens = null; // Figli del nodo corrente del thesauro di classificazione
	
	private int thPathLength = 0; // path di navigazione del thesauro
	
	public String getChiave() {
		return chiave;
	}

	public void setChiave(String chiave) {
		this.chiave = chiave;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIndice() {
		return indice;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	public boolean isRadice() {
		return radice;
	}

	public void setRadice(boolean radice) {
		this.radice = radice;
	}
	
	public void setLevel(int value) {
		this.level = value;
	}

	public int getLevel() {
		return level;
	}
	
	public List<Thes> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<Thes> childs) {
		this.childrens = childs;
	}
	
	public int getThPathLength() {
		return thPathLength;
	}

	public void setThPathLength(int thPathLength) {
		this.thPathLength = thPathLength;
	}
	
	@Override
	public XmlEntity init(Document dom) {
		return init(dom, thPathLength);
	}

	/**
	 * Inizializzazione del thesauro tramite vista ad albero
	 * @param dom
	 * @param livello
	 * @return
	 */
	public XmlEntity init(Document dom, int livello) {
		this.chiave = 		XMLUtil.parseAttribute(dom, "thes/@chiave", "");
    	this.nome = 		XMLUtil.parseAttribute(dom, "thes/@nome");
    	this.level =		livello;
    	this.childrens = 	this.parseThesThree(dom, "thes/thes", livello);
    	
    	boolean rootLevel = true;
    	if (level > 1)
    		rootLevel = false;
    	
    	if (this.nome != null && this.nome.length() > 0)
    		setThesProperties(this.nome, rootLevel);
    	else
    		setThesProperties(this.chiave, rootLevel);
    	
    	if (this.nome != null && this.nome.equals(Const.TITOLARIO_CLASSIF_NODO_RADICE))
    		this.radice = true;
    	else
    		this.radice = false;
    	
    	return this;
	}
	
	private List<Thes> parseThesThree(Document dom, String xpath, int livello) {
    	List<Thes> retValue = new ArrayList<Thes>();
        if (null == dom) return retValue;
    	try {
            List<?> list = dom.selectNodes(xpath); 
            if ( (null != list) && (list.size() > 0) ) {
                for (int index = 0; index < list.size(); index++) {
                    Element element = (Element)list.get(index);
                    Thes thes = new Thes();
                    retValue.add((Thes) thes.init(DocumentHelper.createDocument(element.createCopy()), livello+1));
                }
            }
        }
        catch(Exception e) {
            Logger.error(e.getMessage(), e);
        }
        return retValue;
    }
	
	/**
	 * Inizializzazione del thesauro tramite vista a livelli
	 * @param dom
	 * @param livello
	 * @return
	 */
	public XmlEntity initByLevel(Document dom) {
		this.chiave = XMLUtil.parseAttribute(dom, "thes/@chiave", "");
    	this.nome = XMLUtil.parseAttribute(dom, "thes/@nome");

    	this.childrens =  new ArrayList<Thes>();
    	List<?> list = dom.selectNodes("thes/thes"); 
        if ( (null != list) && (list.size() > 0) ) {
            for (int index = 0; index < list.size(); index++) {
                Element element = (Element)list.get(index);
                Thes thes = new Thes();
                thes.setThPathLength(thPathLength);
                thes.initByLevel(DocumentHelper.createDocument(element.createCopy()));
                this.childrens.add(thes);
            }
        }
    	
    	boolean rootLevel = true;
    	if (level > 1)
    		rootLevel = false;
    	
    	if (this.nome != null && this.nome.length() > 0)
    		setThesProperties(this.nome, rootLevel);
    	else
    		setThesProperties(this.chiave, rootLevel);
    	
    	if (this.nome != null && this.nome.equals(Const.TITOLARIO_CLASSIF_NODO_RADICE))
    		this.radice = true;
    	else
    		this.radice = false;
    	
    	return this;
	}
	
	private void setThesProperties(String nomeCompleto, boolean rootLevel) {
		if (nomeCompleto != null) {
	    	int spIndex = nomeCompleto.indexOf(" ");
	    	if (spIndex != -1) {
	    		this.indice = nomeCompleto.substring(0, spIndex).trim();
	    		
	    		String[] classifFormat = ClassifUtil.getSplittedClassifFormat();
	    		if (classifFormat != null && classifFormat.length == 2) {
	    			if (rootLevel)
	    				this.codice = ClassifUtil.classifNumToFormat(this.indice, classifFormat[0]);
	    			else
	    				this.codice = ClassifUtil.classifNumToFormat(this.indice, classifFormat[1]);
	    		}
	    		
	    		String tmp = nomeCompleto.substring(spIndex+1).trim();
	    		int trIndex = tmp.indexOf("-");
	    		if (trIndex != -1)
	    			this.titolo = tmp.substring(trIndex+1).trim();
	    	}
    	}
	}
	
	public void initThesByChiave(String chiaveValue, boolean rootLevel) {
		if (chiaveValue != null && chiaveValue.length() > 0) {
			this.chiave = chiaveValue;
			setThesProperties(this.chiave, rootLevel);
		}
	}
	
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
}
