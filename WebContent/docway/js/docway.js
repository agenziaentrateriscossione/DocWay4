var resetJobsIwx = true; // identifica se occorre resettare IWX in caso di reload della pagina o ritorno da chiamata ajax

$(document).ready(function() {
	// eventuale caricamento del plugin IWX
	try {
		if (loadIWX != undefined && typeof (loadIWX) == 'function')
			loadIWX();
	}
	catch (e) { }
	
	// eventuale caricamento del plugin SWF Upload (upload di files senza IWX)
	try {
		if (loadSWFUpload != undefined && typeof (loadSWFUpload) == 'function')
			loadSWFUpload();
	}
	catch (e) { }
});

function checkNumAllegato(obj) {
	if (obj != undefined && obj != null && obj.value == "0")
		obj.value = "0 - nessun allegato";
	return false;
}

// controllo dei duplicati eseguito all'uscita di un campo relativo a data o protocollo mittente (durante
// l'editing del form tramite chiamata ajax)
function verificaDuplicatiDocMittente() {
	var nomeMittenteInit = $('#templateForm\\:nomeMittenteInit').val();
	var dataProtMittenteInit = $('#templateForm\\:dataProtMittenteInit').val();
	var numProtMittenteInit = $('#templateForm\\:numProtMittenteInit').val();
	
	var newDataValue = $('#templateForm\\:dataProtMittente').val();
	var newNumProtValue = $('#templateForm\\:protMittente').val();
	var mittenteValue = $('#templateForm\\:nomeMittente_input').val();
	if (mittenteValue != '' && (newDataValue != '' || newNumProtValue != '')) {
		if (newDataValue != dataProtMittenteInit || newNumProtValue != numProtMittenteInit || mittenteValue != nomeMittenteInit) {
			$('#templateForm\\:duplicatoVerificato').val('false');
			
			$('#templateForm\\:nomeMittenteInit').val(mittenteValue);
			$('#templateForm\\:dataProtMittenteInit').val(newDataValue);
			$('#templateForm\\:numProtMittenteInit').val(newNumProtValue);
			
			$('#templateForm\\:existsDuplicatiBtn').trigger('click');
			$('#templateForm\\:protMittente').focus();
		}
	}
}

function confermaDuplicato() {
	window.opener.$('#templateForm\\:duplicatoVerificato').val('true');
	self.close();
}

function annullaDuplicato() {
	window.opener.$('#templateForm\\:duplicatoVerificato').val('false');
	self.close();
}

/*
 * salvataggio in bozza di un protocollo
 */
function salvaDocInBozza() {
	$('#templateForm\\:docBozza').attr('checked', true);
	$('#templateForm\\:sendMailRifInterniCheck').removeAttr('checked');
	
	$('#templateForm\\:saveBtn').trigger('click');
	return false; // per non attivare il link jsf
}

var checkDuplicatiDaSalva = false; // variabile utilizzata per identificare se il controllo dei duplicati e' stato avviato tramite pulsante 'salva' o dall'uscita da un campo

// controllo dei duplicati eseguito al salvataggio del documento
function controlloDuplicatiInSalvataggio(funzDocVerificaDuplicati) {
	if (funzDocVerificaDuplicati == 'Si' || funzDocVerificaDuplicati == 'Fine') {
		//var nomeMittenteInit = $('#templateForm\\:nomeMittenteInit').val();
		//var dataProtMittenteInit = $('#templateForm\\:dataProtMittenteInit').val();
		//var numProtMittenteInit = $('#templateForm\\:numProtMittenteInit').val();
		
		var newDataValue = $('#templateForm\\:dataProtMittente').val();
		var newNumProtValue = $('#templateForm\\:protMittente').val();
		var mittenteValue = $('#templateForm\\:nomeMittente_input').val();
		var duplicatoVerificato = $('#templateForm\\:duplicatoVerificato').val();
		
		if (mittenteValue != '' && (newDataValue != '' || newNumProtValue != '')) {
			if (duplicatoVerificato == 'false') {
				//if (newDataValue != dataProtMittenteInit || newNumProtValue != numProtMittenteInit || mittenteValue != nomeMittenteInit) {
					//eliminazione del class di caricamento sul pulsante 'save'
					$('#templateForm\\:saveBtn').removeClass('openwaitmsg');
					checkDuplicatiDaSalva = true;
					$('#templateForm\\:existsDuplicatiBtn').trigger('click');
					return false;
				//}
				//else {
				//	$('#templateForm\\:duplicatoVerificato').val('true');
				//}
			}
			
			//aggiunta del class di caricamento sul pulsante 'save'
			$('#templateForm\\:saveBtn').addClass('openwaitmsg');
		}
	}
	return true;
}

