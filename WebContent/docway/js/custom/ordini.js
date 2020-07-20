function calcolaTotaliOfferta(tipoLista, indice) {
	try {
		var costo = $('#templateForm\\:' + tipoLista + '\\:' + indice + '\\:costo').val();
		if (costo != '') {
			var indexMigliaia = costo.indexOf('.');
			var indexDecimali = costo.indexOf(',');
			if (indexMigliaia != -1 && indexDecimali == -1) {
				alert("La suddivisione delle migliaia non è consistente.");
				$('#templateForm\\:' + tipoLista + '\\:' + indice + '\\:costo').val('');
			}
			else {
				costo = costo.replace(/\./g, "");
				costo = costo.replace(/,/g, ".");
			    costo = parseFloat(costo);
			    if (!isNaN(costo)) {
				    $('#templateForm\\:' + tipoLista + '\\:' + indice + '\\:costo').val(number_format(costo, 2, ",", "."));
				
				    $('#templateForm\\:calcolaOffertaBtn').trigger('click');
			    }
			    else {
			    	alert("Formato numerico non valido. Si prega di reimpostarlo.");
					$('#templateForm\\:' + tipoLista + '\\:' + indice + '\\:costo').val('');
			    }
			}
		}
	}
	catch(err) {
		alert("Formato numerico non valido. Si prega di reimpostarlo.");
		$('#templateForm\\:' + tipoLista + '\\:' + indice + '\\:costo').val('');
	}
}

function number_format( number, decimals, dec_point, thousands_sep ) {
	var n = number, c = isNaN(decimals = Math.abs(decimals)) ? 2 : decimals;
	var d = dec_point == undefined ? "." : dec_point;
	var t = thousands_sep == undefined ? "," : thousands_sep, s = n < 0 ? "-" : "";
	var i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
    
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
}