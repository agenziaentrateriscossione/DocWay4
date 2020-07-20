package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class PropostaOdg extends XmlEntity {
	private String nrecord_prop = "";
	private String cod_categoria = "";
	private String tipo = "";
	private String numero_delibera = "";
	private String risultato_seduta = "";
	private boolean delibera_prodotta;
	private String pos = "";
	private String radio = "";
	private String oggetto_prop = "";
	private String warning = "";
	private boolean delib_seduta_stante;
	private String commento_comunicazioni = "";
	private String nota_risultato = "";
	
	private boolean followingSibling = false;
	private boolean precedingSibling = false;
	
	// variabili utili per gestire gli editing delle proposte in @maschRis
	private String posField = "";
	private String radioField = "";

	@Override
	public XmlEntity init(Document dom) {
		String XPath = dom.getRootElement().getName();
		this.setNrecord_prop(XMLUtil.parseAttribute(dom, XPath + "/@nrecord_prop"));
		this.setCod_categoria(XMLUtil.parseAttribute(dom, XPath + "/@cod_categoria"));
		this.setTipo(XMLUtil.parseAttribute(dom, XPath + "/@tipo"));
		this.setPos(XMLUtil.parseAttribute(dom, XPath + "/@pos"));
		this.setRadio(XMLUtil.parseAttribute(dom, XPath + "/@radio"));
		this.setOggetto_prop(XMLUtil.parseElement(dom, XPath + "/oggetto_prop"));
		
		this.setRisultato_seduta(XMLUtil.parseAttribute(dom, XPath + "/@risultato_seduta"));
		this.setDelibera_prodotta(StringUtil.booleanValue(XMLUtil.parseAttribute(dom, XPath + "/@delibera_prodotta","")));
		this.setWarning(XMLUtil.parseAttribute(dom, XPath + "/@warning",""));
		this.setDelib_seduta_stante(StringUtil.booleanValue(XMLUtil.parseAttribute(dom, XPath + "/@delib_seduta_stante","")));
		this.setNumero_delibera(XMLUtil.parseAttribute(dom, XPath + "/@numero_delibera",""));
			if(this.numero_delibera != null && !this.numero_delibera.isEmpty())
			{
				String offsetString = StringUtil.substringAfter(this.numero_delibera, "-");
				if(offsetString != null && !offsetString.isEmpty())
					this.numero_delibera =  StringUtil.trimzero(offsetString.substring(4)) + '/' + offsetString.substring(0,4);
			}		
		this.setNota_risultato(XMLUtil.parseElement(dom, XPath + "/nota_risultato"));
		this.setCommento_comunicazioni(XMLUtil.parseElement(dom, XPath + "/commento_comunicazioni"));
		
		this.setPosField("");
		this.setRadioField("");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNrecord_prop() {
		return nrecord_prop;
	}

	public void setNrecord_prop(String nrecord_prop) {
		this.nrecord_prop = nrecord_prop;
	}

	public String getCod_categoria() {
		return cod_categoria;
	}

	public void setCod_categoria(String cod_categoria) {
		this.cod_categoria = cod_categoria;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getOggetto_prop() {
		return oggetto_prop;
	}

	public void setOggetto_prop(String oggetto_prop) {
		this.oggetto_prop = oggetto_prop;
	}

	public String getNumero_delibera() {
		return numero_delibera;
	}

	public void setNumero_delibera(String numero_delibera) {
		this.numero_delibera = numero_delibera;
	}

	public String getRisultato_seduta() {
		return risultato_seduta;
	}

	public void setRisultato_seduta(String risultato_seduta) {
		this.risultato_seduta = risultato_seduta;
	}

	public boolean isDelibera_prodotta() {
		return delibera_prodotta;
	}

	public void setDelibera_prodotta(boolean delibera_prodotta) {
		this.delibera_prodotta = delibera_prodotta;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public boolean isDelib_seduta_stante() {
		return delib_seduta_stante;
	}

	public void setDelib_seduta_stante(boolean delib_seduta_stante) {
		this.delib_seduta_stante = delib_seduta_stante;
	}

	public String getNota_risultato() {
		return nota_risultato;
	}

	public void setNota_risultato(String nota_risultato) {
		this.nota_risultato = nota_risultato;
	}

	public boolean isFollowingSibling() {
		return followingSibling;
	}

	public void setFollowingSibling(boolean followingSibling) {
		this.followingSibling = followingSibling;
	}

	public boolean isPrecedingSibling() {
		return precedingSibling;
	}

	public void setPrecedingSibling(boolean precedingSibling) {
		this.precedingSibling = precedingSibling;
	}

	public String getCommento_comunicazioni() {
		return commento_comunicazioni;
	}

	public void setCommento_comunicazioni(String commento_comunicazioni) {
		this.commento_comunicazioni = commento_comunicazioni;
	}

	public String getPosField() {
		return posField;
	}

	public void setPosField(String posField) {
		this.posField = posField;
	}

	public String getRadioField() {
		return radioField;
	}

	public void setRadioField(String radioField) {
		this.radioField = radioField;
	}
}
