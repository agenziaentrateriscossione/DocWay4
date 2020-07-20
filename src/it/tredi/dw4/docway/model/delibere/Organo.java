package it.tredi.dw4.docway.model.delibere;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Organo extends XmlEntity {
	
	private String nome = "";
	private String cod;
	private String indice;
	private boolean visualizza;
	private boolean edita;
	private boolean nuoveProposte;
	private String sel = "";
	private String db = "";
	
	private SedutaUtile sedutaUtile = null;
	
	private List<VaschettaDelibere> vaschettePersonali = new ArrayList<VaschettaDelibere>();
	private List<VaschettaDelibere> vaschetteUfficio = new ArrayList<VaschettaDelibere>();
	
	private XwFileModello xwfile; 

	@Override
	public XmlEntity init(Document dom) {
		this.setNome(XMLUtil.parseElement(dom, "organo"));
		this.setCod(XMLUtil.parseAttribute(dom, "organo/@cod"));
		this.setIndice(XMLUtil.parseAttribute(dom, "organo/@indice"));
		this.setVisualizza(Boolean.parseBoolean(XMLUtil.parseAttribute(dom, "organo/@visualizza")));
		this.setEdita(Boolean.parseBoolean(XMLUtil.parseAttribute(dom, "organo/@edita")));
		this.setNuoveProposte(Boolean.parseBoolean(XMLUtil.parseAttribute(dom, "organo/@nuoveProposte")));
		
		this.setSedutaUtile((SedutaUtile) XMLUtil.parseElement(dom, "organo/seduta_utile", new SedutaUtile()));
		
		this.xwfile = new XwFileModello(XMLUtil.parseAttribute(dom, "node()[name()='xw:file']/@name"), XMLUtil.parseAttribute(dom, "node()[name()='xw:file']/@title"));
		
		this.setSel(XMLUtil.parseAttribute(dom, "organo/@sel"));
		this.setDb(XMLUtil.parseAttribute(dom, "organo/@db"));
		
		initVaschette(dom);
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//inizializzo le vaschette
	private void initVaschette(Document dom) {
		vaschettePersonali = new ArrayList<VaschettaDelibere>();
		vaschetteUfficio = new ArrayList<VaschettaDelibere>();
		
		VaschettaDelibere vaschettaRPA = (VaschettaDelibere)XMLUtil.parseElement(dom, "/organo/RPA_props", new VaschettaDelibere());
		VaschettaDelibere vaschettaCC = (VaschettaDelibere)XMLUtil.parseElement(dom, "/organo/CC_props", new VaschettaDelibere());
		VaschettaDelibere vaschettaUOR = (VaschettaDelibere)XMLUtil.parseElement(dom, "/organo/UOR_props", new VaschettaDelibere());
		VaschettaDelibere vaschettaUORCC = (VaschettaDelibere)XMLUtil.parseElement(dom, "/organo/UORCC_props", new VaschettaDelibere());
		
		if(vaschettaRPA != null) vaschettePersonali.add(vaschettaRPA);
		if(vaschettaCC != null) vaschettePersonali.add(vaschettaCC);
		if(vaschettaUOR != null) vaschetteUfficio.add(vaschettaUOR);
		if(vaschettaUORCC != null) vaschetteUfficio.add(vaschettaUORCC);
	}

	/*
	 * getter / setter
	 * */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getIndice() {
		return indice;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}

	public boolean isVisualizza() {
		return visualizza;
	}

	public void setVisualizza(boolean visualizza) {
		this.visualizza = visualizza;
	}

	public boolean isEdita() {
		return edita;
	}

	public void setEdita(boolean edita) {
		this.edita = edita;
	}

	public boolean isNuoveProposte() {
		return nuoveProposte;
	}

	public void setNuoveProposte(boolean nuoveProposte) {
		this.nuoveProposte = nuoveProposte;
	}

	public SedutaUtile getSedutaUtile() {
		return sedutaUtile;
	}

	public void setSedutaUtile(SedutaUtile sedutaUtile) {
		this.sedutaUtile = sedutaUtile;
	}

	public List<VaschettaDelibere> getVaschettePersonali() {
		return vaschettePersonali;
	}

	public void setVaschettePersonali(List<VaschettaDelibere> vaschettePersonali) {
		this.vaschettePersonali = vaschettePersonali;
	}

	public List<VaschettaDelibere> getVaschetteUfficio() {
		return vaschetteUfficio;
	}

	public void setVaschetteUfficio(List<VaschettaDelibere> vaschetteUfficio) {
		this.vaschetteUfficio = vaschetteUfficio;
	}

	public XwFileModello getXwfile() {
		return xwfile;
	}

	public void setXwfile(XwFileModello xwfile) {
		this.xwfile = xwfile;
	}

	public String getSel() {
		return sel;
	}

	public void setSel(String sel) {
		this.sel = sel;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

}
