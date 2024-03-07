package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Arrivo;
import it.tredi.dw4.docway.model.intraaoo.IntraAoo;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocArrivo extends ShowdocDoc {

	public ShowdocArrivo() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		Element root = dom.getRootElement();

		xml = dom.asXML();
		if (root.attributeValue("view", "").equals("verificaDuplicati"))
			this.verificaDuplicati = true;
		doc = new Arrivo();
		doc.init(dom);

		// mbernardini 28/10/2016 : gestione delle aoo configurate per la comunicazione intra-aoo
		setIntraAoo(new IntraAoo());
		getIntraAoo().init(XMLUtil.createDocument(dom, "/response/doc/extra/intraAoo"));

		initAvailableIntraAoos(dom);

		initCommon(dom);

		// in caso di presenza di dati relativi a intraAOO occorre visualizzare la sezione 'stati documento'
		if (!isShowSectionStatiDocumento()) {
			if (getIntraAoo() != null && (!getIntraAoo().getFrom().isEmpty() || !getIntraAoo().getTo().isEmpty()))
				setShowSectionStatiDocumento(true);
		}
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@arrivo");
	}

}
