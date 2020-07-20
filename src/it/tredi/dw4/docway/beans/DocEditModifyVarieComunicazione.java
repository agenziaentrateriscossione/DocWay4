package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.delibere.Categoria_select;
import it.tredi.dw4.docway.model.delibere.VarieComunicazione;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import java.util.List;

import org.dom4j.Document;

public class DocEditModifyVarieComunicazione extends DocEditModifyVarie {
	private VarieComunicazione doc = new VarieComunicazione();
	private Document clearDocument;
	
	private List<Categoria_select> listof_categorie;
	private String selectedCategoriaCod = "";
	private String personalViewToUse = "";
	private String categoriaLabel = "";
	
	public DocEditModifyVarieComunicazione() throws Exception {
		super();
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public void init(Document dom) {
		this.clearDocument = dom;
		this.doc = new VarieComunicazione();
		this.doc.init(dom);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(dom);
				
		this.listof_categorie		=	XMLUtil.parseSetOfElement(dom, "/response/doc/categoria_select", new Categoria_select());
		this.personalViewToUse		= 	XMLUtil.parseStrictAttribute(dom, "/response/@personalViewToUse");
		
		initSelectCategoria();
		
		String dicitCategoria 		=	getFormsAdapter().getParameterFromCustomTupleValue("dicitCategoria", formsAdapter.getCustomTupleName()); 
		if(!dicitCategoria.equals(""))
			this.categoriaLabel		= 	dicitCategoria.substring(0, dicitCategoria.indexOf("|"));
	}

	@Override
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return super.getFormsAdapter();
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			boolean isRepertorio = false;
			if (doceditRep)
				isRepertorio = true;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, isRepertorio));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//passo "varieComunicazione" perchè nella response non c'è la personalViewToUse settata...
			buildSpecificShowdocPageAndReturnNavigationRule("varieComunicazione", response);
			return "showdoc@varie@comunicazione@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@Override
	public boolean checkRequiredField() {
		boolean result = false;
		
		result = super.checkRequiredFieldCommon(false); // controlli comuni a tutte le tipologie di documenti
		
		if(doc.getCurrDate() == null || doc.getCurrDate().isEmpty())
		{
			result = true;
			this.setErrorMessageNoInputField("templateForm:currDate", I18N.mrs("dw4.occorre_valorizzare_la_data_del_documento"));
		}
		
		// Controllo sulla categoria
		if (checkCategoria())
			result = true;
		
		// Controllo che l'RPA sia stato selezionato (proponente)
		if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docRPAEreditabile", false)) {
		
			if (getDoc().getAssegnazioneRPA() == null || 
					((getDoc().getAssegnazioneRPA().getNome_uff() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_uff().trim())) &&
							(getDoc().getAssegnazioneRPA().getNome_persona() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_persona().trim())))) {
				
				String[] fieldIds = { "templateForm:rpa_nome_uff_input", "templateForm:rpa_nome_persona_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo") + " '" + I18N.mrs("dw4.proponente") + "'");
				result = true;
			}
		}
		
		//Controllo che il primo file allegato sia un .rtf
		if (super.checkPrimoAllegatoRTF("RTFComunicazioneObbligatorio"))
			result = true;
		
		return result;
	};
	
	/**
	 * controllo se la categoria scelta è concorde con la tipologia della proposta
	 * */
	private boolean checkCategoria(){
		if(!selectedCategoriaCod.isEmpty())
		{
			String cod = selectedCategoriaCod.substring(0, selectedCategoriaCod.indexOf("|"));
			String nome = selectedCategoriaCod.substring(selectedCategoriaCod.indexOf("|") + 1, selectedCategoriaCod.lastIndexOf("|"));
			String tipo = selectedCategoriaCod.substring(selectedCategoriaCod.lastIndexOf("|")+1);
			
			if(tipo.equals(personalViewToUse.substring(1))){
				doc.getDatiProposta().getCategoria().setCod(cod);
				doc.getDatiProposta().getCategoria().setNome(nome);
				return false;
			}
		}
		
		doc.getDatiProposta().getCategoria().setCod("");
		doc.getDatiProposta().getCategoria().setNome("");
		
		this.setErrorMessageNoInputField("templateForm:docCategoria", I18N.mrs("dw4.value") + " '" + getCategoriaLabel() + "' " + I18N.mrs("dw4.non_valido_per_questo_tipo_di_proposta"));
		
		return true;
	}
	
	// @ TODO
	@Override
	public String clearDocument() throws Exception {
		try {
			//getFormsAdapter().clearDocument(getCodiceRepertorio(),getDescrizioneRepertorio());
			//XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			this.selectedCategoriaCod = "";
			
			if (!isDocEditModify()) { 
				this.init(clearDocument);
				return "docEdit@varie@comunicazione";
			}else
			{
				getFormsAdapter().clearDocument();
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				buildSpecificShowdocPageAndReturnNavigationRule("varieComunicazione", response);
				return "showdoc@varie@comunicazione@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	private void initSelectCategoria(){
		for(Categoria_select categoria : listof_categorie)
			if(!categoria.getSelected().isEmpty())
			{
				this.selectedCategoriaCod = categoria.getCod();
				return;
			}
	}
	
	/*
	 * getter / setter
	 * */

	public List<Categoria_select> getListof_categorie() {
		return listof_categorie;
	}

	public void setListof_categorie(List<Categoria_select> listof_categorie) {
		this.listof_categorie = listof_categorie;
	}

	public String getCategoriaLabel() {
		return categoriaLabel;
	}

	public void setCategoriaLabel(String categoriaLabel) {
		this.categoriaLabel = categoriaLabel;
	}

	public String getSelectedCategoriaCod() {
		return selectedCategoriaCod;
	}

	public void setSelectedCategoriaCod(String selectedCategoriaCod) {
		this.selectedCategoriaCod = selectedCategoriaCod;
	}

	public VarieComunicazione getDoc() {
		return doc;
	}

	public void setDoc(VarieComunicazione doc) {
		this.doc = doc;
	}
	  
}