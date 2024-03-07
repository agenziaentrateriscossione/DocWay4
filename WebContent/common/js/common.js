$(document).ready(function() {
	var language = window.navigator.userLanguage || window.navigator.language;
	language = language.substring(0,2);

	backToTop();

	// mbernardini 30/05/2016 : recupero dinamico del nome della webapp (in base a URL richiesto)
	var appName = getApplicationName();

	//carica il locale per il datepicker
	if (!$.fn.datepicker.dates[language]) {
		$.getScript( "/" + appName + "/common/js/locales/bootstrap-datepicker." + language + ".js")
		.done(function( script, textStatus ) {
			activateDatePicker(language);
		})
		.fail(function( jqxhr, settings, exception ) {
			activateDatePicker(language);
		});
	}
	else
		activateDatePicker(language);
	
	activatePrependFields();

	//carica il locale per parsley.js
	$.getScript( "/" + appName + "/common/js/locales/parsleyjs/" + language + ".js")
	.done(function( script, textStatus ) {
		$.getScript( "/" + appName + "/common/js/locales/parsleyjs/" + language + ".extra.js")
	})

	activateSubmitEnterKeyCode();
	activateSubmitOnSingleInputEnterKeyCode();
	activatePopover();
	addErrorStyleToFields();
	limitShowdocHeight();
	preventDoubleClick();

	addModalOpenedStyles();

	activateCurrencyFields();

	activateLookupControl();

	activateHtmlEditor();

	activateDragAndDropSortable();
});

$(window).load(function() {
	jsf.ajax.addOnEvent(function(data) {
		if (data.status == 'begin') {
			showLoadingIndicator();
		}
		else if (data.status == 'complete') {
			hideLoadingIndicator();
		}
		else if (data.status == 'success') {
			if ($('.jsf-modal').length == 0) {
				$('body').removeClass('modal-open');
				$('.modal-backdrop').remove();

				// riattivato iwx (se presente nella pagina) in caso di chiusura di un popup
				//$('#iwx_holder').css('visibility', 'visible');
				$("#iwx_holder").css("width", "auto");
			}
			else if ($('.jsf-modal').length != 0) {
				addModalOpenedStyles();
			}

			// caricamento e posizionamento componenti affix dopo refresh ajax della pagina
			$('[data-spy="affix"]').each(function () {
				$(this).affix();
			});

			backToTop();

			var language = window.navigator.userLanguage || window.navigator.language;
			language = language.substring(0,2);
			activateDatePicker(language);
			
			activatePrependFields();

			activateSubmitEnterKeyCode();
			activateSubmitOnSingleInputEnterKeyCode();
			activatePopover();
			addErrorStyleToFields();
			limitShowdocHeight();
			preventDoubleClick();

			activateCurrencyFields();

			activateLookupControl();

			moveOnFocusField(); // focus su un campo specifico dopo un'operazione ajax sul form (es. lookup)
			moveByScrollbarPosition();

			activateHtmlEditor();

			activateDragAndDropSortable();

		}
	});
});

/**
 * Attivazione del plug-in sortable per le ul di classe "drag-and-drop-sortable"
 */
function activateDragAndDropSortable() {
	var $sel = $("ul.drag-and-drop-sortable");
	if($sel.length > 0) {
		var defaultHeight = 0;
		var	docsDragged = [];
		var docIndex = 0;
		var destinationIndex = 0;
		var afterLevel = 0;
		var numDocsDragged = 0;
		$sel.sortable({
			placeholder: '<li class="placeholder-sortable doc-raccoglitore"/>',
			tolerance: 0,
			distance: 5,
			onDragStart: function ($item, container, _super, event) {
				docIndex = $item.index();
				defaultHeight = $item.height();
				var level = $item.attr("level");
				var $next = $item.next();
				while ($next != null && $next.attr("level") > level) {
					docsDragged.unshift($next);
					$next.hide();
					$next = $next.next();
					numDocsDragged++;
				}
				numDocsDragged++;
				_super($item, container);
			},
			onDrag: function ($item, position, _super, event) {
				_super($item, position);
			},
			afterMove: function ($placeholder, container, $closestItemOrContainer) {
				$placeholder.height(defaultHeight * numDocsDragged);
			},
			onDrop: function ($item, container, _super, event) {
				_super($item, container);
				var $after = docsDragged.length > 0 ? docsDragged[0] : $item;
				for(var i = 0; i < docsDragged.length; i++) {
					var $next = docsDragged[i];
					$next.insertAfter($item);
					$next.show();
				}
				destinationIndex = $item.index();
				if($after.next() != null)
					afterLevel = $after.next().attr("level");
				else
					afterLevel = 0;
				callExecuteFormDragAndDrop(docIndex, destinationIndex, afterLevel);
				//reset
				defaultHeight = 0;
				docsDragged = [];
				docIndex = 0;
				destinationIndex = 0;
				afterLevel = 0;
				numDocsDragged = 0;
				saveScrollbarPosition();
			}
		});
	}
}

