package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocumentFormsAdapter;

public class DocWayDocumentFormsAdapter extends DocumentFormsAdapter {

	public DocWayDocumentFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		Element root = response.getRootElement();
		
		//visualizzazione di contenuto di fascicolo
		defaultForm.addParam("num_fasc", root.attributeValue("num_fasc", ""));
		defaultForm.addParam("classif_fasc", root.attributeValue("classif_fasc", ""));
		defaultForm.addParam("physDoc_fasc", root.attributeValue("physDoc_fasc", ""));
		defaultForm.addParam("oggetto_fasc", root.attributeValue("oggetto_fasc", ""));
		defaultForm.addParam("nome_persona_fasc", root.attributeValue("nome_persona_fasc", ""));
		defaultForm.addParam("nome_uff_fasc", root.attributeValue("nome_uff_fasc", ""));
		
		//trasmetto alla pagina dei titoli dei documenti di un fascicolo l'informazione	sull'archiviazione del fascicolo
		defaultForm.addParam("archiviato_fasc", root.attributeValue("archiviato_fasc", ""));
		
		//inserimento in un fascicolo
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("diritto_infasc", root.attributeValue("diritto_infasc", ""));
		defaultForm.addParam("nome_persona_infasc", root.attributeValue("nome_persona_infasc", ""));
		defaultForm.addParam("nome_uff_infasc", root.attributeValue("nome_uff_infasc", ""));
		defaultForm.addParam("cod_persona_infasc", root.attributeValue("cod_persona_infasc", ""));
		defaultForm.addParam("cod_uff_infasc", root.attributeValue("cod_uff_infasc", ""));
		
		//introdotto tipo del valore contenuto nei campi ufficio
		defaultForm.addParam("tipo_uff_infasc", root.attributeValue("tipo_uff_infasc", ""));
		
		defaultForm.addParam("classif_infasc", root.attributeValue("classif_infasc", ""));
		defaultForm.addParam("classif_cod_infasc", root.attributeValue("classif_cod_infasc", ""));
		defaultForm.addParam("soggetto_infasc", root.attributeValue("soggetto_infasc", ""));
		
		//Bottone assegnazione link fascicolo-documento
		defaultForm.addParam("bAssegnaLinkFasc", root.attributeValue("bAssegnaLinkFasc", ""));
		
		//Inserimento/cancellazione di un fascicolo collegato
		defaultForm.addParam("num_linkFasc", root.attributeValue("num_linkFasc", ""));
		
		//inserimento di una persona interna in una struttura interna
		defaultForm.addParam("physDoc_struint", root.attributeValue("physDoc_struint", ""));
		defaultForm.addParam("selid_struint", root.attributeValue("selid_struint", ""));
		defaultForm.addParam("pos_struint", root.attributeValue("pos_struint", ""));
		
		//per editing della gerarchia
		defaultForm.addParam("physDocOrigine", "'0'");
		defaultForm.addParam("relType", "");
//defaultForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
		
		//per la gestione delle bozze
		defaultForm.addParam("bozzaDaProt","");
		defaultForm.addParam("pne", "");
		defaultForm.addParam("pnce", "");
		
		defaultForm.addParam("stampaSegnatura", response.getElementText("//stampa_segnatura", ""));
		defaultForm.addParam("stampaInfo", response.getElementText("//stampa_info", ""));
		
		defaultForm.addParam("interventoConfigurabileSuDoc", (null != this.funzionalitaDisponibili.get("interventoConfigurabileSuDoc")) ? this.funzionalitaDisponibili.get("interventoConfigurabileSuDoc").toString() : "");
		defaultForm.addParam("interventoConfigurabileSuMinuta", (null != this.funzionalitaDisponibili.get("interventoConfigurabileSuMinuta")) ? this.funzionalitaDisponibili.get("interventoConfigurabileSuMinuta").toString():"");
		
		if (root.attributeValue("dbTable", "").equals("@reload"))
			defaultForm.addParam("enableSegnaturaFD", (null!=this.funzionalitaDisponibili.get("enableSegnatura"))?this.funzionalitaDisponibili.get("enableSegnatura").toString():"");
		
		defaultForm.addParam("SITesto", root.attributeValue("SITesto", ""));
		
		//pagina intermedia per testare se IW e' presente (entrata da email per visualizzare i doc. notificati in modo differito).
		defaultForm.addParam("urlToLaunch", root.attributeValue("urlToLaunch", ""));
		
