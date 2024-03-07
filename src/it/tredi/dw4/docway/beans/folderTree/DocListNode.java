package it.tredi.dw4.docway.beans.folderTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.NumFascConverter;

public class DocListNode {
	
	private static NumFascConverter FCC = new NumFascConverter();
	
	private List<XwFileSimple> xwFiles;
	private String tipo;
	private String data_prot;
	private String cod_fasc;
	private String nrecord;
	private String num_prot;
	private String oggetto;
	private String title;
	private String selPos;
	private String physDoc;
	private String descr1;
	private String descr2;
	
	public DocListNode(Element node) {
		
		tipo = node.attributeValue("tipo");
		data_prot = node.attributeValue("data_prot");
		cod_fasc = node.attributeValue("cod_fasc");
		nrecord = node.attributeValue("nrecord");
		num_prot = node.attributeValue("num_prot"); 
		selPos = node.attributeValue("selPos"); 
		physDoc = node.attributeValue("physDoc"); 
		descr1 = node.attributeValue("descr1");
		descr2 = node.attributeValue("descr2");
		oggetto = node.selectSingleNode("oggetto").getText();
		
		title = "NRec.<i>" + nrecord + "</i>&#160;-&#160;";
		if(num_prot.length() > 0){
			title += I18N.mrs("dw4.prot_n") + num_prot;
		}
		if(data_prot.length() > 0){
			if(num_prot.length() > 0){
				title += "&#160;-&#160;";
			}
			String vd = data_prot.length() == 8 ?  data_prot.substring(6)+"/"+data_prot.substring(4, 6)+"/"+data_prot.substring(0, 4) : data_prot;
			title += vd + "&#160;-&#160;";
		}
		
		title += "<b>" + oggetto + "</b><br/><i>" + FCC.getAsString(null,  null, cod_fasc);
		
		if(descr1 !=null && descr1.length() > 0) {
			title += "&#160;-&#160;" + descr1;
			
			if(descr2 !=null && descr2.length() > 0) {
				title += "&#160;-&#160;" + descr2;
			} 
		} 
		
		title += "</i>";
		
		xwFiles = new ArrayList<>();
		
		List<Node> files = node.selectNodes("*[name()='xw:file']");
		for(Node nn : files){
			// XwFile xwf = new XwFile();
			// xwf.init(DocumentHelper.createDocument((Element)nn.clone()));
			xwFiles.add(new XwFileSimple((Element)nn));
		}
	}
	
	public String getTitle(){
		return title;
	}
	
	public boolean hasXwFiles() {
		return xwFiles.size() > 0;
	}

	public List<XwFileSimple> getXwFiles() {
		return xwFiles;
	}

	public String getTipo() {
		return tipo;
	}

	public String getData_prot() {
		return data_prot;
	}

	public String getCod_fasc() {
		return cod_fasc;
	}

	public String getNrecord() {
		return nrecord;
	}

	public String getNum_prot() {
		return num_prot;
	}

	public String getOggetto() {
		return oggetto;
	}

	public String getSelPos() {
		return selPos;
	}

	public String getPhysDoc() {
		return physDoc;
	}
	
	
}
