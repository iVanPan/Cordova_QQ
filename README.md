# Cordova Plugin For QQ SDK
[![version](https://img.shields.io/badge/version-0.3.9-blue.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ)
[![platform](https://img.shields.io/badge/platform-iOS%2FAndroid-lightgrey.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)
[![Contact](https://img.shields.io/badge/contact-Van-green.svg?style=flat)](http://VanPan.me)					

This is a Cordova Plugin for QQ SDK . [简体中文](https://github.com/iVanPan/Cordova_QQ/blob/master/README_ZH.md).     
I also write a cordova plugin for WeiboSDK [here](https://github.com/iVanPan/cordova_weibo).
## Feature
1. QQ SSO Login
2. QQ Logout 
3. QQ Share 
4. QZone Share
5. QQ Favorites
6. checkClientInstalled		

## Requirements
- Cordova Version 3.5+ 
- Cordova-Android >=4.0			

##Installation
1. ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID``` or ```cordova plugin add cordova-plugin-qqsdk --variable QQ_APP_ID=YOUR_QQ_APPID```                  
2. cordova build          			

##Notes			
1.  <del>you may get a error like this "platforms/android/libs/android-support-v4.jar" already exists!",because you may have duplicate android-support-v4.jar files in your android project. Remove android-support-v4.jar from the /libs folder of your project.	</del> fixed by hook	        		
2. This plugin is required cordova-android version >=4.0,so using cordova  5.0.0 or higher is recommended
3. This plugin should be used after the deviceready event has been fired!!!				
4. ~~If cordova version  <5.1.1,when two cordova plugins are modifying “*-Info.plist” CFBundleURLTypes, only the first added plugin is getting the changes applied.so after installing plugin,please check the URLTypes in your Xcode project.You can find this issue [here](https://issues.apache.org/jira/browse/CB-8007)~~ Update:This Bug is fixed in last cordova version(5.1.1)	
5. For Android: make sure your signature is correct !!!

## About iOS 9

###App Transport Security    				
iOS 9 introduces a new security feature that blocks non-HTTPS traffic in your app. However,   Tencent QQ SDK not support HTTPS yet, this Plugin will turn off https and allow non-HTTPS traffic						

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
### QZone Share
```Javascript
 var args = {};
 args.url = "http://www.baidu.com";
 args.title = "This is cordova QZone share ";
 args.description = "This is cordova QZone share ";
 var imgs =['https://www.baidu.com/img/bdlogo.png',
 'https://www.baidu.com/img/bdlogo.png',
 'https://www.baidu.com/img/bdlogo.png'];
  args.imageUrl = imgs;
  YCQQ.shareToQzone(function () {
      alert("share success");
  }, function (failReason) {
      alert(failReason);
  }, args);
```
###QQ Favorites
```Javascript
 var args = {};
 args.url = "http://www.baidu.com";
 args.title = "这个是cordova QQ 收藏测试";
 args.description = "这个是cordova QQ 收藏测试";
 args.imageUrl = "https://www.baidu.com/img/bdlogo.png";
 args.appName = "cordova—QQ";
 YCQQ.addToQQFavorites(function () {
   alert("share success");
 }, function (failReason) {
   alert(failReason);
 }, args);
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
##Example			
1. install this plugin
2. backup www folder in your cordova project
3. replace www by example_www
4. cordova build & test			
<div style="text-align:center"><img src="https://github.com/iVanPan/Cordova_QQ/blob/master/ScreenShot.png?raw=true" alt="example" style="width:300px"></div>

						
##ERROR_CODE					
When you use qq login,you may get an error code.If you get one, find detail error msg from [here](http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#6._.E8.BF.94.E5.9B.9E.E7.A0.81.E8.AF.B4.E6.98.8E%E3%80%82) please.



