var IWxApi = (function(){
	//private 
	var _id = '';
	var _iwx =  null;
	var _onCreated = null;
	var _onCreationFailed = null;
	var _testPresenceAttempt = 0;
	
	var _event_map = {};
	var _currentGUID = '';
	
	function domReplaceChildren(dest, what) {
		while (dest.childNodes.length > 0) {
			dest.removeChild(dest.childNodes[0]);
		}
		dest.appendChild(what);
	};

	//public
	var ret = {
		
		testPresenceLoop : function(iwxId, param) {
			setTimeout(function () {
				this.testPresence(iwxId, param);				
				_testPresenceAttempt++;                 
				if ((_testPresenceAttempt < 30) && (_iwx == null)) {
					IWxApi.testPresenceLoop(iwxId, param);          
				} 
			}, 1);
		},
		
		testPresence : function(iwxId, param) {
			try {
				_iwx = document.getElementById(param);
	
				if ((_iwx != undefined) && (_iwx != null)) {
					try {
						//forward cookies from page to iwx
						_iwx.setCookie(document.cookie);
					}
					catch (e) { 
						this.log("error : " + e);
					}
					
					_iwx.name = ""; // riga aggiunta per problema parsing elementi del form da parte di JSF 2.2.6
					
					//get guid
					_currentGUID = _iwx.getPluginGUID();
					
					if (typeof (_onCreated) == 'function') {
						_onCreated();
					}
				} else {
					//document.getElementById(iwxId).innerHTML = '<a href="#">Missing IWx controll</a>';
					_iwx = null;
					
					if (typeof (_onCreationFailed) == 'function') {
						_onCreationFailed();
					}
				}
			} catch (e) {
				//document.getElementById(iwxId).innerHTML = '<a href="#">Missing IWx controll [</a>' + e + ']';
				_iwx = null;
				this.log("error : " + e);
				
				if (typeof (_onCreationFailed) == 'function') {
					_onCreationFailed();
				}
			}
			
			
		},
	
		getGUID : function(){
			return _currentGUID;
		},
		
		initialize : function(id, iwxId, params) {
			_onCreated = (params.onCreated != undefined && typeof (params.onCreated) == 'function') ? params.onCreated : null;
			_onCreationFailed = (params.onCreationFailed != undefined && typeof (params.onCreationFailed) == 'function') ? params.onCreationFailed : null;
			_id = id;
			_iwx = null;
			
			var id = iwxId + 'plugin';
			var width = params.width ? params.width : '100%';
			var height = params.height ? params.height : '100%';
	
			document.getElementById(iwxId).innerHTML = '<object type="application/x-iwxplugin" name="" id="'
					+ id + '" width="' + width + '" height="' + height
					+ '"/>';
			
			//this timeout must be super global cause we can arrive here in loop, during multiple iwx reload
			if(window.__iwx__global__test__presence_timeout != undefined && window.__iwx__global__test__presence_timeout != null){
				clearTimeout(window.__iwx__global__test__presence_timeout) ;
				window.__iwx__global__test__presence_timeout = null;
			}
			
			window.__iwx__global__test__presence_timeout = setTimeout(function(){ 
				IWxApi.testPresence(iwxId, id); 
				clearTimeout(window.__iwx__global__test__presence_timeout);
				window.__iwx__global__test__presence_timeout = null;
			}, 100);	
			
			return this;
		},
		
		log : function(msg){
			if(window.console != undefined) window.console.log(msg);
		},
		
		/**
		 * reset event map and others
		 */
		destroy : function(){
			try{
				for( var k in _event_map){
					delete _event_map[k];
				}
				_event_map = {};
				this.log('reseted');
			}catch(e){
				this.log(e);
			}
		},
	
		/**
		 * return reference to IWx DOMElemnt
		 * @return	 DOMElement
		 */
		getIWx : function() {
			if(_iwx == undefined || _iwx == null){
				throw 'Missing IWx plugin!';
			}
			return _iwx;
		},
	
		/**
		 * return current list of file IDs(GUID) managet by IWx Job
		 * this list can be then use for singolar file access
		 *fire events GetFilesListSuccessfully  or Exception
		 * @return 	array of file  GUIDs
		 */
		getFilesList : function() {
			return _iwx ? _iwx.getFilesList() : [];
		},
	
		/**
		 * return file descriptor object from IWx Job using id like identificator
		 *fire events GetFileDescriptorSuccessfully or Exception
		 * @param		id - file ID(GUID)
		 * @return 	FileDescriptor object (XWID, GUID, Name, Path, Status and FTP params)
		 * Status : FT_UNDEFINED = 10, FT_LOCAL = 20, FT_SENDING = 30, FT_RECEIVING = 40, FT_REMOTE = 50, FT_LOCAL_AND_REMOTE = 60
		 */
		getFileDescriptor : function(id) {
			return _iwx ? _iwx.getFileDescriptor(id) : null;
		},
	
		/**
		 *print file
		 *@param		keys - array of GUIDS
		 *@param		profileName - name of profile for store printer settings
		 *@param		print params: TEXT, ANGLE, FONT_SIZE, FONT_NAME, PERCENT_LEFT, PERCENT_TOP, BOLD, ITALIC, UNDERLINE
		 *@return		void
		 */
		printFile : function(keys, profileName, params) {
			if(_iwx) _iwx.printFile({KEY:keys}, profileName, params);
		},
	
		/**
		 * remove one file from IWx Job. Once file removed it will be non more handled by IWx Job
		 *fire event RemoveFileSuccessfully
		 * @param		id  - file ID(GUID) of file destinated to be removed
		 * @return 	void 
		 */
		removeFile : function(id) {
			if(_iwx) _iwx.removeFile(id);
		},
	
		/**
		 * resets current IWx Job (remove all files from IWxJob)
		 * @param jobName  name of iwx job, should be undefined
		 * @return 	void 
		 */
		resetJob : function(jobName) {
			if(_iwx) _iwx.resetJob(jobName || '');
		},
	
		/**
		 * show file to media viewer
		 * @param		id - file ID(GUID)
		 * @return 	void 
		 */
		showFile : function(id) {
			if(_iwx) _iwx.showFile(id);
		},
		/**
		 * get version of plugin
		 *@return 	version  of plugin
		 */
		getVersion : function() {
			return _iwx ? _iwx.version : 'Missing iwx!!!';
		},
		/**
		 * get last error of plugin. Work for HDD, TWAIN, FTP, HTTP 
		 * values:
			* IWX_OK							-- 0 --
			* CURLE_UNSUPPORTED_PROTOCOL    		-- 1 --
			* CURLE_FAILED_INIT	             			-- 2 --
			* CURLE_URL_MALFORMAT          			-- 3 --
			* CURLE_OBSOLETE4              				-- 4 - NOT USED --
			* CURLE_COULDNT_RESOLVE_PROXY   		-- 5 --
			* CURLE_COULDNT_RESOLVE_HOST   		-- 6 --
			* CURLE_COULDNT_CONNECT         			-- 7 --
			* CURLE_FTP_WEIRD_SERVER_REPLY  		-- 8 --
			* CURLE_REMOTE_ACCESS_DENIED    		-- 9 --
			* CURLE_OBSOLETE10	              			-- 10 - NOT USED --
			* CURLE_FTP_WEIRD_PASS_REPLY    		-- 11 --
			* CURLE_OBSOLETE12	              			-- 12 - NOT USED --
			* CURLE_FTP_WEIRD_PASV_REPLY    		-- 13 --
			* CURLE_FTP_WEIRD_227_FORMAT    		-- 14 --
			* CURLE_FTP_CANT_GET_HOST	       		-- 15 --
			* CURLE_OBSOLETE16	              			-- 16 - NOT USED --
			* CURLE_FTP_COULDNT_SET_TYPE    		-- 17 --
			* CURLE_PARTIAL_FILE            			-- 18 --
			* CURLE_FTP_COULDNT_RETR_FILE   		-- 19 --
			* CURLE_OBSOLETE20				      	-- 20 - NOT USED --
			* CURLE_QUOTE_ERROR             			-- 21 - quote command failure --
			* CURLE_HTTP_RETURNED_ERROR			-- 22 --
			* CURLE_WRITE_ERROR             			-- 23 --
			* CURLE_OBSOLETE24              			-- 24 - NOT USED --
			* CURLE_UPLOAD_FAILED           			-- 25 - failed upload "command" --
			* CURLE_READ_ERROR              			-- 26 - could open/read from file --
			* CURLE_OUT_OF_MEMORY           			-- 27 --    
			* CURLE_OPERATION_TIMEDOUT      		-- 28 - the timeout time was reached --
			* CURLE_OBSOLETE29              			-- 29 - NOT USED --
			* CURLE_FTP_PORT_FAILED         			-- 30 - FTP PORT operation failed --
			* CURLE_FTP_COULDNT_USE_REST    		-- 31 - the REST command failed --
			* CURLE_OBSOLETE32              			-- 32 - NOT USED --
			* CURLE_RANGE_ERROR             			-- 33 - RANGE "command" didn't work --
			* CURLE_HTTP_POST_ERROR         			-- 34 --
			* CURLE_SSL_CONNECT_ERROR       		-- 35 - wrong when connecting with SSL --
			* CURLE_BAD_DOWNLOAD_RESUME     		-- 36 - couldn't resume download --
			* CURLE_FILE_COULDNT_READ_FILE 		-- 37 --
			* CURLE_LDAP_CANNOT_BIND        			-- 38 --
			* CURLE_LDAP_SEARCH_FAILED      			-- 39 --
			* CURLE_OBSOLETE40              			-- 40 - NOT USED --
			* CURLE_FUNCTION_NOT_FOUND      		-- 41 --
			* CURLE_ABORTED_BY_CALLBACK	     		-- 42 --
			* CURLE_BAD_FUNCTION_ARGUMENT  		-- 43 --
			* CURLE_OBSOLETE44              			-- 44 - NOT USED --
			* CURLE_INTERFACE_FAILED        			-- 45 - * CURLOPT_INTERFACE failed --
			* CURLE_OBSOLETE46              			-- 46 - NOT USED --
			* CURLE_TOO_MANY_REDIRECTS      		-- 47 - catch endless re-direct loops --
			* CURLE_UNKNOWN_TELNET_OPTION  	 	-- 48 - User specified an unknown option --
			* CURLE_TELNET_OPTION_SYNTAX    		-- 49 - Malformed telnet option --
			* CURLE_OBSOLETE50              			-- 50 - NOT USED --
			* CURLE_PEER_FAILED_VERIFICATION 		-- 51  --
			* CURLE_GOT_NOTHING             			-- 52 - when this is a specific error --
			* CURLE_SSL_ENGINE_NOTFOUND     		-- 53 - SSL crypto engine not found --
			* CURLE_SSL_ENGINE_SETFAILED    		-- 54 --
			* CURLE_SEND_ERROR              			-- 55 - failed sending network data --
			* CURLE_RECV_ERROR              				-- 56 - failure in receiving network data --
			* CURLE_OBSOLETE57              			-- 57 - NOT IN USE --
			* CURLE_SSL_CERTPROBLEM         			-- 58 - problem with the local certificate --
			* CURLE_SSL_CIPHER             				-- 59 - couldn't use specified cipher --
			* CURLE_SSL_CACERT              				-- 60 - problem with the CA cert (path?) --
			* CURLE_BAD_CONTENT_ENCODING    		-- 61 - Unrecognized transfer encoding --
			* CURLE_LDAP_INVALID_URL        			-- 62 - Invalid LDAP URL --
			* CURLE_FILESIZE_EXCEEDED       			-- 63 - Maximum file size exceeded --
			* CURLE_USE_SSL_FAILED          			-- 64 - Requested FTP SSL level failed --
			* CURLE_SEND_FAIL_REWIND        			-- 65 - Sending the data requires a rewind  that failed --
			* CURLE_SSL_ENGINE_INITFAILED   		-- 66 - failed to initialise ENGINE --
			* CURLE_LOGIN_DENIED            			-- 67 - user, password or similar was not  accepted and we failed to login --
			* CURLE_TFTP_NOTFOUND           			-- 68 - file not found on server --
			* CURLE_TFTP_PERM               				-- 69 - permission problem on server --
			* CURLE_REMOTE_DISK_FULL        			-- 70 - out of disk space on server --
			* CURLE_TFTP_ILLEGAL            			-- 71 - Illegal TFTP operation --
			* CURLE_TFTP_UNKNOWNID          			-- 72 - Unknown transfer ID --
			* CURLE_REMOTE_FILE_EXISTS      		-- 73 - File already exists --
			* CURLE_TFTP_NOSUCHUSER         			-- 74 - No such user --
			* CURLE_CONV_FAILED             			-- 75 - conversion failed --
			* CURLE_CONV_REQD               			-- 76 - caller must register conversion
													callbacks using * CURL_easy_setopt options
			* CURLOPT_CONV_FROM_NETWORK_FUNCTION,
			* CURLOPT_CONV_TO_NETWORK_FUNCTION, and
			* CURLOPT_CONV_FROM_UTF8_FUNCTION --
			* CURLE_SSL_CACERT_BADFILE      			-- 77 - could not load CACERT file, missing  or wrong format --
			* CURLE_REMOTE_FILE_NOT_FOUND   		-- 78 - remote file not found --
			* CURLE_SSH                     				-- 79 - error from the SSH layer, somewhat   generic so the error message will be of interest when this has happened --
			* CURLE_SSL_SHUTDOWN_FAILED     		-- 80 - Failed to shut down the SSL   connection --
			* CURL_FORMADD_MEMORY                 		-- 101 --
			* CURL_FORMADD_OPTION_TWICE           		-- 102 --
			* CURL_FORMADD_NULL                   			-- 103 --
			* CURL_FORMADD_UNKNOWN_OPTION         		-- 104 --
			* CURL_FORMADD_INCOMPLETE             		-- 105 --
			* CURL_FORMADD_ILLEGAL_ARRAY          		-- 106--
			* IWX_ALLOCATION_PROBLEM              		-- 1000 --
			* IWX_MISSING_PARAMS                  			-- 1001 --
			* IWX_CANNOT_OPEN_FILE                		-- 1002 --
			* IWX_INVALID_TWAIN_DRIVER            		-- 1003 --
			* IWX_UNHANDLED_EXCEPTION             		-- 1004 --
			* @return 	ID of error
		*/
		getLastError : function() {
			return _iwx ?_iwx.getLastError : 'Missing iwx!';
		},
	
		/**
		 * add event received from plugin
		 *@param	evt - name of event
		 *@param	func - function to be called
		 */
		addEvent : function(evt, func) {
			_event_map["on" + evt] = func;
			return;

			/****************   THIS IS BECOUSE OF IE11 buggy event model  !!!!!
			var isIE = false;
			if (/MSIE (\d+\.\d+);/.test(navigator.userAgent))
				isIE = true;
	
			if (!isIE) {
				_iwx.addEventListener(evt, func, false);
			}else if (isIE) {
				_iwx.attachEvent("on" + evt, func);
			}
			*/
		},
		
		fireEvent : function (event, guid, data){
			if(guid == _currentGUID){
				if(_event_map[event] != undefined){
					_event_map[event](data);
				}
			}else{
				this.log("Skipped event: " + event + " from: " + guid);
			}
		},
	
		/**
		set param to file's descriptor
		 *@param	key - ID of file(GUID)
		 *@param	param - name of parameter
		 *@param	value - value of parameter
		 *possible params:
			FTP.Protocol
			FTP.Login
			FTP.Pwd
			FTP.Host
			FTP.Port
			HTTP.Host
			HTTP.Login
			HTTP.Pwd	
		 */
		setParam : function(key, param, value) {
			if(_iwx) _iwx.setParam(key, param, value);
		},
		
		/**
		Print basic information about plugin to log (console and log file)
		*/
		getSystemInfo : function()
		{
			if(_iwx) _iwx.getSystemInfo();
		}
	}
	
	return ret;
})();




