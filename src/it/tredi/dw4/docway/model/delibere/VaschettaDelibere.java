package it.tredi.dw4.docway.model.delibere;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Vaschetta;
import it.tredi.dw4.model.XmlEntity;

public class VaschettaDelibere extends Vaschetta {

	@Override
	public XmlEntity init(Document dom) {
		super.init(dom);
		
		//inverto la label con il tipo, perchè per le deliebere ho <UOR_propos> | <RPA_props> ...
		String temp = this.getTipo();
		this.setTipo(this.getLabel());
		this.setLabel(temp);
		
		return this;
	}
}
