# Cordova_QQ_Plugin
This is a Cordova Plugin for QQ Login (Both on android and iOS).For Chinese,See [中文说明](https://github.com/iVanPan/Cordova_QQ/blob/master/README_ZH.md).     
# Feature
QQ SSO Login, QQ Logout , QQ Share 
# Install
1. ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID```              
2. cordova build      
3.  If you are using this plugin for iOS,check the URLTypes in your Xcode project.If you don't  find URLTypes for qqsdk，manually add it.    			

#important			
1. you may get a error like this "platforms/android/libs/android-support-v4.jar" already exists!",because you may have duplicate android-support-v4.jar files in your android project. Remove android-support-v4.jar from the /libs folder of your project.		        		
2. This plugin is required cordova-android version >=4.0,so using cordova  5.0.0 or higher is recommended
3.  This plugin should be used after the deviceready event has been fired!!!!!!!   		

# Usage                								
					     
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
    
    			
#ERROR_CODE					
when you using qq login,you may get an error code.if you get one, find detail error msg from [here](http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#6._.E8.BF.94.E5.9B.9E.E7.A0.81.E8.AF.B4.E6.98.8E%E3%80%82) please.

#Notice      
When two cordova plugins are modifying “*-Info.plist” CFBundleURLTypes, only the first added plugin is getting the changes applied.so after installing plugin,please check the URLTypes in your Xcode project.You can find this issue [here](https://issues.apache.org/jira/browse/CB-8007)


#LICENSE

[MIT LICENSE](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)

