package it.tredi.dw4.docway.model.indici;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class PreviewDoc extends Doc {

	private String statoRaccIndice;

	// oggetto per la formattazione per la stampa in pdf
	private String oggetto_stampa;

	@Override
	public XmlEntity init(Document dom) {
		super.init(dom);

		// caricamento dei campi extra relativi all'indice
		this.statoRaccIndice = XMLUtil.parseStrictAttribute(dom, "/response/doc/extra/raccIndice/@stato", "");

		// caricamento dei campi extra relativi all'indice
		this.oggetto_stampa = XMLUtil.parseElement(dom, "doc/oggetto_stampa", false);

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);

		params.put(prefix+".extra.raccIndice.@stato", this.statoRaccIndice);
		params.put(prefix+".oggetto_stampa", this.oggetto_stampa);
		return params;
	}

	public String getStatoRaccIndice() {
		return statoRaccIndice;
	}

	public void setStatoRaccIndice(String stato) {
		this.statoRaccIndice = stato;
	}

	public String getOggetto_stampa() {
		return oggetto_stampa;
	}

	public void setOggetto_stampa(String oggetto_stampa) {
		this.oggetto_stampa = oggetto_stampa;
	}

}
