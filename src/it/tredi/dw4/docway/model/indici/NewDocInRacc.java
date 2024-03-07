package it.tredi.dw4.docway.model.indici;

import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity utilizzata per la creazione rapida di documenti all'interno dell'interfaccia di
 * visualizzazione di raccoglitori di tipo indice
 * @author mbernardini
 */
public class NewDocInRacc extends XmlEntity {

	private String oggetto;
	private String tipo;
	private String codRep;
	private boolean sendEmailNotifica = true;
	private boolean importAllegati = false;
	private String oggetto_lookup = "";
	private String nrecord_lookup = "";

	private String tipoDocSelected = "";
	private Map<String, String> tipiDoc = new LinkedHashMap<String, String>();


	public NewDocInRacc() {
		// default sendEmailNotifica false per NewDocInRacc "vuoti"
		this.sendEmailNotifica = false;
	}

	/**
	 * Costruttore (si occupa di caricare le tipologie di documenti supportate)
	 * @param dom
	 */
	public NewDocInRacc(Document dom) {
		if (dom != null) {
			List<?> nodes = dom.selectNodes("/response/tipinewdoc/doc");
			if (nodes != null && nodes.size() > 0) {
				for (int i=0; i<nodes.size(); i++) {
					Element eldoc = (Element) nodes.get(i);
					String tipo = eldoc.attributeValue("tipo", "");

					Element elrep = eldoc.element("rep");
					if (elrep != null) {
						// caso di un repertorio
						tipiDoc.put(tipo + "_" + elrep.attributeValue("cod", ""), elrep.getTextTrim());
					}
					else {
						// caso di un tipo semplice (arrivo, partenza, varie, interno)
						tipiDoc.put(tipo, I18N.mrs("dw4.raccdoc_" + tipo));
					}
				}
			}
		}
	}

	@Override
	public XmlEntity init(Document dom) {
		// questo metodo non dovrebbe mai essere invocato (non si tratta di un XmlEntity classico).
		// XmlEntity e' stato esteso per potergli agganciare il lookup
		return null;
	}

	/**
	 * Formattazione dei parametri dell'oggetto per caricamento su formsAdapter
	 * @param prefix
	 * @return
	 */
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		Map<String, String> params = new HashMap<String, String>();

		if (prefix == null)
			prefix = "";

		params.put(prefix+"docInRacc.oggetto", this.oggetto);
		if (this.tipoDocSelected != null && !this.tipoDocSelected.isEmpty()) {
			int index = this.tipoDocSelected.indexOf("_");
			if (index != -1) {
				this.tipo = this.tipoDocSelected.substring(0, index);
				this.codRep = this.tipoDocSelected.substring(index+1);
			}
			else {
				this.tipo = this.tipoDocSelected;
				this.codRep = "";
			}
		}
		params.put(prefix+"docInRacc.tipo", this.tipo);
		params.put(prefix+"docInRacc.codrep", this.codRep);
		params.put(prefix+"docInRacc.sendEmailNotifica", Boolean.toString(this.sendEmailNotifica));
		params.put(prefix+"docInRacc.importaAllegati", Boolean.toString(this.isImportAllegati()));
		params.put(prefix+"docInRacc.nrecord_lookup", this.nrecord_lookup);

		return params;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodRep() {
		return codRep;
	}

	public void setCodRep(String codRep) {
		this.codRep = codRep;
	}

	public boolean isSendEmailNotifica() {
		return sendEmailNotifica;
	}

	public void setSendEmailNotifica(boolean sendEmailNotifica) {
		this.sendEmailNotifica = sendEmailNotifica;
	}

	public boolean isImportAllegati() {
		return importAllegati;
	}

	public void setImportAllegati(boolean importAllegati) {
		this.importAllegati = importAllegati;
	}

	public String getTipoDocSelected() {
		return tipoDocSelected;
	}

	public void setTipoDocSelected(String tipoDocSelected) {
		this.tipoDocSelected = tipoDocSelected;
	}

	public Map<String, String> getTipiDoc() {
		return tipiDoc;
	}

	public void setTipiDoc(Map<String, String> tipiDoc) {
		this.tipiDoc = tipiDoc;
	}

	public String getOggetto_lookup() {
		return oggetto_lookup;
	}

	public void setOggetto_lookup(String oggetto_lookup) {
		this.oggetto_lookup = oggetto_lookup;
	}

	public String getNrecord_lookup() {
		return nrecord_lookup;
	}

	public void setNrecord_lookup(String nrecord_lookup) {
		this.nrecord_lookup = nrecord_lookup;
	}

}
