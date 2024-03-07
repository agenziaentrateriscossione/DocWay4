package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.Appartenenza;
import it.tredi.dw4.acl.model.PersonaEsterna;
import it.tredi.dw4.acl.model.Responsabilita;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocPersonaEsterna extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private PersonaEsterna persona_esterna;
	
	private String xml;
	
	private String label_dati_attivita = "";
	private String label_dati_personali = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getLabel_dati_attivita() {
		return label_dati_attivita;
	}

	public void setLabel_dati_attivita(String label_dati_attivita) {
		this.label_dati_attivita = label_dati_attivita;
	}

	public String getLabel_dati_personali() {
		return label_dati_personali;
	}

	public void setLabel_dati_personali(String label_dati_personali) {
		this.label_dati_personali = label_dati_personali;
	}

	public ShowdocPersonaEsterna() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	persona_esterna = new PersonaEsterna();
    	persona_esterna.init(domDocumento.getDocument());
    	
    	// verifica che per tutte le strutture di appertenenza sia specificato il nome
    	// della struttura. In caso di ruoli multipli su stessa struttura solo la prima istanza
    	// della struttura contiene il nome, le altre solo il codice
    	if (persona_esterna.getAppartenenza() != null 
    			&& persona_esterna.getAppartenenza().size() > 1) {
    		for (int i=0; i<persona_esterna.getAppartenenza().size(); i++) {
    			if (persona_esterna.getAppartenenza().get(i) != null && persona_esterna.getAppartenenza().get(i).getNome() != null 
    					&& persona_esterna.getAppartenenza().get(i).getNome().length() == 0 && persona_esterna.getAppartenenza().get(i).getCod_uff().length() > 0) {
    				// struttura senza nome
    				int j = 0;
    				boolean found = false;
    				while (j<persona_esterna.getAppartenenza().size() && !found) {
    					if (persona_esterna.getAppartenenza().get(j) != null && persona_esterna.getAppartenenza().get(j).getCod_uff() != null 
    							&& persona_esterna.getAppartenenza().get(j).getCod_uff().equals(persona_esterna.getAppartenenza().get(i).getCod_uff())) {
    						persona_esterna.getAppartenenza().get(i).setNome(persona_esterna.getAppartenenza().get(j).getNome());
    						persona_esterna.getAppartenenza().get(i).setPhysDoc(persona_esterna.getAppartenenza().get(j).getPhysDoc());
    						found = true;
    					}
    					j++;
    				}
    			}
    		}
    	}
    	
    	label_dati_attivita = XMLUtil.parseStrictAttribute(domDocumento, "/response/@dicitDatiAttivita");
    	label_dati_personali = XMLUtil.parseStrictAttribute(domDocumento, "/response/@dicitDatiPersonali");
    	
    	// inizializzazione di componenti common
    	initCommons(domDocumento);
    }	
	
	public AclDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void reload(ComponentSystemEvent event) throws Exception {
		reload();
	}
	
	public void reload() throws Exception {
		// Richiamo il _reload generico perchè non è detto che debba essere caricata la showdoc
		// della persona esterna (in caso di inserimento da struttura esterna viene caricata la 
		// showdoc della struttura esterna)
		super._reload();
		//super._reload("showdoc@persona_esterna");
	}

	public void setPersona_esterna(PersonaEsterna persona_esterna) {
		this.persona_esterna = persona_esterna;
	}

	public PersonaEsterna getPersona_esterna() {
		return persona_esterna;
	}
	
	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("persona_esterna"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		DocEditPersonaEsterna docEditPersonaEsterna = new DocEditPersonaEsterna();
		docEditPersonaEsterna.getFormsAdapter().fillFormsFromResponse(response);
		docEditPersonaEsterna.init(response.getDocument());
		setSessionAttribute("docEditPersonaEsterna", docEditPersonaEsterna);
		
		return "docEdit@persona_esterna";
	}
	
	public String navigateResponsabilita() throws Exception{
		Responsabilita responsabilita = (Responsabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("responsabilita");
		int physDoc = (null != responsabilita.getPhysDoc() && responsabilita.getPhysDoc().trim().length() > 0 ) ? Integer.valueOf(responsabilita.getPhysDoc()) : -1;
		
		return loadStructure(physDoc);
	}
	
	public String navigateStructure() throws Exception{
		Appartenenza appartenenza = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int physDoc = (null != appartenenza.getPhysDoc() && appartenenza.getPhysDoc().trim().length() > 0 ) ? Integer.valueOf(appartenenza.getPhysDoc()) : -1;
		
		return loadStructure(physDoc);
	}
	
	private String loadStructure(int physDoc) throws Exception {
		formsAdapter.seekDoc(physDoc, true);
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		formsAdapter.fillFormsFromResponse(response);
		
		ShowdocStrutturaEsterna showdocStrutturaEsterna = new ShowdocStrutturaEsterna();
		showdocStrutturaEsterna.getFormsAdapter().fillFormsFromResponse(response);
		showdocStrutturaEsterna.init(response.getDocument());
		setSessionAttribute("showdocStrutturaEsterna", showdocStrutturaEsterna);
		
		return "showdoc@struttura_esterna";
	}
}
