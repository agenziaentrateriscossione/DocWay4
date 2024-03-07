package it.tredi.dw4.docway.model.analisivirus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Informazioni relative alla richiesta di verifica di virus su un documento (scan degli allegati)
 * @author mbernardini
 */
public class VerificaVirus extends XmlEntity {
	
	private String status;
	private Richiedente richiedente = new Richiedente();
	private Esecutore esecutore = new Esecutore();
	private List<FileInfetto> fileInfetti = new ArrayList<FileInfetto>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.status = XMLUtil.parseStrictAttribute(dom, "verificaVirus/@status");
		this.richiedente.init(XMLUtil.createDocument(dom, "verificaVirus/richiedente"));
		this.esecutore.init(XMLUtil.createDocument(dom, "verificaVirus/esecutore"));
		this.fileInfetti = XMLUtil.parseSetOfElement(dom, "/verificaVirus/fileInfetti/file", new FileInfetto());
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Richiedente getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}
	
	public Esecutore getEsecutore() {
		return esecutore;
	}

	public void setEsecutore(Esecutore esecutore) {
		this.esecutore = esecutore;
	}
	
	public List<FileInfetto> getFileInfetti() {
		return fileInfetti;
	}

	public void setFileInfetti(List<FileInfetto> fileInfetti) {
		this.fileInfetti = fileInfetti;
	}
	
}
