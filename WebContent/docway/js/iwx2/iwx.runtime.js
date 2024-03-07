var IwxRuntime = {
	version : '1.0.2'
};

/* Iwx Runtime Singleton */
IwxRuntime.Client = (function() {
	var iwx = null;

	// variabili utilizzate per l'upload di files a Docway tramite HTTP
	var hostHTTP = '';
	var userLogin = '';
	var userMatricola = '';
	var jSessionID = '';
	var customTupleName = '';
	var dbName = '';
	
	var customPortHTTP = ''; // da valorizzare su Windows in caso di autenticazione integrata
	var customHostHTTP = ''; // da valorizzare se l'host Tomcat e' diverso dall'host webserver (caso di autenticazione integrata)
	
	var resetJobRequired = 'false'; // se true richiesta di reset del job su init di iwx

	// tipologie di file e immagini (estensioni) che devono essere gestite con iwx
	var fileTypes = '*.*';
	var imageTypes = '*.*';
	
	var maxFileSize = 0; // eventuale dimensione massima in upload di files
	var maxImageSize = 0; // eventuale dimensione massima in upload di images

	// variabili utilizzate per l'upload/download dei file tramite IWX
	var guidsToUpload = null;
	var uploadListType = ''; // tipo di doc informatici gestiti in upload (xwFiles, xwImages)
	var uploadErrors = '';
	var currentDownloadIndex = ''; // indice del file (immagine) in scaricamento attraverso IWX
	
	// definisce l'eventuale azione successiva da compiere su un evento di terminazione di una precedente. Utilizzata
	// per eliminare i vari timeout di attesa che portavano a crash del browser (da ver. 2.1.7r di IWX)
	// azioni gestite:
	// - downloadCurrentImage
	// - openDocEditCurrentImage
	var nextAction = ''; 
	
	// definisce l'eventuale azione successiva da compiere su un evento di scansione terminata con successo. Utilizzata per
	// differenziare la scansione classica (con successivo upload del file) dall'aggiunta/sostituzione di pagine su un 
	// tiff multipagina tramite nuova scansione
	var nextActionAfterScan = '';
	
	// definisce l'eventuale azione successiva da compiere su un evento di upload di file terminato con successo
	var nextActionAfterUpload = '';
	
	// definisce l'eventuale azione successiva da compiere su un evento di stampa terminato con successo
	var nextActionAfterPrint = '';
	
	// definisce l'eventuale azione successiva da compiere su un evento di download di immagine/file
	var nextActionAfterDownlaod = '';
	

	/**
	 * ritorna true se IWX e' attivo, false altrimenti
	 */
	function isActiveIWX() {
		try {
			var active = false;
			if (iwx != undefined && iwx != null && iwx.getVersion() != undefined
						&& iwx.getVersion() != null && iwx.getVersion() != '' && iwx.getVersion() != 'Missing iwx!!!')
				active = true;
			return active;
		}
		catch(e) {
			return false;
		}
	}
	
	/**
	 * visualizzazione di un messaggio di errore
	 */
	function appendProcessMessage(text, newline, type) {
		var messages = $('#iwx_process_messages').html();
		if (text != undefined && text != null && text != '') {
			if (newline)
				text = text + '<br/>';

			if (type == undefined || type == null)
				type = "info";
			
			if (type == 'info')
				text = '<span class="iwx-process-message iwx-process-message-info">[INFO]</span> ' + text;
			else if (type == 'error')
				text = '<span class="iwx-process-message iwx-process-message-error">[ERROR]</span> ' + text;
			else if (type == 'warning')
				text = '<span class="iwx-process-message iwx-process-message-warning">[WARNING]</span> ' + text;
			
			if (messages == '')
				$('#iwx_process_messages').html(text);
			else
				$('#iwx_process_messages').html(messages + text);
		}
	}
	
	/**
	 * estrae le informazioni contenute nel qrcode associato ad un documento
	 */
	function extractQrCodeInfo(guid) {
		var qrcode = null;
		if (guid != undefined && guid != null) {
			var curFileDescriptor = iwx.getFileDescriptor(guid);
			if (curFileDescriptor != undefined && curFileDescriptor != null) {
				var qrcodevalue = curFileDescriptor['QRCODE'];
				//alert("QRCODE: " + qrcodevalue);
				if (qrcodevalue != null && qrcodevalue != undefined && qrcodevalue != '') {
					var qrcode = qrcodevalue.split("|");
					// se il qrcode non contiene tutte le informazioni viene restituito null
					if (qrcode.length != 4)
						qrcode = null;
				}
			}
		}
		
		return qrcode;
	}
	
	/**
	 * Confronta 2 versioni di IWX passate. Ritorna 0 se le due versioni sono identiche, 1 se la prima versione e' 
	 * maggiore della seconda, -1 se la seconda versione e' maggiore della prima.
	 */
	function compareIwxVersionNumbers(version1, version2) {
		var esito = 0;
		if (version1 && version2) {
			var arr1 = getIwxVersionNumber(version1).split(".");
			var arr2 = getIwxVersionNumber(version2).split(".");
			
			var i = 0;
			while (i < 3 && esito == 0) {
				var val1 = arr1[i];
				if (!val1)
					val1 = '0';
				var val2 = arr2[i];
				if (!val2)
					val2 = '0';
				
				if (parseInt(val1) > parseInt(val2))
					esito = 1;
				else if (parseInt(val1) < parseInt(val2))
					esito = -1;
				
				i++;
			}
			
		}
		return esito;
	}
	
	/**
	 * Ritorna il numero di versione di IWX (solo parte numerica della versione)
	 */
	function getIwxVersionNumber(version) {
		if (version) {
			var suffix = "r";
			var index = version.indexOf(suffix, version.length - suffix.length);
			if (index !== -1)
				version = version.substring(0, index);
		}
		return version;
	}

	/**
	 * restituisce il messaggio di scaricamento di IWX
	 */
	function getIwxDownloadMessage(msg, enablefilteralert) {
		// url di scaricamento e installazione IWX
		var downloadURL = hostHTTP;
		if (downloadURL.indexOf("fileupload") != -1)
			downloadURL = downloadURL.replace("/fileupload", "");
		
		// FIXME perche' era necessario eliminare la porta custom? c'erano casi nei quali non funzionava il download diretto da tomcat?
		//if (customPortHTTP != '' && downloadURL.indexOf(":" + customPortHTTP) != -1)
		//	downloadURL = downloadURL.replace(":" + customPortHTTP, "");
		
		downloadURL += "/common/iwx/iwx2/setup_iwx.msi";

		var msghtml = '<a href="' + downloadURL
				+ '">' + msg
				+ '</a> (NB. ricaricare la pagina dopo l\'installazione)'; // TODO multilingua
		if (enablefilteralert) {
			var wikiURL = 'http://wiki.3di.it/doku.php?id=documentazione_3di:ie_issues#filtro_sui_contenuti_activex_ie9'; // pagina WIKI 3DI
			if (typeof window.external.msActiveXFilteringEnabled != "undefined")
				msghtml = msghtml
						+ '<br/><strong>Nel caso IWX risulti installato</strong> verificare la configurazione di <strong>ActiveX Filtering</strong> del browser. Per maggiori informazione consultare la <a href="'
						+ wikiURL + '" target="blank_">pagina WIKI</a>';
			else 
				msghtml = msghtml 
						+ '<br/><strong>Nel caso IWX risulti installato</strong> verificare di non avere attivi dei filtri sui plugin/activeX. Per maggiori informazione consultare la <a href="' 
						+ wikiURL + '" target="blank_">pagina WIKI</a>';
		}
		
		// nel caso in cui l'area relativa a IWX non sia visualizzata (nessuna
		// immagine da caricare), viene resa visibile
		// in modo da mostrare il messaggio di download
		/*
		$("#iwx_holder").css("visibility", "visible");
		if ($("#iwx_holder").css("height") == '0px')
			$("#iwx_holder").css("height", "auto");
		if ($("#iwx_holder").css("width") == '0px')
			$("#iwx_holder").css("width", "auto");
		*/
		
		return msghtml;
	}
	
	/**
	 * visualizzazione di un messaggio di errore
	 */
	function showErrorMessage(texterror, level) {
		var errors = $('#iwx_error_messages').html();
		if (!level)
			level = 'error';
		if (texterror != undefined && texterror != null && texterror != '') {
			texterror = '<div class="' + level + '">' + texterror + '</div>';
			if (errors == '')
				$('#iwx_error_messages').html(texterror);
			else
				$('#iwx_error_messages').html(errors + texterror);
		}
	}
	
	/**
	 * attivazione del pulsante di applicazione delle modifiche su un file
	 * tiff (salvataggio post inserimento/sostituzione di pagine, cancellazione di
	 * pagine, spostamento di pagine, ecc.)
	 */
	function showApplyChangesBtn() {
		if (iwx.image != null) {
			iwx.image.adaptToWidth();
			showImageDescriptor();
		}
		$('#templateForm\\:iwx_apply_changes_btn').removeClass('disabled');
	}

	/**
	 * recupero dati relativi all'immagine visualizzata (gestione paginazione)
	 */
	function showImageDescriptor() {
		var descrImage = iwx.image.getImageDescriptor();
		var curPage = descrImage['PAGEACTUAL'];
		var maxPage = descrImage['PAGETOTAL'];

		$('#iwx-pages-curpage').html(curPage);
		$('#iwx-pages-maxpage').html(maxPage);

		if (curPage == 1) {
			$('#templateForm\\:iwx_first_page_btn').addClass('disabled');
			$('#templateForm\\:iwx_prev_page_btn').addClass('disabled');
		} else {
			$('#templateForm\\:iwx_first_page_btn').removeClass('disabled');
			$('#templateForm\\:iwx_prev_page_btn').removeClass('disabled');
		}
		if (curPage == maxPage) {
			$('#templateForm\\:iwx_last_page_btn').addClass('disabled');
			$('#templateForm\\:iwx_next_page_btn').addClass('disabled');
		} else {
			$('#templateForm\\:iwx_last_page_btn').removeClass('disabled');
			$('#templateForm\\:iwx_next_page_btn').removeClass('disabled');
		}

		$('#iwx-pages-buttons').show();
		//if (maxPage > 1)
			$('#iwx-update-buttons').show();
	}

	/**
	 * aggiunta di file con IWX tramite pulsante sfoglia o scanner
	 * 
	 * @param idLista identificativo della lista di file
	 * @param tipoAdd tipologia di add (hdd, twain)
	 * @param twainMode modalita' di scansione
	 */
	function addFileIWX(idLista, tipoAdd, twainMode) {
		if (isActiveIWX()) {
			if (tipoAdd == "TWAIN") {
				uploadListType = 'xwImages';
				if (twainMode == null || twainMode == undefined
						|| twainMode == '')
					twainMode = 'TWAIN_UI_MP'; // scansione multipagina con properties scanner
				iwx.twain.addFiles({
					SCAN:{ TYPE: twainMode }
				});
			} else { // tipoAdd = "HDD"
				uploadListType = idLista;
				if (idLista == "xwFiles")
					iwx.hdd.addFiles({
						Filter : [ fileTypes ]
					});
				else
					iwx.hdd.addFiles({
						Filter : [ imageTypes ]
					});
			}
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
	}

	/**
	 * upload del primo file selezionato tramite sfoglia o scanner e salvataggio
	 * su eXtraWay
	 */
	function uploadFilesIWX(isMergeAction, isImage) {
		if (isMergeAction)
			nextActionAfterUpload = 'updateFileAfterMerge';
		
		if (guidsToUpload != null && guidsToUpload.length > 0) {
			var guid = guidsToUpload.splice(0, 1); // estrazione del primo id dall'array
			if (guid != null && guid != '') {
				// chiamata al metodo di upload del file tramite IWX
				curGuid = guid[0];
				// alert('UploadFiles call: ' + curGuid);

				iwx.setParam(curGuid, 'HTTP.Host', hostHTTP);
				
				var compareResult = compareIwxVersionNumbers('2.2.10r', iwx.getVersion());
				if (compareResult == 0 || compareResult == -1) {
					// dimensione massima dei file in upload supportato solo dalla versione
					
					if (uploadListType && uploadListType == 'xwImages')
						iwx.setParam(curGuid, 'HTTP.MaxFileSize', maxImageSize);
					else
						iwx.setParam(curGuid, 'HTTP.MaxFileSize', maxFileSize);
				}
				
				iwx.http.uploadFiles([ curGuid ], {
					'matricola' : userMatricola,
					'login' : userLogin,
					'_cd' : customTupleName,
					'db' : dbName
				});
			}
		} else {
			hideOpenwaitmsg();

			// procedura di upload terminata (stampa di eventuali errori o
			// reload della sezione doc informatici della scheda del doc.)
			if (uploadErrors != null && uploadErrors != '') {
				alert('Errori in upload di files: ' + uploadErrors); // TODO il messaggio andrebbe gestito in multilingua
				uploadErrors = '';
			}

			// reload della sezione doc informatici
			var btnSelector = '#templateForm\\:addFilesButton';
			if (uploadListType == 'xwImages') { // caso di upload di immagini
				btnSelector = '#templateForm\\:addImagesButton';
			}
			$(btnSelector).trigger('click');
		}
	}

	/**
	 * funzione chiamata dall'evento di upload di un file completetato con
	 * successo STANDARD
	 */
	function uploadDoneIWX(uploadResponse) {
		var curFileDescriptor = iwx.getFileDescriptor(uploadResponse.GUIDS[0]);
		var xwayId = getUploadRemoteFile(uploadResponse.TEXT); // funzione presente in docway.js (utilizzata anche dall'upload classico)
		
		var iwxname = curFileDescriptor['NAME'].replace(curFileDescriptor['GUID'] + "_", "");
		var fileTitle = iwxname;
		if (curFileDescriptor['PATH'] != undefined && curFileDescriptor['PATH'] != null && curFileDescriptor['PATH'] != '') {
			var path = curFileDescriptor['PATH'];
			var index = path.lastIndexOf('/');
			if (index != -1)
				path = path.substring(index+1);
			fileTitle = path;
		}
		var fileName = curFileDescriptor['GUID'] + iwxname.substring(iwxname.lastIndexOf('.'));

		//alert("Upload result: " + xwayId + " - " + fileName + " - " + fileTitle);

		if (xwayId) {
			var idsSelector = '#templateForm\\:nFileId';
			var fNamesSelector = '#templateForm\\:nFileName';
			var fTitlesSelector = '#templateForm\\:nFileTitle';
			if (uploadListType == 'xwImages') { // caso di upload di immagini
				idsSelector = '#templateForm\\:nImageId';
				fNamesSelector = '#templateForm\\:nImageName';
				fTitlesSelector = '#templateForm\\:nImageTitle';
			}
			
			if ($(idsSelector).val().indexOf(xwayId) == -1) {
				$(idsSelector).val($(idsSelector).val() + '|' + xwayId);
				$(fNamesSelector).val($(fNamesSelector).val() + '|' + fileName);
				$(fTitlesSelector).val($(fTitlesSelector).val() + '|' + fileTitle);
			}
		}
		else {
			// TODO traduzioni
	  		alert("Riscontrato errore in fase di salvataggio del file " + fileTitle + ".\nContattare l'amministratore di sistema per maggiori informazioni.");
		}

		// se ci sono altri file da caricare viene chiamato nuovamente l'upload ...
		uploadFilesIWX(false);
	}
	
	/**
	 * funzione chiamata dall'evento di upload di un file completetato con
	 * successo in caso di aggiornamento di un file dovuto a mergeAction (es. sostituzione/aggiunta di 
	 * pagine ad un tiff multipagina da nuova scansione)
	 */
	function updateFileAfterMerge(uploadResponse) {
		//alert('updateFileAfterMerge CALLED!');
		
		var curFileDescriptor = iwx.getFileDescriptor(uploadResponse.GUIDS[0]);
		var xwayId = getUploadRemoteFile(uploadResponse.TEXT); // funzione presente in docway.js (utilizzata anche dall'upload classico)
		
		if (xwayId) {
			var iwxGuId = curFileDescriptor['GUID'];
			
			if (xwayId != null && xwayId != undefined && xwayId != '' && iwxGuId != null && iwxGuId != undefined && iwxGuId != '') {
				// aggiornamento dell'id xway al file sul quale e' stata eseguita l'azione di merge ...
				$('#templateForm\\:xwImages_' + currentDownloadIndex + '_xwayId').val(xwayId);
				$('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val(iwxGuId);
				
				// ... e successivo refresh dell'anteprima su iwx
				openImageIWX(currentDownloadIndex);
			}
			
			$('#templateForm\\:iwx_apply_changes_btn').addClass('disabled');
		}
		else {
			// TODO traduzioni
	  		alert("Riscontrato errore in fase di aggiornamento del file.\nContattare l'amministratore di sistema per maggiori informazioni.");
		}
		
		hideOpenwaitmsg();
	}

	/**
	 * recupero l'indice dell'immagine corrente
	 */
	function getCurrentImageIndex() {
		var curImageIndex = -1;
		var found = false;
		var i = 0;
		var curImageFileName = $('#templateForm\\:iwxImageSelector :selected')
				.val();
		var numImages = $('#templateForm\\:iwxImageSelector option').size();
		while (i != numImages && !found) {
			var fileName = $('#templateForm\\:xwImages_' + i + '_fileName')
					.val();
			if (fileName == curImageFileName) {
				curImageIndex = i;
				found = true;
			}
			i = i + 1;
		}

		return curImageIndex;
	}

	/**
	 * assegnazione delle tipologie di file che possono essere caricati tramite
	 * il plugin iwx
	 */
	function setFileTypesIWX(types) {
		if (types != undefined && types != null && types != '')
			fileTypes = types;
	}

	/**
	 * assegnazione delle tipologie di immagini che possono essere caricati
	 * tramite il plugin iwx
	 */
	function setImageTypesIWX(types) {
		if (types != undefined && types != null && types != '')
			imageTypes = types;
	}
	
	/**
	 * assegnazione della dimensione massima in upload di files tramite il plugin iwx
	 */
	function setMaxFileSize(size) {
		if (size != undefined && size != null)
			maxFileSize = size;
	}
	
	/**
	 * assegnazione della dimensione massima in upload di images tramite il plugin iwx
	 */
	function setMaxImageSize(size) {
		if (size != undefined && size != null)
			maxImageSize = size;
	}

	/**
	 * aggiunta di un file doc (sfoglia)
	 */
	function addDocIWX() {
		addFileIWX('xwFiles', 'HDD');
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * aggiunta di un file immagine (sfoglia)
	 */
	function addImageIWX() {
		addFileIWX('xwImages', 'HDD');
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * aggiunta di un file immagine (sfoglia)
	 * 
	 * @param twainMode
	 *            modalita' di scansione (singola pagine, pagine multiple,
	 *            scansione rapida, con preferenze, ecc.) TWAIN_NOUI: scansione
	 *            rapida, singola pagina TWAIN_UI: scansione con configurazione
	 *            scanner, singola pagina TWAIN_NOUI_MP: scansione rapida,
	 *            multipagina TWAIN_UI_MP: scansione con configurazione scanner,
	 *            multipagina
	 */
	function addImageTwainIWX(twainMode) {
		addFileIWX('xwImages', 'TWAIN', twainMode);
		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * acquisizione di immagini tramite barcode
	 */
	function aquireImagesByQrcode() {
		if (isActiveIWX()) {
			showLoadingIndicator(); // visaulizzazione dell'icona di attesa (inizio attivita' di acquisizione massiva)
			
			appendProcessMessage("Scansione dei documenti in corso...", true, "info");
			
			nextActionAfterScan = 'acquiredocs';
			iwx.twain.addFiles({SCAN:{TYPE:'TWAIN_UI_MP'}, QRCODE:{WORK:'TRUE', PERCENT_LEFT:0, PERCENT_TOP:0, PERCENT_RIGHT:30, PERCENT_BOTTOM:30, HISTOGRAM_LEVEL:100}})
		}
		else {
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
		}
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * selezione della sorgente per TWAIN
	 */
	function selectTwainSourceIWX() {
		if (isActiveIWX())
			iwx.twain.selectSource();
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * apertura del file di tipo doc con IWX
	 */
	function openDocIWX(indice) {
		if (isActiveIWX()) {
			currentDownloadIndex = indice;
			var guid = $('#templateForm\\:xwFiles_' + indice + '_iwxGuId')
					.val();
			iwx.showFile(guid);
		}
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * apertura del file immagine all'interno di IWX (viene impostato di default
	 * l'adattamento alla larghezza di IWX)
	 */
	function openImageIWX(indice) {
		if (isActiveIWX()) {
			currentDownloadIndex = indice;
			var guid = $('#templateForm\\:xwImages_' + indice + '_iwxGuId')
					.val();
			iwx.showFile(guid);

			if (iwx.image != null) {
				iwx.image.adaptToWidth();
				showImageDescriptor();
			}
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * download del file di tipo doc con IWX
	 */
	function downloadDocIWX(indice) {
		if (isActiveIWX()) {
			var guid = $('#templateForm\\:xwFiles_' + indice + '_iwxGuId')
					.val();
			if (guid != null && guid != undefined && guid != '') {
				// doc gia' scaricato lo apro direttamente
				openImageIWX(guid);
			} else {
				// doc ancora da scaricare
				displayOpenwaitmsg();

				currentDownloadIndex = indice;
				var fileName = $(
						'#templateForm\\:xwFiles_' + indice + '\\:fileName')
						.val();

				if (fileName != null && fileName != undefined && fileName != '')
					iwx.http.addFiles({
						FileList : [ fileName ]
					});
			}
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * download del file immagine all'interno di IWX (viene impostato di default
	 * l'adattamento alla larghezza di IWX)
	 */
	function downloadImageIWX(indice) {
		if (isActiveIWX()) {
			var guid = $('#templateForm\\:xwImages_' + indice + '_iwxGuId')
					.val();
			if (guid != null && guid != undefined && guid != '') {
				// doc gia' scaricato lo apro direttamente
				openImageIWX(indice);
			} else {
				// doc ancora da scaricare
				// displayOpenwaitmsg();

				currentDownloadIndex = indice;
				var fileName = $(
						'#templateForm\\:xwImages_' + indice + '_fileName')
						.val();

				if (fileName != null && fileName != undefined && fileName != '')
					iwx.http.addFiles({
						FileList : [ fileName ]
					});
			}
		}
		
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * download del file immagine corrente (showdoc con IWX, select con l'elenco
	 * delle immagini)
	 */
	function downloadCurrentImageIWX() {
		curImageIndex = getCurrentImageIndex();

		if (curImageIndex > -1)
			downloadImageIWX(curImageIndex);
		// else
		// alert(''); // TODO messaggio di errore in caso di immagine non
		// trovata?
	}
	
	/**
	 * eliminazione del riferimento ad un file caricato con IWX
	 */
	function removeFileIWX(idLista, indice) {
		if (isActiveIWX()) {
			var key = $(
					'#templateForm\\:' + idLista + '\\:' + indice
							+ '\\:fileName').val();
			if (key != '')
				iwx.removeFile(key);
		}
	}
	
	/**
	 * aggiornamento di una o piu' pagine del file corrente tramite nuova scansione 
	 * della/e pagina/e
	 */
	function replacePageFromScanner() {
		updateImagePagesFromScanner('0');
		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * aggiunta di una o piu' pagine del file corrente tramite nuova scansione 
	 * della/e pagina/e.
	 * le pagine scansionate vengono aggiunte dopo la pagina corrente selezionata su IWX
	 */
	function addPageFromScanner() {
		updateImagePagesFromScanner('1');
		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * eliminazione di una pagine da un file tiff tramite IWX
	 */
	function removeCurrentPage(message) {
		if (isActiveIWX()) {
			if (message != undefined && message != null && message != '') {
				if (!confirm(message)) {
					return false;
				}
			}
			
			var maxPage = $('#iwx-pages-maxpage').html();
			if (maxPage == '1') {
				alert('Non è possibile procedere con l\'eliminazione di una pagina da un documento di una sola pagina'); // TODO gestione traduzione
				return false;
			}
				
			// chiamata alla funzione di eliminazione della pagina corrente
			iwx.image.deleteCuttInMultipage();
		}
		
		return false;  // per non attivare il commandLink JSF
	}
	
	/**
	 * spostamento di una pagina all'interno di un tiff multipagina. Sposta la pagina
	 * corrente avanti o indietro di una posizione.
	 * 
	 * mode = 0 -> sposta INDIETRO di una posizione
	 * mode = 1 -> sposta AVANTI di una posizione
	 */
	function moveCurrentPage(mode) {
		if (isActiveIWX()) {
			if (mode == undefined || mode == null || (mode != '0' && mode != '1'))
				mode = '1'; // se il parametro mode non viene passato o viene passato con un valore errato si forza lo spostamento in avanti
			
			if (mode == '1')
				iwx.image.moveCurrInMultipageToRight();
			else
				iwx.image.moveCurrInMultipageToLeft();
		}
		
		return false;  // per non attivare il commandLink JSF
	}
	
	/**
	 * funzione che si occupa di avviare il salvataggio del file sul server. Utilizzata dopo la
	 * procedura di modifica di un file tiff multipagina tramite i comandi di IWX (inserimento/sostituzione
	 * di pagine, eliminazione di pagine, spostamento di pagine, ecc.)
	 */
	function applyChangesImageIWX() {
		displayOpenwaitmsg(); // visaulizzazione dell'icona di attesa
		
		guidsToUpload = $('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val().split(',');
		uploadFilesIWX(true);
		
		return false;  // per non attivare il commandLink JSF
	}
	
	/**
	 * aggiornamento di un file immagine.
	 * action = 0 -> replace di pagine
	 * action = 1 -> add di pagine
	 */
	function updateImagePagesFromScanner(action) {
		if (isActiveIWX()) {
			// indice dell'immagine corrente (visualizzata tramite IWX e sulla quale sostituire/appendere pagine da scanner)
			//alert('currentDownloadIndex: ' + currentDownloadIndex);
			
			if (!isNaN(currentDownloadIndex) && currentDownloadIndex > -1 && (action == '0' || action == '1')) {
				if (action == '0')
					nextActionAfterScan = 'replacePages';
				else
					nextActionAfterScan = 'addPages';
				addImageTwainIWX('TWAIN_UI_MP'); // apertura scansione con visualizzazione delle properties dello scanner
			}
			else {
				// richiesta non riconosciuta: messaggio di errore?
			}
		}
	}
	
	/**
	 * merge di file (aggiunta/sostituzione di pagine su un tiff multipagina)
	 * @param newGuid guid del file contenente le pagine da inserire/sostituire al file selezionato (es. guid derivante da scansione)
	 * @param action azione da compiere ('replacePages' = sostituzione di pagine, 'addPages' = aggiunta di pagine)
	 */
	function mergeFilesFromScanner(newGuid, action) {
		if (newGuid != null && newGuid != undefined && newGuid != '' && (action == 'replacePages' || action == 'addPages')) {
			if (action == 'replacePages')
				iwx.image.mergeMultipage(newGuid, false, 1);
			else
				iwx.image.mergeMultipage(newGuid, true, 2); // 2 = aggiunta della/e pagina/e dopo la corrente sul file selezionato
		}
	}
	
	/**
	 * completata la scansione dei documenti in acquisizione massiva. Upload dei files.
	 */
	function startUploadFromAcquireDocs(guids) {
		if (guids != undefined && guids != null) {
			var totguids = guids.length;
			var r = confirm("Trovati " + totguids + " documenti. Procedere con l'importazione?");
			if (r) {
				
				// aggiornamento dei dati relativi alla progressbar di upload
				$('#iwx_progress_bar').attr('aria-valuemax', totguids);
				$('#iwx_progress_bar').attr('aria-valuenow', '0');
				$('#iwx_progress_bar_curvalue').html('0');
				$('#iwx_progress_bar').css('width', '0%');
				
				guidsToUpload = guids.toString().split(',');
				appendProcessMessage("Avvio della procedura di importazione...", true, "info");
				acquireDocsFromScanner();
			}
			else {
				appendProcessMessage("Acquisizione massiva interrotta.", true, "warning");
				
				hideLoadingIndicator(); // eliminazione dell'icona di attesa (fine attivita' di acquisizione massiva)
			}
		}
	}
	
	/**
	 * acquisizione massiva di documenti da scanner
	 */
	function acquireDocsFromScanner() {
		nextActionAfterUpload = 'acquireDocsFromScanner';
		
		if (guidsToUpload != null && guidsToUpload.length > 0) {
			var guid = guidsToUpload.splice(0, 1); // estrazione del primo id dall'array
			if (guid != null && guid != '') {
				// chiamata al metodo di upload del file tramite IWX
				curGuid = guid[0];
				// alert('AcquireDocsFromScanner call: ' + curGuid);
				
				var qrcode = extractQrCodeInfo(curGuid);
				if (qrcode != null) {
					
					var docinfo = '';
					if (qrcode[1] == 'varie') {
						docinfo = "<strong>documento non protocollato del " + qrcode[3] + "</strong>";
					}
					else {
						if (qrcode[2] == '')
							docinfo = "<strong>documento in bozza del " + qrcode[3] + "</strong>";
						else
							docinfo = "<strong>protocollo " + qrcode[2] + " del " + qrcode[3] + "</strong>";
					}
					
					appendProcessMessage('Caricamento del ' + docinfo + " in corso... ", false, "info");
					
					iwx.setParam(curGuid, 'HTTP.Host', hostHTTP);
					var compareResult = compareIwxVersionNumbers('2.2.10r', iwx.getVersion());
					if (compareResult == 0 || compareResult == -1) {
						// dimensione massima dei file in upload supportato solo dalla versione
						iwx.setParam(curGuid, 'HTTP.MaxFileSize', maxImageSize);
					}
					iwx.http.uploadFiles([ curGuid ], {
						'matricola' : userMatricola,
						'login' : userLogin,
						'_cd' : customTupleName,
						'db' : dbName,
						'nrecord' : qrcode[0], 
						'verbo' : 'importqrcodes',
						'xverb' : ''
					});
				}
				else {
					// errore nel recupero del qrcode.. processo il documento successivo
					appendProcessMessage('Errore nel recupero del QR Code dal file ' + curGuid, true, "error");

					// aggiornamento della progressbar di acquisizione
					updateIwxProgressBar($('#iwx_progress_bar').attr('aria-valuenow') + 1, $('#iwx_progress_bar').attr('aria-valuemax'), false);
					
					acquireDocsFromScanner();
				}
			}
		} else {
			// nessun ulteriore file da caricare
			appendProcessMessage('Procedura di acquisizione massiva terminata.', true, "info");
			
			// aggiornamento della progressbar di acquisizione
			updateIwxProgressBar($('#iwx_progress_bar').attr('aria-valuenow') + 1, $('#iwx_progress_bar').attr('aria-valuemax'), true);
			
			hideLoadingIndicator(); // eliminazione dell'icona di attesa (fine attivita' di acquisizione massiva)
		}
	}
	
	/**
	 * funzione chiamata dall'evento di upload di un file completetato con successo in caso di 
	 * acquisizione massiva da scanner
	 */
	function uploadDoneInAcquireFromScanner(uploadResponse) {
		//alert('uploadDoneInAcquireFromScanner CALLED!');
		nextActionAfterUpload = '';
		
		//var curFileDescriptor = iwx.getFileDescriptor(uploadResponse.GUIDS[0]);
		var risposta = uploadResponse.TEXT;
		//alert(risposta);
		
		// aggiunta del messaggio di status dell'upload
		if (risposta == '<--response:SUCCESS;-->')
			appendProcessMessage('completato con <strong>SUCCESSO</strong>', true, '');
		else {
			var errordetails = '';
			var index = risposta.indexOf("<--remotefile:ERROR:") + 20;
			if (index != -1)
				errordetails = risposta.substring(index, risposta.indexOf(";", index));
	        appendProcessMessage('riscontrato <strong>ERRORE</strong>: ' + errordetails, true, '');
		}
		
		// aggiornamento della progressbar di acquisizione
		updateIwxProgressBar($('#iwx_progress_bar').attr('aria-valuenow') + 1, $('#iwx_progress_bar').attr('aria-valuemax'), false);
		
		// nuova chiamata a funzione di caricamento dei documenti (acquisizione massiva in corso)
		acquireDocsFromScanner();
	}
	
	/**
	 * funzione chiamata dall'evento di upload di un file completato con errori in caso di 
	 * acquisizione massiva da scanner
	 */
	function uploadFailedInAcquireFromScanner(uploadResponse) {
		//alert('uploadFailedInAcquireFromScanner CALLED!');
		nextActionAfterUpload = '';
		
		// aggiunta del messaggio di status dell'upload
		appendProcessMessage('riscontrato <strong>ERRORE</strong>: ' + uploadResponse, true, '');
		
		// aggiornamento della progressbar di acquisizione
		updateIwxProgressBar($('#iwx_progress_bar').attr('aria-valuenow') + 1, $('#iwx_progress_bar').attr('aria-valuemax'), false);
		
		// nuova chiamata a funzione di caricamento dei documenti (acquisizione massiva in corso)
		acquireDocsFromScanner();
	}
	
	/**
	 * aggiornamento della progressbar di iwx
	 */
	function updateIwxProgressBar(current, tot, complete) {
		if (complete) {
			$('#iwx_progress_bar').attr('aria-valuenow', tot);
			$('#iwx_progress_bar_curvalue').html(tot);
			$('#iwx_progress_bar').css('width', '100%');
		}
		else {
			var percentage = 100 * current / tot;
			
			$('#iwx_progress_bar').attr('aria-valuenow', current);
			$('#iwx_progress_bar_curvalue').html(current);
			$('#iwx_progress_bar').css('width', percentage+'%');
		}
	}

	//
	// //////////////////////////// Funzioni di visualizzazione delle immagini
	//

	/**
	 * prima pagina (in caso di visualizzazione di file tiff)
	 */
	function firstPageIWX() {
		if (isActiveIWX()) {
			iwx.image.firstPage();
			showImageDescriptor();
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * pagina precedente (in caso di visualizzazione di file tiff)
	 */
	function prevPageIWX() {
		if (isActiveIWX()) {
			iwx.image.prevPage();
			showImageDescriptor();
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * pagina successiva (in caso di visualizzazione di file tiff)
	 */
	function nextPageIWX() {
		if (isActiveIWX()) {
			iwx.image.nextPage();
			showImageDescriptor();
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * ultima pagina (in caso di visualizzazione di file tiff)
	 */
	function lastPageIWX() {
		if (isActiveIWX()) {
			iwx.image.lastPage();
			showImageDescriptor();
		}

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * visualizza l'immagine con dimensione originale
	 */
	function originalSizeIWX() {
		if (isActiveIWX())
			iwx.image.adaptToOriginalSize();

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * visualizza l'immagine adattandola all'altezza della pagina
	 */
	function adaptToPageIWX() {
		if (isActiveIWX())
			iwx.image.adaptToPage();

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * visualizza l'immagine adattandola alla larghezza della pagina
	 */
	function adaptToWidthIWX() {
		if (isActiveIWX())
			iwx.image.adaptToWidth();

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * ruota l'immagine di 90 gradi in senso antiorario
	 */
	function rotateLeftIWX() {
		if (isActiveIWX())
			iwx.image.rotateLeft();

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * ruota l'immagine di 90 gradi in senso orario
	 */
	function rotateRightIWX() {
		if (isActiveIWX())
			iwx.image.rotateRight();

		return false; // per non attivare il commandLink JSF
	}

	/**
	 * salva l'immagine corrente
	 */
	function saveImageIWX() {
		if (isActiveIWX()) {
			curImageIndex = getCurrentImageIndex();
			if (curImageIndex > -1) {
				// visto che l'immagine e' stata gia' scaricata perche' mostrata
				// in anteprima
				// su IWX ho gia' il relativo guID
				var guid = $('#templateForm\\:xwImages_' + curImageIndex + '_iwxGuId').val();
				if (guid != '')
					iwx.hdd.uploadFiles([ guid ]);
			}
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua

		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * salvataggio di tutte le immagini del documento (compatibilita' con vecchio IW, un file TIFF per
	 * ogni pagina scansionata)
	 */
	function saveAllImagesIWX() {
		if (isActiveIWX()) {
			displayOpenwaitmsg(); // visaulizzazione dell'icona di attesa (inizio attivita' di salvataggio)
			
			// array di GUID da salvare tramite IWX
			var guidarray = [];
			
			// scaricamento di tutte le immagini presenti sul documento
			var i = 0;
			var downloaded = true;
			var numImages = $('#templateForm\\:iwxImageSelector option').size();
			
			while (i < numImages && downloaded) {
				var fileName = $('#templateForm\\:xwImages_' + i + '_fileName').val();
				var guid = $('#templateForm\\:xwImages_' + i + '_iwxGuId').val();
				
				if (fileName != undefined && fileName != null && fileName != '') {
					// se il guid non e' valorizzato occorre scaricare il file. In caso contrario e' probabile che l'utente 
					// abbia gia' in precedenza selezionato il file per la visualizzazione (il file quindi risulta gia' scaricato)
					if (guid == '') {
						
						currentDownloadIndex = i;
						nextActionAfterDownlaod = 'saveAllImagesIWX';
						downloaded = false;
						
						iwx.http.addFiles({ FileList : [ fileName ] });
					}
					else {
						// aggiunta del guid dall'array delle immagini da stampare
						guidarray[guidarray.length] = guid;
					}
				}
				
				i = i + 1;
			}
			
			//alert(downloaded + ' - ' + i);
			if (downloaded) {
				// chiamata a salvataggio di IWX
				//alert("chiamata a salvataggio di IWX: " + guidarray);
				iwx.hdd.uploadFiles(guidarray);
				
				hideOpenwaitmsg(); // eliminazione dell'icona di attesa (fine attivita' di salvatagio)
			}
			
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
		
		return false; // per non attivare il commandLink JSF
	}

	/**
	 * stampa l'immagine corrente
	 */
	function printImageIWX() {
		if (isActiveIWX()) {
			curImageIndex = getCurrentImageIndex();
			if (curImageIndex > -1) {
				// visto che l'immagine e' stata gia' scaricata perche' mostrata
				// in anteprima
				// su IWX ho gia' il relativo guID
				var guid = $('#templateForm\\:xwImages_' + curImageIndex + '_iwxGuId').val();
				if (guid != '') {
					var guidarray = [ guid ];
					
					// caricamento del testo da stampare sull'immagine (es. dati di protocollo)
					var siTesto = $('#templateForm\\:iwxSITesto').val();
					if (siTesto != null && siTesto != undefined && siTesto != '') {
						// stampa immagine con testo
						iwx.printFile(guidarray, 'default', {"TEXTS":[{"TEXT": siTesto, "ANGLE": "90", "FONT_NAME": "Arial", "BOLD": "true", "PERCENT_TOP": "90", "PERCENT_LEFT": "1"}]});
					}
					else {
						// stampa immagine senza testo
						iwx.printFile(guidarray, 'default');
					}
				}
			}
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua

		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * stampa di tutte le immagini presenti sul documento (compatibilita' con vecchio IW, un file per
	 * ogni pagina scansionata)
	 */
	function printAllImagesIWX() {
		if (isActiveIWX()) {
			
			displayOpenwaitmsg(); // visaulizzazione dell'icona di attesa (inizio attivita' di stampa)
			
			// array di GUID da stampare tramite IWX
			var guidarray = [];
			
			// scaricamento di tutte le immagini presenti sul documento
			var i = 0;
			var downloaded = true;
			var numImages = $('#templateForm\\:iwxImageSelector option').size();
			
			while (i < numImages && downloaded) {
				var fileName = $('#templateForm\\:xwImages_' + i + '_fileName').val();
				var guid = $('#templateForm\\:xwImages_' + i + '_iwxGuId').val();
				
				if (fileName != undefined && fileName != null && fileName != '') {
					// se il guid non e' valorizzato occorre scaricare il file. In caso contrario e' probabile che l'utente 
					// abbia gia' in precedenza selezionato il file per la visualizzazione (il file quindi risulta gia' scaricato)
					if (guid == '') {
						
						currentDownloadIndex = i;
						nextActionAfterDownlaod = 'printAllImagesIWX';
						downloaded = false;
						
						iwx.http.addFiles({ FileList : [ fileName ] });
					}
					else {
						// aggiunta del guid dall'array delle immagini da stampare
						guidarray[guidarray.length] = guid;
					}
				}
				
				i = i + 1;
			}
			
			//alert(downloaded + ' - ' + i);
			if (downloaded) {
				// caricamento del testo da stampare sull'immagine (es. dati di protocollo)
				var siTesto = $('#templateForm\\:iwxSITesto').val();
				if (siTesto != null && siTesto != undefined && siTesto != '') {
					// stampa immagine con testo
					
					// TODO occorre eliminare dal testo di stampa della segnatura il riferimento al numero di pagina?
					
					// chiamata a stampa array di immagini con testo
					iwx.printFile(guidarray, 'default', {"TEXTS":[{"TEXT": siTesto, "ANGLE": "90", "FONT_NAME": "Arial", "BOLD": "true", "PERCENT_TOP": "90", "PERCENT_LEFT": "1"}]});
				}
				else {
					// stampa immagine senza testo
					
					// chiamata a stampa array di immagini con testo
					iwx.printFile(guidarray, 'default');
				}
				
				hideOpenwaitmsg(); // eliminazione dell'icona di attesa (inizio attivita' di stampa)
			}
			
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
		
		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * stampa di un testo tramite IWX
	 * @param text testo da stampare (formato JSON richiesto da IWX)
	 * @param salvasegnatura true se occorre salvare la segnatura (solo per segnatura, non info), false altrimenti
	 * @param profilostampa profilo di stampa da utilizzare
	 */
	function printTextIWX(text, salvasegnatura, profilostampa) {
		if (isActiveIWX()) {
			
			// parse JSON del/i testo/i da stampare tramite IWX
			//alert(text);
			if (text != null && text != undefined && text != '') {
				var reg_exp = new RegExp("/r/n",'g');
				if (compareIwxVersionNumbers('2.2.5r', iwx.getVersion()) == -1) {
					text = text.replace(reg_exp, "\\r\\n");
				}
				else {
					// versione di IWX pre 2.2.6r
					text = text.replace(reg_exp, " ");
				}
				text = text.replace(/\\'/g, "'");
				//alert('fixedText: ' + text)
				
				if (salvasegnatura == 'true')
					nextActionAfterPrint = 'salvaSegnatura';
				
				if (profilostampa == undefined || profilostampa == null || profilostampa == '')
					profilostampa = 'defaulttext';
				
				iwx.printFile(null, profilostampa, JSON.parse(text));
			}
			
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
	}
	
	/**
	 * stampa di un QR Code tramite IWX
	 * @param text testo da includere nel QR Code
	 * @param profilostampa profilo di stampa da utilizzare
	 */
	function printQrcodeIWX(text, profilostampa) {
		if (isActiveIWX()) {
			
			//alert(text);
			if (text != null && text != undefined && text != '') {
				text = text.replace(/\\'/g, "'");
				//alert('fixedText: ' + text)
				
				if (profilostampa == undefined || profilostampa == null || profilostampa == '')
					profilostampa = 'qrcodeprint';
				
				//alert('chiamata a stampa QR Code: ' + text);
				iwx.printFile(null, profilostampa, { QRCODE:{ TEXT:text, PERCENT_LEFT:5, PERCENT_TOP:5 } });
			}
			
		} else
			alert("Problema nel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
	}
	
	/**
	 * salvataggio delle impostazioni di stampa su IWX per un determinato profilo di stampa. Al
	 * momento viene anche avviata una stampa di prova
	 * N.B.: UTILIZZATO DALLA PROCEDURA DI STAMPA DELLA SEGNATURA (STAMPA ETICHETTE)
	 * 
	 * @param profilostampa profilo di stampa da salvare
	 */
	function configPrinterIWX(profilostampa) {
		if (isActiveIWX()) {
			
			var text = '{"TEXTS":['
				+ '{"TEXT": "Stampa di Prova", "FONT_SIZE": "10", "FONT_NAME": "Arial", "PERCENT_TOP": "10"},'
				+ '{"TEXT": "3D Informatica SRL", "FONT_SIZE": "12", "FONT_NAME": "Arial", "BOLD": "true", "PERCENT_TOP": "20"}'
				+ ']}';
			text = text.replace("/r/n", " ");
			text = text.replace(/\\'/g, "'");
			//alert('fixedText: ' + text)
				
			iwx.printFile(null, profilostampa, JSON.parse(text));
			
		} else
			alert("Configurazione della stampante non possibile, problema\nnel caricamento di IWX"); // TODO il messaggio andrebbe gestito in multilingua
		
		return false; // per non attivare il commandLink JSF
	}
	
	/**
	 * data una risposta ottenuta da IWX recupera il relativo messaggio di errore
	 */
	function getErrorText(resp) {
		var text = resp["TEXT"]; 
		if (!text)
			text = resp;
		return text;
	}
	
	/**
	 * dato un text di errore restituito da IWX, recupera il relativo codice. Se non è possibile svolgere 
	 * l'azione viene restituita la stringa vuota
	 */
	function getErrorCode(textError) {
		var code = "";
		if (textError) {
			// esempio di testo d'errore: "Error code:1016"
			var errorCodePrefix = "Error code:";
			if (textError.indexOf(errorCodePrefix == 0)) {
				code = textError.substring(errorCodePrefix.length).trim();
			}
		}
		return code;
	}
	
	/**
	 * dato il GUID relativo ad un file viene restituito il nome reale del file caricato tramite IWX
	 */
	function getFileNameByGUID(fileDescriptor) {
		var fileTitle = "";
		if (fileDescriptor) {
			var iwxname = fileDescriptor['NAME'].replace(fileDescriptor['GUID'] + "_", "");
			var fileTitle = iwxname;
			if (fileDescriptor['PATH'] != undefined && fileDescriptor['PATH'] != null && fileDescriptor['PATH'] != '') {
				var path = fileDescriptor['PATH'];
				var index = path.lastIndexOf('/');
				if (index != -1)
					path = path.substring(index+1);
				fileTitle = path;
			}
		}
		return fileTitle;
	}

	// DEFINIZIONE DEI METODI PUBBLICI
	ret = {

		/**
		 * definizione dei metodi handler di IWX
		 */
		iwxCreatedHandler : function() {
			//alert('iwxCreatedHandler called');

			// evento in caso di errore del plugin
			iwx.addEvent('Exception', function(error) {
				showErrorMessage(error);
				hideOpenwaitmsg();
				// alert(error);
			});
			
			// evento in caso di reset di IWX (utilizzato in caso debbano essere avviate azioni
			// al termine dell'inizializzazione del plugin)
			iwx.addEvent( 'ResetJobSuccessfully', function(x) {
				// esecuzione di eventuali azioni da compiere
				IwxRuntime.Client.performAction();
			});

			// eventi di gestione dell'upload di file tramite HTTP
			iwx.addEvent('HTTPUploadSuccessfully', function(resp) {
				if (nextActionAfterUpload == 'updateFileAfterMerge')
					updateFileAfterMerge(resp);
				else if (nextActionAfterUpload == 'acquireDocsFromScanner')
					uploadDoneInAcquireFromScanner(resp);
				else
					uploadDoneIWX(resp);
				nextActionAfterUpload = '';
			});
			iwx.addEvent('HTTPUploadFailed', function(resp) {
				if (nextActionAfterUpload == 'acquireDocsFromScanner') {
					uploadFailedInAcquireFromScanner(resp);
				}
				else {
					var text = getErrorText(resp);
					var errorCode = getErrorCode(text);
					if (!errorCode)
						errorCode = ""; // mi assicuro che errorCode sia almeno definito
					
					if (errorCode == "1016") { // dimensione del file eccessiva
						
						var errorText = "Impossibile procedere con l'upload del file; Dimensione eccessiva!";
						if (resp['GUIDS'] && resp['GUIDS'][0]) {
							var curFileDescriptor = iwx.getFileDescriptor(resp['GUIDS'][0]);
							var fileTitle = getFileNameByGUID(curFileDescriptor);
							if (fileTitle) {
								errorText = "Impossibile procedere con l'upload del file " + fileTitle + ";\nDimensione eccessiva!";
								if (curFileDescriptor['HTTP.MaxFileSize'])
									errorText += " [MAX = " + curFileDescriptor['HTTP.MaxFileSize'] + "KB]";
							}
						}
						alert(errorText);
						// se ci sono altri file da caricare viene chiamato nuovamente l'upload ...
						uploadFilesIWX(false);
					}
					// TODO altri codici d'errore da gestire
					else {
						// nessun codice d'errore individuato, caso di default
						showErrorMessage(text);
						if (uploadErrors == '')
							uploadErrors = text;
						else
							uploadErrors = uploadErrors + ", " + text;
					}
				}
				nextActionAfterUpload = '';
			});
			
			// eventi relativi a salvataggio di file (salva come...)
			iwx.addEvent('HDDUploadFailed', function(x) {
				showErrorMessage(x["TEXT"]);
			});

			// eventi di aggiunta file/immagini tramite pulsante sfoglia o
			// scanner
			iwx.addEvent('HDDDownloadSuccessfully', function(x) {
				// in caso di selezione multipla di file vengono restituiti
				// tutti i GUIDS separati da virgola
				if (x["GUIDS"] != null && x["GUIDS"] != '') {
					displayOpenwaitmsg();

					// alert(x["GUIDS"]);
					guidsToUpload = x["GUIDS"].toString().split(',');
					uploadFilesIWX(false);
				}
			});
			iwx.addEvent('HDDDownloadFailed', function(x) {
				// commentato perche' chiamato anche in caso di 'annulla'
				// alert("Errore nel caricamento del file:\nGUIDS = " +
				// x["GUIDS"] + "\nTEXT=" + x["TEXT"]);
				showErrorMessage(x["TEXT"]);
			});
			iwx.addEvent('TWAINDownloadSuccessfully', function(x) {
				// alert('TWAINDownloadSuccessfully called');
				if (x["GUIDS"] != null && x["GUIDS"] != '') {
					//alert(nextActionAfterScan + ' from TWAIN: ' + x["GUIDS"]);
					if (nextActionAfterScan == 'addPages' || nextActionAfterScan == 'replacePages') {
						// aggiunta/sostituzione di pagine derivanti da scansione al file corrente
						mergeFilesFromScanner(x["GUIDS"]+'', nextActionAfterScan);
					}
					else if (nextActionAfterScan == 'acquiredocs') {
						// acquisizione massiva di documento da qrcode
						startUploadFromAcquireDocs(x["GUIDS"]);
					}
					else {
						// caso di default: scansione classica con upload del file 
						displayOpenwaitmsg();
	
						// alert('Added with TWAIN: ' + x["GUIDS"]);
						guidsToUpload = x["GUIDS"].toString().split(',');
						uploadFilesIWX(false);
						// TODO sostituzione del record..
					}
					
					nextActionAfterScan = ''; // azzeramento del parametro nextActionAfterScan
				}
			});
			iwx.addEvent('TWAINDownloadFailed', function(x) {
				// commentato perche' chiamata anche in caso di 'annulla'
				// alert("Errore nel caricamento del file:\nGUIDS = " +
				// x["GUIDS"] + "\nTEXT=" + x["TEXT"]);
				showErrorMessage(x["TEXT"]);
			});
			
			// eventi relativi ad aggiunta/sostituzione/eliminazione/spostamento di pagine su tiff multipagina
			iwx.addEvent('MergeMultipageSuccessfully', function(x) {
				//alert('MergeMultipageSuccessfully DONE!');
				
				// alert('Added with TWAIN: ' + x["GUIDS"]);
				
				// TODO commentato perche' e' stato aggiunto lo specifico pulsante di salvataggio
				// se l'operazione di merge va a buon fine il file selezionato risulta aggiornato con le pagine 
				// aggiunte/sostituite. In questo caso occorre uploadare sul server il file
				
				//displayOpenwaitmsg();
				//guidsToUpload = $('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val().split(',');
				//uploadFilesIWX(true);
				
				// attivazione del pulsante di salvataggio del file (applicazione delle modifiche)
				showApplyChangesBtn();
				
			});
			iwx.addEvent('DeleteCurrInMultipageSuccessfully', function(x) {
				//alert('DeleteCurrInMultipageSuccessfully DONE!');
				
				// TODO commentato perche' e' stato aggiunto lo specifico pulsante di salvataggio
				// se l'operazione di merge va a buon fine il file selezionato risulta aggiornato con le pagine 
				// aggiunte/sostituite. In questo caso occorre uploadare sul server il file

				//displayOpenwaitmsg();
				//guidsToUpload = $('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val().split(',');
				//uploadFilesIWX(true);
				
				// attivazione del pulsante di salvataggio del file (applicazione delle modifiche)
				showApplyChangesBtn();
								
			});
			iwx.addEvent('MoveCurrInMultipageToLeftSuccessfully', function(x) {
				//alert('MoveCurrInMultipageToLeftSuccessfully DONE!');
				
				// TODO commentato perche' e' stato aggiunto lo specifico pulsante di salvataggio
				// se l'operazione di merge va a buon fine il file selezionato risulta aggiornato con le pagine 
				// aggiunte/sostituite. In questo caso occorre uploadare sul server il file

				//displayOpenwaitmsg();
				//guidsToUpload = $('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val().split(',');
				//uploadFilesIWX(true);
				
				// attivazione del pulsante di salvataggio del file (applicazione delle modifiche)
				showApplyChangesBtn();
								
			});
			iwx.addEvent('MoveCurrInMultipageToRightSuccessfully', function(x) {
				//alert('MoveCurrInMultipageToRightSuccessfully DONE!');
				
				// TODO commentato perche' e' stato aggiunto lo specifico pulsante di salvataggio
				// se l'operazione di merge va a buon fine il file selezionato risulta aggiornato con le pagine 
				// aggiunte/sostituite. In questo caso occorre uploadare sul server il file

				//displayOpenwaitmsg();
				//guidsToUpload = $('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val().split(',');
				//uploadFilesIWX(true);
				
				// attivazione del pulsante di salvataggio del file (applicazione delle modifiche)
				showApplyChangesBtn();
								
			});
			
			// eventi relativi al download di immagini
			iwx.addEvent('AddFilesSuccessfully', function(x) {
				// chiamato dopo la selezione del file con impostazione dei
				// parametri http (scatena il download vero e proprio del file
				// da DocWay)

				// recupero del contextPath dall'url di upload
				var downloadHostHTTP = hostHTTP.replace("/fileupload",
						"/filedownload");
				var downloadHost = downloadHostHTTP
						+ "?name="
						+ encodeURIComponent($('#templateForm\\:xwImages_' + currentDownloadIndex + '_fileName').val()) 
						+ "&title=" + encodeURIComponent($('#templateForm\\:xwImages_' + currentDownloadIndex + '_fileName').val().replace(/\//g, ''))
						+ "&login=" + userLogin + "&matricola="
						+ userMatricola + "&_cd=" + escape(customTupleName) + "&db=" + dbName; // TODO senza parametri in get non funziona (bug??)

				$('#templateForm\\:xwImages_' + currentDownloadIndex + '_iwxGuId').val(x["GUIDS"] + '');
				//currentDownloadIndex = ''; // TODO non dovrebbe essere necessario azzerare il valore... il file selezionato rimane sempre lo stesso.

				iwx.setParam(x["GUIDS"] + '', 'HTTP.Host', downloadHost);
				iwx.http.downloadFiles(x["GUIDS"], { // TODO non funziona il passaggio di parametri in downlod!!!
					'matricola' : userMatricola,
					'login' : userLogin,
					'_cd' : customTupleName,
					'db' : dbName
				});
			});
			iwx.addEvent('HTTPDownloadSuccessfully', function(x) {
				//alert('HTTPDownloadSuccessfully : ' + nextActionAfterDownlaod);
				if (nextActionAfterDownlaod == 'printAllImagesIWX') {
					// chiamata a download eseguito da procedura di stampa di tutte le 
					// immagini, ridireziono quindi al metodo di stampa delle immagini allegate al doc
					
					nextActionAfterDownlaod = '';
					printAllImagesIWX();
				}
				if (nextActionAfterDownlaod == 'saveAllImagesIWX') {
					// chiamata a download eseguito da procedura di salvataggio di tutte le 
					// immagini, ridireziono quindi al metodo di salvataggio delle immagini allegate al doc
					
					nextActionAfterDownlaod = '';
					saveAllImagesIWX();
				}
				else {
					// caso di default, visualizzazione del file tramite IWX
					
					hideOpenwaitmsg();
	
					iwx.showFile(x["GUIDS"] + ''); // si ipotizza che venga sempre scaricato un solo file e che debba essere visualizzato tramite IWX
	
					if (iwx.image != null) {
						iwx.image.adaptToWidth(); // TODO questa attivita' andrebbe eseguita solo in caso di apertura di immagini (attualmente l'unica possibile)
						showImageDescriptor(); // TODO questa attivita' andrebbe eseguita solo in caso di apertura di immagini (attualmente l'unica possibile)
					}
				}
			});
			iwx.addEvent('HTTPDownloadFailed', function(x) {
				// alert("HTTPDownloadFailed:\nGUIDS = " + x["GUIDS"] +
				// "\nTEXT=" + x["TEXT"]);
				showErrorMessage(x["TEXT"]);
				hideOpenwaitmsg();
			});
			
			// eventi relativi alla stampa
			iwx.addEvent( 'PrintFileSuccessfully', function(x) { 
				// visualizzazione temporanea dei messaggi di ritorno della stampa // TODO da decommentare in caso di problemi
				/*var errors = $('#iwx_error_messages').html();
				if (errors == '')
					$('#iwx_error_messages').html("PrintFileSuccessfully: " + x);
				else
					$('#iwx_error_messages').html(errors + "<br/>PrintFileSuccessfully: " + x);*/
				
				if (nextActionAfterPrint == 'salvaSegnatura') {
					$('#templateForm\\:salvaSegnatura').trigger('click');
				}
				
				nextActionAfterPrint = '';
			});
						
			if (resetJobRequired == 'true')
				IwxRuntime.Client.resetJobIWX(); // da eseguire solamente al primo caricamento della pagina (inizializzazione IWX, azzeramento precedenti file caricati)
			else
				IwxRuntime.Client.performAction(); // esecuzione di eventuali azioni da compiere in assenza di reset del job
			
			IwxRuntime.Client.displayIwxVersion();
		},
		
		iwxCreationFailedHandler : function() {
			//alert('iwxCreationFailedHandler called');
			IwxRuntime.Client.displayIwxVersion();
		},
		
		performAction : function() {
			var actionToPerform = nextAction;
			nextAction = ''; // azzeramento di nextAction
			
			// verifica della presenza di eventuali azioni da compiere
			if (actionToPerform != '') {
				if (actionToPerform == 'downloadCurrentImage') {
					downloadCurrentImageIWX();
				}
				else if (actionToPerform == 'openDocEditCurrentImage') {
					// caricamento dell'ultima immagine presente nella sezione di caricamento immagini
					// in docEdit/docEditModify
					var xwimages_length = $('.xwimageinput').length;
					if (xwimages_length > 1) {
						var lastGuid = $('#templateForm\\:xwImages_' + (xwimages_length-2) + '_iwxGuId').val();
						if (lastGuid != '')
							IwxRuntime.Client.openImageIWX(xwimages_length-2);
					}
				}
			}
		},

		/**
		 * caricamento del plugin IWX
		 */
		loadIWX : function(httpHost, jsessionid, login, matricola, cd, db, resetJob, actionToPerform, widthIWX, heightIWX, jobName) {
			try {
				//alert(clientconfig.tomcatForwardHost + ' - ' + clientconfig.tomcatForwardPort);
				customHostHTTP = clientconfig.tomcatForwardHost;
				customPortHTTP = clientconfig.tomcatForwardPort;
				
				hostHTTP = window.location.protocol + "//"
								+ (customHostHTTP != "" ? customHostHTTP : window.location.host) + (customPortHTTP != "" ? ":" + customPortHTTP : "") + httpHost;
				userLogin = login;
				userMatricola = matricola;
				jSessionID = jsessionid;
				customTupleName = cd;
				dbName = db;
				
				resetJobRequired = resetJob;
				nextAction = actionToPerform;

				if (iwx != null) {
					iwx.destroy();
					node = document.getElementById("iwx_holder");
					while (node && node.hasChildNodes()) {
						node.removeChild(node.lastChild);
					}
					iwx = null;
					if(window.console){
						console.log("destroy called");
					}
				}
				
				iwx = IWxApi.initialize("iwxId", "iwx_holder", {
					onCreated : this.iwxCreatedHandler,
					onCreationFailed: this.iwxCreationFailedHandler,
					width : widthIWX,
					height : heightIWX
				});

			} catch (err) {
				// IWX non supportato (err.message)
				alert(err);
			}
		},
		
		// visualizzazione della versione di IWX
		displayIwxVersion : function() {
			var version = '<span style=\'color:red;\'>error</span>';
			if (isActiveIWX()) {
				version = iwx.getVersion();
				
				if (compareIwxVersionNumbers($('#iwx_required_version').val(), version) > 0) {
					// richiesto aggiornamento di IWX
					showErrorMessage(getIwxDownloadMessage('E\' necessario aggiornare il plugin IWX almeno alla versione ' + $('#iwx_required_version').val(), false));
				}
				/*
				else {
					// controllo se la versione installata corrisponde all'ultima disponibile
					if ($('#iwx_current_version').val() != version) {
						var errors = $('#iwx_error_messages').html();
						if (errors.indexOf('(' + $('#iwx_current_version').val() + ')') == -1)
							showErrorMessage(getIwxDownloadMessage('E\' disponibile una nuova versione di IWX (' + $('#iwx_current_version').val() + ').', false) + ' Per i dettagli sulle nuove funzionalità consultare le "Informazioni su DocWay"', 'info');
					}
				}
				*/
			} else {
				showErrorMessage(getIwxDownloadMessage('Occorre scaricare e installare il plugin IWX', true));
			}
			$('#iwx_version').html(version);
		},

		/**
		 * eliminazione dei riferimenti a tutti i file caricati/gestiti con IWX.
		 * da chiamare al primo caricamento della pagina (per azzerare
		 * precedenti elaborazioni) o su pressione del pulsante 'Pulisci'
		 */
		resetJobIWX : function(jobName) {
			try {
				if (isActiveIWX()) {
					if (jobName == undefined || jobName == null)
						iwx.resetJob('');
					else
						iwx.resetJob(jobName);
				}
			} 
			catch (err) { /* occorre gestire questo caso? */ }
		},

		// ESPOSIZIONE DI METODI PRIVATI

		setFileTypesIWX : setFileTypesIWX,

		setImageTypesIWX : setImageTypesIWX,
		
		setMaxFileSize : setMaxFileSize,
		
		setMaxImageSize : setMaxImageSize,

		addDocIWX : addDocIWX,

		addImageIWX : addImageIWX,

		addImageTwainIWX : addImageTwainIWX,

		selectTwainSourceIWX : selectTwainSourceIWX,
		
		aquireImagesByQrcode : aquireImagesByQrcode,

		openDocIWX : openDocIWX,

		openImageIWX : openImageIWX,

		downloadDocIWX : downloadDocIWX,

		downloadImageIWX : downloadImageIWX,

		downloadCurrentImageIWX : downloadCurrentImageIWX,

		removeFileIWX : removeFileIWX,

		firstPageIWX : firstPageIWX,

		prevPageIWX : prevPageIWX,

		nextPageIWX : nextPageIWX,

		lastPageIWX : lastPageIWX,

		originalSizeIWX : originalSizeIWX,

		adaptToPageIWX : adaptToPageIWX,

		adaptToWidthIWX : adaptToWidthIWX,

		rotateLeftIWX : rotateLeftIWX,

		rotateRightIWX : rotateRightIWX,

		saveImageIWX : saveImageIWX,
		
		saveAllImagesIWX : saveAllImagesIWX,

		printImageIWX : printImageIWX,
		
		printAllImagesIWX: printAllImagesIWX,
		
		printTextIWX : printTextIWX,
		
		printQrcodeIWX : printQrcodeIWX,
		
		configPrinterIWX : configPrinterIWX, 
		
		replacePageFromScanner : replacePageFromScanner,
		
		addPageFromScanner : addPageFromScanner,
		
		removeCurrentPage: removeCurrentPage,
		
		moveCurrentPage : moveCurrentPage,
		
		applyChangesImageIWX : applyChangesImageIWX

	};

	return ret;

})();