/**
 * Funzione che finge la chiamata asincrona alla gestione dell'update della lista indice eseguita col drag&drop
 */
function callExecuteFormDragAndDrop(docIndex, destinationIndex, afterLevel) {
	$("#templateForm\\:docIndex").val(docIndex);
	$("#templateForm\\:destinationIndex").val(destinationIndex);
	$("#templateForm\\:afterLevel").val(afterLevel);
	$("#templateForm\\:commandLinkDragAndDrop").click();
}

/**
 * Attivazione di eventuali editor HTML presenti all'interno della pagina (conversione di elementi textarea contenenti codice HTML in un iframe con
 * visualizzato l'output (editabile o meno)
 */
function activateHtmlEditor() {
	var appName = getApplicationName();

	try {
		if (typeof tinymce != undefined) {
			tinymce.remove();

			// campi editabili
			tinymce.init({
				selector:'.html-field',
				language: 'it',
				toolbar_items_size: 'small',
				plugins: [
					'code table',
					'textcolor'
				],
				// tiommi 19/03/2018 (aggiunto underline ai pulsanti disponibili nella toolbar)
				// tiommi 25/01/2019 (aggiunto reset format)
				toolbar: [
					"undo redo | fontselect fontsizeselect styleselect | forecolor backcolor | bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | selectall removeformat lol",
				],
				content_css: [
					'/' + appName + '/bootstrap/css/bootstrap.css',
					'/' + appName + '/common/css/dashboard.css',
					'/' + appName + '/common/css/common.css',
					'/' + appName + '/common/css/htmlpreview.css',
					getTinyMCECustomStyle() ? '/' + appName + '/common/css/' + getTinyMCECustomStyle() : ''
				],
				font_formats : //"Andale Mono=andale mono,times;"+
					"Arial=arial,helvetica,sans-serif;"+
					"Arial Black=arial black,avant garde;"+
					"Book Antiqua=book antiqua,palatino;"+
					"Comic Sans MS=comic sans ms,sans-serif;"+
					"Courier New=courier new,courier;"+
					"Georgia=georgia,palatino;"+
					"Helvetica=helvetica;"+
					"Impact=impact,chicago;"+
					//"Symbol=symbol;"+
					"Tahoma=tahoma,arial,helvetica,sans-serif;"+
					//"Terminal=terminal,monaco;"+
					"Times New Roman=times new roman,times;"+
					"Trebuchet MS=trebuchet ms,geneva;"+
					"Verdana=verdana,geneva;",
				//"Webdings=webdings;"+
				//"Wingdings=wingdings,zapf dingbats",
				fontsize_formats: '8pt 9pt 10pt 11pt 12pt 13pt 14pt 15pt 18pt 24pt 36pt',
				// tiommi 14/07/2017 (per i submit ajax ho bisogno che le textarea siano sempre sincronizzate con l'iframe del plugin)
				setup : function(editor) {
					editor.on("change keyup", function(e){
						editor.save(); // updates this instance's textarea
					});
					// editor.addButton('lol', {
					// 	icon: 'glyphicon glyphicon-envelope',
					// 	tooltip: "Insert LOL LOL",
					// 	onclick: function(){
					// 		alert('lmaoaoao');
					// 	}
					// });
				}
			});

			var editors = tinymce.get(".html-field");
			console.log(editors);

			// campi readonly
			tinymce.init({
				selector:'.html-readonly-field',
				language: 'it',
				readonly: true,
				toolbar: false,
				menubar: false,
				statusbar: false,
				content_css: [
					'/' + appName + '/bootstrap/css/bootstrap.css',
					'/' + appName + '/common/css/dashboard.css',
					'/' + appName + '/common/css/common.css',
					'/' + appName + '/common/css/htmlpreview.css',
					getTinyMCECustomStyle() ? '/' + appName + '/common/css/' + getTinyMCECustomStyle() : ''
				]
			});
		}
	}
	catch (e) { }
}

/**
 * Ritorna il nome dell'applicazione in base all'analisi dell'URL utilizzato per il caricamento
 * @returns {String}
 */
function getApplicationName() {
	var appName = "DocWay4";
	var pathname = window.location.pathname;
	//alert("pathname -> " + pathname);
	if (pathname) {
		if (pathname.indexOf("/") == 0)
			pathname = pathname.substring(1);

		var index = pathname.indexOf("/");
		if (index != -1)
			appName = pathname.substring(0, index);
	}
	//alert("appname -> " + appName);
	return appName;
}

