package it.tredi.dw4.docway.beans;

public class DocWayConsoleApvPaginator {
	private DocWayConsoleApvPaginatorInterface docWayConsoleApvPaginatorInterface;
	int totalRecords = 0;	
	int numRecordsForPage = 10;
	
	int requestPage = 1;
	int currentPage = 1;
	int totalPages = 0;

	//properties del formsAdapter
	//private boolean primaPaginaEnabled = false;
	//private boolean paginaPrecedenteEnabled = false;
	//private boolean paginaSuccessivaEnabled = false;
	//private boolean ultimaPaginaEnabled = false;
	
	//TODO a cosa servono controlare nel bean
	//int total = 0; > numPages
	//int current = 0; > currentPage
	
	//int currentPage = 1;
	//int firstRecordInPage = 0;
	//int lastRecordInPage = 0;
	//int count = 0; > numRecords
		
	//Actions del bean
	//paginaSuccessiva
	//ultimaPagina
	//paginaSpecifica
	//primaPagina
	//paginaPrecedente
	
	public DocWayConsoleApvPaginator(DocWayConsoleApvPaginatorInterface docWayConsoleApvPaginatorInterface, int totalRecords, int numRecordsForPage) throws Exception {
		this.docWayConsoleApvPaginatorInterface = docWayConsoleApvPaginatorInterface;
		this.totalRecords = totalRecords;
		this.numRecordsForPage = numRecordsForPage;
		
		if (totalRecords % numRecordsForPage == 0)
			totalPages = totalRecords/numRecordsForPage;
		else
			totalPages = (totalRecords/numRecordsForPage) + 1;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int pageToGo) {
		requestPage = pageToGo;
		//gotoPage(pageToGo);
	}

	public int getTotalPages() {
		return totalPages;
	}

	public boolean isPrimaPaginaEnabled() {
		return currentPage > 1;
	}

	public boolean isPaginaPrecedenteEnabled() {
		return currentPage > 1;
	}
	
	public boolean isPaginaSuccessivaEnabled() {
		return totalPages > currentPage;
	}		

	public boolean isUltimaPaginaEnabled() {
		return totalPages > currentPage;
	}

	public int getFirstRecordInPage(){
		return (currentPage - 1)*numRecordsForPage + 1;
	}

	public int getLastRecordInPage(){
		int lastRecs = currentPage*numRecordsForPage;
		return (totalRecords < lastRecs) ? totalRecords : lastRecs;
	}
	
	public String gotoPage(int page) {
		if(page > 0 && page <= getTotalPages()) {
			int prevPage = currentPage; 
			currentPage = page;
			if(!docWayConsoleApvPaginatorInterface.gotoPage(currentPage, getFirstRecordInPage(), getLastRecordInPage()))
				currentPage = prevPage;
		}
		return null;
	}

	public String paginaSuccessiva() {
		if(isPaginaSuccessivaEnabled()) {
			currentPage++;
			if(!docWayConsoleApvPaginatorInterface.gotoPage(currentPage, getFirstRecordInPage(), getLastRecordInPage()))
				currentPage--;
		}
		return null;
	}

	public String paginaPrecedente() {
		if(isPaginaPrecedenteEnabled()) {
			currentPage--;
			if(!docWayConsoleApvPaginatorInterface.gotoPage(currentPage, getFirstRecordInPage(), getLastRecordInPage()))
				currentPage++;
		}
		return null;
	}

	public String ultimaPagina() {
		if(isUltimaPaginaEnabled()) {
			int prevPage = currentPage; 
			currentPage = totalPages;
			if(!docWayConsoleApvPaginatorInterface.gotoPage(currentPage, getFirstRecordInPage(), getLastRecordInPage()))
				currentPage = prevPage;
		}
		return null;
	}

	public String primaPagina() {
		if(isPrimaPaginaEnabled()) {
			int prevPage = currentPage; 
			currentPage = 1;
			if(!docWayConsoleApvPaginatorInterface.gotoPage(currentPage, getFirstRecordInPage(), getLastRecordInPage()))
				currentPage = prevPage;
		}
		return null;
	}

	//Metodo chiamato dalla pagina consoleAjaxNavigationBar in realta' avviene il tutto nel metodo setCurrentPage
	public String paginaSpecifica() {
		if(requestPage != currentPage)
			gotoPage(requestPage);
		return null;
	}
}
