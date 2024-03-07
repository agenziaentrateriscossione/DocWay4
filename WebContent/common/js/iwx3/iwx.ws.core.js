//new IWx object page singleton
IWx.Control = (function($){
	var $_image = null;
	var _viewerOn = false;
	var _currentGuid = "";
	var _Client = IWx.Net.Socket;
	var _event_map = {};

	var _options = {
			debugLog : true,
	};

	var ret = {

			init : function(imgDivId, url, params){

				$.extend(_options, params);

				var self = this;
				$('<img/>')
					.attr('id', 'iwx_image')
					.attr('src', '')
					.attr('alt', 'picture')
					.attr('style', 'display:none')
					//.css('max-width', '100%')
					.appendTo('#'+imgDivId);


				//setup
				$_image = $('#iwx_image');

				$_image.on({
				      'build.viewer': function (e) {
				    	  self.log(e.type);
				      },
				      'built.viewer':  function (e) {
				    	  self.log(e.type);
				      },
				      'show.viewer':  function (e) {
				    	  self.log(e.type);
				      },
				      'shown.viewer':  function (e) {
				    	  self.log(e.type);
				      },
				      'hide.viewer':  function (e) {
				    	  self.log(e.type);
				      },
				      'hidden.viewer': function (e) {
				    	  self.log(e.type);
				      },
				      'view.viewer':  function (e) {
				    	  self.log(e.type);
				      },
				      'viewed.viewer': function (e) {
				    	  self.log(e.type);

				      }
				});
				//self event handling
				//connect to image event
				this.addEvent('immageData', __handleImageData);

				_Client.addEvent('open', function(m){
					__fireEvent('onsocketOpen'); //we have prefix on
		        });
				_Client.addEvent('close', function(m){
					__fireEvent('onsocketClosed', m); //we have prefix on
		        });
				_Client.addEvent('error', function(d){
					if(_options.onError){
						// identify error
						_options.onError.call(self, [d]);
					}
					//__fireEvent('onsocketError', d); //we have prefix on
		        });

				_Client.addEvent('data', function(d){
					try{

						//json object can be:

						/*
						 {"state":"ok","job_guid":"iwx69bbdef5-1744-4c65-b46f-746c3f9fdeac","function":"test","result":{"foo":{"bar":[{"baz":1}]}}}
						 {"state":"ok","job_guid":"iwx69bbdef5-1744-4c65-b46f-746c3f9fdeac","function":"testAsync"}
						 {"state":"ok","job_guid":"iwx69bbdef5-1744-4c65-b46f-746c3f9fdeac","event":"testAsyncResponse","args":{"foo":{"bar":[{"baz":1}]}}}

						 1: function + result
						 2: event + args

						 */
						if(d.state === "ok"){
							if(d.hasOwnProperty('event')){
								//handle particular responses
								/*
								 * 1: onShowFileSuccessfully we will remove image data and call particular event
								 */
								if(
									d.event === 'onShowFileSuccessfully' ||
									d.event === 'onRotateImageSuccessfully'	 ||
									d.event === 'onNextPageSuccessfully' ||
									d.event === 'onPrevPageSuccessfully' ||
									d.event === 'onFirstPageSuccessfully' ||
									d.event === 'onMoveCurrInMultipageToLeftSuccessfully' ||
									d.event === 'onMoveCurrInMultipageToRightSuccessfully' ||
									d.event === 'onDeleteCurrInMultipageSuccessfully' ||
									d.event === 'onMergeMultipageSuccessfully' ||
									d.event === 'onLastPageSuccessfully'

								){
									if(d.hasOwnProperty('args') && d.args.hasOwnProperty('image')){
										__fireEvent('onimmageData', d.args.image);
										//now remove image data from event and forward it
										delete d.args.image;
										if(d.args.hasOwnProperty('guid')){
											_currentGuid = d.args.guid;
											__fireEvent(d.event, d.args.guid);
										}else{
											__fireEvent(d.event, d.args);
										}
									}
								}else{
									__fireEvent(d.event, d.hasOwnProperty('args') ?  d.args : null);
								}
							}else if(d.hasOwnProperty('function')){
								__fireEvent(d['function'].capitalizeFirstOnly(), d.hasOwnProperty('result') ?  d.result : null);
							}
						}else{
							self.log("Error : " + d.what);
						}
					}catch(e){
						self.log("Iwx error : " + e);
						self.log(e.stack);
					};
		        });

				_Client.open(url || 'ws://127.0.0.1:4959/', {debugLog : _options.debugLog});
			},

			/**
			 * add event received from plugin
			 *@param	evt - name of event
			 *@param	func - function to be called
			 */
			addEvent : function(evt, func) {
				_event_map["on" + evt] = func;
				return;
			},

			log : function(msg){
				if(_options.debugLog && window.console != undefined) window.console.log(msg);
			},

			//there is no return here, just async data event
			send : function (cmd, data){
				_Client.send({command:cmd, message: data});
			},

			//here will be returned promise
			call : function (cmd, data){
				return _Client.call({command:cmd, message: data});
			},

			// current image handling
			getImageView : function() {
				return $_image;
			},
			getCurrentGuid : function() {
				return _currentGuid;
			}

	};

	var __fireEvent = function (event, data){
		if(_event_map[event] != undefined){
			_event_map[event](data);
		}
	};

	function __handleImageData(imgData){
		var eh = function(e){
			// mbernardini 01/08/2016 : commentato perche' se la chiamata avviene al caricamento della pagina 'this.parent' non e' definito
			// Dettaglio Errore: 'TypeError: Impossibile recuperare la proprietà 'width' di un riferimento nullo o non definito'
			//$_image.viewer('zoomWidth');
			$_image.off({'viewed.viewer':eh});
		};
		$_image.on({'viewed.viewer': eh});

		var img = $_image;
		img.attr('src', imgData);

		// mbernardini 01/08/2016 : rimosso il caso di '_viewerOn' perche' problemi in casi di ritorno AJAX post caricamento tramite JSF
		/*
		if( _viewerOn){
			img.viewer('update');
		}else{
			//ret.log('Image view create ... ');
			img.viewer({
				inline:true,
				navbar:false,
				toolbar:false,
				title:false,
				zoomRatio:1,
				transition: false
			});
			_viewerOn = true;
		}
		*/
		img.viewer({
			inline:true,
			navbar:false,
			toolbar:false,
			title:false,
			zoomRatio:1,
			transition: false
		}).viewer('update');
		_viewerOn = true;
	};

	return ret;
})(jQuery);

