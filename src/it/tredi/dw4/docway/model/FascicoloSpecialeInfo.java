package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class FascicoloSpecialeInfo extends XmlEntity {

	private String id = "";
	private String titolo = "";
	private boolean inserimento = false;
	private Classif classif = new Classif();

	@Override
	public XmlEntity init(Document dom) {
		this.id = XMLUtil.parseAttribute(dom, "fascicolo_speciale_info/@id", "");
		this.titolo = XMLUtil.parseAttribute(dom, "fascicolo_speciale_info/@titolo", "");
		if (XMLUtil.parseAttribute(dom, "fascicolo_speciale_info/@inserimento", "false").equals("true"))
			this.inserimento = true;
		else
			this.inserimento = false;
		this.classif.init(XMLUtil.createDocument(dom, "fascicolo_speciale_info/classif"));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public boolean isInserimento() {
		return inserimento;
	}

	public void setInserimento(boolean inserimento) {
		this.inserimento = inserimento;
	}

	public Classif getClassif() {
		return classif;
	}

	public void setClassif(Classif classif) {
		this.classif = classif;
	}
	
}