/**
 * IWx api for hdd file source 
 */
IWxApi.hdd = (function(){
	var api = null;
	var ret = {
		/**
		 * add files to IWxJob from HDD(OpenFileNameDialog)
		 * 
		 *@param		filter for open dialog. For example : {Filter:['All Files(*.*),*.*,JPG(*.jpg),*.jpg,BMP(*.bmp),*.bmp']}
		 * @return void
		 */
		addFiles : function(params) {
			api.getIWx().addFiles('HDD', params);
		},
	
		/**
		 *upload file HDD. Save as dialog
		 *@param		key - array of IDs(GUIDs)
		 *@return		void
		 */
		uploadFiles : function(key) {
			api.getIWx().uploadFiles('HDD', {KEY : key });
		}
	}
	api = IWxApi;
	return ret;
})();	

/**
 * twain management api
 */
IWxApi.twain = (function(){ 
	var api = null;
	var ret = {
		/**
		   * add files to IWxJob from TWAIN(scanner)
		   *@param  params = {
		   *  // mandatory
		   *  SCAN:{
		   *   TYPE: 'TWAIN_NOUI' | 'TWAIN_UI' | 'TWAIN_NOUI_MP' | 'TWAIN_UI_MP'
		   *  },
		   *  // optional 
		   *   QRCODE:{
		   *    WORK: 'TRUE' | 'FALSE', 
		   *    PERCENT_LEFT:0, 
		   *    PERCENT_TOP:0, 
		   *    PERCENT_RIGHT:30, 
		   *    PERCENT_BOTTOM:30, 
		   *    HISTOGRAM_LEVEL:100  //0 will not be considered, and it will not make optimisation
		   *   }
		   *  }
		   * @return  void
		   */
		addFiles : function(params) {
			api.getIWx().addFiles('TWAIN', params);
		},
	
		/**
		 *   Display the "Select Source" dialog box for selecting a new current source.
		 * @return 	void
		 */
		selectSource : function() {
			api.getIWx().selectTWAINSource();
		}
	}
	api = IWxApi;
	return ret;
})();	

