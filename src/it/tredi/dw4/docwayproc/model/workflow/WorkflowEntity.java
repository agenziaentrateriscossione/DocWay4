package it.tredi.dw4.docwayproc.model.workflow;

import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Storia;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class WorkflowEntity extends XmlEntity {

	private String nrecord = "";
	private String name = "";
	private String version = "";
	private String label = "";
	private String description = "";
	private boolean active = false;
	private boolean subprocess = false;
	private String bonitaVersion = "";
	
	// utilizzati per inserimento/modifica di un workflow
	private String fileNameBar = "";
	private String fileNameImage = "";
	private List<Option> bonitaActiveVersions = new ArrayList<>(); //versioni di bonita attive - vengono lette da property
	 
	// storia workflow
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	private List<Storia> storia;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.nrecord = 		XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@nrecord", ".");
		this.name = 		XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@name", "");
		this.version = 		XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@version", "");
		this.active = 		StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@active", ""));
		this.label = 		XMLUtil.parseStrictElement(dom, "/response/bwf_entity/label");
		this.description = 	XMLUtil.parseStrictElement(dom, "/response/bwf_entity/description");
		this.subprocess =	StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@subprocess", "false"));
		this.bonitaVersion = XMLUtil.parseStrictAttribute(dom, "/response/bwf_entity/@bonitaVersion");
		
		this.creazione.init(XMLUtil.createDocument(dom, "/response/bwf_entity/storia/creazione"));
		this.ultima_modifica.init(XMLUtil.createDocument(dom, "/response/bwf_entity/storia/ultima_modifica"));
		this.storia = XMLUtil.parseSetOfElement(dom, "/response/bwf_entity/storia/node()", new Storia());
		
		this.bonitaActiveVersions = XMLUtil.parseSetOfElement(dom, "/response/activeVersions/option", new Option() );
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@nrecord", this.nrecord);
    	
    	params.put(prefix+".@name", this.name);
    	params.put(prefix+".@version", this.version);
    	if (this.active)
    		params.put(prefix+".@active", "true");
    	else
    		params.put(prefix+".@active", "false");
    	params.put(prefix+".label", this.label);
    	params.put(prefix+".description", this.description);
    	
    	if (this.subprocess)
    		params.put(prefix+".@subprocess", "true");
    	else
    		params.put(prefix+".@subprocess", "false");
    	
    	if (fileNameBar != null && fileNameBar.length() > 0)
    		params.put("*barfilename", fileNameBar);
    	if (fileNameImage != null && fileNameImage.length() > 0)
    		params.put("*imagefilename", fileNameImage);
    	
    	params.put(prefix+".@bonitaVersion", this.bonitaVersion);
    	
		return params;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFileNameBar() {
		return fileNameBar;
	}

	public void setFileNameBar(String fileNameBar) {
		this.fileNameBar = fileNameBar;
	}

	public String getFileNameImage() {
		return fileNameImage;
	}

	public void setFileNameImage(String fileNameImage) {
		this.fileNameImage = fileNameImage;
	}
	
	public Creazione getCreazione() {
		return creazione;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public List<Storia> getStoria() {
		return storia;
	}

	public void setStoria(List<Storia> storia) {
		this.storia = storia;
	}
	
	public boolean isSubprocess() {
		return subprocess;
	}

	public void setSubprocess(boolean subprocess) {
		this.subprocess = subprocess;
	}

	public String getBonitaVersion() {
		return bonitaVersion;
	}

	public void setBonitaVersion(String bonitaVersion) {
		this.bonitaVersion = bonitaVersion;
	}

	public List<Option> getBonitaActiveVersions() {
		return bonitaActiveVersions;
	}

	public void setBonitaActiveVersions(List<Option> bonitaActiveVersions) {
		this.bonitaActiveVersions = bonitaActiveVersions;
	}
	
}
