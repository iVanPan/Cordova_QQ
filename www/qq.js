
var exec    = require('cordova/exec');
var cordova = require('cordova');

module.exports = {
	ssoLogin:function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "QQSDK", "ssoLogin",[]);
	},
	logout:function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "QQSDK", "logout", []);
	},
	checkClientInstalled:function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "QQSDK", "checkClientInstalled", []);
	}
};