/**
 * ftp management
 */
IWxApi.ftp = (function(){ 
	var api = null;
	var ret = {
		/**
		 *set FTP params
		 *@param		params - array of params (case sensitive) : Protocol, Login, Pwd, Host, Port(default : '21')
		 *@return	void
		 */
		setParams : function(params) {
			api.getIWx().setParamsFTP(params);
		},
	
		/**
		 * add files to IWxJob from FTP
		 * @param 	params - array of params(case sensitive) : FileList[]
		 * @return 	void
		 */
		addFiles : function(params) {
			api.getIWx().addFiles('FTP', params);
		},
	
		/**
		 *upload file to FTP
		 *@param		key - array of IDs(GUIDs)
		 *@return	void
		 */
		uploadFiles : function(key) {
			api.getIWx().uploadFiles('FTP', {KEY : key});
		},
	
		/**
		 * download file from FTP
		 *@param		key - array of IDs(GUIDs)
		 */
		downloadFiles : function(key) {
			api.getIWx().downloadFiles('FTP', { KEY : key });
		}
	}
	api = IWxApi;
	return ret;
})();	

/**
 * http management
 */
IWxApi.http = (function(){ 
	var api = null;
	var ret = {
		/**
		 *set FTP params
		 *@param		params - array of params (case sensitive) : Protocol, Login, Pwd, Host, Port(default : '21')
		 *@return	void
		 */
		setParams : function(params) {
			api.getIWx().setParamsHTTP(params);
		},
	
		/**
		 * add files to IWxJob from HTTP
		 * @param 	params - array of params(case sensitive) : FileList[]
		 * @return 	void
		 */
		addFiles : function(params) {
			api.getIWx().addFiles('HTTP', params);
		},
	
		/**
		 *upload file to HTTP
		 *@param		key -  array of IDs(GUIDs)
		 *@param		postParams - Post Params
		 *@return	void
		 */
		uploadFiles : function(key, postParams) {
			api.getIWx().uploadFiles('HTTP', {
				KEY : key,
				POSTPARAMS : postParams,
				COOKIES : document.cookie
			});
		},
	
		/**
		 * download file from HTTP
		 *@param		key - array of IDs(GUIDs)
		 *@param		postParams - Post Params
		
		If post param with param's name : name is not set -> plugin will use XWID : There will  be name = XWID in post params
		 */
		downloadFiles : function(key, postParams) {
			api.getIWx().downloadFiles('HTTP', {
				KEY : key,
				POSTPARAMS : postParams
			});
		}
	}
	api = IWxApi;
	return ret;
})();	

