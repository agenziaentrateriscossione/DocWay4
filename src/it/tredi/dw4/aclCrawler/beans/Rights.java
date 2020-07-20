package it.tredi.dw4.aclCrawler.beans;

import it.tredi.dw4.aclCrawler.model.AclSection;
import it.tredi.dw4.aclCrawler.model.RepSection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Font.FontFamily;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//
//import au.com.bytecode.opencsv.CSVWriter;

public class Rights {
	private List<AclSection> rightsDocs;
	private List<Document> rightsXmls;
	private RepSection repSection;
	
	public RepSection getRepSection() {
		return repSection;
	}
	public List<AclSection> getRightsDocs() {
		return rightsDocs;
	}

	public Rights() {
		this.rightsDocs = new ArrayList<AclSection>();
		this.rightsXmls = new ArrayList<Document>();
	}
	
	public void loadRightFile(Element rightFile) throws Exception {
        //parsa il file xml ed estrai le singole sezioni (elemento db oppure elemento common in acl.xml) 
        Document rightsDoc = DocumentHelper.createDocument(rightFile);
        rightsXmls.add(rightsDoc);
        
        if (rightsDoc.getRootElement().getName().equals("listof_rights"))
        	this.rightsDocs.addAll(parseRightFile(rightsDoc));
        else if (rightsDoc.getRootElement().getName().equals("listof_rep"))
        	this.repSection = parseRepertoriFile("Repertori di xdocway", rightsDoc);
	}
	
	public List<AclSection> copySectionObjsList() {
		List<AclSection> sections = new ArrayList<AclSection>();
		
		Iterator<Document> docsIt = this.rightsXmls.iterator();
		while (docsIt.hasNext()) {
			sections.addAll(this.parseRightFile(docsIt.next()));
		}
		
		return sections;
	}
	
