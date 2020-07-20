
// funzione javascript che controlla se il browser utilizzato e' supportato. Non puo' essere realizzata
// tramite una funzione lato server perche' IE 11 in modalita' compatibilita' invia come userAgent quello
// di IE 8 (browser non supportato)
function detectDocWaySupport() {
	var loginAvailable = true;
	try {
		var userAgent = navigator.userAgent;
		//alert('user-agent: ' + userAgent);
		
		if (userAgent != undefined && userAgent != null && userAgent.indexOf("MSIE") != -1) {
			//alert('MSIE detected');
			var tmp = userAgent.substring(userAgent.indexOf("MSIE") + 4);
			var index = tmp.indexOf(".");
			if (index != -1) {
				tmp = tmp.substring(0, index);
				tmp = tmp.replace(/^\s+|\s+$/g, ''); // trim
				//alert('tmp: ' + tmp)
				var majorVersion = parseInt(tmp);
				//alert('majorVersion: ' + majorVersion);
				
				if (majorVersion < 9)
					loginAvailable = false;
			}
		}
	}
	catch (e) {
		loginAvailable = true; // in caso di errore nel riconoscimento del browser viene permesso il login all'applicazione
	}
	
	if (!loginAvailable) {
		$('#loginform').css("display","none");
		$('#loginerror').css("display","block");
	}
}