/**
 * immage management api
 */
IWxApi.image = (function(){ 
	var api = null;
	var ret = {
		/**
		 *rotate image Left
		 *@return 	void
		 */
		rotateLeft : function() {
			api.getIWx().rotateLeft();
		},
	
		/**
		 *rotate image Right
		 *@return 	void
		 */
		rotateRight : function() {
			api.getIWx().rotateRight();
		},
	
		/**
		 *rescale image to ratio
		 *@param		ratio - ratio of rescaling
		 *@return 	void
		 */
		rescale : function(ratio) {
			api.getIWx().rescaleImage(ratio);
		},
	
		/**
		 *save image (usually after rotate, rescale, ...)
		 *@return	void
		 */
		save : function() {
			api.getIWx().saveFile();
		},
	
		/**
		 *adapt to page image 
		 *@return	void
		 */
		adaptToPage : function() {
			api.getIWx().adaptToPage();
		},
	
		/**
		 *adapt to width image
		 *@return	void
		 */
		adaptToWidth : function() {
			api.getIWx().adaptToWidth();
		},
	
		/**
		 *adapt to original size
		 *@return	void
		 */
		adaptToOriginalSize : function() {
			api.getIWx().adaptToOriginalSize();
		},
		/**
		 *next page in multi page image
		 *@return	void
		 */
		nextPage : function() {
			api.getIWx().nextPage();
		},
		/**
		 *prev page in multi page image
		 *@return	void
		 */
		prevPage : function() {
			api.getIWx().prevPage();
		},
		/**
		 *first page in multi page image
		 *@return	void
		 */
		firstPage : function() {
			api.getIWx().firstPage();
		},
		/**
		 *last page in multi page image
		 *@return	void
		 */
		lastPage : function() {
			api.getIWx().lastPage();
		},
		/**
		 *split multi page to tiff files
		 *@param		id - file ID(GUID)
		 *@return		void
		 */
		splitMultipage : function(id) {
			api.getIWx().splitMultipage(id);
		},
		/**
		 * return image descriptor object of actual image
		 *fire events GetImageDescriptorSuccessfully or Exception
		 * @return 	ImageDescriptor object (FILENAME< PAGEACTUAL, PAGETOTAL)	 
		 */
		getImageDescriptor : function() {
			var ret = api.getIWx().getImageDescriptor(); 
			return ret.PAGEACTUAL != undefined ? ret : {PAGEACTUAL: 0, PAGETOTAL: 0}; 
		},
		
		setImagePosition: function(x, y)
		{
			api.getIWx().setImagePosition(x, y);
		},
		
		/**
		 *merge multipage tiff with file by id
		 *@param id - file ID(GUID)
		 *@param bInsert - type of action : true = INSERT, false = REPLACE
		 *@param iInsertType - type of inserting : 0 = before current possition, 1 = current possition, 2 = after current possition
		 *Note : iInsertType works only for action : INSERT
		 *fire events MergeMultipageSuccessfully or Exception		 
		 */		
		mergeMultipage: function(id, bInsert, iInsertType)
		{
			api.getIWx().mergeMultipage(id, bInsert, iInsertType);
		},
		
		/**
		 *delete image from multipage file in current possition
		 *fire events DeleteCurrInMultipageSuccessfully or Exception		 
		 */		
		deleteCuttInMultipage: function()
		{
			api.getIWx().deleteCurrInMultipage();
		},
		
		/**
		 *move image from multipage file in current possition to the left
		 *fire events onMoveCurrInMultipageToLeftSuccessfully or Exception		 
		 */		
		moveCurrInMultipageToLeft: function()
		{
			api.getIWx().moveCurrInMultipageToLeft();
		},
		
		/**
		 *move image from multipage file in current possition to the right
		 *fire events onMoveCurrInMultipageToRightSuccessfully or Exception		 
		 */		
		moveCurrInMultipageToRight: function()
		{
			api.getIWx().moveCurrInMultipageToRight();
		}		
		
		
	}
	api = IWxApi;
	return ret;
})();

