package it.tredi.dw4.docway.model.intraaoo;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Caricamento dei dati relativi a comunicazione intra-aoo
 * @author mbernardini
 */
public class IntraAoo extends XmlEntity {

	List<FromAoo> from;
	List<ToAoo> to;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.from = XMLUtil.parseSetOfElement(dom, "intraAoo/from", new FromAoo());
		this.to = XMLUtil.parseSetOfElement(dom, "intraAoo/to", new ToAoo());
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// non dovrebbe essere necessario inviare questi dati in fase di salvataggio del documento
		return null;
	}
	
	public List<FromAoo> getFrom() {
		return from;
	}

	public void setFrom(List<FromAoo> from) {
		this.from = from;
	}

	public List<ToAoo> getTo() {
		return to;
	}

	public void setTo(List<ToAoo> to) {
		this.to = to;
	}

}