/**
 * Torna il css con il quale overridare l'editor TinyMCE
 */
function getTinyMCECustomStyle() {
	var inputHidden = $("#overrideTinyMceCss");
	if (inputHidden) {
		return inputHidden.val();
	}
	return '';
}

// evita l'invio di richieste multiple a causa di doppio click sui pulsanti da parte degli utenti
function preventDoubleClick() {
	hideWaitMsgDiv(); // al caricamento della pagina occorre disattivare il div di wait (se attivo)

//	$(document).on("click", "a.btn.btn-primary", function() {
//		showLoadingIndicator();
//		showWaitMsgDiv();
//	});

	$(document).on("click", "a.openwaitmsg", function() {
		showLoadingIndicator();
		showWaitMsgDiv();
	});
}

// attivazione del div di wait (inibisce il click su tutti i pulsanti presenti nella pagina)
function showWaitMsgDiv() {
	$('#waitmsgdiv').css('display','block');
	$('#waitmsgdiv').addClass('prevent-doubleclick');
	//$('a.openwaitmsg').prop('disabled', true);
}

// disattivazione del div di wait (ripristina il click su tutti i pulsanti presenti nella pagina)
function hideWaitMsgDiv() {
	if ($('#waitmsgdiv').hasClass('prevent-doubleclick')) {
		$('#waitmsgdiv').css('display','none');
		$('#waitmsgdiv').removeClass('prevent-doubleclick');
		//$('a.openwaitmsg').prop('disabled', false);
	}
}

// visualizza l'indicatore di caricamento al centro della pagina
function showLoadingIndicator() {
	var loading = $('.loading-indicator-icon');
	var top = ($(window).height() / 2) + $(document).scrollTop();
	var left = ($(window).width() / 2) + $(document).scrollLeft();

	//alert(top + " - " + left);
	loading.css({
		left: left,
		top: top
	}).show();
}

// nasconde l'indicatore di caricamento dal centro della pagina
function hideLoadingIndicator() {
	$('.loading-indicator-icon').hide();
}

/**
 * aggiunge al body gli stili necessari alla gestione dei popup bootstrap nel
 * caso nella pagina risulti presente un popup aperto (da jsf senza javascript)
 */
function addModalOpenedStyles() {
	if ($('.jsf-modal').length != 0) {
		if (!$('body').hasClass('modal-open'))
			$('body').addClass('modal-open');

		if ($('.modal-backdrop').length == 0)
			$('body').append('<div class="modal-backdrop fade in"></div>');

		// viene nascosto iwx in caso di popup aperto perche' altrimenti rimarrebbe in primo
		// piano nascondendo cosi' il popup
		//$('#iwx_holder').css('visibility', 'hidden');
		$("#iwx_holder").css("width", "0px");

	}
}

/**
 *	Resetta la posizione che aveva la scrollbar prima della chiamata ajax e del refresh del fragment
 */
function moveByScrollbarPosition() {
	var scrollbarSelector = $("#scrollbarElementSelectorAfterAjax").val();
	var scrollbarTop = $("#scrollbarTopAfterAjax").val();

	if (scrollbarSelector && scrollbarTop) {
		$(scrollbarSelector).scrollTop(scrollbarTop);

		// reset dei campi di identificazione della posizione della scrollbar
		$("#scrollbarElementSelectorAfterAjax").val("");
		$("#scrollbarTopAfterAjax").val("");
	}
}

/**
 * Salva le variabili necessarie al ripristino della posizione della scrollbar dopo una chiamata ajax
 * @param elementSelector (selettore del fragmet con scrollbar)
 */
function setScrollBarPosition(elementSelector) {
	if(elementSelector) {
		$("#scrollbarElementSelectorAfterAjax").val(elementSelector);
		$("#scrollbarTopAfterAjax").val($(elementSelector).scrollTop());
	}
}

/**
 * Funzione mock per tutti i template che non hanno implementata la saveScrollbarPosition()
 * @returns
 */
function saveScrollbarPosition() {
	return null;
}



/**
 * focus su un campo specifico dopo un'operazione che comporta il refresh ajax di
 * tutto il form
 */
