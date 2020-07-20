package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Oggetto che identifica un elemento delle molliche associate a un document e relative alla
 * classificazione
 * 
 * Ogni mollica corrisponde a codice xml con la seguente struttura:
 * <thPath cPath="cPath_della_mollica" thChannel="canale_del_th_da_usare" searchAlias="alias_per_la_ricerca" classifElemName="nome_elemento_xml">
 * 		<thnode name="nome_del_nodo_da_visualizzare_nella_mollica" pos="posizione_nella_mollica" link="y|n"/>
 * 		<thnode .../>
 * 		...
 * </thPath>
 * 
 * thPath indica l'elemento che identifica la mollica
 * 
 * @author mbernardini
 */
public class Thnode extends XmlEntity {

	// Indica il nome del nodo da visualizzare nella mollica
	private String name = "";
	// Indica la posizione nella mollica (0, 1, ...). I thnode vengono ordinati dall'xsl in base a
	//questo parametro
	private String pos = "";
	// Indica se visualizzare o meno un link che consente di posizionarsi sul nodo (i valori
	// possibili sono y e n). Attualmente solo l'ultimo nodo del percorso (che indica la posizione
	// attuale) non ha un link
	private String link = "";
	
	private String indice = "";
	private String codice = "";
	private String titolo = "";

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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
	
	@Override
	public XmlEntity init(Document dom) {
		this.pos = 	XMLUtil.parseAttribute(dom, "thnode/@pos", "");
		this.name = 	XMLUtil.parseAttribute(dom, "thnode/@name", "");
    	this.link = 	XMLUtil.parseAttribute(dom, "thnode/@link", "");
    	
    	if (this.name != null) {
	    	int spIndex = this.name.indexOf(" ");
	    	if (spIndex != -1) {
	    		this.indice = this.name.substring(0, spIndex).trim();
	    		
	    		String tmp = this.name.substring(spIndex+1).trim();
	    		int trIndex = tmp.indexOf("-");
	    		if (trIndex != -1) {
	    			this.codice = tmp.substring(0, trIndex).trim();
	    			this.titolo = tmp.substring(trIndex+1).trim();
	    		}
	    	}
    	}
    	
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
}