var IWxApi = (function($){
	//private
	var _id = '';
	var _iwx =  null;


	var _event_map = {};
	var _currentGUID = 'NOT_CONNECTED';
	var _resetjobObserver = null;

	var _options = {
			debugLog : true,
			min_height : '100px',
			width : '100%',
			height : '100%',
			handleImagesBinary : false
	};


	function domReplaceChildren(dest, what) {
		while (dest.childNodes.length > 0) {
			dest.removeChild(dest.childNodes[0]);
		}
		dest.appendChild(what);
	};

	function onResetJobSuccessfully(evt){
		if(_resetjobObserver != null){
			_resetjobObserver.resolve();
			_resetjobObserver = null;
		}
	};

	//public
	var ret = {

		getHandleImageBinary : function(){
			return _options.handleImagesBinary;
		},

		getGUID : function(){
			return _currentGUID;
		},

		initialize : function(id, iwxId, params) {

			var observer = $.Deferred();
			var id = iwxId + 'plugin';
			var self = this;

			_id = id;
			_iwx = null;

			//take options
			$.extend(_options, params);


			/*
			 * Check if browser is capable for binary image handling
			 * Ther emust be Object window.Url and window.Glob
			 *
			 */
			if(_options.handleImagesBinary){
				_options.handleImagesBinary = (window.URL != undefined) && (window.Blob != undefined);
			}


			$('<div/>')
				.attr('class', 'img-container')
				.attr('id', id)
				.attr('name', '')
				.css('width', _options.width)
				.css('height', _options.height)
				.css('min-height', _options.min_height)
				.appendTo('#'+iwxId);

			try {
				_iwx = IWx.Control;


				if ((_iwx != undefined) && (_iwx != null)) {
					try {
						_iwx.init(id, 'ws://127.0.0.1:4959/', {
							debugLog : _options.debugLog,
							onError :
								function(e){
									observer.reject(self, $.toJSON(e));

									//call evntual external onError
									if(_options.onError){
										_options.onError.call(self, e);
									}
								}

						});

						// forward cookies from page to iwx
						_iwx.send('setCookie',{cookie : document.cookie});
					}
					catch (e) {
						self.log("error : " + e);
					}

					_iwx.name = ""; // riga aggiunta per problema parsing elementi del form da parte di JSF 2.2.6

					//get guid
					 _iwx.call('getPluginGUID',{}).done(function(d){
						 _currentGUID = d.guid;

						 // attach to onResetJobSuccessfully this event we will do with promise
						 _iwx.addEvent("ResetJobSuccessfully", onResetJobSuccessfully);

						 observer.resolve(self);
					 }).fail(function(d){
						 self.log("error : " + $.toJSON(d));

						 //tell to the rest of worl somthing not work
						 observer.reject(self, $.toJSON(d));
					 });


				} else {
					//document.getElementById(iwxId).innerHTML = '<a href="#">Missing IWx controll</a>';
					_iwx = null;
					observer.reject(self, 'Missing IWx control');
				}
			} catch (e) {
				_iwx = null;

				self.log("error : " + e);

				observer.reject(self, e);
			}

			return observer.promise();
		},

		log : function(msg){
			if(_options.debugLog && window.console != undefined) window.console.log(msg);
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
				throw 'Missing IWx object!';
			}
			return _iwx;
		},

		/**
		 * return current list of file IDs(GUID) managet by IWx Job
		 * this list can be then use for singolar file access
		 *fire events GetFilesListSuccessfully  or Exception
		 * @return 	promise which will obtain array of file  GUIDs
		 */
		getFilesList : function() {
			return _iwx.call('getFilesList',{});
		},

		/**
		 * return file descriptor object from IWx Job using id like identificator
		 *fire events GetFileDescriptorSuccessfully or Exception
		 * @param		id - file ID(GUID)
		 * @return 	Promise which can return FileDescriptor object (XWID, GUID, Name, Path, Status and FTP params)
		 * Status : FT_UNDEFINED = 10, FT_LOCAL = 20, FT_SENDING = 30, FT_RECEIVING = 40, FT_REMOTE = 50, FT_LOCAL_AND_REMOTE = 60
		 */
		getFileDescriptor : function(id) {
			return _iwx.call('getFileDescriptor',{guid : id});
		},

		/**
		 *print file
		 *@param		keys - array of GUIDS
		 *@param		profileName - name of profile for store printer settings
		 *@param		print params: TEXT, ANGLE, FONT_SIZE, FONT_NAME, PERCENT_LEFT, PERCENT_TOP, BOLD, ITALIC, UNDERLINE
		 *@return		void
		 */
		printFile : function(keys, profileName, params) {
			params.GUIDS = keys;
			params.PROFILE_NAME = profileName;
			_iwx.send('printFile', params);
		},

		/**
		 * remove one file from IWx Job. Once file removed it will be non more handled by IWx Job
		 *fire event RemoveFileSuccessfully
		 * @param		id  - file ID(GUID) of file destinated to be removed
		 * @return 	void
		 */
		removeFile : function(id) {
			_iwx.send('removeFile',{ guid : id});
		},

		/**
		 * resets current IWx Job (remove all files from IWxJob)
		 * @param jobName  name of iwx job, should be undefined
		 * @return 	void
		 */
		resetJob : function(jobName) {
			if(_resetjobObserver != null){
				if(_resetjobObserver.state() == "pending"){
					// one is still pending
					return _resetjobObserver.promise();
				}
			}
			// make new deferred
			_resetjobObserver = $.Deferred();

			// call action
			_iwx.send('resetJob',{ prefix : jobName || ''});

			//return promise
			return _resetjobObserver.promise();
		},

		/**
		 * show file to media viewer
		 * @param		id - file ID(GUID)
		 * @return 	void
		 */
		showFile : function(id) {
			_iwx.send('showFile',{ guid : id, doImageBinary: _options.handleImagesBinary });
		},


		/**
		 * get version of plugin
		 *@return 	promise with version  of plugin
		 */
		getVersion : function() {
			return _iwx.call('version', {});
		},
		/**
		 * get last error of plugin. Work for HDD, TWAIN, FTP, HTTP
		 * values:
			* IWX_OK									-- 0 --
			* CURLE_UNSUPPORTED_PROTOCOL    			-- 1 --
			* CURLE_FAILED_INIT	             			-- 2 --
			* CURLE_URL_MALFORMAT          				-- 3 --
			* CURLE_OBSOLETE4              				-- 4 - NOT USED --
			* CURLE_COULDNT_RESOLVE_PROXY   			-- 5 --
			* CURLE_COULDNT_RESOLVE_HOST   				-- 6 --
			* CURLE_COULDNT_CONNECT         			-- 7 --
			* CURLE_FTP_WEIRD_SERVER_REPLY  			-- 8 --
			* CURLE_REMOTE_ACCESS_DENIED    			-- 9 --
			* CURLE_OBSOLETE10	              			-- 10 - NOT USED --
			* CURLE_FTP_WEIRD_PASS_REPLY    			-- 11 --
			* CURLE_OBSOLETE12	              			-- 12 - NOT USED --
			* CURLE_FTP_WEIRD_PASV_REPLY    			-- 13 --
			* CURLE_FTP_WEIRD_227_FORMAT    			-- 14 --
			* CURLE_FTP_CANT_GET_HOST	       			-- 15 --
			* CURLE_OBSOLETE16	              			-- 16 - NOT USED --
			* CURLE_FTP_COULDNT_SET_TYPE    			-- 17 --
			* CURLE_PARTIAL_FILE            			-- 18 --
			* CURLE_FTP_COULDNT_RETR_FILE   			-- 19 --
			* CURLE_OBSOLETE20				      		-- 20 - NOT USED --
			* CURLE_QUOTE_ERROR             			-- 21 - quote command failure --
			* CURLE_HTTP_RETURNED_ERROR					-- 22 --
			* CURLE_WRITE_ERROR             			-- 23 --
			* CURLE_OBSOLETE24              			-- 24 - NOT USED --
			* CURLE_UPLOAD_FAILED           			-- 25 - failed upload "command" --
			* CURLE_READ_ERROR              			-- 26 - could open/read from file --
			* CURLE_OUT_OF_MEMORY           			-- 27 --
			* CURLE_OPERATION_TIMEDOUT      			-- 28 - the timeout time was reached --
			* CURLE_OBSOLETE29              			-- 29 - NOT USED --
			* CURLE_FTP_PORT_FAILED         			-- 30 - FTP PORT operation failed --
			* CURLE_FTP_COULDNT_USE_REST    			-- 31 - the REST command failed --
			* CURLE_OBSOLETE32              			-- 32 - NOT USED --
			* CURLE_RANGE_ERROR             			-- 33 - RANGE "command" didn't work --
			* CURLE_HTTP_POST_ERROR         			-- 34 --
			* CURLE_SSL_CONNECT_ERROR       			-- 35 - wrong when connecting with SSL --
			* CURLE_BAD_DOWNLOAD_RESUME     			-- 36 - couldn't resume download --
			* CURLE_FILE_COULDNT_READ_FILE 				-- 37 --
			* CURLE_LDAP_CANNOT_BIND        			-- 38 --
			* CURLE_LDAP_SEARCH_FAILED      			-- 39 --
			* CURLE_OBSOLETE40              			-- 40 - NOT USED --
			* CURLE_FUNCTION_NOT_FOUND      			-- 41 --
			* CURLE_ABORTED_BY_CALLBACK	     			-- 42 --
			* CURLE_BAD_FUNCTION_ARGUMENT  				-- 43 --
			* CURLE_OBSOLETE44              			-- 44 - NOT USED --
			* CURLE_INTERFACE_FAILED        			-- 45 - * CURLOPT_INTERFACE failed --
			* CURLE_OBSOLETE46              			-- 46 - NOT USED --
			* CURLE_TOO_MANY_REDIRECTS      			-- 47 - catch endless re-direct loops --
			* CURLE_UNKNOWN_TELNET_OPTION  	 			-- 48 - User specified an unknown option --
			* CURLE_TELNET_OPTION_SYNTAX    			-- 49 - Malformed telnet option --
			* CURLE_OBSOLETE50              			-- 50 - NOT USED --
			* CURLE_PEER_FAILED_VERIFICATION 			-- 51  --
			* CURLE_GOT_NOTHING             			-- 52 - when this is a specific error --
			* CURLE_SSL_ENGINE_NOTFOUND     			-- 53 - SSL crypto engine not found --
			* CURLE_SSL_ENGINE_SETFAILED    			-- 54 --
			* CURLE_SEND_ERROR              			-- 55 - failed sending network data --
			* CURLE_RECV_ERROR              			-- 56 - failure in receiving network data --
			* CURLE_OBSOLETE57              			-- 57 - NOT IN USE --
			* CURLE_SSL_CERTPROBLEM         			-- 58 - problem with the local certificate --
			* CURLE_SSL_CIPHER             				-- 59 - couldn't use specified cipher --
			* CURLE_SSL_CACERT              			-- 60 - problem with the CA cert (path?) --
			* CURLE_BAD_CONTENT_ENCODING    			-- 61 - Unrecognized transfer encoding --
			* CURLE_LDAP_INVALID_URL        			-- 62 - Invalid LDAP URL --
			* CURLE_FILESIZE_EXCEEDED       			-- 63 - Maximum file size exceeded --
			* CURLE_USE_SSL_FAILED          			-- 64 - Requested FTP SSL level failed --
			* CURLE_SEND_FAIL_REWIND        			-- 65 - Sending the data requires a rewind  that failed --
			* CURLE_SSL_ENGINE_INITFAILED   			-- 66 - failed to initialise ENGINE --
			* CURLE_LOGIN_DENIED            			-- 67 - user, password or similar was not  accepted and we failed to login --
			* CURLE_TFTP_NOTFOUND           			-- 68 - file not found on server --
			* CURLE_TFTP_PERM               			-- 69 - permission problem on server --
			* CURLE_REMOTE_DISK_FULL        			-- 70 - out of disk space on server --
			* CURLE_TFTP_ILLEGAL            			-- 71 - Illegal TFTP operation --
			* CURLE_TFTP_UNKNOWNID          			-- 72 - Unknown transfer ID --
			* CURLE_REMOTE_FILE_EXISTS      			-- 73 - File already exists --
			* CURLE_TFTP_NOSUCHUSER         			-- 74 - No such user --
			* CURLE_CONV_FAILED             			-- 75 - conversion failed --
			* CURLE_CONV_REQD               			-- 76 - caller must register conversion
													callbacks using * CURL_easy_setopt options
			* CURLOPT_CONV_FROM_NETWORK_FUNCTION,
			* CURLOPT_CONV_TO_NETWORK_FUNCTION, and
			* CURLOPT_CONV_FROM_UTF8_FUNCTION --
			* CURLE_SSL_CACERT_BADFILE      			-- 77 - could not load CACERT file, missing  or wrong format --
			* CURLE_REMOTE_FILE_NOT_FOUND   			-- 78 - remote file not found --
			* CURLE_SSH                     			-- 79 - error from the SSH layer, somewhat   generic so the error message will be of interest when this has happened --
			* CURLE_SSL_SHUTDOWN_FAILED     			-- 80 - Failed to shut down the SSL   connection --
			* CURL_FORMADD_MEMORY                 		-- 101 --
			* CURL_FORMADD_OPTION_TWICE           		-- 102 --
			* CURL_FORMADD_NULL                   		-- 103 --
			* CURL_FORMADD_UNKNOWN_OPTION         		-- 104 --
			* CURL_FORMADD_INCOMPLETE             		-- 105 --
			* CURL_FORMADD_ILLEGAL_ARRAY          		-- 106--
			* IWX_ALLOCATION_PROBLEM              		-- 1000 --
			* IWX_MISSING_PARAMS                  		-- 1001 --
			* IWX_CANNOT_OPEN_FILE                		-- 1002 --
			* IWX_INVALID_TWAIN_DRIVER            		-- 1003 --
			* IWX_UNHANDLED_EXCEPTION             		-- 1004 --
			* IWX_TWAIN_ACQUIRE_IMAGE_FAILED			-- 1005 --
			* IWX_TWAIN_INIT_FAILED 					-- 1006 --
			* IWX_TWAIN_IMAGE_TRANSFER_FAILED 			-- 1007 --
			* IWX_TWAIN_DS_NOT_OPENED 					-- 1008 --
			* IWX_TWAIN_NOT_DS_EVENT 					-- 1009 --
			* IWX_TWAIN_CLOSE_DS 						-- 1010 --
			* IWX_TWAIN_IMAGEINFO_FAILED 				-- 1011 --
			* IWX_TWAIN_BAD_FORMAT 						-- 1012 --
			* IWX_TWAIN_READY_DATA_FAILED 				-- 1013 --
			* IWX_TWAIN_SELECT_SOURCE_FAILED 			-- 1014 --
			* IWX_CANNOT_SAVE_FILE 						-- 1015 --
			* IWX_TOO_BIG_FILE_SIZE						-- 1016 --
			* @return 	promise with error in ID of error
		*/
		getLastError : function() {
			return _iwx.call('getLastError', {});
		},

		/**
		 * add event received from plugin
		 *@param	evt - name of event
		 *@param	func - function to be called
		 */
		addEvent : function(evt, func) {
			if(evt == "ResetJobSuccessfully"){
				throw new Error("Reset job return promise!!!");
			}
			_iwx.addEvent (evt, func);
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
			_iwx.send('setParam',{ guid : key, param : param, value : value});
		},

		/**
		Print basic information about plugin to log (console and log file)
		*/
		getSystemInfo : function()
		{
			_iwx.send('getSystemInfo',{});
		},

		getMIME : function(id)
		{
			_iwx.send('getMIME',{ guid : id });
		}
	};




	return ret;
})(jQuery);




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
			params.from = 'HDD';
			api.getIWx().send('addFiles', params);
		},

		/**
		 *upload file HDD. Save as dialog
		 *@param		key - array of IDs(GUIDs)
		 *@return		void
		 */
		uploadFiles : function(key) {
			api.getIWx().send('uploadFiles',{from : 'HDD', keys : key });
		}
	};
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
			params.from = 'TWAIN';
			api.getIWx().send('addFiles', params);
		},

		/**
		 *   Display the "Select Source" dialog box for selecting a new current source.
		 * @return 	void
		 */
		selectSource : function() {
			api.getIWx().send('selectTWAINSource',{});
		}
	};
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
			api.getIWx().send('setParamsFTP', params);
		},

		/**
		 * add files to IWxJob from FTP
		 * @param 	params - array of params(case sensitive) : FileList[]
		 * @return 	void
		 */
		addFiles : function(params) {
			params.from = 'FTP';
			api.getIWx().send('addFiles', params);
		},

		/**
		 *upload file to FTP
		 *@param		key - array of IDs(GUIDs)
		 *@return	void
		 */
		uploadFiles : function(key) {
			api.getIWx().send('uploadFiles', {from : 'FTP', keys : key});
		},

		/**
		 * download file from FTP
		 *@param		key - array of IDs(GUIDs)
		 */
		downloadFiles : function(key) {
			api.getIWx().send('downloadFiles', {from : 'FTP', keys : key });
		}
	};
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
			api.getIWx().send('setParamsHTTP', params);
		},

		/**
		 * add files to IWxJob from HTTP
		 * @param 	params - array of params(case sensitive) : FileList[]
		 * @return 	void
		 */
		addFiles : function(params) {
			params.from = 'HTTP';
			api.getIWx().send('addFiles', params);
		},

		/**
		 *upload file to HTTP
		 *@param		key -  array of IDs(GUIDs)
		 *@param		postParams - Post Params
		 *@return	void
		 */
		uploadFiles : function(key, postParams) {
			// iwx3 read refresh for page cookies from postparams map !!!
			postParams = postParams || {};
			postParams.COOKIE = document.cookie;

			api.getIWx().send('uploadFiles', {
				from : 'HTTP',
				keys : key,
				postparams : postParams
			});
		},

		/**
		 * download file from HTTP
		 *@param		key - array of IDs(GUIDs)
		 *@param		postParams - Post Params

		If post param with param's name : name is not set -> plugin will use XWID : There will  be name = XWID in post params
		 */
		downloadFiles : function(key, postParams) {
			// iwx3 read refresh for page cookies from postparams map !!!
			postParams = postParams || {};
			postParams.COOKIE = document.cookie;

			api.getIWx().send('downloadFiles', {
				from : 'HTTP',
				keys : key,
				postparams : postParams
			});
		}
	};
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
			api.getIWx().send('rotateLeft',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},

		/**
		 *rotate image Right
		 *@return 	void
		 */
		rotateRight : function() {
			//api.getIWx().getImageView().cropper('rotate', -45);
			api.getIWx().send('rotateRight',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});

		},

		/**
		 *rescale image to ratio
		 *@param		ratio - ratio of rescaling
		 *@return 	void
		 */
		rescale : function(ratio) {
			api.getIWx().send('rescaleImage',{guid: api.getIWx().getCurrentGuid(), ratio : ratio, doImageBinary: api.getHandleImageBinary()});
		},

		/**
		 *save image (usually after rotate, rescale, ...)
		 *@return	void
		 */
		save : function() {
			api.getIWx().send('saveFile',{guid: api.getIWx().getCurrentGuid()});
		},

		/**
		 *adapt to page image
		 *@return	void
		 */
		adaptToPage : function() {
			api.getIWx().getImageView().viewer('zoomPage');
			//api.getIWx().send('adaptToPage',{guid: api.getIWx().getCurrentGuid()});
		},

		/**
		 *adapt to width image
		 *@return	void
		 */
		adaptToWidth : function() {
			api.getIWx().getImageView().viewer('zoomWidth');
			//api.getIWx().send('adaptToWidth',{guid: api.getIWx().getCurrentGuid()});
		},

		/**
		 *adapt to original size
		 *@return	void
		 */
		adaptToOriginalSize : function() {
			api.getIWx().getImageView().viewer('zoomOrig');
			//api.getIWx().send('adaptToOriginalSize',{guid: api.getIWx().getCurrentGuid()});
		},
		/**
		 *next page in multi page image
		 *@return	void
		 */
		nextPage : function() {
			api.getIWx().send('nextPage',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},
		/**
		 *prev page in multi page image
		 *@return	void
		 */
		prevPage : function() {
			api.getIWx().send('prevPage',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},
		/**
		 *first page in multi page image
		 *@return	void
		 */
		firstPage : function() {
			api.getIWx().send('firstPage',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},
		/**
		 *last page in multi page image
		 *@return	void
		 */
		lastPage : function() {
			api.getIWx().send('lastPage',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},
		/**
		 *split multi page to tiff files
		 *@param		id - file ID(GUID)
		 *@return		void
		 */
		splitMultipage : function(id) {
			api.getIWx().send('splitMultipage',{work_guid : id, guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},
		/**
		 * return image descriptor object of actual image
		 *fire events GetImageDescriptorSuccessfully or Exception
		 * @return 	ImageDescriptor object (FILENAME< PAGEACTUAL, PAGETOTAL)
		 */
		getImageDescriptor : function() {
			return api.getIWx().call('getImageDescriptor',{guid: api.getIWx().getCurrentGuid()});
			//return ret.PAGEACTUAL != undefined ? ret : {PAGEACTUAL: 0, PAGETOTAL: 0};
		},

		setImagePosition: function(x, y)
		{
			api.getIWx().send('setImagePosition',{X : x, Y : y, guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
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
			api.getIWx().send('mergeMultipage',{work_guid : id, INSERT : bInsert, INSERTTYPE : iInsertType, guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},

		/**
		 *delete image from multipage file in current possition
		 *fire events DeleteCurrInMultipageSuccessfully or Exception
		 */
		deleteCuttInMultipage: function()
		{
			api.getIWx().send('deleteCurrInMultipage',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},

		/**
		 *move image from multipage file in current possition to the left
		 *fire events onMoveCurrInMultipageToLeftSuccessfully or Exception
		 */
		moveCurrInMultipageToLeft: function()
		{
			api.getIWx().send('moveCurrInMultipageToLeft',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		},

		/**
		 *move image from multipage file in current possition to the right
		 *fire events onMoveCurrInMultipageToRightSuccessfully or Exception
		 */
		moveCurrInMultipageToRight: function()
		{
			api.getIWx().send('moveCurrInMultipageToRight',{guid: api.getIWx().getCurrentGuid(), doImageBinary: api.getHandleImageBinary()});
		}
	};
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
			api.getIWx().send('playVideo',{});
		},

		/**
		 *pause video/audio
		 *@return	void
		 */
		pause : function() {
			api.getIWx().send('pauseVideo',{});
		},

		/**
		 *stop video/audio
		 *@return	void
		 */
		stop : function() {
			api.getIWx().send('stopVideo',{});
		}
	};
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
			api.getIWx().send('createP7M',{guid : id, subjectCN : cn});
		},
		/**
		 * verify p7m
		 * @param		id - file ID(GUID)
		 *@param		cn - CubjectCN
		 * @return 	void
		 */
		verifyP7M : function(id, cn) {
			api.getIWx().send('verifyP7M',{guid : id, subjectCN : cn});
		},

		/**
		 * return list of Certificates (SubjectCN)
		 *
		 *fire events GetCertificatesListSuccessfully  or Exception
		 * @return 	promise with array of file  SubjectCN
		 */
		getCertificatesList : function() {
			return api.getIWx().call('getCertificatesList',{});
		}
	};
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
			api.getIWx().send('getBarCode',{
				guid : id,
				PercentLeft : percentLeft,
				PercentTop : percentTop,
				PercentRight : percentRight,
				PercentBottom : percentBottom,
				HistogramLevel : histogramLevel
			});
		}
	};
	api = IWxApi;
	return ret;
})();
/*
$Log$
Revision 1.4  2016/11/08 08:29:39  mbernardini-3di
gestione prima installazione di IWX ver. 3.0.11.0

Revision 1.1.2.2  2016/11/04 15:34:34  lsopko-3di
4.7.x.1 03/11/2016
	- MOD: adapt for IWX 3.0.x

Revision 1.1.2.1  2016/11/03 17:15:53  lsopko-3di
devel in progress

Revision 1.1  2016/05/31 08:38:29  palap
*** empty log message ***

Revision 1.12  2014/12/17 16:45:09  palap
Paly 2.2.3 again. some small updates

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