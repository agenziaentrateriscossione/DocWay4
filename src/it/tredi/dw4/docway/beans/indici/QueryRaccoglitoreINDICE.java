package it.tredi.dw4.docway.beans.indici;

import org.dom4j.Document;

import it.tredi.dw4.docway.beans.QueryRaccoglitore;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Ricerca (query) di raccoglitori custom di tipo indice
 */
public class QueryRaccoglitoreINDICE extends QueryRaccoglitore {
	
	private String descrizione = "";
	private String codice = "";

	public QueryRaccoglitoreINDICE() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		descrizione	= XMLUtil.parseStrictAttribute(dom, "/response/@descrizione_racc");
		codice	= XMLUtil.parseStrictAttribute(dom, "/response/@codice_racc");
	}
	
	/**
	 * Inserimento di un nuovo raccoglitore di tipo Indice
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insTableDocRaccoglitore() throws Exception {
		getFormsAdapter().insTableDocRaccoglitoreIndice(codice, descrizione); 

		XMLDocumento responseDoc = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, responseDoc, this.isPopupPage());
	}
	
	/**
	 * Creazione della query di ricerca raccoglitori su eXtraWay in base ai 
	 * parametri specificati dall'operatore
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String createQuery() throws Exception {
		String query = super.createQuery();
		
		if (codice != null && !codice.isEmpty()) {
			if (!query.isEmpty())
				query += " AND ";
			query += "([/raccoglitore/tipologia/@cod/]=\"" + codice + "\")";
		}
		
		return query;
	}
	
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
}
