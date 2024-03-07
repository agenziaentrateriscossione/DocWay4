package it.tredi.dw4.model.azionimassive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Parametri di input definiti per una specifica azione massiva
 */
public class ParametroInput extends XmlEntity {

	private String id;
	private String label;
	private String helpMessage;
	private String tipo;
	private boolean required;
	private List<ParametroOption> options = new ArrayList<ParametroOption>(); // eventuali opzioni associate al parametro (se select)
	
	private String value; // valore associato al parametro (da form di avvio della stored procedure)
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.id = XMLUtil.parseStrictAttribute(dom, "param/@id");
		this.label = XMLUtil.parseStrictAttribute(dom, "param/@label");
		this.helpMessage = XMLUtil.parseStrictAttribute(dom, "param/@help");
		this.tipo = XMLUtil.parseStrictAttribute(dom, "param/@tipo");
		this.required = XMLUtil.parseStrictAttribute(dom, "param/@required", "false").toLowerCase().equals("true");
		this.options = XMLUtil.parseSetOfElement(dom, "param/options/option", new ParametroOption());

		// se il parametro non e' obbligatorio occorre aggiungere l'opzione vuota
		if (!this.required)
			this.options.add(0, new ParametroOption());
		
		// in caso di campo select assegno a value il valore di options settato come selected
		if ((this.value == null || this.value.isEmpty()) && this.options != null && !this.options.isEmpty()) {
			for (ParametroOption opt : this.options) {
				if (opt != null && opt.isSelected())
					this.value = opt.getValue();
			}
		}
		
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public List<ParametroOption> getOptions() {
		return options;
	}

	public void setOptions(List<ParametroOption> options) {
		this.options = options;
	}

}