function moveOnFocusField() {
	var focusFieldId = $("#globalFocusFieldAfterAjax").val();
	// e' richiesto il focus su un campo e nessun popup e' attivo
	if (focusFieldId != '' && !$('body').hasClass('modal-open')) {
		try {
			log("info", "moveOnFocusField-> focusFieldId = " + focusFieldId);

			var current = $("#" + focusFieldId.replace(/:/g, "\\:"));
			var inputs = current.closest("form").find(".form-control").not(':input[readonly]');
			console.info("info", "moveOnFocusField-> input.length = " + inputs.length);
			var titlechecks = current.closest("form").find(".title-check");
			log("info", "moveOnFocusField-> titlechecks.length = " + titlechecks.length);

			var currentInput = inputs.index(current);
			log("info", "moveOnFocusField-> currentInput = " + currentInput);

			var currentTitle = titlechecks.index(current);
			log("info", "moveOnFocusField-> currentTitle = " + currentTitle);

			var focusId = "";
			if (currentInput != -1) {
				// focus su campo input di docedit
				var inputPos = currentInput+1;
				if (inputPos >= inputs.length)
					inputPos = currentInput;
				log("info", "moveOnFocusField-> inputPos = " + inputPos);

				if (inputs.eq(inputPos) && inputs.eq(inputPos).attr("id"))
					focusId = inputs.eq(inputPos).attr("id");
			}
			else if (currentTitle != -1) {
				// focus su campo check di una lista titoli
				var titlePos = currentTitle+1;
				if (titlePos >= titlechecks.length)
					titlePos = currentTitle;
				log("info", "moveOnFocusField-> titlePos = " + titlePos);

				if (titlechecks.eq(titlePos) && titlechecks.eq(titlePos).attr("id"))
					focusId = titlechecks.eq(titlePos).attr("id");
			}

			if (focusId != undefined && focusId != null && focusId != "") {
				log("info", "moveOnFocusField-> move to " + focusId);
				//$("#" + focusId).focus();

				// mbernardini 17/03/2016 : fix IE11 focus problem
				window.setTimeout(function() {
					var find = ':';
					var re = new RegExp(find, 'g');
					$("#" + focusId.replace(re, '\\:')).focus();
				}, 50);
			}
		}
		catch (e) { }

		$("#globalFocusFieldAfterAjax").val(""); // reset del campo di identificazione del focus
	}
}

/**
 * impostazione del campo globale sul quale impostare il focus (es. dopo un'attivita' di lookup)
 */
function setGlobalFocusFieldId(fieldId) {
	if (fieldId != null && fieldId != undefined && fieldId != '') {
		// se il nome del campo termina con '_button' (pulsante lookup) viene fatto il replace a '_input'
		// in modo da puntare al campo di input di lookup
		var re = new RegExp('_button$');
		var fieldId = fieldId.replace(re, '_input');
		//alert(fieldId);

		$('#globalFocusFieldAfterAjax').val(fieldId);

		// azzeramento dei campi hidden relativi al focus su lookup o thesauri
		$('#templateForm\\:focusElementLookup').val('');
		$('#templateForm\\:focusElementTh').val('');
	}
}

/**
 * dato un campo imposta l'id come id globale di focus per un ritorno da chiamata ajax
 */
function setFieldAsGlobalFocusFieldId(field) {
	if (field != null && field != undefined) {
		var id = field.id;
		//alert(id);
		if (id != null && id != undefined && id != '')
			$('#globalFocusFieldAfterAjax').val(id);
	}
}

/**
 * formattazione dei campi di tipo valuta
 */
function activateCurrencyFields() {
	$('.currencyField').blur(function() {
		var value = $(this).val();
		//alert('input value = ' + value);
		if (value != '') {
			var dotsMatch = value.match(new RegExp('\\.','g'))
			if ((dotsMatch != null && dotsMatch.length > 1) || (value.indexOf(".") != -1 && value.indexOf(",") != -1)) // punto utilizzato come separatore delle migliaia
				value = value.replace(/\./g, '');
			//alert('cleaned value = ' + value);

			if (value.indexOf(".") == -1)
				value = value.replace(',', '.');
			//alert('final value = ' + value);

			$(this).val(value);
		}
		//alert($(this).val());
		$(this).formatCurrency({ symbol: '', negativeFormat: '-%s%n', digitGroupSymbol: '', roundToDecimalPlace: 2 });
	});
}

/**
 * in caso di scroll, visualizza il link in basso a dx per tornare all'inizio della pagina
 */
function backToTop() {
	var offset = 220;
	var duration = 100; //500;
	$(window).scroll(function() {
		if ($(this).scrollTop() > offset) {
			$('.back-to-top').fadeIn(duration);
		} else {
			$('.back-to-top').fadeOut(duration);
		}
	});

	$('.back-to-top').click(function(event) {
		event.preventDefault();
		$('html, body').animate({scrollTop: 0}, duration);
		return false;
	});
}

var advancedSearchActivated = false; // utilizzata per evitare il submit multiplo (solo su IE) in caso di sezioni del form caricate tramite AJAX

/**
 * submit di un form in base alla pressione del tasto invio su un input di tipo
 * text senza la presenza di un button submit (utilizzo di link con id specifico)
 */
