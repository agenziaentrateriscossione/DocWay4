var ES = ES || {};

ES.SignatureManager = function(endpoint, fileServerEndpoint, frameId, frameContainerId, frameClass) {
	this.endpoint = endpoint;
	this.fileServerEndpoint = fileServerEndpoint;
	this.container = document.getElementById(frameContainerId);
	this.frameId = frameId;

	this.frame = document.createElement('iframe');
	this.frame.setAttribute('id', frameId);
	this.frame.setAttribute('name', frameId);
	this.frame.setAttribute('src', '');
	this.frame.setAttribute('style', 'visibility:hidden;');
	
	if (frameClass)
		this.frame.setAttribute('class', frameClass);

	this.container.appendChild(this.frame);

	this.documentsToSignHeader = '<FIRMA_FILES><DOCUMENTS><DOCUMENT>'; 
	this.documentsToSignFooter = '</DOCUMENT></DOCUMENTS></FIRMA_FILES>';
	
	this.files = [];
	
	this.callback = function() {};
};

ES.SignatureManager.constructor = ES.SignatureManager;

ES.SignatureManager.prototype.init = function(idUtente) {
	this.user = idUtente;
	
	this.frame.setAttribute('style', 'visibility:hidden;');
	
	if (this.form)
		this.form.parentNode.removeChild(this.form);

	this.form = document.createElement('form');
	this.form.setAttribute('name', 'firma');
	this.form.setAttribute('method', 'POST');
	this.form.setAttribute('action', this.endpoint);
	this.form.setAttribute('style', 'visibility:hidden;');
	this.form.setAttribute('target', this.frame.getAttribute('name'));

	var hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'ACTION_NAME');
	hiddenInput.setAttribute('value', 'SubmitFirmaRemotaOTPAction');
	this.form.appendChild(hiddenInput);

	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'id_utente');
	hiddenInput.setAttribute('value', this.user);
	this.form.appendChild(hiddenInput);

	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'servizioAut');
	hiddenInput.setAttribute('value', 'PM');
	this.form.appendChild(hiddenInput);

	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'MESSAGE');
	hiddenInput.setAttribute('value', 'LIST');
	this.form.appendChild(hiddenInput);

	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'NEW_SESSION');
	hiddenInput.setAttribute('value', 'TRUE');
	this.form.appendChild(hiddenInput);
	
	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'val_referer');
	hiddenInput.setAttribute('value', this.fileServerEndpoint);
	this.form.appendChild(hiddenInput);

	hiddenInput = document.createElement('input');
	hiddenInput.setAttribute('type', 'hidden');
	hiddenInput.setAttribute('name', 'documentsToSign');
	hiddenInput.setAttribute('id', 'documentsToSign_' + this.frameId);
	hiddenInput.setAttribute('value', '');
	this.form.appendChild(hiddenInput);

	this.container.appendChild(this.form);
	
	this.files = [];
};

ES.SignatureManager.prototype.addAttachment = function(nRecord, fileId, fileName, mimeType, tipoRiconsegna, login, matricola) {
	if (!tipoRiconsegna)
		tipoRiconsegna = 'APPEND';
	
	this.files.push('<FILE name="' + fileName + '"><SOURCE mimeType="' + mimeType + '" url="' + this.fileServerEndpoint + '?action=getattach&amp;fileId=' + fileId + '"></SOURCE>' + 
					'<INFORMAZIONI>' + fileId + '|' + nRecord + '|' + tipoRiconsegna + '|' + login + '|' + matricola + '</INFORMAZIONI></FILE>');
	
//	this.files.push('<FILE name="' + fileName + '"><SOURCE mimeType="' + mimeType + '" url="http://login-coll.avepa.it/FirmaWeb/ausiliaryFiles/fileTest.pdf"></SOURCE>' + 
//			'<INFORMAZIONI>' + fileId + '|' + nRecord + '|' + tipoRiconsegna + '</INFORMAZIONI></FILE>');
};

ES.SignatureManager.prototype.sign = function() {
	var documentsToSign = this.documentsToSignHeader;
	for (var i = 0; i < this.files.length; i++) {
		documentsToSign += this.files[i];
	}
	documentsToSign += this.documentsToSignFooter;
	
	var documentsToSignInput = document.getElementById('documentsToSign_' + this.frameId);
	documentsToSignInput.setAttribute('value', documentsToSign);

	this.frame.setAttribute('style', 'visibility:visible;');
	
	this.form.submit();
};

ES.SignatureManager.prototype.reset = function() {
	this.init(this.user);
};

ES.SignatureManager.prototype.setCallback = function(callback) {
	ES.SignatureManager._callback = callback;
};

//metodo "statico"
ES.SignatureManager.signTerminated = function(err, success) {
	ES.SignatureManager._callback(err, success);
};

ES.SignatureManager._callback = function(err, success) { };