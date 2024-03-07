package it.tredi.dw4.docway.model.intraaoo;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ToAoo extends XmlEntity {

	private String nomeOperatore;
	private String ufficioOperatore;
	private String data;
	private String ora;
	private String aoo;
	private String numProt;
	private String dataProt;
	
	@Override
	public XmlEntity init(Document dom) {
		this.nomeOperatore = XMLUtil.parseStrictAttribute(dom, "to/@oper");
		this.ufficioOperatore = XMLUtil.parseStrictAttribute(dom, "to/@uffOper");
		this.data = XMLUtil.parseStrictAttribute(dom, "to/@data");
		this.ora = XMLUtil.parseStrictAttribute(dom, "to/@ora");
		this.aoo = XMLUtil.parseStrictAttribute(dom, "to/@aoo");
		this.numProt = XMLUtil.parseStrictAttribute(dom, "to/@numProt");
		this.dataProt = XMLUtil.parseStrictAttribute(dom, "to/@dataProt");
		
		return this;
	}
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// non dovrebbe essere necessario inviare questi dati in fase di salvataggio del documento
		return null;
	}
	
	public String getNomeOperatore() {
		return nomeOperatore;
	}

	public void setNomeOperatore(String nomeOperatore) {
		this.nomeOperatore = nomeOperatore;
	}

	public String getUfficioOperatore() {
		return ufficioOperatore;
	}

	public void setUfficioOperatore(String ufficioOperatore) {
		this.ufficioOperatore = ufficioOperatore;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOra() {
		return ora;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}
	
	public String getAoo() {
		return aoo;
	}
	
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}
	
	public String getNumProt() {
		return numProt;
	}
	
	public void setNumProt(String numProt) {
		this.numProt = numProt;
	}
	
	public String getDataProt() {
		return dataProt;
	}
	
	public void setDataProt(String dataProt) {
		this.dataProt = dataProt;
	}
	
}
