package it.tredi.dw4.docway.model.intraaoo;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class FromAoo extends XmlEntity {

	private String nomeOperatore;
	private String ufficioOperatore;
	private String data;
	private String ora;
	private String aoo;
	private String numProt;
	private String dataProt;
	
	@Override
	public XmlEntity init(Document dom) {
		this.nomeOperatore = XMLUtil.parseStrictAttribute(dom, "from/@oper");
		this.ufficioOperatore = XMLUtil.parseStrictAttribute(dom, "from/@uffOper");
		this.data = XMLUtil.parseStrictAttribute(dom, "from/@data");
		this.ora = XMLUtil.parseStrictAttribute(dom, "from/@ora");
		this.aoo = XMLUtil.parseStrictAttribute(dom, "from/@aoo");
		this.numProt = XMLUtil.parseStrictAttribute(dom, "from/@numProt");
		this.dataProt = XMLUtil.parseStrictAttribute(dom, "from/@dataProt");
		
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
