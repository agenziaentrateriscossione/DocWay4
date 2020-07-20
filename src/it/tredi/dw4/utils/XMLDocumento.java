package it.tredi.dw4.utils;

import it.tredi.dw4.adapters.FormAdapter;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Sottoinsieme di metodi della classe XMLDocumento presente in common4.jar. E' stata "copiata" per evitare di dover
 * caricare l'intero jar (problemi in fase di debug dell'applicazione service).
 * 
 * FIXME da rimuovere quando verranno caricati i progetti su maven e ristrutturati in modo da estrarre dal common una serie di classi di utils comuni a diversi progetti
 */
public class XMLDocumento implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -7560155229430114452L;

	public static final String ENCODING = FormAdapter.ENCODING_UFT_8; // encoding di output (xml formato stringa)
	
    public static final String XW_NAMESPACE = "http://www.3di.it/ns/xw-200303121136";

    public static final String GML_NAMESPACE = "http://www.opengis.net/gml";

    public static final String H_NAMESPACE = "http://www.w3.org/HTML/1998/html4";

    /**
     * Forza la rimozione dell'eventuale processing instruction <?xml...?> dal codice xml
     * passato (come stringa) al costruttore.
     *
     * @see #XMLDocumento(String, int, boolean)
     *
     */
    public static final int REMOVE_VER_ENC_DECLARATION = 0;

    /**
     * Indica di lasciare l'eventuale processing instruction <?xml...?> nel codice xml
     * passato (come stringa) al costruttore.
     *
     * @see #XMLDocumento(String, int, boolean)
     *
     */
    public static final int LEAVE_VER_ENC_DECLARATION = 1;

    /**
     * Forza l'aggiunta (se non c'e') della processing instruction <?xml...?> nel codice xml
     * passato (come stringa) al costruttore.
     *
     * @see #XMLDocumento(String, int, boolean)
     *
     */
    public static final int FORCE_VER_ENC_DECLARATION = 2;

    private Document document; //org.dom4j.Document - documento vero e proprio

    /**
     * Indica se la dichiarazione dell'encoding e' stata rimossa o meno dal codice xml in fase di creazione
     * di un'istanza della classe a partire da una stringa.
     *
     * @see #XMLDocumento(String)
     * @see #XMLDocumento(String, int, boolean)
     *
     */
    private boolean encodingDeclarationRemoved = false;

    /**
     * Restituisce la stringa dei namespace usati nei db extraway.
     * <br><br>
     *
     * author 3D Informatica - fgr
     *
     * @return La stringa dei namespace usati nei db extraway.
     *
     */
    public static String getNameSpacesString() {
        return "xmlns:xw=\"" + XW_NAMESPACE + "\" xmlns:gml=\"" + GML_NAMESPACE + "\" xmlns:h=\"" + H_NAMESPACE + "\"";
    }

    /**
     * Costruttore da stringa.<br>
     * Vengono aggiunti i namespace opportuni e viene rimossa l'eventuale
     * processing instruction &lt;?xml...?&gt;.<br>
     * Inoltre, vengono effettuate le sostituzioni "&amp;#xd;" --&gt; "" e
     * "&#" --&gt; "&amp;amp;#".
     *
     * @param xmlString Documento xml di input sotto forma di stringa.
     *
     * @throws DocumentException in caso di errore.
     *
     * @see #XMLDocumento(String, int, boolean)
     *
     */
    public XMLDocumento(String xmlString) throws DocumentException {
    	// mbernardini 27/05/2015 : modificata la chiamata di default per gestire la codifica utf-8
    	this(xmlString, FORCE_VER_ENC_DECLARATION, true);
    	//this(xmlString, REMOVE_VER_ENC_DECLARATION, false);
    }

    /**
     * Costruttore da stringa.<br>
     * Vengono aggiunti i namespace opportuni e, <u>se indicato</u>, viene rimossa l'eventuale
     * processing instruction &lt;?xml...?&gt; e vengono effettuate le sostituzioni "&amp;#xd;" --&gt; "" e
     * "&#" --&gt; "&amp;amp;#".
     *
     * @param xmlString                      Documento xml di input sotto forma di stringa.
     * @param xmlVerEncDeclarationManagement Indica come gestire la processing instruction
     *                                       <?xml...?> [RW 0044527].<br>
     *                                       Per le operazioni consentite si faccia riferimento
     *                                       alle variabili <i>*_VER_ENC_DECLARATION</i>.
     * @param doNotModifyEntities            Se true, fa in modo che le entity '&#' non vengano
     *                                       toccate; altrimenti vengono effettuate le sostituzioni
     *                                       "&amp;#xd;" --&gt; "" e "&#" --&gt; "&amp;amp;#".
     *
     * @throws DocumentException in caso di errore.
     *
     */
    public XMLDocumento(String xmlString, int xmlVerEncDeclarationManagement, boolean doNotModifyEntities) throws DocumentException {
        Logger.debug("XMLDocumento(String, int, boolean): xmlVerEncDeclarationManagement: " + xmlVerEncDeclarationManagementToString(xmlVerEncDeclarationManagement));
        Logger.debug("XMLDocumento(String, int, boolean): doNotModifyEntities: " + doNotModifyEntities);

        try {
            // Federico 29/05/07: introdotte gestioni alternative alla rimozione dell'eventuale
            // processing instruction <?xml...?> [RW 0044527]
            switch (xmlVerEncDeclarationManagement) {
                case REMOVE_VER_ENC_DECLARATION:
                    if (xmlString.startsWith("<?xml")) {
                        // rimozione della processing instruction <?xml...?>
                        xmlString = xmlString.substring(xmlString.indexOf("?>") + 2);
                        encodingDeclarationRemoved = true;
                    }

                    break;

                case FORCE_VER_ENC_DECLARATION:
                    if (!xmlString.startsWith("<?xml")) {
                        // aggiunta della processing instruction <?xml...?>
                        xmlString = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n" + xmlString;
                    }
            }
            
            // mbernardini 29/05/2015 : conversione dell'entity xml utilizzata per l'euro per problemi di conversione sul dom
            xmlString = xmlString.replaceAll(StringUtil.EURO_HTML_ENTITY, StringUtil.EURO_HTML_ENTITY_2);

            document = DocumentHelper.parseText(xmlString);
        }
        catch (DocumentException e) {
            Logger.error("XMLDocumento(String, int, boolean): Error parsing xml:\r\n\n" + xmlString + "\r\n", e);
            throw e;
        }
    }

    /**
     * Converte in stringa il valore numerico dalle variabili <i>*_VER_ENC_DECLARATION</i>.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     *
     * @param xmlVerEncDeclarationManagement Indica al costruttore <i>XMLDocumento(String, int, boolean)</i>
     *                                       come gestire la processing instruction <?xml...?> [RW 0044527].<br>
     *                                       Per le operazioni consentite si faccia riferimento
     *                                       alle variabili <i>*_VER_ENC_DECLARATION</i>.
     *
     * @return Il nome della variabile che corrisponde all'operazione indicata; <i>"unknown operation"</i> se
     *         l'operazione non e' riconosciuta.
     *
     */
    public static String xmlVerEncDeclarationManagementToString(int xmlVerEncDeclarationManagement) {
        switch (xmlVerEncDeclarationManagement) {
            case REMOVE_VER_ENC_DECLARATION:
                return "REMOVE_VER_ENC_DECLARATION";

            case FORCE_VER_ENC_DECLARATION:
                return "FORCE_VER_ENC_DECLARATION";

            case LEAVE_VER_ENC_DECLARATION:
                return "LEAVE_VER_ENC_DECLARATION";

            default: return "unknown operation";
        }
    }

    /**
     * Restituisce il qualified name dell'element root, ovvero che definisce l'information unit
     * @return il qualified name dell'element root, ovvero che definisce l'information unit
     */
    public String getIUName() {
        return document.getRootElement().getQualifiedName();
    }

    /**
     * Restituisce il valore di un attributo della prima istanza di un elemento
     * @param xpath xpath dell'attributo da restiuire
     * @return valore dell'attributo ricercato, null se xpath non trovato
     */
    public String getAttributeValue(String xpath) {
        Attribute at = (Attribute) document.selectSingleNode(xpath);
        if (at == null)
            return null;
        else
            return at.getValue();
    }

    /**
     * Restituisce il valore di un attributo della prima istanza di un elemento
     * @param xpath xpath dell'attributo da restiuire
     * @return valore dell'attributo ricercato, default se xpath non trovato
     */
    public String getAttributeValue(String xpath, String defValue) {
        String val = getAttributeValue(xpath);
        if (val == null)
            return defValue;
        else
            return val;
    }

    /**
     * Restituisce i valori dei nodi individuati da <i>xpath</i> separandoli con il separatore indicato.<br>
     * Se l'xpath non produce alcun risultato viene restituito il valore di default [RW 0045059].
     * <br><br>
     *
     * author 3D Informatica - fgr
     *
     * @param xpath        Xpath dei nodi da cercare.
     * @param defValue     Valore di default da restituire se l'xpath non trova nulla.
     * @param valSeparator Separatore da utilizzare per separare i valori dei nodi trovati.
     *
     * @return I valori dei nodi trovati separati con <i>valSeparator</i>; <i>defValue</i> se l'xpath
     *         non trova nulla.
     *
     */
    public String getNodesValues(String xpath, String defValue, String valSeparator) {
        List nodes = document.selectNodes(xpath);

        if (nodes.size() == 0) {
            return defValue;
        }
        else {
            String result = "";

            for (int j = 0; j < nodes.size(); j++) {
                Node node = (Node)nodes.get(j);
                result += node.getText() + ((j < nodes.size() - 1) ? valSeparator : "");
            }

            return result;
        }
    }

    /**
     * Testa il valore di un attributo della prima istanza di un elemento.
     *
     * @param xpath xpath dell'attributo da testare
     * @param value valore da confrontare con quello dell'attributo trovato
     * @return true se attributo assume il valore richiesto, false altrimenti (attributo ha valore differente o assente)
     */
    public boolean testAttributeValue(String xpath, String value) {
        return testAttributeValue(xpath, value, false);
    }

    /**
     * Testa il valore di un attributo della prima istanza di un elemento ignorando il case dei valori
     * da confrontare.
     *
     * @param xpath xpath dell'attributo da testare
     * @param value valore da confrontare con quello dell'attributo trovato
     * @return true se attributo assume il valore richiesto, false altrimenti (attributo ha valore differente o assente)
     */
    public boolean testAttributeValueIgnoreCase(String xpath, String value) {
        return testAttributeValue(xpath, value, true);
    }

    /**
     * Testa il valore di un attributo della prima istanza di un elemento.
     *
     * @param xpath  xpath dell'attributo da testare
     * @param value  valore da confrontare con quello dell'attributo trovato
     * @param ignore indica se ignorare o meno il case dei valori da confrontare
     * @return true se attributo assume il valore richiesto, false altrimenti (attributo ha valore differente o assente)
     */
    public boolean testAttributeValue(String xpath, String value, boolean ignore) {
        String xpathVal = getAttributeValue(xpath);
        if (xpathVal == null)
            return false;
        else {
            if (ignore)
                return xpathVal.equalsIgnoreCase(value);
            else
                return xpathVal.equals(value);
        }
    }

    /**
     * Verifica se un certo xpath esiste nel documento xml
     * @param xpath xpath di cui si vuole sapere l'esistenza
     * @return true se xpath trovato, false altrimenti
     */
    public boolean isXPathFound(String xpath) {
        return document.selectSingleNode(xpath) == null ? false : true;
    }

    /**
     * Verifica se un certo xpath esiste nel documento xml e se il nodo trovato contiene
     * del testo (i whitespace non vengono considerati come testo valido).
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     *
     * @param xpath Xpath da controllare.
     *
     * @return True se xpath trovato e non vuoto; false altrimenti.
     *
     */
    public boolean isXPathFoundAndNotEmpty(String xpath) {
        Node n = document.selectSingleNode(xpath);

        if (n != null && n.getText().trim().length() > 0) return true;
        else                                              return  false;
    }

    /**
     * Restituisce la lista dei nodi corrispondenti a un xpath
     * @param xpath xpath
     * @return nodi trovati
     */
    public List selectNodes(String xpath) {
        return document.selectNodes(xpath);
    }

    /**
     * Restituisce la lista dei nodi corrispondenti a un xpath ordinati
     * @param xpath xpath
     * @return nodi trovati
     */
    public List selectNodes(String xpath, String ord) {
        return document.selectNodes(xpath, ord);
    }

    /**
     * Restituisce il nodo corrispondenti a un xpath
     * @param xpath xpath
     * @return nodo trovati
     */
    public Node selectSingleNode(String xpath) {
        return document.selectSingleNode(xpath);
    }

    /**
     * Restituisce il root element
     * @return il root element del documento
     */
    public Element getRootElement() {
        return document.getRootElement();
    }

    /**
     * Restituisce l'encoding del documento xml
     * @return encoding del documento xml
     */
    public String getXMLEncoding() {
        return document.getXMLEncoding();
    }

    /**
     * Questo metodo consente di recuperare un elemento dal documento dato il suo xpath (RW0027037).<br>
     * L'XPath deve essere riferito alla radice del documento (omettendola) e deve essere espresso mediante la
     * dot notation "&lt;nodo&gt;.&lt;nodo&gt;[x].@&lt;attr&gt;".<br>
     * Notare che nell'esempio è stato indicato anche il numero di istanza che interessa (notazione "[x]").
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     * version 1.0
     *
     * @param xPath XPath dell'elemento da cercare, completo e riferito alla radice del documento che, quindi, va
     *              omessa.
     *
     * @return L'elemento cercato o null se tale elemento non esiste.
     *
     * @see #extractElement(String)
     */
    public Node getElement(String xPath) {
        return findElement(xPath, false);
    }

    /**
     * Questo metodo consente di estrarre un elemento dal documento dato il suo xpath (RW0027037).<br>
     * L'XPath deve essere riferito alla radice del documento (omettendola) e deve essere espresso mediante la
     * dot notation "&lt;nodo&gt;.&lt;nodo&gt;[x].@&lt;attr&gt;".<br>
     * Notare che nell'esempio è stato indicato anche il numero di istanza che interessa (notazione "[x]").
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     * version 1.0
     *
     * @param xPath XPath dell'elemento da estrarre, completo e riferito alla radice del documento che, quindi, va
     *              omessa.
     *
     * @return L'elemento cercato o null se tale elemento non esiste. Notare che l'elemento restituito viene <b>tolto</b>
     *         dal documento.
     *
     * @see #getElement(String)
     */
    public Node extractElement(String xPath) {
        return findElement(xPath, true);
    }

    /**
     * Questo metodo consente di cercare/estrarre un elemento dal documento dato il suo xpath (RW0027037).<br>
     * L'XPath deve essere riferito alla radice del documento (omettendola) e deve essere espresso mediante la
     * dot notation "&lt;nodo&gt;.&lt;nodo&gt;[x].@&lt;attr&gt;".<br>
     * Notare che nell'esempio è stato indicato anche il numero di istanza che interessa (notazione "[x]").
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     * version 1.0
     *
     * @param xPath          XPath dell'elemento da cercare/estrarre, completo e riferito alla radice del documento che, quindi, va
     *                       omessa.
     * @param extractElement Indica se estrarre o meno dal documento l'elemento indicato da 'xPath'.
     *
     * @return L'elemento cercato o null se tale elemento non esiste. Notare che se 'extractElement' è true,
     *         l'elemento restituito viene <b>tolto</b> dal documento.
     *
     * @see #getElement(String)
     * @see #extractElement(String)
     */
    public Node findElement(String xPath, boolean extractElement) {
        String currentEl;
        int index, instPos, index2;
        Element theRoot = document.getRootElement();
        List l;

        Node lastSeenEl = theRoot;
        xPath += ".";               // per iterare su ogni elemento di xPath (compreso l'ultimo)

        while ((index = xPath.indexOf(".")) != -1) {
            currentEl = xPath.substring(0, index);
            xPath = xPath.substring(index + 1);     // elimino currentEl da xPath

            if (currentEl.endsWith("]")) {
                index2 = currentEl.indexOf("[");
                instPos = Integer.parseInt(currentEl.substring(index2 + 1, currentEl.indexOf("]")));
                currentEl = currentEl.substring(0, index2);
                instPos++;
            }
            else {
                instPos = 1;
            }

            //currentEl è il nome dell'elemento da cui passare per la navigazione
            //instPos è il numero dell'istanza di currentEl

            l = lastSeenEl.selectNodes(currentEl);

            if (l.size() >= instPos) {
                lastSeenEl = (Node)l.get(instPos - 1);
            }
            else {
                // l.size() < instPos --> l'elemento cercato non esiste
                return null;
            }
        } // fine while

        // restituisco l'elemento cercato
        if (extractElement) {
            // è richiesta l'estrazione dal documento dell'elemento cercato
            return lastSeenEl.detach();
        }
        else {
            // restituisco l'elemento cercato senza estrarlo dal documento
            return lastSeenEl;
        }
    }

    /**
     * Rimuove i nodi identificati da un xpath (se esistono).
     *
     * @param xpath xpath dei nodi da eliminare.
     *
     */
    public void removeXPath(String xpath) {
        List l = document.selectNodes(xpath);

        for (int i = 0; i < l.size(); i++) ((Node) l.get(i)).detach();
    }

    /**
     * Costruttore che crea un document con radice root.
     * <br><br>
     *
     * author 3DInformatica - ss<br>
     *
     * @param root Radice del documento xml.
     *
     */
    public XMLDocumento(Element root) {
        root.addNamespace("xw", XMLDocumento.XW_NAMESPACE);
        root.addNamespace("gml", XMLDocumento.GML_NAMESPACE);
        root.addNamespace("h", XMLDocumento.H_NAMESPACE);
        document = DocumentHelper.createDocument(root);
        
        // mbernardini 28/05/2015 : encoding utf-8
        document.setXMLEncoding(ENCODING);
    }

    /**
     * Costruttore che crea un XMLDocumento a partire da un document dom4j.
     * <br><br>
     *
     * author 3DInformatica - ss<br>
     *
     * @param doc Documento dom4j con cui costruire l'XMLDocumento.
     *
     */
    public XMLDocumento(Document doc) {
        // Federico 09/07/09: spostato codice nel costruttore 'XMLDocumento(Document, boolean)' [M 0000498]
        this(doc, true);
    }

    /**
     * Costruttore che crea un XMLDocumento a partire da un document dom4j.
     * <br><br>
     *
     * author 3DInformatica - ss<br>
     *
     * @param doc                Documento dom4j con cui costruire l'XMLDocumento.
     * @param checkTextInXWFiles Se <code>true</code>, comporta l'eliminazione del testo nei nodi xw:file
     *                           (testo estratto in dw 2) con l'aggiunta dell'attributo "convert=yes"
     *                           per forzare la conversione tramite FCS.
     *
     */
    public XMLDocumento(Document doc, boolean checkTextInXWFiles) {
        document = doc;
    }

    /**
     * clone method
     */
    public Object clone() throws CloneNotSupportedException {
        return new XMLDocumento((Document) document.clone());
    }

    /**
     * Questo metodo restituisce l'oggetto Document contenuto in questa classe.
     * <br><br>
     *
     * author 3D Informatica - ss<br>
     *
     * @return L'oggetto Document contenuto in questa classe.
     *
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Questo metodo restituisce una <b>copia</b> dell'oggetto Document contenuto in questa classe.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     * version 1.0
     *
     * @return Una <b>copia</b> dell'oggetto Document contenuto in questa classe.
     *
     */
    public Document getDocumentCopy() {

        return (Document)document.clone();
    }

    /**
     * Questo metodo restituisce una <b>copia</b> dell'oggetto Document contenuto in questa classe, aggiungendo
     * la radice indicata.<br><br>
     *
     * <b>NOTA:</b> la vecchia radice diventa figlia della nuova.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     *
     * @param rootName Nome della nuova radice.
     *
     * @return Una <b>copia</b> dell'oggetto Document contenuto in questa classe con la radice indicata;
     *         null se 'rootName' e' null o vuoto.
     *
     */
    public Document getDocumentCopyWithNewRoot(String rootName) {
        if (rootName == null || rootName.length() == 0) return null;

        Document copy = (Document)document.clone();
        Element oldRoot = (Element)copy.getRootElement().detach();
        Element newRoot = DocumentHelper.createElement(rootName);
        newRoot.add(oldRoot);
        copy.setRootElement(newRoot);

        return copy;
    }

    /**
     * Questo metodo consente di impostare l'oggetto Document contenuto in questa classe.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     * version 1.0
     *
     * @param doc Un oggetto Document da immagazzinare nella classe.
     *
     */
    public void setDocument(Document doc) {

        document = doc;
    }

    /**
     * Restituisce il testo di un elemento
     * @param xpath xpath dell'elemento da restiuire
     * @return valore del text ricercato, null se xpath non trovato
     */
    public String getElementText(String xpath) {
        Element el = (Element) document.selectSingleNode(xpath);
        if (el == null)
            return null;
        else
            return el.getText();
    }

    /**
     * Restituisce il testo di un elemento
     * @param xpath xpath dell'elemento da restiuire
     * @param defValue valore di default
     * @return valore dell'elemento ricercato, 'defValue' se xpath non trovato
     */
    public String getElementText(String xpath, String defValue) {
        String val = getElementText(xpath);
        if (val == null || val.length() == 0)
            return defValue;
        else
            return val;
    }

    /**
     * Restituisce il codice xml del documento senza apportare alcuna modifica ad esso e formattandolo in
     * modo che sia facilmente leggibile.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     *
     * @return Il codice xml del documento formattato per la lettura o non formattato in caso di errore
     *         nella formattazione (prodotto con <i>org.dom4j.Node.asXML()</i>).
     *
     */
    public String asXML() {
        StringWriter sw = new StringWriter();

        try {
            asXML(sw);

            return sw.toString();
        }
        catch (Exception ex) {
            Logger.error("XMLDocumento.asXML(): got exception formatting xml code:", ex);
            Logger.error("XMLDocumento.asXML(): using org.dom4j.Node.asXML()...");

            return this.document.asXML();
        }
    }

    /**
     * Restituisce il codice xml del documento senza apportare alcuna modifica ad esso e formattandolo in
     * modo che sia facilmente leggibile.
     * <br><br>
     *
     * author 3D Informatica - fgr<br>
     *
     * @param w Oggetto writer su cui scrivere l'xml.
     *
     * @throws Exception in caso di errore nella scrittura sul Writer.
     *
     */
    public void asXML(Writer w) throws Exception {
        OutputFormat outformat = OutputFormat.createPrettyPrint();

        // Federico 03/08/07: l'encoding deve essere quello del documento (se non e' stato rimosso dal costruttore)
        String encoding = (encodingDeclarationRemoved ? ENCODING : document.getXMLEncoding());

        if (encoding == null) encoding = ENCODING; // 'getXMLEncoding' puo' restituire null

        outformat.setEncoding(encoding);
        XMLWriter writer = new XMLWriter(w, outformat);
        writer.write(document);

        // Federico 23/07/08: la chiusura del writer e' giusto che sia a carico del chiamante
        //writer.close();
    }

}