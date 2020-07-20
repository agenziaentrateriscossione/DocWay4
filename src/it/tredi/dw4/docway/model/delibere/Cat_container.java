package it.tredi.dw4.docway.model.delibere;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Cat_container extends XmlEntity {
	private String cod = "";
	private String cat_container = "";
	private List<PropostaOdg> proposte;// = new ArrayList<PropostaOdg>();
	private String tipo = "";

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.setCod(XMLUtil.parseAttribute(dom, "cat_container/@cod"));
		this.setCat_container(XMLUtil.parseElement(dom, "cat_container"));
		this.proposte = (List<PropostaOdg>)XMLUtil.parseSetOfElement(dom, "cat_container/proposta", new PropostaOdg());
		checkPropostaSiblings();
		if(this.proposte != null && this.proposte.get(0) != null)
		{
			this.tipo = this.proposte.get(0).getTipo();
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * setta le opzioni per lo spostamento su/giu delle proposte
	 * */
	private void checkPropostaSiblings(){
		if (this.proposte != null && this.proposte.size() > 1)
		{
			//il primo ha solo fratelli-discendenti
			this.proposte.get(0).setFollowingSibling(true);
			
			//gli elementi centrali hanno fratelli-discendenti/precedenti
			for (int i=1; i<this.proposte.size()-1;i++)
			{
				this.proposte.get(i).setFollowingSibling(true);
				this.proposte.get(i).setPrecedingSibling(true);
			}
			
			//l'ultimo ha solo fratelli-precedenti
			this.proposte.get(this.proposte.size()-1).setPrecedingSibling(true);
		}
	}
	
	public PropostaOdg getLast(){
		if(this.proposte.size() > 0)
			return this.proposte.get(this.proposte.size() - 1);
		else
			return null;
	}
	
//	public PropostaOdg getPropostaByNRecord(String nrecord_prop){
//		for(PropostaOdg proposta : proposte){
//			if(proposta.getNrecord_prop().equals(nrecord_prop))
//				return proposta;
//		}
//		return null;
//	}
	
	
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getCat_container() {
		return cat_container;
	}

	public void setCat_container(String cat_container) {
		this.cat_container = cat_container;
	}

	public List<PropostaOdg> getProposte() {
		return proposte;
	}

	public void setProposte(List<PropostaOdg> proposte) {
		this.proposte = proposte;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