function activateSubmitEnterKeyCode() {
	var submitElements = $('.submitEnterKey');
	submitElements.each(function(index) {
		var submitId = $(this).attr('id');
		$('#' + submitId + ' input[type=text]').bind('keypress', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13) { // Enter keycode
				if (!advancedSearchActivated) {
					$('#templateForm\\:' + submitId + 'Btn').trigger('click');
					advancedSearchActivated = true;
				}
				return false;
			}
		});
	});
}

/**
 * ritorna true se la direzione del testo e' 'Right To Left', false se 'Left To Right'
 */
function isRTL() {
	if ($('body').hasClass('rtl'))
		return true;
	else
		return false;
}

/**
 * attivazione dei calendari su input di selezione delle date
 */
function activateDatePicker(language) {
	$('.input-group.date.free').datepicker({
		//autoclose: true, // se viene attivato autoclose non funziona l'evento onblur sul campo (es. verificaDuplicati su arrivo)
		todayHighlight: true,
		rtl: isRTL(),
		language: language,
		forceParse: false
	});
	$('.input-group.date.fromToday').datepicker({
		//autoclose: true, // se viene attivato autoclose non funziona l'evento onblur sul campo (es. verificaDuplicati su arrivo)
		todayHighlight: true,
		startDate: getFormattedDate(new Date()),
		rtl: isRTL(),
		language: language,
		forceParse: false
	});
	
	$('.input-group.date.custom').each(function( index ) {
		var startDate = $(this).attr('data-min-value');
		var endDate = $(this).attr('data-max-value');
		
		$(this).datepicker({
			//autoclose: true, // se viene attivato autoclose non funziona l'evento onblur sul campo (es. verificaDuplicati su arrivo)
			todayHighlight: true,
			startDate: startDate,
			endDate: endDate,
			rtl: isRTL(),
			language: language,
			forceParse: false
		});
	});
}

function getFormattedDate(data) {
	// TODO andrebbero gestiti differenti formati (al momento forzato a DD/MM/YYYY), magari utilizzando moment.js
	if (!data)
		data = new Date();
	var dd = data.getDate();
	var mm = data.getMonth() + 1; //January is 0!
	var yyyy = data.getFullYear();
	if(dd < 10)
		dd='0' + dd;
	if(mm < 10)
		mm='0' + mm;
	return dd + '/' + mm + '/' + yyyy;
}

/**
 * attivazione di campi di tipo prepend (custom fields)
 */
function activatePrependFields() {
	$('div[class*=\'prepend-field\']').each(function( index ) {
		var prependValue = $(this).attr('data-prepend-value');
		var prependFill = $(this).attr('data-prepend-fill');
		
		$(this).children('input[type=\'text\']').blur(function() {
			var value = $(this).val();
			if (prependValue && value) {
				var maxlength = Number($(this).attr('maxlength'));
				if (prependFill && maxlength) { // prepend-fill abilitato solo se maxlength e' defenito
					// aggiunta del prefisso fino al raggiungimento 
					// della lunghezza massima del campo
					while (value.length < maxlength)
						value = prependValue + value;
				}
				else {
					// aggiunta del prefisso solo una volta
					if (!maxlength || value.length < maxlength) {
						if (!value.startsWith(prependValue))
							value = prependValue + value;
					}
				}
				$(this).val(value);
			}
		});
	});
}

/**
 * attivazione del submit (click su pulsante nascosto) in caso di pressione del tasto Enter
 * su un campo specifico (con class 'submitsingleinput')
 */
function activateSubmitOnSingleInputEnterKeyCode() {
	$('.submitsingleinput').bind('keypress', function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13) { // Enter keycode
			$('#' + e.target.id.replace(':', '\\:') + 'Btn').trigger('click');
			return false;
		}
	});
}

/**
 * attivazione del popover su tutti gli elementi con
 * classe 'popoveritem'
 */
function activatePopover() {
	var popover = $('.popoveritem');
	popover.each(function(index) {
		if (!$(this).attr('data-content')) {
			// contenuto popover specificato come elemento html
			var popoverId = $(this).attr('id');
			$('#' + popoverId).popover({
				html : true,
				trigger: 'hover',
				content: function() {
					return $('#' + popoverId + 'Content').html();
				}
			});
		}
		else {
			// contenuto popover e altre properties specificate come attributi
			$(this).popover();
		}
	});
}

function limitShowdocHeight() {
	$('#fixedshowdoc').height(function(index, height) {
		var value = window.innerHeight - $(this).offset().top - 20;
		if (value > 300)
			return value;
		else
			return '100%';
	});
}

/**
 * visualizzazione dei dettagli di un errore
 */
function mostraErrorExtra() {
	return openCloseSection('dialogErrorExtra');
}

/**
 * apre/chiude la sezione indicata
 */
