package it.tredi.dw4.model.azionimassive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Singola azione massiva
 */
public class Azione extends XmlEntity {

	private String titolo;
	private String descrizione;
	private String stored;
	private List<ParametroInput> parametriInput = new ArrayList<ParametroInput>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.titolo = XMLUtil.parseStrictAttribute(dom, "azione/@titolo");
		this.descrizione = XMLUtil.parseStrictAttribute(dom, "azione/@descrizione");
		this.stored = XMLUtil.parseStrictAttribute(dom, "azione/@stored");
		if (dom.selectNodes("azione/parametri_input/param") != null)
			this.parametriInput = XMLUtil.parseSetOfElement(dom, "azione/parametri_input/param", new ParametroInput());
		
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getStored() {
		return stored;
	}

	public void setStored(String stored) {
		this.stored = stored;
	}
	
	public List<ParametroInput> getParametriInput() {
		return parametriInput;
	}

	public void setParametriInput(List<ParametroInput> parametriInput) {
		this.parametriInput = parametriInput;
	}

	/**
	 * Resituisce l'elenco di parametri (in formato XML) richiesti per l'azione massiva e formattati in modo 
	 * da permettere l'invocazione della stored procedure
	 * @param dateFieldFormat Formato dei campi data da interfaccia operatore (default = dd/MM/yyyy) 
	 * @return
	 * @throws Exception
	 */
	public String getXmlParametriInputForStoredProcedureInvocation(String dateFieldFormat) throws Exception {
		if (dateFieldFormat == null || dateFieldFormat.isEmpty())
			dateFieldFormat = Const.DEFAULT_DATE_FORMAT;
		
		XMLDocumento paramsDoc = new XMLDocumento("<stored_params></stored_params>");
		Element root = paramsDoc.getRootElement();
		if (parametriInput != null && !parametriInput.isEmpty()) {
			for (ParametroInput param : parametriInput) {
				if (param != null) {
					Element paramEl = root.addElement("param");
					paramEl.addAttribute("id", param.getId());
					
					String value = param.getValue();
					if (value == null)
						value = "";
					if (!value.isEmpty() && param.getTipo().equals("calendar"))
						value = DateUtil.formatDate2XW(value, dateFieldFormat);
					paramEl.addAttribute("value", value);
				}
			}
		}
		return paramsDoc.asXML();
	}
	
}
