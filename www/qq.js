
var cordova = require('cordova');

module.exports = {
	ssoLogin:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "ssoLogin",[]);
	},
	logout:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "logout", []);
	},
	checkClientInstalled:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "checkClientInstalled", []);
	}
};

