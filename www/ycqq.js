
var exec    = require('cordova/exec'),
cordova = require('cordova');

module.exports = {
	ssoLogin:function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "YCQQ", "ssoLogin", []);
	},
	logout:function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "YCQQ", "logout", []);
	},
	shareToQQ:function(successCallback, errorCallback,args){
		exec(successCallback, errorCallback, "YCQQ", "shareToQQ", [args]);
	}

};

