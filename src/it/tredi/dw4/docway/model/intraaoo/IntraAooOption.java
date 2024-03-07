package it.tredi.dw4.docway.model.intraaoo;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class IntraAooOption extends XmlEntity {

	private String codAmmAoo;
	private String descrizione;
	private String nomePersonaResponsabile;
	private String nomeUfficioResponsabile;
	
	@Override
	public XmlEntity init(Document dom) {
		this.codAmmAoo = XMLUtil.parseStrictAttribute(dom, "aoo/@cod", "");
		this.descrizione = XMLUtil.parseStrictElement(dom, "aoo", true);
		this.nomePersonaResponsabile = XMLUtil.parseStrictAttribute(dom, "aoo/rpa/@nome_persona", "");
		this.nomeUfficioResponsabile = XMLUtil.parseStrictAttribute(dom, "aoo/rpa/@nome_uff", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getCodAmmAoo() {
		return codAmmAoo;
	}

	public void setCodAmmAoo(String codAmmAoo) {
		this.codAmmAoo = codAmmAoo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getNomePersonaResponsabile() {
		return nomePersonaResponsabile;
	}

	public void setNomePersonaResponsabile(String nomePersonaResponsabile) {
		this.nomePersonaResponsabile = nomePersonaResponsabile;
	}

	public String getNomeUfficioResponsabile() {
		return nomeUfficioResponsabile;
	}

	public void setNomeUfficioResponsabile(String nomeUfficioResponsabile) {
		this.nomeUfficioResponsabile = nomeUfficioResponsabile;
	}
	
}