	public RepSection copyRepertoriObj() {
		return new RepSection(this.repSection.getTitle(), this.repSection.getRepertoriElements());
	}
	
//	@SuppressWarnings("unchecked")
//	public String[] buildCsvHeader(CSVWriter writer, String query) {
//		int maxLenght = 0;
//		
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			int numRights = this.rightsXmls.get(i).selectNodes("//right").size();
//			maxLenght += numRights; 
//		}
//		
//		//conta anche i campi fissi
//		maxLenght += 7; //Nome/Cognome/Codice ufficio/Appartenenza/Login/Matricola
//		
//		String[] rowQ = new String[maxLenght];
//		String[] row0 = new String[maxLenght];
//		String[] row1 = new String[maxLenght];
//		String[] row2 = new String[maxLenght];
//		
//		rowQ[0] = query;
//		
//		row0[0] = "Nome";
//		row0[1] = "Cognome";
//		row0[2] = "AOO";
//		row0[3] = "Codice ufficio";
//		row0[4] = "Appartenenza";
//		row0[5] = "Login";
//		row0[6] = "Matricola";
//		
//		int startIndex1 = 7;
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			Iterator<Element> dbsIt = this.rightsXmls.get(i).selectNodes("/listof_rights/db").iterator();
//			while (dbsIt.hasNext()) {
//				Element dbEl = dbsIt.next();
//				row0[startIndex1] = dbEl.attributeValue("nome", "");
//				
//				Iterator<Element> groupsIt = dbEl.selectNodes(".//group[not(@tipo)]").iterator();
//				while (groupsIt.hasNext()) {
//					Element groupEl = groupsIt.next();
//					row1[startIndex1] = groupEl.attributeValue("label", "");
//					
//					Iterator<Element> rightsIt = groupEl.selectNodes("./right").iterator();
//					while (rightsIt.hasNext()) {
//						Element rightEl = rightsIt.next();
//						
//						row2[startIndex1] = rightEl.attributeValue("cod", "");
//						startIndex1++;
//					}
//				}
//			}
//		}
//		
//		writer.writeNext(rowQ);
//		writer.writeNext(row0);
//		writer.writeNext(row1);
//		writer.writeNext(row2);
//		
//		return row2;
//	}
	
//	@SuppressWarnings("unchecked")
//	public String[] buildPdfHeader(PdfPTable table, int colSpan) throws DocumentException {
//		int maxLenght = 0;
//		
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			int numRights = this.rightsXmls.get(i).selectNodes("//right").size();
//			maxLenght += numRights; 
//		}
//		
//		//conta anche i campi fissi
//		maxLenght += 7 - 22; //Nome/Cognome/AOO/Ufficio/Login/Matricola //FIXME
//		
//		String[] row0 = new String[maxLenght];
//		String[] row1 = new String[maxLenght];
//		String[] row2 = new String[maxLenght];
//		
//		row0[0] = "Nome";
//		row0[1] = "Cognome";
//		row0[2] = "AOO";
//		row0[3] = "Codice ufficio";
//		row0[4] = "Appartenenza";
//		row0[5] = "Login";
//		row0[6] = "Matricola";
//		
//		int startIndex1 = 7;
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			Iterator<Element> dbsIt = this.rightsXmls.get(i).selectNodes("/listof_rights/db").iterator();
//			while (dbsIt.hasNext()) {
//				Element dbEl = dbsIt.next();
//				row0[startIndex1] = dbEl.attributeValue("nome", "");
//				
//				Iterator<Element> groupsIt = dbEl.selectNodes(".//group[not(@tipo)]").iterator();
//				while (groupsIt.hasNext()) {
//					Element groupEl = groupsIt.next();
//					row1[startIndex1] = groupEl.attributeValue("label", "");
//					
//					Iterator<Element> rightsIt = groupEl.selectNodes("./right").iterator();
//					while (rightsIt.hasNext()) {
//						Element rightEl = rightsIt.next();
//						
//						row2[startIndex1] = rightEl.attributeValue("cod", "");
//						startIndex1++;
//					}
//				}
//			}
//		}
//		
//		table.resetColumnCount(maxLenght);
//		table.setWidthPercentage(99.0F);
//		table.setHeaderRows(3);
//		
//		for (int i=0; i<maxLenght; i++) {
//			PdfPCell cell = new PdfPCell(new Phrase(row0[i], new Font(FontFamily.HELVETICA, 14, Font.BOLDITALIC, new BaseColor(0, 0, 0))));
//			cell.setColspan(colSpan);
//			table.addCell(cell);
//		}
//		table.completeRow();
//		for (int i=0; i<maxLenght; i++) {
//			PdfPCell cell = new PdfPCell(new Phrase(row1[i], new Font(FontFamily.HELVETICA, 14, Font.BOLDITALIC, new BaseColor(0, 0, 0))));
//			cell.setColspan(colSpan);
//			table.addCell(cell);
//		}
//		table.completeRow();
//		for (int i=0; i<maxLenght; i++) {
//			PdfPCell cell = new PdfPCell(new Phrase(row2[i], new Font(FontFamily.HELVETICA, 14, Font.BOLDITALIC, new BaseColor(0, 0, 0))));
//			cell.setColspan(colSpan);
//			table.addCell(cell);
//		}
//		table.completeRow();
//		
//		return row2;
//	}
	
//	@SuppressWarnings("unchecked")
//	public void buildCsvFooter(CSVWriter writer) {
//		String[] row = new String[2];
//		
//		row[0] = "LEGENDA";
//		row[1] = "";
//		writer.writeNext(row);
//		
//		row[0] = "Codice";
//		row[1] = "Diritto";
//		writer.writeNext(row);
//		
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			Iterator<Element> rightsIt = this.rightsXmls.get(i).selectNodes("/listof_rights//right").iterator();
//			while (rightsIt.hasNext()) {
//				Element right = rightsIt.next();
//				row[0] = right.attributeValue("cod", "");
//				row[1] = right.attributeValue("label", "");
//				writer.writeNext(row);
//			}
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void buildPdfFooter(com.itextpdf.text.Document doc, PdfWriter writer) throws Exception {
//		Paragraph paragraph;
//		
//		for (int i=0; i<this.rightsXmls.size(); i++) {
//			Iterator<Element> dbIt = this.rightsXmls.get(i).selectNodes("/listof_rights/db").iterator();
//			
//			int size = this.rightsXmls.get(i).selectNodes("/listof_rights/db").size();
//			
//			if (size > 0) {
//				PdfPTable table = new PdfPTable(size);
//				table.setWidthPercentage(20);
//				
//				while (dbIt.hasNext()) {
//					Element dbEl = dbIt.next();
//					
//					PdfPCell cell = new PdfPCell();
//					cell.setBorderWidth(0);
//					
//					paragraph = new Paragraph();
//					paragraph.setIndentationLeft(50.0F);
//					
//					Phrase phrase = new Phrase("Legenda " + dbEl.attributeValue("nome", ""), new Font(new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC, new BaseColor(0, 0, 0))));
//					paragraph.add(phrase);
//					
//					cell.addElement(paragraph);
//					
//					paragraph = new Paragraph();
//					paragraph.setIndentationLeft(50.0F);
//					
//					phrase = new Phrase("Codice", new Font(new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC, new BaseColor(255, 0, 0))));
//					paragraph.add(phrase);
//					
//					phrase = new Phrase("    -    ");
//					paragraph.add(phrase);
//					
//					phrase = new Phrase("Diritto", new Font(new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC, new BaseColor(0, 0, 0))));
//					paragraph.add(phrase);
//					
//					cell.addElement(paragraph);
//					
//					Iterator<Element> rightsIt = dbEl.selectNodes(".//right").iterator();
//					
//					while (rightsIt.hasNext()) {
//						paragraph = new Paragraph();
//						paragraph.setIndentationLeft(50.0F);
//						
//						Element right = rightsIt.next();
//						phrase = new Phrase(right.attributeValue("cod", ""), new Font(new Font(FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(255, 0, 0))));
//						paragraph.add(phrase);
//						phrase = new Phrase("    -    ");
//						paragraph.add(phrase);
//						phrase = new Phrase(right.attributeValue("label", ""));
//						paragraph.add(phrase);
//						cell.addElement(paragraph);
//					}
//					
//					table.addCell(cell);
//				}
//				
//				table.setTotalWidth(2000);
//				table.writeSelectedRows(0, -1, doc.left(), doc.top(), writer.getDirectContent());
//			}
//		}
//	}
	
	@SuppressWarnings("unchecked")
	private List<AclSection> parseRightFile(Document rightsDoc) {
		List<AclSection> sections = new ArrayList<AclSection>();
		
		Iterator<Element> dbIt = rightsDoc.selectNodes("/listof_rights/db/group[@tipo = 'base']").iterator();
        while (dbIt.hasNext()) {
        	 Element dbEl = dbIt.next();
        	 sections.add(new AclSection(dbEl));
        }
        
        dbIt = rightsDoc.selectNodes("/listof_rights/common/group[@tipo = 'base']").iterator();
        while (dbIt.hasNext()) {
        	Element commonEl = dbIt.next();
        	sections.add(new AclSection(commonEl));
        }
        
        return sections;
	}
	
	@SuppressWarnings("unchecked")
	private RepSection parseRepertoriFile(String title, Document repertoriDoc) {
		return new RepSection(title, repertoriDoc.selectNodes("/listof_rep/repertorio"));
	}
}