function openCloseSection(sectionid) {
	if ($('#' + sectionid).is(':visible'))
		$('#' + sectionid).fadeOut();
	else {
		$('#' + sectionid).css('filter', 'alpha(opacity=100)');
		$('#' + sectionid).fadeIn();
	}
	return false;
}

/**
 * caricamento del messaggio di conferma su pressione di un pulsante/link
 * @param message
 * @returns {Boolean}
 */
function owmConfirm(message) {
	if (!confirm(message)) {
		return false;
	}
	displayOpenwaitmsg();
	return true;
}

/**
 * in caso di form con campi sui quali e' stato individuato un errore (tramite JSF), si applicano
 * al form-group che contiene il campo gli stili (Bootstrap) relativi ad un errore
 */
function addErrorStyleToFields() {
	$(".invalidinput").closest("div.form-group").addClass('has-error');
}

// ************************************************************************************
// funzioni necessari a vocabolari, lookup e thesauri (INIZIO)
// ************************************************************************************

/*
 * in caso di azione su un campo di lookup/thesauro da parte di un utente disattiva il pulsante di salvataggio in modo
 * da evitare salvataggio di documenti con lookup/thesauri parziali (forza la conclusione della selezione di un lookup/thesauro)
 */
function activateLookupControl() {
	$('.lookup-field, .th-field, .tag-field').on('keypress blur', function(e) {
		//alert("Event type " + e.type);

		// aggiunto il delay per un problema di ordine di eventi su IE. Nel caso venga intercettato il click occorre
		// aumentare il tempo di delay del timeout
		var restoreDelay = 500;
		if (e.type == 'keypress') {
			$('.docedit #templateForm\\:saveBtn').attr('disabled', 'disabled');
			$('.docedit #templateForm\\:saveBozzaBtn').attr('disabled', 'disabled');
			$('.docedit #templateForm\\:protocoolaBtn').attr('disabled', 'disabled');
		}
		else {
			setTimeout(function() { $('.docedit #templateForm\\:saveBtn').removeAttr('disabled') }, restoreDelay);
			setTimeout(function() { $('.docedit #templateForm\\:saveBozzaBtn').removeAttr('disabled') }, restoreDelay);
			setTimeout(function() { $('.docedit #templateForm\\:protocoolaBtn').removeAttr('disabled') }, restoreDelay);
		}
	});
}

function focusByIdFromIndex(e) {
	if (e.status == 'success') {
		var focus = document.getElementById(document.getElementById('templateForm:focusElementIndex').value);
		if(null == focus) return false;
		var text = focus.value;
		focus.value = '';
		focus.focus();
		focus.value = text;
		focus.defaultvalue = text;
	}
	return false;
}

function focusByIdFromTh(e) {
	if (e.status == 'success') {
		var focus = document.getElementById(document.getElementById('templateForm:focusElementTh').value);
		if(null == focus) return false;
		var text = focus.value;
		focus.value = '';
		focus.focus();
		focus.value = text;
		focus.defaultvalue = text;
	}
	return false;
}

function focusByIdFromLookup(e) {
	if (e.status == 'success') {
		var focus = document.getElementById(document.getElementById('templateForm:focusElementLookup').value);
		if(null == focus) return false;
		var text = focus.value;
		focus.value = '';
		focus.focus();
		focus.value = text;
		focus.defaultvalue = text;

	}
	return false;
}

function executeAction(input){
	var id = input.id;
	var re = new RegExp("_input$");
	var id_button = id.replace(re, '_button');
	var id_clear = id.replace(re, '_clear');
	var text = document.getElementById(id).value;
	var defaultText = document.getElementById(id).defaultvalue;
	if (text == '' && defaultText != '' && defaultText != undefined && null != document.getElementById(id_clear)) {
		document.getElementById(id_clear).click();
		input.defaultvalue = '';
	}
	else if (text != '' && text != defaultText) {
		// Imposto il focus sul pulsante di lookup al fine di evitare di finire con un tab su un altro
		// lookup e scatenare la procedura 2 volte
		document.getElementById(id_button).focus();
		document.getElementById(id_button).click();
		input.defaultvalue = text;
	}
	else return true;
}

function setFocusLookup(button) {
	var id = button.id;
	var re = new RegExp("_button$");
	var id_input = id.replace(re, '_input');
	re = new RegExp("_clear$");
	id_input = id.replace(re, '_input');
	document.getElementById('templateForm:focusElementLookup').value = id_input;
}

function setFocusIndex(button){
	var id = button.id;
	var re = new RegExp("_button$");
	var id_input = id.replace(re, '_input');
	document.getElementById('templateForm:focusElementIndex').value = id_input;
}

function setFocusTh(button){
	var id = button.id;
	var re = new RegExp("_button$");
	var id_input = id.replace(re, '_input');
	document.getElementById('templateForm:focusElementTh').value = id_input;
}