/**
 * video player api
 */
IWxApi.video = (function(){ 
	var api = null;
	var ret = {
		/**
		 *play video/audio
		 *@return	void
		 */
		play : function() {
			api.getIWx().playVideo();
		},
	
		/**
		 *pause video/audio
		 *@return	void
		 */
		pause : function() {
			api.getIWx().pauseVideo();
		},
	
		/**
		 *stop video/audio
		 *@return	void
		 */
		stop : function() {
			api.getIWx().stopVideo();
		}
	}
	api = IWxApi;
	return ret;
})();

/**
 * crypt api
 */
IWxApi.crypt = (function(){ 
	var api = null;
	var ret = {
		/**
		 * create p7m
		 * @param		id - file ID(GUID)
		 *@param		cn - CubjectCN
		 * @return 	void 
		 */
		createP7M : function(id, cn) {
			api.getIWx().createP7M(id, cn);
		},
		/**
		 * verify p7m
		 * @param		id - file ID(GUID)
		 *@param		cn - CubjectCN	 
		 * @return 	void 
		 */
		verifyP7M : function(id, cn) {
			api.getIWx().verifyP7M(id, cn);
		},
	
		/**
		 * return list of Certificates (SubjectCN)
		 * 
		 *fire events GetCertificatesListSuccessfully  or Exception
		 * @return 	array of file  SubjectCN
		 */
		getCertificatesList : function() {
			return api.getIWx() ? api.getIWx().getCertificatesList() : [];
		}
	}
	api = IWxApi;
	return ret;
})();
/**
 * recognition
 */