function apriPopupVerificaDuplicatiDoc() {
	var duplicatiPresenti = $('#templateForm\\:duplicatiDocPresenti').val();
	var duplicatoVerificato = $('#templateForm\\:duplicatoVerificato').val();
	if (duplicatiPresenti == 'true' && duplicatoVerificato == 'false') {
		$('#templateForm\\:apriPopupDuplicatiBtn').trigger('click');
		$('#templateForm\\:protMittente').focus();
	}
	else if (checkDuplicatiDaSalva) {
		// il controllo dei duplicati e' stato avviato tramite il pulsante 'Salva'. Non essendoci duplicati
		// viene chiamato nuovamente il pulsante salva
		$('#templateForm\\:saveBtn').trigger('click');
	}
	checkDuplicatiDaSalva = false;
}

function removeSelFromFasc(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare trasferimento di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function removeSelFromFascM(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare rimozione di " + tot + " document" + f + " da fascicolo?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmCambiaClassifDoc(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare cambio classificazione di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmTrasferimento(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare trasferimento di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmAssegnaCC(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare assegnazione dei CC a " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmCambiaClassifFasc(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare cambio classificazione di " + tot + " fascicol" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmCambiaClassifDoc(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare cambio classificazione di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmCambiaClassifDocMinuta(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare cambio classificazione della minuta di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function confirmRitiraBandoSelezione(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare il ritiro pubblicazione bandi di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function raccogli(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else { 
		var f = tot == 1 ? "o" : "i";
		alert(tot+ " document" + f + " raccolt" + f);
		return true;
	}
}

// visualizzazione ad albero del titolario di classificazione - espande tutti i rami
function espandiGerarchiaThesauro() {
	$(".showThesNodeInLevel").show();
	$(".showThesGerarchiaPlus").hide();
	$(".showThesGerarchiaMinus").show();
}

// visualizzazione ad albero del titolario di classificazione - contrae tutti i rami
function contraiGerarchiaThesauro() {
	$(".showThesNodeInLevel").hide();
	$(".showThesGerarchiaPlus").show();
	$(".showThesGerarchiaMinus").hide();
}

// apertura/chiusura dei nodi del titolario di classificazione
function openCloseGerarchiaThesauro(codBreadcrumbs, level) {
	level = parseInt(level) + 1;
	if ($(".parent_" + level + "[class^='thes_" + codBreadcrumbs + "']").is(":hidden")) {
		$("#cmd_open_img_" + codBreadcrumbs).hide();
		$("#cmd_close_img_" + codBreadcrumbs).show();
		$(".parent_" + level + "[class^='thes_" + codBreadcrumbs + "']").fadeIn();
	}
	else {
		$("div[class^='thes_" + codBreadcrumbs + "_']").fadeOut();
		$("span[id^='cmd_open_img_" + codBreadcrumbs + "']").show();
		$("span[id^='cmd_close_img_" + codBreadcrumbs + "']").hide();
	}
}

function checkTutti(btnId, msg) {
	var alertForTutti = $("#templateForm\\:cc_alertForTutti").val();
	if (alertForTutti != undefined && alertForTutti == 'true') {
		var ret = confirm(msg);
		if (ret == true) {
			$("#templateForm\\:cc_alertForTutti").val('false');
		}
	}
	$('.' + btnId).trigger('click');
}

function trasferisciMinuta() {
	if (!confirm('Per i documenti tra uffici sovrascrivere l\'originale?\n\'OK\' per trasferire l\'originale\n\'Annulla\' per trasferire la minuta'))
		$('#templateForm\\:trasferMinuta').val(true);
	else
		$('#templateForm\\:trasferMinuta').val(false);
	
	return true;
}

//variabili/metodi utilizzate/i per l'upload di files senza e con iwx
var iwxUtils = null;

// upload di files tramite swfUpload / jQuery
function initSWFU(uploadUrl, swfuType, loginUtente, matricolaUtente, cd, dbName, fileSizeLimit, fileQueueLimit, fileTypesDescriptions, fileTypes) {
	var swfuIdSelector = '#swfupload-files-control';
	var buttonPlaceholderId = 'swfuploadFilesHolder';
	var fileIdsSelector = '#templateForm\\:nFileId';
	var fileNamesSelector = '#templateForm\\:nFileName';
	var fileTitlesSelector = '#templateForm\\:nFileTitle';
	var addButtonSelector = '#templateForm\\:addFilesButton';
	if (swfuType == 'image') { // caso di upload di immagini
		swfuIdSelector = '#swfupload-images-control';
		buttonPlaceholderId = 'swfuploadImagesHolder';
		fileIdsSelector = '#templateForm\\:nImageId';
		fileNamesSelector = '#templateForm\\:nImageName';
		fileTitlesSelector = '#templateForm\\:nImageTitle';
		addButtonSelector = '#templateForm\\:addImagesButton';
	}
	
	$(swfuIdSelector).swfupload({
		upload_url: buildSWFuploadURL(uploadUrl),
		file_size_limit : fileSizeLimit,
		file_types : fileTypes,
		file_types_description : fileTypesDescriptions,
		file_upload_limit : fileQueueLimit,
		file_queue_limit : fileQueueLimit,
		flash_url : '/' + uploadUrl.split('/')[1] + '/docway/js/swfupload/swfupload.swf',
		//button_image_url : 'js/swfupload/button.png',
		//button_width : 60,
		//button_height : 22,
		//button_placeholder : $('#swfubutton')[0],
		button_placeholder_id : buttonPlaceholderId,
		button_width: 52,
		button_height: 22,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		debug: false//,
		//custom_settings : {something : "here"}
	});
	
	var swfuploadControl = $(swfuIdSelector);
	
	swfuploadControl.unbind('fileQueued');
	swfuploadControl.unbind('uploadComplete');
	swfuploadControl.unbind('queueComplete');
	//swfuploadControl.unbind('uploadError');
	swfuploadControl.unbind('uploadSuccess');
	
	swfuploadControl.bind('fileQueued', function(event, file) {
		var swfu = $.swfupload.getInstance(swfuIdSelector);
		swfu.addPostParam('login', loginUtente);
		swfu.addPostParam('matricola', matricolaUtente);
		swfu.addPostParam('_cd', cd);
		swfu.addPostParam('db', dbName);
		
		displayOpenwaitmsg();
		
        // start the upload once a file is queued
        $(this).swfupload('startUpload');
    })
    .bind('uploadComplete', function(event, file) {
    	// start the upload (if more queued) once an upload is complete
        $(this).swfupload('startUpload');
    })
    .bind('queueComplete', function(swfuObject, totFiles) {
    	hideOpenwaitmsg();
    	$(addButtonSelector).trigger('click');
    })
    /*.bind('uploadError', function(swfuObject, errorCode, message) {
    	// TODO gestire il caso di errore in upload
    })*/
    .bind('uploadSuccess', function(swfuObject, serverData, responseReceived) {
    	var remoteFile = getUploadRemoteFile(responseReceived);
    	if ($(fileIdsSelector).val().indexOf(remoteFile) == -1) {
	    	$(fileIdsSelector).val($(fileIdsSelector).val() + '|' + remoteFile);
	    	$(fileNamesSelector).val($(fileNamesSelector).val() + '|' + serverData.name);
	    	$(fileTitlesSelector).val($(fileTitlesSelector).val() + '|' + serverData.name);
    	}
    	
    	if (fileQueueLimit == '1') {
    		// caso di checkin di un allegato (nessuna coda perche' un unico file)
    		hideOpenwaitmsg();
        	$(addButtonSelector).trigger('click');
    	}
    });
}

// inizializzazione di IWX post caricamento della pagina
function initIwxAfterReload(url, jsessionid, login, matricola, customTupleName, db, resetJobs, actionToExec, width, height, fileTypes, imageTypes) {
	//alert("initIwxAfterReload");
	if (fileTypes == null || fileTypes == undefined || fileTypes == '')
		fileTypes = "Tutti i file (*.*),*.*";
	if (imageTypes == null || imageTypes == undefined || imageTypes == '')
		imageTypes = "File immagini (TIFF;JPEG;BITMAP;PNG),*.tif;*.tiff;*.jpg;*.jpeg;*.bmp;*.png,TIFF (*.tif;*.tiff),*.tif;*.tiff,JPEG (*.jpg;*.jpeg),*.jpg;*.jpeg,BMP (*.bmp),*.bmp,PNG (*.png),*.png";
	
	if (width == null || width == undefined || width == '')
		width = "100%";
	if (height == null || height == undefined || height == '') {
		var h = $(window).height();
		h = h - 130; // 100px sono di header del template
		var height =  h + "px";
	}
	
	// eventuale azione da compiere immediatamente dopo l'inizializzazione di IWX
	if (actionToExec == null || actionToExec == undefined)
		actionToExec = "";
	
	IwxRuntime.Client.loadIWX(url, jsessionid, login, matricola, customTupleName, db, resetJobs, actionToExec, width, height);
	IwxRuntime.Client.setFileTypesIWX(fileTypes);
	IwxRuntime.Client.setImageTypesIWX(imageTypes);
	
	resetJobsIwx = 'false';
}

// inizializzazione di IWX post chiamata AJAX
function initIwxAfterAjax(url, jsessionid, login, matricola, customTupleName, db, actionToExec, width, height, fileTypes, imageTypes) {
	//alert("initIwxAfterAjax");
	if (fileTypes == null || fileTypes == undefined || fileTypes == '')
		fileTypes = "Tutti i file (*.*),*.*";
	if (imageTypes == null || imageTypes == undefined || imageTypes == '')
		imageTypes = "File immagini (TIFF;JPEG;BITMAP;PNG),*.tif;*.tiff;*.jpg;*.jpeg;*.bmp;*.png,TIFF (*.tif;*.tiff),*.tif;*.tiff,JPEG (*.jpg;*.jpeg),*.jpg;*.jpeg,BMP (*.bmp),*.bmp,PNG (*.png),*.png";
	
	if (width == null || width == undefined || width == '')
		width = "100%";
	if (height == null || height == undefined || height == '') {
		var h = $(window).height();
		h = h - 130; // 100px sono di header del template
		var height =  h + "px";
	}
	
	// eventuale azione da compiere immediatamente dopo l'inizializzazione di IWX
	if (actionToExec == null || actionToExec == undefined)
		actionToExec = "";
	
	IwxRuntime.Client.loadIWX(url, jsessionid, login, matricola, customTupleName, db, false, actionToExec, width, height);
	IwxRuntime.Client.setFileTypesIWX(fileTypes);
	IwxRuntime.Client.setImageTypesIWX(imageTypes);
}

// recupero del nome del file uploadato dalla response di docway
function getUploadRemoteFile(uploadResponse) {
	var fileName = '';
	if (uploadResponse != null && uploadResponse != undefined && uploadResponse != '') {
		if (uploadResponse.indexOf("<--remotefile:") == -1)
	        return '';
		
		var index;
	    if (uploadResponse.indexOf("<--remotefile:WithSize:") != -1) {
	        index = uploadResponse.indexOf("<--remotefile:") + 23;
	        var index1 = uploadResponse.indexOf(";", index);
	        
	        // TODO gestire questo caso
	        //var index2 = uploadResponse.indexOf(";", index1 + 1);
	        //getForm("hxpForm").filesWithSize.value += uploadResponse.substring(index1 + 1, index2) + "\n";
	        
	        fileName = uploadResponse.substring(index, index1);
	    }
	    else {
	        index = uploadResponse.indexOf("<--remotefile:") + 14;
	        
	        fileName = uploadResponse.substring(index, uploadResponse.indexOf(";", index));
	    }
	    //alert(filehandle);
	    if ( uploadResponse == "UPLOAD_ERROR" )
	        return '';
	}
	return fileName;
}

// recupero del nome del file (temporaneo) uploadato dalla response di docway
function getUploadTempFile(uploadResponse) {
	var fileName = '';
	if (uploadResponse != null && uploadResponse != undefined && uploadResponse != '') {
		if (uploadResponse.indexOf("<--tempfile:") == -1)
	        return '';
		
		var index = uploadResponse.indexOf("<--tempfile:") + 12;
	        
		fileName = uploadResponse.substring(index, uploadResponse.indexOf(";", index));
	    
		//alert(filehandle);
	    if ( uploadResponse == "UPLOAD_ERROR" )
	        return '';
	}
	return fileName;
}

// apertura/chiusura della sezione relativa ai metadati
function showMetadata(id) {
	if ($("#"+id).is(":hidden"))
		$("#"+id).show();
	else
		$("#"+id).hide();
	
	return false;
}

// refresh di una pagina di showdoc di un documento
function refreshDocPage() {
	$('#templateForm\\:reloadPage').trigger('click');
}
function refreshDocPageWithDelay(delayInMilliseconds) {
	t = setTimeout("refreshDocPage()", delayInMilliseconds);
}

// copia di un link a documento negli appunti
function fnJsCopiaLinkDocInClipboard(url) {
    var wHost = vGetHost();
    var wPort = window.location.port;
    if (wPort.length > 0)
        wPort = ":" + wPort;
    var protocol = window.location.protocol;
    url = protocol + "//" + wHost + wPort + url;

    //DD 17/11/04 Lancia la copia in clipboard e cambia l'icona in una di conferma per tre secondi.
    if ( fnJsCopyToClipboard(url) ) {
        //TimedOutSimpleRollover('copy2clip', '/docway/images/icone/clipboard.gif', '/docway/images/icone/clipboard_ok.gif', 2000);
    }
}
//DD 5/5/6 Nel caso non è gestita la clipboard, permessa la selezione manuale.
function fnJsCopyToClipboard(sText) {
    //DD 17/11/04 Copia il testo passato nella clipboard di sistema. Solo IE > v.5
    Ret = true;
    if (window.clipboardData) {
        window.clipboardData.setData("Text", sText);
    }else{
        prompt("Impossibile accedere alla clipboard di sistema: Il browser o il sistema operativo non permettono la funzionalità.\nE' possibile copiare MANUALMENTE il testo dal campo sottostante:", sText); // TODO gestire il multilingua
        Ret = false;
    }
    return Ret;
}
function vGetHost() {
    /* DD 19/12/2005 - RW: 0031529
    var host = window.location.host;
    if ( host.indexOf(':') != -1 )
        host = host.substring(0, host.indexOf(':'));
    return host;
     */
    return window.location.hostname;
}

// invio tramite email di un link a documento
//DD 5/5/6 Non funzionava con Firefox, e con Eudora
function mailUrlTo(oggetto, data, url) {
    var wHost = vGetHost();
    var wPort = window.location.port;
    if (wPort.length > 0)
        wPort = ":" + wPort;
    var protocol = window.location.protocol;
    url = protocol + "//" + wHost + wPort + realEscape(url);

    var sToWrite = 'mailto:?subject=';
    sToWrite+= oggetto;
    sToWrite+= '&body=';
    sToWrite+= escape('Per visualizzare il documento  del ' + data + ':'); // TODO gestire il multilingua
    sToWrite+= escape('\r\n\r\n');
    sToWrite+= url;
    window.location = sToWrite;
    //oLink.href = 'mailto:a?subject=Maio&body=dfd';
    //return false;
}
/*
 * Created on: 28/11/05
 * by: Federico Grillini
 * organization: 3D Informatica
 * description: Questa funzione, spostata da 'filelist.js', effettua l'escaping della stringa passata come
 *              parametro.
 *              'realEscape' è particolarmente utile per escapare il contenuto di tuple da inserire in un URL.
 */
function realEscape(s) {
    var ret = escape(s);
    ret = ret.replace(/\+/g, '%2B');
    ret = ret.replace(/\*/g, '%2A');
    ret = ret.replace(/\@/g, '%40');
    ret = ret.replace(/\-/g, '%2D');
    ret = ret.replace(/\_/g, '%5F');
    ret = ret.replace(/\./g, '%2E');

    // Federico 02/12/08: introdotto escaping del carattere '?' [RW 0055649]
    ret = ret.replace(/\?/g, '%3F');

    return ret;
}

// warning all'operatore in caso di documento privo di allegati
function controlloWarningSeSenzaAllegato(active, msg) {
	if (active) {
		if (msg == null || msg == undefined || msg == '')
			msg = 'Non è stato indicato alcun documento informatico. Continuare con la registrazione?';
		
		if ($('#templateForm\\:xwFiles\\:0\\:fileName').val() == ''
				&& $('#templateForm\\:xwImages\\:0\\:fileName').val() == '') {
			return owmConfirm(msg);
		}
	}
	return true;
}

// inizializzazione di SWFUpload per upload su pagina di amministrazione di DocWay
function initAdmSWFU(swfuIdSelector, buttonPlaceholderId, swfuFileNameSelector, uploadUrl, loginUtente, matricolaUtente) {
	$("#" + swfuIdSelector).swfupload({
		upload_url: buildSWFuploadURL(uploadUrl),
		file_size_limit : '0',
		file_types : "*.*",
		file_types_description : "#{i18n['dw4.all_files']}",
		file_upload_limit : '0', // numero massimo di upload possibili (n di click con upload su sfoglia)
		file_queue_limit : '1', // numero massimo di file caricabili tramite una sola operazione
		flash_url : 'js/swfupload/swfupload.swf',
		//button_image_url : 'js/swfupload/button.png',
		//button_width : 60,
		//button_height : 22,
		//button_placeholder : $('#swfubutton')[0],
		button_placeholder_id : buttonPlaceholderId,
		button_width: 62,
		button_height: 29,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		debug: false//,
		//custom_settings : {something : "here"}
	});
	
	var swfuploadControl = $("#" + swfuIdSelector);
	
	swfuploadControl.unbind('fileQueued');
	swfuploadControl.unbind('uploadComplete');
	swfuploadControl.unbind('queueComplete');
	//swfuploadControl.unbind('uploadError');
	swfuploadControl.unbind('uploadSuccess');
	
	swfuploadControl.bind('fileQueued', function(event, file) {
		var swfu = $.swfupload.getInstance('#' + swfuIdSelector);
		swfu.addPostParam('login', loginUtente);
		swfu.addPostParam('matricola', matricolaUtente);
		swfu.addPostParam('tempUpload', 'true');
		
		displayOpenwaitmsg();
		
        // start the upload once a file is queued
        $(this).swfupload('startUpload');
    })
    .bind('uploadComplete', function(event, file) {
    	// start the upload (if more queued) once an upload is complete
        $(this).swfupload('startUpload');
    })
    .bind('queueComplete', function(swfuObject, totFiles) {
    	hideOpenwaitmsg();
    })
    .bind('uploadSuccess', function(swfuObject, serverData, responseReceived) {
    	var fileuploaded = getUploadTempFile(responseReceived);
    	$('#' + swfuFileNameSelector + 'Text').html(fileuploaded);
    	$('#templateForm\\:' + swfuFileNameSelector + 'Hidden').val(fileuploaded);
    	
    	hideOpenwaitmsg();
    });
}

// inizializzazione di SWFUpload per upload su pagina di caricamento dei workflow di Bonita (xdocwayproc)
function initWorkflowUploadSWFU(swfuIdSelector, buttonPlaceholderId, swfuFileNameSelector, fileType, fileTypeDescr, uploadUrl, loginUtente, matricolaUtente) {
	if (fileType == undefined || fileType == null || fileType == '')
		fileType = '*.*';
	if (fileTypeDescr == undefined || fileTypeDescr == null || fileTypeDescr == '')
		fileTypeDescr = "#{i18n['dw4.all_files']}";
	
	$("#" + swfuIdSelector).swfupload({
		upload_url: buildSWFuploadURL(uploadUrl),
		file_size_limit : '0',
		file_types : fileType,
		file_types_description : fileTypeDescr,
		file_upload_limit : '0', // numero massimo di upload possibili (n di click con upload su sfoglia)
		file_queue_limit : '1', // numero massimo di file caricabili tramite una sola operazione
		flash_url : 'js/swfupload/swfupload.swf',
		//button_image_url : 'js/swfupload/button.png',
		//button_width : 60,
		//button_height : 22,
		//button_placeholder : $('#swfubutton')[0],
		button_placeholder_id : buttonPlaceholderId,
		button_width: 62,
		button_height: 29,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		debug: false//,
		//custom_settings : {something : "here"}
	});
	
	var swfuploadControl = $("#" + swfuIdSelector);
	
	swfuploadControl.unbind('fileQueued');
	swfuploadControl.unbind('uploadComplete');
	swfuploadControl.unbind('queueComplete');
	//swfuploadControl.unbind('uploadError');
	swfuploadControl.unbind('uploadSuccess');
	
	swfuploadControl.bind('fileQueued', function(event, file) {
		var swfu = $.swfupload.getInstance('#' + swfuIdSelector);
		swfu.addPostParam('login', loginUtente);
		swfu.addPostParam('matricola', matricolaUtente);
		swfu.addPostParam('tempUpload', 'true');
		swfu.addPostParam('keepUserDir', 'true');
		displayOpenwaitmsg();
		
        // start the upload once a file is queued
        $(this).swfupload('startUpload');
    })
    .bind('uploadComplete', function(event, file) {
    	// start the upload (if more queued) once an upload is complete
        $(this).swfupload('startUpload');
    })
    .bind('queueComplete', function(swfuObject, totFiles) {
    	hideOpenwaitmsg();
    })
    .bind('uploadSuccess', function(swfuObject, serverData, responseReceived) {
    	var fileuploaded = getUploadTempFile(responseReceived);
    	$('#' + swfuFileNameSelector + 'Text').html(fileuploaded);
    	$('#templateForm\\:' + swfuFileNameSelector + 'Hidden').val(fileuploaded);
    	
    	hideOpenwaitmsg();
    });
}

function initPrintProfilesTypeSelection() {
	$("#typeSelection_" + $("#templateForm\\:hiddenTypeSelection").val()).attr('checked', true);
	$("input[class='typeSelectionRadio']").change(function() { changePrintProfilesTypeSelection($(this)); });
}

function changePrintProfilesTypeSelection(radio) {  
	$("#templateForm\\:hiddenTypeSelection").val(radio.get(0).value);
	$("#templateForm\\:setVerboBtn").trigger('click');
}

function initPrintProfilesProfileSelection() {
	$("#profileSelection_" + $("#templateForm\\:hiddenProfileSelection").val()).attr('checked', true);
	$("input[class='profileSelectionRadio']").change(function() { changePrintProfilesProfileSelection($(this)); });
}

function changePrintProfilesProfileSelection(radio) {
	$("#templateForm\\:hiddenProfileSelection").val(radio.get(0).value);
}

// stampa della segnatua di un documento tramite IWX
function stampaSegnaturaIWX(tipodoc, save) {
	var segnatura = $('#templateForm\\:testoStampaSegnatura').val();
	if (segnatura != '') {
		segnatura = decodeBase64(segnatura);
		
		var profilostampa = getProfiloStampa(tipodoc); // caricamento del corretto profilo di stampa
		
		// invio a IWX della stampa
		IwxRuntime.Client.printTextIWX(segnatura, save, profilostampa);
	}
	//return false; // per non attivare il commandLink JSF
}

// stampa delle info di un documento tramite IWX
function stampaInfoIWX(tipodoc) {
	var info = $('#templateForm\\:testoStampaInfo').val();
	if (info != '') {
		info = decodeBase64(info);
		
		var profilostampa = getProfiloStampa(tipodoc); // caricamento del corretto profilo di stampa
		
		// invio a IWX della stampa
		IwxRuntime.Client.printTextIWX(info, 'false', profilostampa);
	}
	//return false; // per non attivare il commandLink JSF
}

// dato il tipo di documento corrente, restituisce il relativo profilo di stampa
function getProfiloStampa(tipodoc) {
	if (tipodoc == undefined || tipodoc == null)
		tipodoc = '';
	//alert(tipodoc);
	
	var printProfile = '';
	
	if (tipodoc == 'arrivo')
		printProfile = 'sign_arrivo';
	else if (tipodoc == 'partenza')
		printProfile = 'sign_partenza';
	else if (tipodoc == 'interno')
		printProfile = "sign_interno";
	else if (tipodoc == 'varie')
		printProfile = "sign_varie";
	
	return printProfile;
}

// stampa del qrcode di un documento tramite IWX
function stampaQrcodeIWX() {
	var qrcode = $('#templateForm\\:testoStampaQrcode').val();
	if (qrcode != '') {
		qrcode = decodeBase64(qrcode);
		
		// richiesta a IWX della stampa del qrcode
		IwxRuntime.Client.printQrcodeIWX(qrcode);
	}
}

// aggiornamento delle preferenze di stampa della segnatura da profilo personale
function changePreferenzeStampaSegnatura(sezioneSegnatura) {
	var tipoStampa = $('#templateForm\\:' + sezioneSegnatura + '_select option:selected').val();
	//alert(tipoStampa);
	if (tipoStampa == 'endorscan') {
		$('#' + sezioneSegnatura + '_seriale').hide();
		$('#' + sezioneSegnatura + '_iwx').hide();
	}
	else if (tipoStampa == 'seriale') {
		$('#' + sezioneSegnatura + '_seriale').show();
		$('#' + sezioneSegnatura + '_iwx').hide();
	}
	else { // tipoStampa = ''
		$('#' + sezioneSegnatura + '_seriale').hide();
		$('#' + sezioneSegnatura + '_iwx').show();
	}
}

// cancellazione massiva di documenti da lista titoli
function deleteDocs(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento"); // TODO traduzioni
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare la cancellazione di " + tot + " document" + f + "?")) // TODO traduzioni
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

/*DOCWAY DELIBERE*/
//inizializzazione di SWFUpload per upload dei modelli durante la creazione di un organo (acl) e upload del testo della delibera in Docway Delibere
function initDeliberaSWFU(swfuIdSelector, buttonPlaceholderId, swfuFileNameSelector, uploadUrl, loginUtente, matricolaUtente, cd, dbName) {
	$("#" + swfuIdSelector).swfupload({
		upload_url: buildSWFuploadURL(uploadUrl),
		file_size_limit : '0',
		file_types : "*.rtf",
		file_types_description : "#{i18n['dw4.all_files']}",
		file_upload_limit : '0', // numero massimo di upload possibili (n di click con upload su sfoglia)
		file_queue_limit : '1', // numero massimo di file caricabili tramite una sola operazione
		flash_url : './../docway/js/swfupload/swfupload.swf',
		//button_image_url : 'js/swfupload/button.png',
		//button_width : 60,
		//button_height : 22,
		//button_placeholder : $('#swfubutton')[0],
		button_placeholder_id : buttonPlaceholderId,
		button_width: 62,
		button_height: 29,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		debug: false//,
		//custom_settings : {something : "here"}
	});
	
	var swfuploadControl = $("#" + swfuIdSelector);
	
	swfuploadControl.unbind('fileQueued');
	swfuploadControl.unbind('uploadComplete');
	swfuploadControl.unbind('queueComplete');
	//swfuploadControl.unbind('uploadError');
	swfuploadControl.unbind('uploadSuccess');
	
	swfuploadControl.bind('fileQueued', function(event, file) {
		var swfu = $.swfupload.getInstance('#' + swfuIdSelector);
		swfu.addPostParam('login', loginUtente);
		swfu.addPostParam('matricola', matricolaUtente);
		//swfu.addPostParam('tempUpload', 'true');
		swfu.addPostParam('_cd', cd);
		swfu.addPostParam('db', dbName);
		
		displayOpenwaitmsg();
		
      // start the upload once a file is queued
      $(this).swfupload('startUpload');
  })
  .bind('uploadComplete', function(event, file) {
  	// start the upload (if more queued) once an upload is complete
      $(this).swfupload('startUpload');
  })
  .bind('queueComplete', function(swfuObject, totFiles) {
  	hideOpenwaitmsg();
  })
	
 .bind('uploadSuccess', function(swfuObject, serverData, responseReceived) {
  	var remoteFile = getUploadRemoteFile(responseReceived);
  	$('#' + swfuFileNameSelector + 'Name').html(serverData.name);
  	$('#templateForm\\:' + swfuFileNameSelector + 'NameHidden').val(serverData.name);
  	$('#templateForm\\:' + swfuFileNameSelector + 'IdHidden').val(remoteFile);
  	hideOpenwaitmsg();
  });
}

/**
 * costruzione dell'url di uplado file tramite SWFUpload con gestione del forward a tomcat in caso di autenticazione integrata 
 * su IIS (dipende dai parametri impostati nel file docway.properties)
 * @param uploadUrl
 */
function buildSWFuploadURL(uploadUrl) {
	if (uploadUrl) {
		if (uploadUrl.indexOf("http") != 0) {
			if (uploadUrl.indexOf("/") != 0)
				uploadUrl = '/' + uploadUrl;
			uploadUrl = window.location.protocol + "//" + ((clientconfig.tomcatForwardHost) ? clientconfig.tomcatForwardHost : window.location.host) + ((clientconfig.tomcatForwardPort) ? ":" + clientconfig.tomcatForwardPort : "") + uploadUrl;
		}
	}
	return uploadUrl;
}
