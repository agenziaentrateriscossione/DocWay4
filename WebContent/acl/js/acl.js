$(document).ready(function() {
	disableCopyPasteOnPassword();
	
	evaluateAlfaRight();
});

$(window).load(function() {
	jsf.ajax.addOnEvent(function(data) {
		if (data.status == 'success') {
			disableCopyPasteOnPassword();
			
			evaluateAlfaRight();
		}
	});
});

/**
 * disabilita il copia/incolla sui campi secret (utilizzati per l'immissione delle password)
 */
function disableCopyPasteOnPassword() {
	$('input[type=password]').bind("cut copy paste",function(e) {
        e.preventDefault();
    });
}

function evaluateAlfaRight() {
	$('.form-control.alfa-right').change(function() {
		//alert("alfa-right onchange...");
		var alfaValue = $(this).val();
		//alert("alfa-value = " + alfaValue);
		
		var spanClass = 'ban glyphicons-pro icon';
		var liClass = 'block-item-disabled';
		if (alfaValue && alfaValue != '' && alfaValue != '*NHL*') {
			spanClass = 'ok glyphicons-pro icon';
			liClass = 'block-item-enabled';
		}
		
		// aggiornamento dell'icona associata al campo alfa
		var spanElement = $(this).parent().find('span');
		if (spanElement)
			spanElement.attr('class', spanClass);
		
		// aggiornamento dello stile del pulsante
		var liElement = $(this).closest('li')
		if (liElement)
			liElement.attr('class', liClass);
	});
}

function displayOpenwaitmsg() {
	showLoadingIndicator(); // chiamata a common.js
}

function hideOpenwaitmsg() {
	hideLoadingIndicator(); // chiamata a common.js
}

/**
 * apertura box di gestione diritti di ACL
 */
function lunchboxOpen(lunchID, label) {
	$("#clasp_" + lunchID + " a").attr("href", "javascript:lunchboxClose('" + lunchID + "', '" + label.replace('\'', '\\\'') + "');");
	var newImage = $("#clasp_" + lunchID + " span").attr("class").replace("glyphicon-plus", "glyphicon-minus");
	$("#clasp_" + lunchID + " span").attr("class", newImage);
	$("#lunch_" + lunchID).show();
}

/**
 * chiusura box di gestione diritti di ACL
 */
function lunchboxClose(lunchID, label) {
	$("#lunch_" + lunchID).hide();

	$("#clasp_" + lunchID + " a").attr("href", "javascript:lunchboxOpen('" + lunchID + "', '" + label.replace('\'', '\\\'') + "');");
	var newImage = $("#clasp_" + lunchID + " span").attr("class").replace("glyphicon-minus", "glyphicon-plus");
	$("#clasp_" + lunchID + " span").attr("class", newImage);
}

/**
 * in inserimento/modifica PEC aggiornamento del campo PORTA in base alla selezione del PROTOCOLLO
 */
function changeHostPort(protocolElement, portElement) {
	var protocol = $('#' + protocolElement + ' option:selected').val();
	if (protocol != null) {
		var port = '';
		if (protocol == 'pop3')
			port = '110';
		else if (protocol == 'pop3s')
			port = '995';
		else if (protocol == 'imap')
			port = '143';
		else if (protocol == 'imaps')
			port = '993';
		else if (protocol == 'smtp')
			port = '25';
		else if (protocol == 'smtps')
			port = '465';
		else if (protocol == 'smtp-tls')
			port = '587';

		if (port != '')
			$('#' + portElement).val(port);
	}
}

/**
 * apertura/chiusura della storia modifiche su record ACL
 */
function openCloseHistory() {
	if ($('#historyPanel').is(":hidden")) {
		$('#historyPanel').fadeIn();
		$('#hideHistoryLink').show();
		$('#showHistoryLink').hide();
	}
	else {
		$('#historyPanel').fadeOut();
		$('#hideHistoryLink').hide();
		$('#showHistoryLink').show();
	}

	return false;
}

/**
 * seleziona/deselezione opzione per assegnare all'ufficio in CC nelle mailBox
 */
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

/**
 * apertura box di gestione archiveGroup di Account
 */
function accountboxOpen(lunchID, label) {
	$("#clasp_" + lunchID + " a").attr("href", "javascript:accountboxClose('" + lunchID + "', '" + label.replace('\'', '\\\'') + "');");
	$("#lunch_" + lunchID).show();
}

/**
 * chiusura box di gestione archiveGroup di Account
 */
function accountboxClose(lunchID, label) {
	$("#lunch_" + lunchID).hide();
	$("#clasp_" + lunchID + " a").attr("href", "javascript:accountboxOpen('" + lunchID + "', '" + label.replace('\'', '\\\'') + "');");
}
