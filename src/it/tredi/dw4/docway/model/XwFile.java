package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XwFile extends XmlEntity {
	
	private String name = "";
	private String der_from = "";
	private String der_to = "";
	private String title = "";
	private String index;
	private String force_sign;
	private String agent_meta = "";
	private String agent_pdf = "";
	private String agent_xml = "";
	private Chkin chkin = new Chkin();
	private Chkout chkout = new Chkout();
	private Metadata metadata = new Metadata();
	private boolean da_firmare = false; // identifica se e' stata richiesta la firma digitale per il file (gestione libri firma)
	private boolean readonly = false; // identifica se l'allegato non deve poter essere eliminato in fase di modifica del documento
	
	// identificano lo stato corrente del file
	private boolean checkinFile = false;
	private boolean checkoutFile = false;
	private boolean usedFile = false;
	
	private boolean withVersions = false; // identifica se il file corrente ha versioni o meno (checkin/checkout)
	
	private String xwayId = ""; // identificativo assegnato al file da xway in upload
	private String iwxGuId = ""; // identificativo assegnato dal plugin iwx (se attivo iwx) utilizzato per antreprima documenti su iwx (si evitano inutili dowload del documento)
	
	private String size = ""; // dimensione del file in byte (N.B. disponibile solo se attivo lo specifico script lua su trigger beforeSave)
	
	// mbernardini 14/05/2015 : adeguato il calcolo dell'impronta alla nuova normativa (SHA256 su ogni singolo file allegato)
	private String impronta = "";
	private String tipoImpronta = "";
	
	// mbernardini 07/02/2017 : in caso di deleteImagesAfterPDF inibire l'invio telematico fino a conversione completata
	private boolean agent_delete = false;
	
	public XwFile() {}
    
	public XwFile(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
	
	/**
	 * Costruttore utilizzato in fase di upload di doc
	 * informatici
	 * 
	 * @param xwayId identificativo restituito da xway nella response dell'upload
	 * @param name nome del file caricato
	 * @param title titolo del file caricato
	 * @throws Exception
	 */
	public XwFile(String xwayId, String name, String title) {
        this.xwayId = xwayId;
        this.name = name;
        this.title = title;
        
        if (this.name != null && this.name.length() > 0) 
        	if (this.name.lastIndexOf(".") != -1)
        		this.iwxGuId = this.name.substring(0, this.name.lastIndexOf(".")); // in caso di upload senza IWX questo valore non viene utilizzato (conterra' il nome del file senza estensione)
    }
    
	public XwFile init(Document dom) {
		Document lastVersionDom = getLastVersionFileDom(dom);
    	this.name = 		XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@name");
    	this.der_from = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@der_from");
    	this.der_to = 		XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@der_to");
    	this.title = 		XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@title");
    	this.index = 		XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@index");
    	this.force_sign = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@force_sign");
    	this.agent_meta = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@agent.meta");
    	this.agent_pdf = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@agent.pdf");
    	this.agent_xml = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@agent.xml");
    	this.size = 		XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@size");
    	
    	// der_from con ';' : conversione in pdf di vecchie scansioni fatte con IW con pagine in files separati
    	if (this.der_from != null && this.der_from.contains(";")) {
    		// il file convertito viene associato solo alla prima immagine
    		this.der_from = this.der_from.substring(0, this.der_from.indexOf(";"));
    	}
    	
    	// gestione libri firma
    	this.da_firmare =	StringUtil.booleanValue(XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@da_firmare"));
    	
    	// mbernardini 17/03/2015 : identificazione di allegati non modificabili (es. eml relativi a mail scaricate dall'archiviatore)
    	this.readonly =		StringUtil.booleanValue(XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@readonly"));
    	
    	// checkin / checkout del file corrente
    	this.chkin.init(XMLUtil.createDocument(lastVersionDom, "node()[name()='xw:file']/chkin"));
    	this.chkout.init(XMLUtil.createDocument(lastVersionDom, "node()[name()='xw:file']/chkout"));
    	
    	// metadati del file
    	this.metadata.init(XMLUtil.createDocument(lastVersionDom, "node()[name()='xw:file']/node()[name()='metadata']"));
    	
    	String ckin = XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@chkin");
    	if (ckin != null && ckin.equals("true"))
    		this.checkinFile = true;
    	String ckout = XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@chkout");
    	if (ckout != null && ckout.equals("true"))
    		this.checkoutFile = true;
    	String used = XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@used");
    	if (used != null && used.equals("true"))
    		this.usedFile = true;
    	
    	this.impronta = 	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@impronta");
    	this.tipoImpronta =	XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@tipoImpronta");
    	
    	// mbernardini 07/02/2017 : in caso di deleteImagesAfterPDF inibire l'invio telematico fino a conversione completata
    	this.agent_delete =	StringUtil.booleanValue(XMLUtil.parseAttribute(lastVersionDom, "node()[name()='xw:file']/@agent.delete", "false"));
    	
        return this;
    }
	
	/**
	 * Ritorna il dom relativo alla porzione di xml dell'ultima versione del file
	 * @param dom
	 * @return
	 */
	private Document getLastVersionFileDom(Document dom) {
		if (dom != null) {
    		List<?> nodes = dom.selectNodes("node()[name()='xw:file']//node()[name()='xw:file']");
    		if (nodes != null && nodes.size() > 0) {
    			Element element = (Element) nodes.get(nodes.size()-1);
	            Document doc = DocumentHelper.createDocument();
	            if (null != element) doc.setRootElement(element.createCopy());
	            
	            // esistono versioni del file, imposto il flag di versioning a true
    			this.withVersions = true;
    			
	            return doc;
    		}
    		else
    			return dom;
    	}
		return null;
	}
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.name);
    	return params;
    }
    
    @Override
    public String toString() {
    	return this.name;
    }
    
    public String getName() {
		return name;
	}
    
    /**
     * ritorna l'id del file encodato per l'utilizzo in URL
     * @return
     */
    public String getUrlEncodedName() {
    	String encodedName = getName();
    	try {
    		encodedName = URLEncoder.encode(encodedName, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedName;
    }

	public void setName(String cod) {
		this.name = cod;
	}
	
	public String getXwayId() {
		return xwayId;
	}

	public void setXwayId(String id) {
		this.xwayId = id;
	}

	public String getIwxGuId() {
		return iwxGuId;
	}

	public void setIwxGuId(String iwxGuId) {
		this.iwxGuId = iwxGuId;
	}

	public void setDer_from(String der_from) {
		this.der_from = der_from;
	}

	public String getDer_from() {
		return der_from;
	}

	public void setDer_to(String der_to) {
		this.der_to = der_to;
	}

	public String getDer_to() {
		return der_to;
	}

	public void setChkout(Chkout chkout) {
		this.chkout = chkout;
	}

	public Chkout getChkout() {
		return chkout;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	/**
     * ritorna il nome del file encodato per l'utilizzo in URL
     * @return
     */
    public String getUrlEncodedTitle() {
    	String encodedTitle = getTitle();
    	try {
    		encodedTitle = URLEncoder.encode(encodedTitle, "UTF-8");
    	}
    	catch (Exception e) { 
    		Logger.error(e.getMessage(), e); 
    	}
    	return encodedTitle;
    }

	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex() {
		return index;
	}

	public void setChkin(Chkin chkin) {
		this.chkin = chkin;
	}

	public Chkin getChkin() {
		return chkin;
	}

	public void setForce_sign(String force_sign) {
		this.force_sign = force_sign;
	}

	public String getForce_sign() {
		return force_sign;
	}
	
	public boolean isCheckinFile() {
		return checkinFile;
	}

	public void setCheckinFile(boolean checkinFile) {
		this.checkinFile = checkinFile;
	}

	public boolean isCheckoutFile() {
		return checkoutFile;
	}

	public void setCheckoutFile(boolean checkoutFile) {
		this.checkoutFile = checkoutFile;
	}

	public boolean isUsedFile() {
		return usedFile;
	}

	public void setUsedFile(boolean usedFile) {
		this.usedFile = usedFile;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public boolean isWithVersions() {
		return withVersions;
	}

	public void setWithVersions(boolean withVersions) {
		this.withVersions = withVersions;
	}
	
	public String getAgent_meta() {
		return agent_meta;
	}

	public void setAgent_meta(String agent_meta) {
		this.agent_meta = agent_meta;
	}

	public String getAgent_pdf() {
		return agent_pdf;
	}

	public void setAgent_pdf(String agent_pdf) {
		this.agent_pdf = agent_pdf;
	}

	public String getAgent_xml() {
		return agent_xml;
	}
	
	public boolean isAgent_delete() {
		return agent_delete;
	}

	public void setAgent_delete(boolean agent_delete) {
		this.agent_delete = agent_delete;
	}

	public void setAgent_xml(String agent_xml) {
		this.agent_xml = agent_xml;
	}
	
	public boolean isDa_firmare() {
		return da_firmare;
	}

	public void setDa_firmare(boolean da_firmare) {
		this.da_firmare = da_firmare;
	}
	
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getImpronta() {
		return impronta;
	}

	public void setImpronta(String impronta) {
		this.impronta = impronta;
	}

	public String getTipoImpronta() {
		return tipoImpronta;
	}

	public void setTipoImpronta(String tipoImpronta) {
		this.tipoImpronta = tipoImpronta;
	}
	
	/**
	 * Ritorna l'estensione del file, stringa vuota in caso di file senza estensione
	 * @return
	 */
	public String getExtension() {
		String ext = "";
		if (this.name != null && !this.name.equals("")) {
			int index = this.name.lastIndexOf(".");
			if (index != -1)
				ext = this.name.substring(index+1).toLowerCase();
		}
		return ext;
	}
	
	/**
	 * Ritorna il nome dell'icona relativa al file corrente in base all'estensione del file
	 * @return
	 */
	public String getIconName() {
		String fileName = "page"; // icona di default
		String ext = getExtension();
		if (!ext.equals("")) {
			if (ext.equals("p7m") || ext.equals("s@s")) {
				// file firmato, si verifica la presenza dell'estensione reale nel nome del file
				
				String originalName = "";
				int index = this.name.lastIndexOf(".");
				if (index != -1)
					originalName = this.name.substring(0, index).toLowerCase();
				
				if (originalName != null && !originalName.equals("")) {
					index = originalName.lastIndexOf(".");
					if (index != -1)
						ext = originalName.substring(index+1).toLowerCase();
				}
			}
			
			if (!DocWayProperties.readProperty("file.extension.icons", "").contains(ext+","))
				ext = "page"; // estensione di file non gestita in termini di icona
			
			fileName = ext;
		}
		
		return fileName;
	}
	
	/**
	 * ritorna la dimensione in KB del file
	 * @return
	 */
	public String getSizeKB() {
		String sizeKb = size;
		if (!sizeKb.equals("")) {
			try {
				sizeKb = (new Integer(sizeKb).intValue() / 1024) + " KB";
			}
			catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		return sizeKb;
	}
	
}