		//Gerarchia nei fascicoli
		defaultForm.addParam("fascicoli_MostraGerarchia", root.attributeValue("fascicoli_MostraGerarchia", ""));
		defaultForm.addParam("fascicoli_MostraGerarchia_selIdOriginale", root.attributeValue("fascicoli_MostraGerarchia_selIdOriginale", ""));
		
		//per le sedute
		if (root.attributeValue("dbTable", "").equals("@seduta"))
		defaultForm.addParam("maxPosRis", "129");
	}
	
	public void removeFromRac(String nrecordRac, String nrecordDoc){
	    //setGlobalFormRestore('verbo','xverb');

		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@removeRacc@" + nrecordRac + ";" + nrecordDoc);
	}
	
	public void removeRifInt(String cod_persona, String cod_uff, String diritto) {
		//TODO: Verificare i campi vuoti nel metodo chiamante
//		if ( cod_persona == "" || cod_uff == "" || diritto == "" ) {
//			alert("Si è verificato un errore");
//			return false;
//		}

//	    setGlobalFormRestore('verbo','xverb','rifInt');

		defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|'+ diritto);
		defaultForm.addParam("verbo", "rifint_response");
		defaultForm.addParam("xverb", "@removeRif");
	}
	
	/**
	 * eliminazione di un CC di un fascicolo
	 * @param cod_persona
	 * @param cod_uff
	 * @param diritto
	 */
	public void removeRifIntCConFascicolo(String cod_persona, String cod_uff, String diritto) {
		defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|'+ diritto);
		defaultForm.addParam("verbo", "rifint_response");
		defaultForm.addParam("xverb", "@removeCCFascicolo");
	}
	
	public void scartaDoc(String cod_persona, String cod_uff, String diritto){
		//TODO: Verificare i campi vuoti nel metodo chiamante
//		if ( cod_persona == "" || cod_uff == "" ) {
//			alert("Si è verificato un errore");
//			return false;
//		}
//	    setGlobalFormRestore('verbo','xverb','rifInt');

		defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|' + diritto);
		defaultForm.addParam("verbo", "rifint_response");
		defaultForm.addParam("xverb", "@scartaDoc");
	}
	
	public void showHistory(String ordinamento) throws Exception
	{
		defaultForm.addParam("verbo", "storia_response");
		defaultForm.addParam("xverb", "@" + ordinamento);

		if ( defaultForm.getParam("oldTable").length() == 0 )
			defaultForm.addParam("oldTable", defaultForm.getParam("dbTable"));
		
	}
	
	/* DD 18/04/2006 Copiato da document.js (RW: 0006138)  */
	public void fascContent(String numfasc, String nome_persona, String nome_uff, String classif, String oggetto){
//	    setGlobalFormRestore('verbo','xverb','num_fasc','oggetto_fasc','nome_persona_fasc','nome_uff_fasc','classif_fasc','selid');

		defaultForm.addParam("verbo", "queryplain");

		//in maniera 'maldestra' si passano le informazioni sul fascicolo in xverb
		defaultForm.addParam("xverb", "@fasccontent");

		defaultForm.addParam("num_fasc", numfasc);
		defaultForm.addParam("oggetto_fasc", oggetto);
		defaultForm.addParam("nome_persona_fasc", nome_persona);
		defaultForm.addParam("nome_uff_fasc", nome_uff);
		defaultForm.addParam("classif_fasc", classif);

	    defaultForm.addParam("selid", "");
		//DD 29/10/2005 - Anche i fascicoli collegati devono apparire
		String query = "[doc_rifinternirifcodfasc]=" + numfasc + " OR [doc_fascicolocollegato]=" + numfasc;
		defaultForm.addParam("query", query);
	}
	
	//Da visualizzazione documento, rimuove il fascicolo collegato.
	public void removeLinkFasc(String numFasc){
//		setGlobalFormRestore('verbo','xverb');
//		iForm = getForm("hxpForm");
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@removeLinkFasc");
		defaultForm.addParam("num_linkFasc", numFasc);
//		iForm['num_linkFasc']", numFasc;
	}
	
	public void downloadFile(String id, String name) throws Exception {
		this.defaultForm.addParam("verbo", "attach");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("id", id);
		this.defaultForm.addParam("name", name);
	}
	
	public void verificaFirma(String id, String name) throws Exception {
		this.defaultForm.addParam("verbo", "showdoc");
		this.defaultForm.addParam("xverb", "verificaFirmaVOL");
		this.defaultForm.addParam("fileid", id);
		this.defaultForm.addParam("filename", name);
	}

	public void removeFromFasc(){
//	    setGlobalFormRestore('verbo','xverb');
		this.defaultForm.addParam("verbo", "fascicolo_response");
		this.defaultForm.addParam("xverb", "@removeFascRPA");
	}


	public void removeFromFascMinuta(){
//	    setGlobalFormRestore('verbo','xverb');
		this.defaultForm.addParam("verbo", "fascicolo_response");
		this.defaultForm.addParam("xverb", "@removeFascRPAM");
	}

	public void queryFasc(String num_fasc){
//	    setGlobalFormRestore('verbo','xverb','query','selid');
		this.defaultForm.addParam("verbo", "queryplain");
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("query", "[fasc_numero]=" + num_fasc);
	    //modifica di mbussetti del 22/02/2005 - RW26452
	    if (this.defaultForm.getParam("xverb").equals("fasccontent")) this.defaultForm.addParam("xverb", "");
	}
	
	public void assegnaIntervento(String cod_persona, String cod_uff, String diritto){
	    //TODO: Controllare gli elementi vuoti
//		if ( cod_persona == "" || cod_uff == "" || diritto == '') {
//	        alert("Si è verificato un errore");
//	        return false;
//	    }
//	    setGlobalFormRestore('verbo','xverb','rifInt');

	    this.defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|' + diritto);
	    this.defaultForm.addParam("verbo", "rifint_response");
	    this.defaultForm.addParam("xverb", "@assegnaIntervento");
	}
	
	/**
	 * assegnazione di intervento ai CC di un fascicolo
	 * @param cod_persona
	 * @param cod_uff
	 * @param diritto
	 */
	public void assegnaInterventoCConFascicolo(String cod_persona, String cod_uff, String diritto){
	    this.defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|' + diritto);
	    this.defaultForm.addParam("verbo", "rifint_response");
	    this.defaultForm.addParam("xverb", "@assegnaInterventoCCFascicolo");
	}

	public void rimuoviIntervento(String cod_persona, String cod_uff, String diritto){
	    //TODO: Controllare gli elementi vuoti
//		if ( cod_persona == "" || cod_uff == "" || diritto == '') {
//	        alert("Si è verificato un errore");
//	        return false;
//	    }
//	    setGlobalFormRestore('verbo','xverb','rifInt');

	    this.defaultForm.addParam("rifInt", cod_persona + "|" + cod_uff + "|" + diritto);
	    this.defaultForm.addParam("verbo", "rifint_response");
	    this.defaultForm.addParam("xverb", "@rimuoviIntervento");
	}
	
	/**
	 * eliminazione di intervento ai CC di un fascicolo
	 * @param cod_persona
	 * @param cod_uff
	 * @param diritto
	 */
	public void rimuoviInterventoCConFascicolo(String cod_persona, String cod_uff, String diritto){
	    this.defaultForm.addParam("rifInt", cod_persona + '|' + cod_uff + '|' + diritto);
	    this.defaultForm.addParam("verbo", "rifint_response");
	    this.defaultForm.addParam("xverb", "@rimuoviInterventoCCFascicolo");
	}

	public void scartaRuoli(){
//	    setGlobalFormRestore('verbo','xverb','rifInt');

		this.defaultForm.addParam("verbo", "rifint_response");
		this.defaultForm.addParam("xverb", "@scartaRuoli");
	}

	public void delPostit(int postitPos){
//	        setGlobalFormRestore('verbo','xverb','postitPos');
		 this.defaultForm.addParam("verbo", "postit_response");
		 this.defaultForm.addParam("xverb", "remove");
		 this.defaultForm.addParam("postitPos", postitPos);
	}
	
	public void modPostit(int pos, String text){
		addPostit(pos, text);
	}
	
	public void addPostit(int pos, String text)	{
		addPostit(String.valueOf(pos), text);
	}
	
	public void addPostit(String pos, String text)	{
		this.defaultForm.addParam("verbo", "postit_response");
		this.defaultForm.addParam("xverb", "@addPage");

		this.defaultForm.addParam("postitPos", pos);
		this.defaultForm.addParam("postitText", text);
	}

	public void confirmPostit(String postitText)	{
//		il controllo sulla textarea lo deve fare il metodo chiamante
//		if ( getForm("theForm").postitTextArea.value.length == 0 )
//			postitMSG(noValField);
//		else {
		this.defaultForm.addParam("verbo", "postit_response");
		this.defaultForm.addParam("xverb", "@add");
		this.defaultForm.addParam("postitText", postitText);		
	}
	
	public void rigettaPostit(){
	    // e' richiesto l'inserimento di un postit
    	this.defaultForm.addParam("toDo", this.defaultForm.getParam("toDo")+"%doRigetta%");
    }
	
	public void rigetta(){
//	    setGlobalFormRestore('verbo','xverb');
		this.defaultForm.addParam("verbo", "rifint_response");
		this.defaultForm.addParam("xverb", "@rigetta");
	}
	
	public void openRifInt(String addType){
		openRifInt(addType, false);
	}
	public void openRifInt(String addType, boolean isBozza){
		this.defaultForm.addParam("verbo", "rifint_response");
		this.defaultForm.addParam("xverb", "@openRif");
		this.defaultForm.addParam("rifInt", addType);
	    if (isBozza) this.defaultForm.addParam("toDo", "IS_BOZZA");
	}
	
	/**
	 * Verifica dell'impronta su tutto il documento (SHA1) o su un singolo file (nuova normativa
	 * basata su SHA256)
	 * @param fileid id del file sul quale verificare l'impronta (se null o vuoto, vecchia gestione con verifica dell'impronta dell'intero documento)
	 */
	public void checkImpronta(String fileid)	{
		this.defaultForm.addParam("verbo", "attach_response");
		this.defaultForm.addParam("xverb", "@checkImpronta");
		if (fileid != null && fileid.length() > 0)
			this.defaultForm.addParam("fileId", fileid);
	}
	
	public void insInRaccoglitore(String physDoc){
		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("dbTable", "@raccoglitore");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("selid", ""); // senza questo parametro non funzionerebbero i vocabolari sul form di ricerca
		this.defaultForm.addParam("physDoc_infasc", physDoc);
	    this.defaultForm.addParam("personalView", "");
	}
	
	public void agganciaWorkflow(){
		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("dbTable", "@workflow");
		this.defaultForm.addParam("xverb", "");
	    this.defaultForm.addParam("personalView", "");
	}

	public void ripetiNuovo(String name, String tipoOp){
//	    setGlobalFormRestore('verbo','xverb','pos','selid','dbTable');
	   this.defaultForm.addParam("verbo", "docEdit");
	   this.defaultForm.addParam("xverb", "@" + tipoOp);
	   this.defaultForm.addParam("pos", 0);
	   this.defaultForm.addParam("selid", "");
	   this.defaultForm.addParam("dbTable", '@' + name);
	}

	public void ripetiNuovoRep(String name, String tipoOp, String codRep, String descrRep)	{
		if ( null != codRep ) this.defaultForm.addParam("codice_rep", codRep);
		if ( null != descrRep ) this.defaultForm.addParam("descrizione_rep", descrRep);
		ripetiNuovo(name, tipoOp);
	}
	
	public void insInFasc(String diritto, String physDoc, String nome_persona, String nome_uff, String cod_persona, String cod_uff, String classif, String classif_cod, String soggetto, String tipo_uff){
//		var iForm;
//		var old_target = defaultForm.addParam("target;
//		var old_verbo = defaultForm.addParam("verbo.value;
//		var old_dbTable = defaultForm.addParam("dbTable.value;
//		var old_xverb = defaultForm.addParam("xverb.value;
//	    var old_personalView = defaultForm.addParam("personalView.value;

	    if (soggetto == null)
	        soggetto = "";

		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("dbTable", "@fascicolo");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("selid", ""); // senza questo parametro non funzionerebbero i vocabolari sul form di ricerca
		this.defaultForm.addParam("physDoc_infasc", physDoc);
		this.defaultForm.addParam("nome_persona_infasc", nome_persona);
		this.defaultForm.addParam("nome_uff_infasc", nome_uff);
		this.defaultForm.addParam("cod_persona_infasc", cod_persona);
		this.defaultForm.addParam("cod_uff_infasc", cod_uff);

		this.defaultForm.addParam("tipo_uff_infasc", tipo_uff);

		this.defaultForm.addParam("soggetto_infasc", soggetto);
		this.defaultForm.addParam("personalView", "");

		if ( !classif_cod.equals("00/00") ){
			this.defaultForm.addParam("classif_infasc", classif);
			this.defaultForm.addParam("classif_cod_infasc", classif_cod);
		}

		this.defaultForm.addParam("diritto_infasc", diritto);
	}
	
	/**
	 * Replica del documento
	 */
	public void replicaDoc(){
		replicaDoc(null, null);
	}
	
	/**
	 * Replica del documento con indicazione del repertorio del
	 * nuovo documento
	 */
	public void replicaDoc(String codRep, String descrRep){
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@replicadoc");
	    this.defaultForm.addParam("pos", 0);
	    this.defaultForm.addParam("selid", "");
	    this.defaultForm.addParam("dbTable", "@varie");
	    this.defaultForm.addParam("personalView",  "");
	    if (codRep != null && codRep.length() > 0) this.defaultForm.addParam("codice_rep", codRep);
		if (descrRep != null && descrRep.length() > 0) this.defaultForm.addParam("descrizione_rep", descrRep);
	}
	
	/**
	 * Trasformazione di un doc non protocollato in protocollo in partenza/tra uffici
	 * @param dbTable
	 */
	public void trasfNonProtocollato(String dbTable) {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@replicadoc");
	    this.defaultForm.addParam("pos", 0);
	    this.defaultForm.addParam("selid", "");
	    this.defaultForm.addParam("dbTable", dbTable);
	    this.defaultForm.addParam("personalView",  "");
	}
	
	/**
	 * Trasformazione di un doc in arrivo in bozza in documento non protocollato
	 */
	public void trasfInV() {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@trasfInV");
	}

	public void modifyTableDoc(){
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@modify");
	}

	public void modifyTableDoc(String codRep, String descrRep){
		//setGlobalFormRestore('verbo','xverb','codice_rep','descrizione_rep','dbTable');
		modifyTableDoc();
		this.defaultForm.addParam("codice_rep", codRep);
		this.defaultForm.addParam("descrizione_rep", descrRep);
	}
	
	/**
	 * personalizzazione della modifica del repertorio FPN / FPNALGERIA di Condotte
	 * @param codRep
	 * @param descrRep
	 * @param button
	 */
	public void modifyTableDocFPN(String codRep, String descrRep, String button) {
		modifyTableDoc(codRep, descrRep);
		
		this.defaultForm.addParam("verbo", "apps.xdocway.condotte.DocEditFPN");
		if (button == null || button.length() == 0)
			this.defaultForm.addParam("toDo", "__standard-modify__");
	}

	public void annullaDoc(){
		this.defaultForm.addParam("verbo", "documento_response");
		this.defaultForm.addParam("xverb", "@openAnnullaPage");
	}
	
	public void abrogaDoc(){
		this.defaultForm.addParam("verbo", "documento_response");
		this.defaultForm.addParam("xverb", "@openAbrogaPage");
	}
	
	public void insLinkFasc(String physDoc, String nome_persona, String nome_uff, String cod_persona, String cod_uff, String classif, String classif_cod, String soggetto, String tipo_uff)
	{

		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("dbTable", "@fascicolo");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("selid", ""); // senza questo parametro non funzionerebbero i vocabolari sul form di ricerca

		this.defaultForm.addParam("bAssegnaLinkFasc", "true");
		this.defaultForm.addParam("physDoc_infasc", -1);

		this.defaultForm.addParam("personalView", "");
		this.defaultForm.addParam("nome_persona_infasc", nome_persona);
		this.defaultForm.addParam("nome_uff_infasc", nome_uff);
		this.defaultForm.addParam("cod_persona_infasc", cod_persona);
		this.defaultForm.addParam("cod_uff_infasc", cod_uff);

		this.defaultForm.addParam("tipo_uff_infasc", tipo_uff);

		if ( !classif_cod.equals("00/00") ) {
			this.defaultForm.addParam("classif_infasc", classif);
			this.defaultForm.addParam("classif_cod_infasc", classif_cod);
		}
		this.defaultForm.addParam("soggetto_infasc", soggetto);
	}
	
	public void copyLink(String nrecord) {
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("copyLink", nrecord, defaultForm.getParam(customTupleName)));
	}
	
	public void pasteLink() {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@pasteLink");
	}
	
	public void removeInternalLink(String nrecord) {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@removeLink");
		this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("removelLink", nrecord, defaultForm.getParam(customTupleName)));
	}
	
	public void protocolla(boolean isRepertorio, String newPne, String newPnce, String codRep, String descrRep)	{
//	    setGlobalFormRestore('bozzaDaProt','pne','pnce','verbo','xverb','codice_rep','descrizione_rep');

		this.defaultForm.addParam("bozzaDaProt", "DOC");
		if (isRepertorio)
			this.defaultForm.addParam("bozzaDaProt", this.defaultForm.getParam("bozzaDaProt")+".REP") ;
		
		this.defaultForm.addParam("pne", newPne); //'doc';
	    this.defaultForm.addParam("pnce", newPnce); //'list_of_doc';
	    this.defaultForm.addParam("verbo", "docEdit");
	    this.defaultForm.addParam("xverb", "@save");
	    
	    if (isRepertorio) {
	    	if ( null != codRep && codRep.trim().length()>0 )
	    		this.defaultForm.addParam("codice_rep", codRep);
	    	
	    	if ( null != descrRep && descrRep.trim().length() > 0)
	    		this.defaultForm.addParam("descrizione_rep", descrRep);
	    }
	}
	
	// COMMENTATA PERCHE' NON DOVREBBE ESSERE NECESSARIA
	/*public void protocolla(String newPne, String newPnce)	{
//	    setGlobalFormRestore('bozzaDaProt','pne','pnce','verbo','xverb','codice_rep','descrizione_rep');

	    //if ( this.defaultForm.getParam("bozzaDaProt").length()>0 ) {
	    	this.defaultForm.addParam("bozzaDaProt", "DOC") ;
	    //}
	    this.defaultForm.addParam("pne", newPne); //'doc';
	    this.defaultForm.addParam("pnce", newPnce); //'list_of_doc';
	    this.defaultForm.addParam("verbo", "docEdit");
	    this.defaultForm.addParam("xverb", "@save");
	}*/
	
	public void stampaRicevutaSenzaIW(){
		this.defaultForm.addParam("verbo", "stampa_ricevuta");
	    
	    if ( this.defaultForm.getParam("selid").length() == 0 ) //occorre aprire in base a # fisico
	    	this.defaultForm.addParam("pos", this.defaultForm.getParamAsInt("physDoc") - 1);
	}
	
	public void stampaSegnaturaSenzaIW(boolean save, String prefix){
		this.defaultForm.addParam("xverb", "@stampaSegnaturaSenzaIW");
	    if (save)
	    	this.defaultForm.addParam("xverb", this.defaultForm.getParam("xverb") +"_save");
	    if (prefix != null && prefix.trim().length()>0)
	        this.defaultForm.addParam("xverb", this.defaultForm.getParam("xverb") + "|" + prefix.toLowerCase());

	    if ( this.defaultForm.getParam("selid").length() == 0 ) //occorre aprire in base a # fisico
	    	this.defaultForm.addParam("pos", this.defaultForm.getParamAsInt("physDoc") - 1);
	}
	
	public void fotoOriginale() {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@cercaProtocolloPhysDoc");
		this.defaultForm.addParam("dbTable", "@acquisizione");
	}
	
	/**
	 * repertorio "Richiesta Pubblicazione AOL". da doc protocollato generazione della richiesta
	 * di pubblicazione Albo Online
	 */
	public void richiestaPubblicazioneInAlboOnline() {
		this.defaultForm.addParam("codice_rep", "RAOL");
		this.defaultForm.addParam("descrizione_rep", "Richiesta Pubblicazione AOL");    
	    
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@replicadoc");
		this.defaultForm.addParam("pos", 0);
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("dbTable", "@varie");
		this.defaultForm.addParam("personalView", "");
	}
	
	/**
	 * repertorio "Richiesta Pubblicazione AOL". pubblicazione in albo online (generazione di un doc
	 * varie di repertorio "Albo Online")
	 */
	public void pubblicaInAlboOnline() {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@pubblicaInAlboOnLine");
	}

	public void publishWithThePublisher() {
//	    if (!confirm('Confermare la pubblicazione?\nLa pubblicazione potrebbe richiedere alcuni minuti prima di essere effettiva.\nEvitare di richiedere più volte la pubblicazione.'))
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@publishWithThePublisher");
	}

	public void salvaSegnatura(){
//	    setGlobalFormRestore('xverb','selid','pos');
		this.defaultForm.addParam("xverb", "@salvaSegnatura");
		if ( this.defaultForm.getParam("selid").equals("") ){
			this.defaultForm.addParam("pos", this.defaultForm.getParamAsInt("physDoc") - 1);
		}
	}
	
	public void stampaSegnatura(boolean save, String prefix)	{
	    if ( prefix == null || prefix.trim().length() == 0)
	        prefix = "Segnatura";

		//if ( defaultForm.addParam("enableIW.value != 'true' )
		stampaSegnaturaSenzaIW(save, prefix);
	}
	
	public void doAssignFasc(String physDoc_doc, String physDoc_fasc){
//	    setGlobalFormRestore('verbo','xverb','selid','physDoc','physDoc_infasc');
		this.defaultForm.addParam("verbo", "fascicolo_response");
		this.defaultForm.addParam("xverb", "@add");
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("physDoc", physDoc_doc);
		this.defaultForm.addParam("physDoc_infasc", physDoc_fasc);
	}

	
	public void linkFasc(String numFasc){
//		setGlobalFormRestore('verbo','xverb');
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@linkFasc");
		this.defaultForm.addParam("num_linkFasc", numFasc);
	}
	
	/**
	 * Assegnazione di documenti (originali o minute) ad un fascicolo
	 * 
	 * @param physDoc_fasc physDoc del fascicolo nel quale inserire i documenti selezionati
	 */
	public void doAssignFascAll(String physDoc_fasc){
		//  il parametro insSelMInFasc e' memorizzato all'interno della tupla _cd
		
		String insSelMInFasc = getParameterFromCustomTupleValue("insSelMInFasc", this.defaultForm.getParam("_cd"));
		//String insSelMInFasc = this.defaultForm.getParam("insSelMInFasc");
		String rpam = "";
		if (insSelMInFasc != null && insSelMInFasc.equals("true"))
			rpam = "M";
		
		this.defaultForm.addParam("verbo", "fascicolo_response");
	    this.defaultForm.addParam("xverb", "@addAll" + rpam);
	    this.defaultForm.addParam("physDoc_infasc", physDoc_fasc);
	    this.defaultForm.addParam("dbTable", "@if");

//	    hxpS.setParameter("insSelMInFasc", null);

	}
	
	public void insertSottoFasc(String numero, String physDoc, String nome_persona, String nome_uff, String cod_persona, String cod_uff, String classif, String classif_cod, String tipo_uff, String codiceTipologia, String descrTipologia){
//	    setGlobalFormRestore('verbo','xverb','physDoc','pos','selid','fasc_numero_sottofasc',
//	            'physDoc_sottofasc','nome_persona_sottofasc','nome_uff_sottofasc','cod_persona_sottofasc',
//	            'cod_uff_sottofasc','classif_sottofasc','classif_cod_sottofasc', 'tipo_uff_sottofasc');

		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("physDoc", 0);
		this.defaultForm.addParam("pos", 0);
		this.defaultForm.addParam("selid", "");

		this.defaultForm.addParam("fasc_numero_sottofasc", numero);
		this.defaultForm.addParam("physDoc_sottofasc", physDoc);
		this.defaultForm.addParam("nome_persona_sottofasc", nome_persona);
		this.defaultForm.addParam("nome_uff_sottofasc", nome_uff);
		this.defaultForm.addParam("cod_persona_sottofasc", cod_persona);
		this.defaultForm.addParam("cod_uff_sottofasc", cod_uff);

		this.defaultForm.addParam("tipo_uff_sottofasc", tipo_uff);

	    this.defaultForm.addParam("classif_sottofasc", classif);
		this.defaultForm.addParam("classif_cod_sottofasc", classif_cod);
		this.defaultForm.addParam("codice_fasc", codiceTipologia);
		this.defaultForm.addParam("descrizione_fasc", descrTipologia);
	}
	
	
	public void openClassifSpec(String operation){

		this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("operationToDo", operation, defaultForm.getParam(customTupleName)).replaceAll("&#38;", "&"));
		this.defaultForm.addParam("selid", "");

		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("dbTable", "@classifspec");

	    if ( !this.defaultForm.getParam("physDoc").equals("") ) {
	    	this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("physDocToUse", this.defaultForm.getParam("physDoc"), defaultForm.getParam(customTupleName)).replaceAll("&#38;", "&"));
	    	this.defaultForm.addParam("physDoc", "");
	    }
		// TODO Da completare
