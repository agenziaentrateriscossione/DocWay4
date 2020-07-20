var TASKFORM = TASKFORM || {};

TASKFORM.chkSetVal = function chkSetVal(chk) {
	hidObj = document.getElementById(chk.id.substr(0, chk.id.length - 4));
	if(chk.checked)
		hidObj.value = 'TRUE';
	else
		hidObj.value = '';
};

