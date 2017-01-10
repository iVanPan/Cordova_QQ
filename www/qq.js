
var cordova = require('cordova');

module.exports = {
	Scene: {
        QQ:  0, // QQ 好友
        QQZone: 1, // QQ 空间
        Favorite: 2  // 收藏
    },
	ssoLogin:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "ssoLogin",[]);
	},
	logout:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "logout", []);
	},
	checkClientInstalled:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "checkClientInstalled", []);
	},
	shareText:function(successCallback, errorCallback,args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareText", [args]);
	},
	shareImage:function(successCallback, errorCallback,args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareImage", [args]);
	},
	shareNews:function(successCallback, errorCallback,args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareNews", [args]);
	},
	shareAudio:function(successCallback, errorCallback,args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareAudio", [args]);
	},
};