function setDefaultValue(e){
	e.defaultvalue=e.value;
}

function confirmLookup(message, idinput) {
	// eventuale messaggio di conferma su lookup
	if (message != undefined && message != null && message != '') {
		if (!confirm(message)) {
			hideOpenwaitmsg();
			$('#templateForm\\:' + idinput).val('');
			return false;
		}
	}
	displayOpenwaitmsg();
	return true;
}

// ************************************************************************************
// funzioni necessari a vocabolari, lookup e thesauri (FINE)
// ************************************************************************************

/**
 * apertura di un popup con indicazione di larghezza e altezza
 */
function openCenterPopup(url, title, width, height) {
	if (width == null || width == undefined || width == '')
		width = 1024;
	if (height == null || height == undefined || height == '')
		height = 768;

	wLeft = window.screenLeft ? window.screenLeft : window.screenX;
	wTop = window.screenTop ? window.screenTop : window.screenY;

	var left = wLeft + (window.innerWidth / 2) - (width / 2);
	var top = wTop + (window.innerHeight / 2) - (height / 2);

	var targetWin = window.open(url, title, 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left);
	targetWin.focus();

	if (url != '' && url.startsWith('http://'))
		return false;
}

/**
 * apertura di un popup fullscreen
 */
function openFullscreenPopup(url, title) {
	var width = screen.width - 150;
	var height = screen.height - 150;

	wLeft = window.screenLeft ? window.screenLeft : window.screenX;
	wTop = window.screenTop ? window.screenTop : window.screenY;

	var left = wLeft + (window.innerWidth / 2) - (width / 2);
	var top = wTop + (window.innerHeight / 2) - (height / 2);

	var targetWin = window.open(url, title, 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left);
	targetWin.focus();

	if (url != '' && url.startsWith('http://'))
		return false;
}

function setInvalidInputFields() {
	var errorIds = $('#templateForm\\:errorFieldIds').val();
	if (errorIds != '') {
		var ids = errorIds.split(',');
		for ( var i=0; i<ids.length; i++) {
			if (ids[i] != '') {
				var fieldId = ids[i].replace(/\:/g,'\\:');
				$('#' + fieldId).addClass('invalidinput');
				$('#' + fieldId).closest("div.form-group").addClass('has-error');
			}
		}
		// azzero il campo hidden per successive chiamate ajax (al ricaricamento
		// della pagina verrebbero indicati sempre i campi sui quali era stato
		// individuato un errore di validazione)
		$('#templateForm\\:errorFieldIds').val('');
	}
}


// in vocabolari maschera di ricerca, selezione dei valori in base a click su checkbox. Non e' stato
// possibile utilizzare h:selectBooleanCheckbox per un bug se usati all'interno di ui:repeat.
function changeSelectionVocabulary(index) {
	var newvalue = 'false';
	if ($('#checkVoc' + index).is(':checked'))
		newvalue = 'true';
	$('#templateForm\\:vocabolari\\:' + index + '\\:voc').val(newvalue);
	$('#templateForm\\:updateVocabolariSelezionatiBtn').trigger('click');
}

//in vocabolari maschera di ricerca, selezione dei valori in base a click su checkbox. Non e' stato
//possibile utilizzare h:selectBooleanCheckbox per un bug se usati all'interno di ui:repeat.
function changeSelectionAdvancedCSV(index0, index1) {
	var newvalue = 'false';
	if ($('#csvgroupselect_' + index0 + '_' + index1).is(':checked'))
		newvalue = 'true';
	$('#templateForm\\:exportsCSV\\:' + index0 + '\\:exportsCSVGroups\\:' + index1 + '\\:csvGroup').val(newvalue);
}

// in lista titoli da ricerca, selezione dei valori in base a click su checkbox. Non e' stato
// possibile utilizzare h:selectBooleanCheckbox per un bug se usati all'interno di ui:repeat.
function changeSelectionTitles(index) {
	var newvalue = 'false';
	if ($('#checkTit' + index).is(':checked'))
		newvalue = 'true';

	$('#templateForm\\:titoli\\:' + index + '\\:tit').val(newvalue);
	$('#templateForm\\:titoli\\:' + index + '\\:updateTitoliSelezionatiBtn').trigger('click');
}

// aggiorna il valore di un campo input hidden in base al click su un checkbox
function changeHiddenOnBooleanCheckbox(checkboxId, inputId) {
	var newvalue = 'false';
	if ($('#' + checkboxId).is(':checked'))
		newvalue = 'true';
	$('#' + inputId).val(newvalue);
}

// apertura di un dialog modal di bootstrap
function openModal(id) {
	$('#' + id).modal('show');
}