//		TODO Dopo il post risettare il valore di physDocToUse a null
//	    hxpS.setParameter("physDocToUse", null);
	}
	
	public void doRestoreClassif()	{
//	    if (!confirm('Confermare il ripristino della classificazione per il fascicolo corrente?'))
//	       return false;

		defaultForm.addParam("selid", "");

		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@RIPRISTINA_CLASSIF");
		defaultForm.addParam("dbTable","@ripristinaClassif");
	}
	
	public void openUrl(String value, String alias)
	{
        String aliasToUse = "doc_nrecord";
        if (null != alias && alias.length() > 0)
            aliasToUse = alias;

        defaultForm.addParam("verbo", "queryplain");
        defaultForm.addParam("xverb", "");
        defaultForm.addParam("query", "[" + aliasToUse + "]=" + value);
        defaultForm.addParam("selid", "");

	}

	public void ripetiNuovoSottoFasc(String template, String numero, int physDoc, String nome_persona, String nome_uff, String cod_persona, String cod_uff, String classif, String classif_cod, String tipo_uff)	{
	    if (numero.indexOf(".", numero.indexOf(".") + 1) == -1){
	        ripetiNuovo(template, "ripetinuovo");
	        return;
	    }

//	    setGlobalFormRestore('verbo','xverb','pos','selid','personalView','fasc_numero_sottofasc','physDoc_sottofasc',
//	            'nome_persona_sottofasc','nome_uff_sottofasc','cod_persona_sottofasc','cod_uff_sottofasc',
//	            'classif_sottofasc','classif_cod_sottofasc', 'tipo_uff_sottofasc');

	    defaultForm.addParam("verbo", "docEdit");
	    defaultForm.addParam("xverb", "ripetinuovo");
	    defaultForm.addParam("pos", 0);
	    defaultForm.addParam("selid", "");

	    defaultForm.addParam("fasc_numero_sottofasc", numero.substring(0, numero.lastIndexOf(".")));
	    defaultForm.addParam("physDoc_sottofasc", physDoc);
	    defaultForm.addParam("nome_persona_sottofasc", nome_persona);
	    defaultForm.addParam("nome_uff_sottofasc", nome_uff);
	    defaultForm.addParam("cod_persona_sottofasc", cod_persona);
	    defaultForm.addParam("cod_uff_sottofasc", cod_uff);

	    defaultForm.addParam("tipo_uff_sottofasc", tipo_uff);

	    defaultForm.addParam("classif_sottofasc", classif);
	    defaultForm.addParam("classif_cod_sottofasc", classif_cod);

	}

    public void doAssignRacAll(String physDoc_fasc){
    	defaultForm.addParam("verbo", "fascicolo_response");
    	defaultForm.addParam("xverb", "@addRacAll");
    	defaultForm.addParam("physDoc_infasc", physDoc_fasc);
    	defaultForm.addParam("dbTable", "@ir");
    }
    
    public void doAssignRac(String physDoc_doc, String physDoc_fasc) {
    	defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@addRac");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", physDoc_doc);
		defaultForm.addParam("physDoc_infasc", physDoc_fasc);
    }
    
    public void racContent(String nrecord, String nome_persona, String nome_uff, String oggetto){
        defaultForm.addParam("verbo", "queryplain");
        defaultForm.addParam("xverb", "@raccontent");
        defaultForm.addParam("num_fasc", "RAC_" + nrecord);
        defaultForm.addParam("oggetto_fasc", oggetto);
        defaultForm.addParam("nome_persona_fasc", nome_persona);
        defaultForm.addParam("nome_uff_fasc", nome_uff);
        defaultForm.addParam("selid", "");
    }

    public void queryRacc(String nrecord){
        defaultForm.addParam("verbo", "queryplain");
        defaultForm.addParam("selid", "");
        defaultForm.addParam("query", "[rac_nrecord]=" + nrecord);
        defaultForm.addParam("dbTable", "");
        if ("fasccontent".equals(defaultForm.getParam("xverb"))) defaultForm.addParam("xverb", "");
    }
    
    /**
     * Chiusura di un raccoglitore
     */
    public void archiviaRaccoglitore(){
    	defaultForm.addParam("verbo", "fascicoli_response");
    	defaultForm.addParam("xverb", "@archiviaRaccoglitore");
    }

    /**
     * Apertura di un raccoglitore
     */
    public void apriRaccoglitore(){
    	defaultForm.addParam("verbo", "fascicoli_response");
		defaultForm.addParam("xverb", "@apriRaccoglitore");
    }
    
    /**
     * Chiusura di un fascicolo
     */
    public void archiviaFascicolo(){
    	defaultForm.addParam("verbo", "fascicoli_response");
    	defaultForm.addParam("xverb", "@archivia");
    }

    /**
     * Apertura di un fascicolo
     */
    public void apriFascicolo(){
    	defaultForm.addParam("verbo", "fascicoli_response");
		defaultForm.addParam("xverb", "@apri");
    }
    
    /**
     * trasformazione di un documento protocollato/non protocollato in repertorio
     * @param cod codice del repertorio
     */
    public void trasformaInRep(String cod) {
    	defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@trasformaInRep|" + cod);
    }

    /**
     * caricamento di tutte le persone assegnate ad un ruolo
     * @param codRuolo codice del ruolo per il quale recuperare le persone
     */
    public void showPersoneRuolo(String codRuolo){
		this.defaultForm.addParam("verbo", "rifint_response");
		this.defaultForm.addParam("xverb", "@showPersoneRuolo|" + codRuolo);
	}
}
