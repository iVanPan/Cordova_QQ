# Cordova Plugin For QQ SDK
[![version](https://img.shields.io/badge/version-0.3.2-blue.svg?style=flat)]()						
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)		
[![Contact](https://img.shields.io/badge/contact-Van-green.svg?style=flat)](http://VanPan.me)						

This is a Cordova Plugin for QQ Login . [简体中文](https://github.com/iVanPan/Cordova_QQ/blob/master/README_ZH.md).     
I also write a cordova plugin   For WeiboSDK [here](https://github.com/iVanPan/cordova_weibo).
## Feature
1. QQ SSO Login
2. QQ Logout 
3.  QQ Share 
4. checkClientInstalled		

## Requirements
- Cordova Version 3.5+ 
- Cordova-Android >=4.0			

##Installation
1. ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID```              
2. cordova build      
3.  If you are using this plugin for iOS and cordova version <=5.0.0,check the URLTypes in your Xcode project.If you don't  find URLTypes for qqsdk，manually add it.    			

##important			
1.  <del>you may get a error like this "platforms/android/libs/android-support-v4.jar" already exists!",because you may have duplicate android-support-v4.jar files in your android project. Remove android-support-v4.jar from the /libs folder of your project.	</del> fixed by hook	        		
2. This plugin is required cordova-android version >=4.0,so using cordova  5.0.0 or higher is recommended
3.  This plugin should be used after the deviceready event has been fired!!!!!!!   		

##Usage                								
					     
### QQ SSO Login
```Javascript
var checkClientIsInstalled = 1;//default is 0,only for iOS
YCQQ.ssoLogin(function(args){
      alert(args.access_token);
      alert(args.userid);
      },function(failReason){
       console.log(failReason);
},checkClientIsInstalled);
```
### QQ Logout
```Javascript
YCQQ.logout(function(){
	console.log('logout success');
},function(failReason){
	console.log(failReason);
});
```
### QQ Share
```Javascript
var args = {};
args.url = "";
args.title = "";
args.description = "";
args.imageUrl = "";
args.appName = "";
YCQQ.shareToQQ(function(){
	console.log("share success");
},function(failReason){
	console.log(failReason);
},args);
```
### CheckClientInstalled
```Javascript
YCQQ.checkClientInstalled(function(){
	 console.log('client is installed');
},function(){
	// if installed QQ Client version is not supported sso,also will get this error
	console.log('client is not installed');
});
```

						
##ERROR_CODE					
When you use qq login,you may get an error code.If you get one, find detail error msg from [here](http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#6._.E8.BF.94.E5.9B.9E.E7.A0.81.E8.AF.B4.E6.98.8E%E3%80%82) please.

##Notice      
If cordova version  <5.1.1,when two cordova plugins are modifying “*-Info.plist” CFBundleURLTypes, only the first added plugin is getting the changes applied.so after installing plugin,please check the URLTypes in your Xcode project.You can find this issue [here](https://issues.apache.org/jira/browse/CB-8007).This Bug is fixed in last cordova version(5.1.1)


