
var cordova = require('cordova');
module.exports = {
	Scene: {
        QQ:  0, // QQ 好友
        QQZone: 1, // QQ 空间
        Favorite: 2  // 收藏
    },
    ClientType: {
        QQ:  0, // QQ 手机客户端
        TIM: 1 // TIM 客户端
    },
	ssoLogin:function(successCallback, errorCallback, args){
		if(args === undefined) {
            args = {}
        }
		cordova.exec(successCallback, errorCallback, "QQSDK", "ssoLogin",[args]);
	},
	logout:function(successCallback, errorCallback){
		cordova.exec(successCallback, errorCallback, "QQSDK", "logout", []);
	},
	checkClientInstalled:function(successCallback, errorCallback, args){
		if(args === undefined) {
            args = {}
        }
		cordova.exec(successCallback, errorCallback, "QQSDK", "checkClientInstalled", [args]);
	},
	shareText:function(successCallback, errorCallback, args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareText", [args]);
	},
	shareImage:function(successCallback, errorCallback, args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareImage", [args]);
	},
	shareNews:function(successCallback, errorCallback, args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareNews", [args]);
	},
	shareAudio:function(successCallback, errorCallback, args){
		cordova.exec(successCallback, errorCallback, "QQSDK", "shareAudio", [args]);
	},
};