// chiusura di un dialog modal di bootstrap
function closeModal(id) {
	$('#' + id).modal('hide');
}

function exportCSV(tot){
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare esportazione di " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

function exportFolderAttachments(isEmpty){
	if (isEmpty){
		alert("Non è stato selezionato alcun fascicolo");
		return false;
	}
	else {
		if (!confirm("Confermare esportazione per fascicoli selezionati?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}


/**
 * Avvio di una specifica azione massiva (stored procedure LUA) su una selezione da lista titoli
 * @param tot
 * @returns
 */
function startAzioneMassiva(tot) {
	if (tot == 0){
		alert("Non è stato selezionato alcun documento");
		return false;
	}
	else {
		var f = tot == 1 ? "o" : "i";
		if (!confirm("Confermare l'avvio dell'operazione su " + tot + " document" + f + "?"))
			return false;
		else {
			displayOpenwaitmsg();
			return true;
		}
	}
}

/**
 * Avvio di una specifica azione massiva (stored procedure LUA) su uno specifico documento
 * @returns
 */
function startAzioneMassivaSingleDoc() {
	if (!confirm("Confermare l'avvio dell'operazione sul documento?"))
		return false;
	else {
		displayOpenwaitmsg();
		return true;
	}
}

// disabilita tutti i link esterni al form templateForm. Utilizzato in caso di modifica
// di documenti per forzare l'utente a premere il pulsante abbandona (rilascio del lock sul
// documento)
function disableNoTemplateFormLinks(message) {
	hideLoadingIndicator(); // in caso di chiamata ajax disattivo l'indicatore di loading

	$('#leftsideContentForm a').prop('onclick', null);
	$('#leftsideContentForm a').prop('href', '#');
	$('#leftsideContentForm a').unbind('click');
	$('#leftsideContentForm a').removeClass('openwaitmsg');

	$('#leftsideContentForm a').click(function(e) {
		if (message != undefined && message != null && message != '')
			alert(message);
	});

	$('#menuForm a').prop('onclick', null);
	$('#menuForm a').prop('href', '#');
	$('#menuForm a').unbind('click');
	$('#menuForm a').removeClass('openwaitmsg');

	$('#menuForm a').click(function(e) {
		if (message != undefined && message != null && message != '')
			alert(message);
	});
}


function displayOpenwaitmsg() {
	showLoadingIndicator();
	showWaitMsgDiv();
}

function hideOpenwaitmsg() {
	hideWaitMsgDiv();
	hideLoadingIndicator();
}

//************************************************************************************
//funzioni necessarie alla gestione dei campi custom (INIZIO)
//************************************************************************************

// in inserimento/modifica di campi custom di tipo checkbox gestisce la selezione dei valori in base a click su checkbox.
// Non e' stato possibile utilizzare h:selectBooleanCheckbox per un bug se usati all'interno di ui:repeat.
function changeSelectionCustomFieldCheckbox(idcheckbox, idhiddenfield) {
	var escapedIdCheckbox = replaceAll(':', '\\:', idcheckbox);
	var escapedIdHiddenField = replaceAll(':', '\\:', idhiddenfield);

	var newvalue = 'false';
	if ($('#' + escapedIdCheckbox).is(':checked'))
		newvalue = 'true';
	$('#' + escapedIdHiddenField).val(newvalue);

	$('#' + escapedIdHiddenField + '_btn').trigger('click');
}

//************************************************************************************
//funzioni necessarie alla gestione dei campi custom (FINE)
//************************************************************************************

// replaceAll di una stringa
function replaceAll(find, replace, str) {
	return str.replace(new RegExp(find, 'g'), replace);
}

// apertura di un URL su nuova finestra
function openUrl(url) {
	var win = window.open(url.indexOf('http') == 0 ? url : 'http://' + url, '_new');
	win.focus();
	return false;
}

// stampa del contenuto della pagina
function printPageContent() {
	$('#fixedshowdoc').height('100%');
	window.print();
	limitShowdocHeight();
	return false;
}

/**
 * Scrittura di log su console javascript
 * @param level info, warn, error
 * @param message messaggio da stampare su console js
 */
function log(level, message) {
	if (typeof console == "object") {
		if (level == undefined || level == null)
			level = "info";

		if (level == "error") {
			console.error(message);
		}
		else if (level == "warn") {
			console.warn(message);
		}
		else {
			console.info(message);
		}
	}
}

/**
 * Validazione di eventuali tags associati alla risorsa
 * @returns
 */
function validateTags() {
	var tags = $('#templateForm\\:tags');
	if (tags != undefined && tags != null && tags.value != "") {
		displayOpenwaitmsg();
		$('#templateForm\\:validateTagsBtn').trigger('click');
	}
	return false;
}
