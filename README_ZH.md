# Cordova_QQ_插件
[![version](https://img.shields.io/badge/version-0.3.9-blue.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ)
[![platform](https://img.shields.io/badge/platform-iOS%2FAndroid-lightgrey.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)
[![Contact](https://img.shields.io/badge/contact-Van-green.svg?style=flat)](http://VanPan.me)
	
这个一个QQ SDK的Cordova 插件。 [English](https://github.com/iVanPan/Cordova_QQ)				
如果你希望使用一个微博的cordova插件可以查看[这里](https://github.com/iVanPan/cordova_weibo).
##主要功能
- QQ 登录
- QQ 登出
- QQ 分享 
- QQ 空间分享
- QQ 收藏
- 检查 QQ 手机客户端端是否安装		

##安装要求
- Cordova Version >=3.5
- Cordova-Android >=4.0

##安装
1. 命令行运行      ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID```  或者  ```cordova plugin add cordova-plugin-qqsdk --variable QQ_APP_ID=YOUR_QQ_APPID```              
2. 命令行运行 cordova build     
 		
##注意事项			
1. <del>在安装过程中遇到如下错误"platforms/android/libs/android-support-v4.jar" already exists!",请将你android工程中的 android-support-v4.jar 文件删除再安装本插件 .</del> 已经通过 hook 脚本解决		        		
2. 这个插件要求cordova-android 的版本 >=4.0,推荐使用 cordova  5.0.0 或更高的版本，因为从cordova 5.0 开始cordova-android 4.0 是默认使用的android版本
3.  请在cordova的deviceready事件触发以后再调用本插件！！！		
4. <del>在低于5.1.1的cordova版本中存在一个Bug，如果你有多个插件要修改iOS工程中的 “*-Info.plist” CFBundleURLTypes, 只有第一个安装的插件才会生效.所以安装完插件请务必在你的Xcode工程里面检查一下URLTypes。 关于这个bug的详情你可以在 [这里](https://issues.apache.org/jira/browse/CB-8007)找到</del> 建议安装使用5.1.1及以上的cordova版本 	
5. Android版本请确保你的签名是正确的			

	
## 关于 iOS 9 适配					
###App Transport Security							
在 iOS 9 中 Apple 默认要求使用HTTPS ，由于目前 QQ SDK 还不支持，安装完这个插件以后将不再默认使用HTTPS						

##使用方式                								
					     
### QQ SSO 登录
```Javascript
var checkClientIsInstalled = 1;//默认值是 0,仅仅针对 iOS平台有效![]()
YCQQ.ssoLogin(function(args){
         alert(args.access_token);
         alert(args.userid);
      },function(failReason){
         console.log(failReason);
},checkClientIsInstalled);
```
### QQ 登出
```Javascript
YCQQ.logout(function(){
	console.log('logout success');
},function(failReason){
	console.log(failReason);
});
```
### QQ 分享
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
### QQ空间分享
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
###QQ 收藏
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
### 检查QQ客户端是否安装了
```Javascript
YCQQ.checkClientInstalled(function(){
	console.log('client is installed');
},function(){
	// 如果安装的QQ客户端版本太低，不支持SSO登录也会返回没有安装客户端的错误
	console.log('client is not installed');
});
```
#测试Demo
在安装完这个插件以后，把 cordova 工程中的代码替换为 example_www中的代码，在build以后可以进行各个功能测试，以下为运行效果图：
<div style="text-align:center"><img src="https://github.com/iVanPan/Cordova_QQ/blob/master/ScreenShot.png?raw=true" alt="example" style="width:300px"></div>			
#错误码				
使用SDK时，所有结果都会通过回调返回给应用。在回调的结果中，会包含每次调用结果的返回码。
正常情况下返回码为0，表示调用成功。
如果返回码不为0，说明调用出错，需要根据返回码的值来定位错误原因。 			
110201：未登陆							
110405：登录请求被限制							
110404：请求参数缺少appid						
110401：请求的应用不存在						
110407：应用已经下架							
110406：应用没有通过审核								
100044：错误的sign					
110500：获取用户授权信息失败						
110501：获取应用的授权信息失败						
110502：设置用户授权失败							
110503：获取token失败							
110504：系统内部错误							

详情查看[这里](http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#6._.E8.BF.94.E5.9B.9E.E7.A0.81.E8.AF.B4.E6.98.8E%E3%80%82) 


