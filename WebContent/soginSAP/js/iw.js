function IwSupport() {
	
	this.deploy =  function(containerId) {
		var version = 999; // we assume a sane browser
		if (navigator.appVersion.indexOf("MSIE") != -1) {
			//bah, IE again, lets downgrade version number
			version = parseFloat(navigator.appVersion.split("MSIE")[1]);
			
			var IW = document.getElementById('IW');
			if (IW == null) {
				if (version == '7') {
					this.deployie7(containerId);
				}
				//else if (version == '8') {
				else {
					this.deployie8(containerId);
				}
			}
		}
		
	};
	
	this.deployie8 = function(containerId) {
		if ( navigator.appName == 'Microsoft Internet Explorer') {
			var iwContainer = document.getElementById(containerId);
			if (iwContainer != null) {
				
				var obj = document.createElement('object');
				obj.id = 'IW';
				obj.classid = 'clsid:ED5D2306-0FF4-11D2-B37C-0000C000D50D';
				obj.codebase = 'rs/nomos/iw/iwfull.cab#Version=1,3,77,294';
				obj.style.position = 'absolute';
//				obj.style.width = '100px';
//				obj.style.height = '100px';
				obj.style.margin = 'auto';
				
				var param = document.createElement('param');
				param.name = 'mode';
				param.value = 1;
				obj.appendChild(param);

				param = document.createElement('param');
				param.name = 'bgcolor';
				param.value = '#d4d8df';
				obj.appendChild(param);
				
				iwContainer.appendChild(obj);
				
				if (typeof(document.IW.ChangeBgcolor) == 'undefined') {
					iwContainer.removeChild(obj);
				}
				
				iwContainer.style.display = 'none';
				
				//aggiungi la gestione degli eventi "alla Microsoft"
				var eventElement = document.createElement('script');
				eventElement.setAttribute('FOR','IW');
				eventElement.setAttribute('EVENT', 'OnDownload(param, filename, status)');
				eventElement.setAttribute('LANGUAGE', 'JavaScript');
				eventElement.text = 'DoneWithDownload(param, filename, status);';
				iwContainer.appendChild(eventElement);
			}
		}
	};
	
	this.deployie7 = function(containerId) {
		if ( navigator.appName == 'Microsoft Internet Explorer') {
			var iwContainer = document.getElementById(containerId);
			if (iwContainer != null) {

				var scriptDiv = document.createElement('div');
				iwContainer.appendChild(scriptDiv);
				
				var innerDiv = document.getElementById('iwcontainerdiv');
				innerDiv.innerHTML = '<object id="IW" name="IW" style="position:absolute;width:100px;height:100px;margin:0 0 0 0;" classid="clsid:ED5D2306-0FF4-11D2-B37C-0000C000D50D" CODEBASE="rs/nomos/iw/iwfull.cab#Version=1,3,77,294"><param name="mode" value="1"/><param name="bgcolor" value="#d4d8df"/></object>';
				iwContainer.style.display = 'none';
				
				var eventElement = document.createElement('script');
				scriptDiv.appendChild(eventElement);
				eventElement.text = 'function document.IW::OnDownload(param, filename, status){ DoneWithDownload(param, filename, status); }';
				
			}
		}
	};
	
	this.scan = function(type)
	{
		var scan_const = 0;

		if ( type == "quick" )
			scan_const = 12;
		
		document.IW.Acquire(scan_const,'temp');
	};
	
	this.getHost = function()
	{
		var host = window.location.host;
		if ( host.indexOf(':') != -1 )
			host = host.substring(0, host.indexOf(':'));
		return host;
	};
	
	this.getElementByName = function(name,tagName)
	{	
		if ("undefined" == typeof(tagName)) tagName = "input";
		var elements = document.getElementsByTagName(tagName);
		
		for (var i = 0; i < elements.length; i++ )
			if ( elements[i].name == name )
				return elements[i];

		return null;			
	};
	
	this.showImage = function(containerId) {
		var iwContainer = document.getElementById(containerId);
		iwContainer.style.display = 'block';
		iwContainer.style.position = 'relative';
		iwContainer.style.width = '100%';
		iwContainer.style.height = '768px';
		iwContainer.style.margin = '0 -2px 0 0';
		
		document.IW.style.width = '100%';
		document.IW.style.height = '768px';
	};
	
	this.firstPage = function() {
		document.IW.FirstPage();
	};
	
	this.prevPage = function() {
		document.IW.PrevPage();
	};
	
	this.nextPage = function() {
		document.IW.NextPage();
	};
	
	this.lastPage = function() {
		document.IW.LastPage();
	};
	
	this.fill = function() {
		document.IW.fill();
	};
	
	this.fit = function() {
		document.IW.Fit();
	};
	
	this.one = function() {
		document.IW.One();
	};
	
	this.enlarge = function() {
		document.IW.Enlarge();
	};
	
	this.reduce = function() {
		document.IW.Reduce();
	};
	
	this.changeBGColor = function(color) {
		document.IW.ChangeBgcolor(color);
	};
	
	this.rotate = function(degrees) {
		document.IW.Rotate(degrees);
	};
	
	this.saveAs = function() {
		document.IW.SaveAs();
	};
	
	this.numPages = function() {
		return document.IW.NumPages();
	};
	
	this.getFile = function(title, flt, mode) {
		return document.IW.GetFile(title, flt, mode);
	};
	
	this.getBuffer = function() {
		return document.IW.buffer;
	};
};

var iw = new IwSupport();