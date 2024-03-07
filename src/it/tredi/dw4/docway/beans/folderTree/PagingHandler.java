package it.tredi.dw4.docway.beans.folderTree;

import org.dom4j.Element;

public class PagingHandler {
	
	/*
	 * si conta da 1 !!!
	 */
	
	
	public static final int PAGE_SIZE = 10;
	
	private int from;
	private int total;
	private String seleId;
	
	private int pages;
	private int page;
	private boolean canFirst;
	private boolean canPrev;
	private boolean canNext;
	private boolean canLast;
	
		
	public PagingHandler(Element node) {
		
		from = Integer.parseInt(node.attributeValue("from"), 10);
		total = Integer.parseInt(node.attributeValue("total"), 10);
		seleId = node.attributeValue("seleId");
		
		pages = (total / PAGE_SIZE)  + 1;
		page = ((from - 1) / PAGE_SIZE) + 1;  // 11 -> 10/10 -> 1+1 -> pag2; 10->9/10->0+1->pag1;
		
		canFirst = page > 1;
		canPrev = page > 1;
		canNext = page < pages;
		canLast = page < pages;
		
	}
	
	private int pageStart(int p) {
		return ((p - 1) * PAGE_SIZE ) + 1;   // p1  => 1; p2 => 11 ....
	}
	
	public int getFirstFrom() {
		return pageStart(1);
	}
	
	public int getLastFrom() {
		return pageStart(pages);
	}
	
	public int getNextFrom() {
		return pageStart( page + 1 );
	}
	
	public int getPrevFrom() {
		return pageStart( page - 1 );
	}
	
	public int getPageFrom() {
		return pageStart( page );
	}
	
	public int getPageFrom(int p) {
		return pageStart( p );
	}
	
	public int getCount() {
		return PAGE_SIZE;
	}


	public int getFrom() {
		return from;
	}


	public int getTotal() {
		return total;
	}


	public String getSeleId() {
		return seleId;
	}

	public int getPages() {
		return pages;
	}

	public int getPage() {
		return page;
	}
	
	public void setPage(int p) {
		page = p;
	}

	public boolean isCanFirst() {
		return canFirst;
	}

	public boolean isCanPrev() {
		return canPrev;
	}

	public boolean isCanNext() {
		return canNext;
	}

	public boolean isCanLast() {
		return canLast;
	}
	
	
}
