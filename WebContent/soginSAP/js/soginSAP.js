function DoneWithDownload(param, filename, status) {
	iw.showImage('iwContainer');
	soginSAPJS.iwDocumentCurrentPage = 1;
	soginSAPJS.updateDocumentPageNumber();
	soginSAPJS.checkPagesButtons();
};

function SoginSAPJS() {
	this.iwDocumentCurrentPage = 1;

	this.checkRepertoriBeforeSearching = function() {
//		var atLeastOne = false;
//
//		var checkboxes = $('.repertoriocheck');
//		$.each(checkboxes, function(index, value) {
//			if (value.checked)
//				atLeastOne = true;
//		});
//
//		if (!atLeastOne) {
//			openwaitmsg = false;
//			alert('Selezionare almeno un repertorio.');
//		}
//
//		return atLeastOne;
		return true;
	};

	this.getFileWithIW = function(url) {
		document.IW.DownloadParam(url, '', document.IW.downloadAsync);
	};

	this.updateDocumentPageNumber = function() {
		var totalPages = $('#totalPages');
		var pagesNum = iw.numPages();
		totalPages.html(' ' + pagesNum);
		var currPage = $('#currPage');
		currPage.html(' ' + soginSAPJS.iwDocumentCurrentPage);
	};
	
	this.firstPage = function() {
		if (soginSAPJS.iwDocumentCurrentPage > 1) {
			iw.firstPage();
			soginSAPJS.iwDocumentCurrentPage = 1;
			soginSAPJS.updateDocumentPageNumber();
			soginSAPJS.checkPagesButtons();
		}
	};
	
	this.prevPage = function() {
		if (soginSAPJS.iwDocumentCurrentPage > 1) {
			iw.prevPage();
			soginSAPJS.iwDocumentCurrentPage -= 1;
			soginSAPJS.updateDocumentPageNumber();
			soginSAPJS.checkPagesButtons();
		}
	};
	
	this.nextPage = function() {
		if (soginSAPJS.iwDocumentCurrentPage < iw.numPages()) {
			iw.nextPage();
			soginSAPJS.iwDocumentCurrentPage += 1;
			soginSAPJS.updateDocumentPageNumber();
			soginSAPJS.checkPagesButtons();
		}
	};
	
	this.lastPage = function() {
		if (soginSAPJS.iwDocumentCurrentPage < iw.numPages()) {
			iw.lastPage();
			soginSAPJS.iwDocumentCurrentPage = iw.numPages();
			soginSAPJS.updateDocumentPageNumber();
			soginSAPJS.checkPagesButtons();
		}
	};
	
	this.checkPagesButtons = function() {
		var firstBtn = document.getElementById('templateForm:firstPageBtn');
		var prevBtn = document.getElementById('templateForm:prevPageBtn');
		var nextBtn = document.getElementById('templateForm:nextPageBtn');
		var lastBtn = document.getElementById('templateForm:lastPageBtn');
		
		if (iw.numPages() == 1) {
			firstBtn.disabled = true;
			firstBtn.setAttribute("class", "iwbuttonfirstd");
			prevBtn.disabled = true;
			prevBtn.setAttribute("class", "iwbuttonprevpd");
			
			nextBtn.disabled = true;
			nextBtn.setAttribute("class", "iwbuttonnextpd");
			lastBtn.disabled = true;
			lastBtn.setAttribute("class", "iwbuttonlastd");
		}
		else if (soginSAPJS.iwDocumentCurrentPage == 1) {
			firstBtn.disabled = true;
			firstBtn.setAttribute("class", "iwbuttonfirstd");
			prevBtn.disabled = true;
			prevBtn.setAttribute("class", "iwbuttonprevpd");
			
			nextBtn.disabled = false;
			nextBtn.setAttribute("class", "iwbuttonnextp");
			lastBtn.disabled = false;
			lastBtn.setAttribute("class", "iwbuttonlast");
		}
		else if (soginSAPJS.iwDocumentCurrentPage == iw.numPages()) {
			firstBtn.disabled = false;
			firstBtn.setAttribute("class", "iwbuttonfirst");
			prevBtn.disabled = false;
			prevBtn.setAttribute("class", "iwbuttonprevp");
			
			nextBtn.disabled = true;
			nextBtn.setAttribute("class", "iwbuttonnextpd");
			lastBtn.disabled = true;
			lastBtn.setAttribute("class", "iwbuttonlastd");
		}
		else {
			firstBtn.disabled = false;
			firstBtn.setAttribute("class", "iwbuttonfirst");
			prevBtn.disabled = false;
			prevBtn.setAttribute("class", "iwbuttonprevp");
			
			nextBtn.disabled = false;
			nextBtn.setAttribute("class", "iwbuttonnextp");
			lastBtn.disabled = false;
			lastBtn.setAttribute("class", "iwbuttonlast");
		}
	};
	
	this.changeDocument = function(selectObj) {
		var filename = selectObj.options[selectObj.selectedIndex].value;
		javascript:soginSAPJS.getFileWithIW('http://${bean.doc.fileDownloadUrl}' + filename);
	};
	
	this.toggle = function(id, button) {
		var element = document.getElementById(id);
	    if(element.className == 'hidden') {
	      element.className = 'unhidden';
	      var img = document.getElementById('templateForm:historySapModify');
	      img.src = img.src.replace('addicon16.png', 'minusicon16.png');
	    } else {
	      element.className = 'hidden';
	      var img = document.getElementById('templateForm:historySapModify');
	      img.src = img.src.replace('minusicon16.png', 'addicon16.png');
	    }
	}
};

var soginSAPJS = new SoginSAPJS();