IWxApi.recognize = (function(){ 
	var api = null;
	var ret = {
		/**
		 * return found bar code
		 * @param		id - file ID(GUID)
		 * @param		percentLeft - left corner of square in % (square where iwx will scan image)
		 * @param		percentTop - top corner of square in %
		 * @param		percentRight - right corner of square in %
		 * @param		percentBottom - bottom corner of square in %
		 * @param		histogramLevel - ratio(level) black/white. For QRcode in white A4 paper format level is 100. It means ratio Black:White is 1:100
						If there is more black in scaned picture (for example more text in picture) then radio is less and then recognize procedure will be skiped
						Note : If dont need use histogram, set histogramLevel = 0
		 *fire events GetBarCodeSuccessfully with value in TEXT or Exception
		 */
		getBarCode : function(id, percentLeft, percentTop, percentRight, percentBottom, histogramLevel) {
			api.getIWx().getBarCode(id, percentLeft, percentTop, percentRight, percentBottom, histogramLevel);
		}
	}
	api = IWxApi;
	return ret;
})();
/*
$Log$
Revision 1.17  2015/02/04 14:57:49  mbernardini-3di
iwx ver. 2.2.3

Revision 1.11  2014/12/16 15:41:00  palap
Paly : 2.2.3

Revision 1.10  2014/12/11 16:58:56  palap
Paly : not finished

Revision 1.9  2014/12/10 10:25:01  palap
Paly 2.2.1 (fixed stability text. Changed JS a lot)

Revision 1.8  2014/12/09 09:58:19  palap
Paly 2.2.0

Revision 1.7  2014/11/19 15:51:04  palap
*** empty log message ***

Revision 1.6  2014/10/22 13:48:16  palap
Paly, version 2.1.28 finished

Revision 1.11  2014/10/13 11:21:47  mbernardini-3di
crash IE9 su WIN7 in caso di plugin IWX assente o da aggiornare

Revision 1.10  2014/10/10 07:41:25  mbernardini-3di
aggiornamento e correzione bugs IWX

Revision 1.3  2014/07/24 19:24:46  palap
Paly : print updates

Revision 1.2  2014/04/25 08:49:21  palap
Paly : some updates. Change organize projects. Not still repaired bug about refresh plugin.

Revision 1.1  2014/04/09 09:45:57  palap
laco moved to this repo

Revision 1.1  2014/04/09 09:38:16  palap
paly just created

Revision 1.1  2013/10/02 13:30:11  lsopko-3di
release of candidate

*/