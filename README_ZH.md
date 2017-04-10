# cordova-plugin-qqsdk
[![npm version](https://badge.fury.io/js/cordova-plugin-qqsdk.svg?style=flat)](https://badge.fury.io/js/cordova-plugin-qqsdk)
[![npm](https://img.shields.io/npm/dm/cordova-plugin-qqsdk.svg)](https://www.npmjs.com/package/cordova-plugin-qqsdk)
[![platform](https://img.shields.io/badge/platform-iOS%2FAndroid-lightgrey.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat)](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)
[![Contact](https://img.shields.io/badge/contact-Van-green.svg?style=flat)](http://VanPan.me)		
	
这个一个QQ SDK的Cordova 插件。 [English](https://github.com/iVanPan/Cordova_QQ)				
如果你希望使用一个微博的cordova插件可以查看[这里](https://github.com/iVanPan/cordova_weibo).                     


## Table of Contents

- [功能](#功能)
- [安装要求](#安装要求)
- [安装](#安装)
- [文档](#文档)     
  - [支持的接口](#支持的接口)
  - [错误码](#错误码)
  - [关于图片](#关于图片)  
  - [使用方式](#使用方式)
    - [检查客户端是否安装](#检查客户端是否安装)
    - [登录](#登录)
    - [登出](#登出)
    - [分享文字](#分享文字)
    - [分享图片](#分享图片)
    - [分享新闻](#分享音乐)
    - [分享音乐](#分享音乐)
    - [获取用户信息](#获取用户信息)
- [关于SDK](#关于sdk) 
- [注意事项 ](#注意事项 ) 
- [Demo](#demo) 
- [贡献代码](#贡献代码) 
- [开源证书](#开源证书) 

## 功能
- QQ 登录
- QQ 登出
- QQ 分享（文字、图片、新闻网页、音乐） 
- QQ 空间分享（文字、图片、新闻网页、音乐）
- QQ 收藏（文字、图片、新闻网页、音乐）
- 检查 QQ 手机客户端端是否安装   

## 安装要求
- Cordova Version 3.5+ 
- Cordova-Android >=4.0
- Cordova-iOS >=4.0     

	
## 安装
1. 命令行运行 ```cordova plugin add cordova-plugin-qqsdk --variable QQ_APP_ID=YOUR_QQ_APPID```              
2. 命令行运行 cordova build               


## 文档

### 支持的接口
1. ssoLogin
2. Logout
3. checkClientInstalled
4. Share(see form below)

|      Platform      |   iOS  |   iOS     |     iOS      | Android |  Android  |    Android   |
|        :---:       | :---:  |   :---:   |    :---:     |   :---: |    :---:  |     :---:    |
|      ShareScene    |   QQ   |   QQZone  |  QQ Favorite |    QQ   |   QQZone  |  QQ Favorite |
|      Text        |    √   |     √     |      √       |    ✕    |     √     |      √       |
|      Image         |    √   |     √     |      √       |    √    |     √     |      √       |
|      News        |    √   |     √     |      √       |    √    |     √     |      √       |
|     Audio        |    √   |     √     |      √       |    √    |     √     |      √       |


### 错误码        
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

### 关于图片
 这个插件中的图片分享、新闻分享、音乐分享功能都要使用到图片，以下类型的图片在这些功能中都支持
  1. 网络图片
  2. Base64
  3. 本地图片(图片的绝对路径)         
 
### 使用方式
##### 检查客户端是否安装
  ```js
  var args = {};
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  QQSDK.checkClientInstalled(function () {
      alert('client is installed');
  }, function () {
      // if installed QQ Client version is not supported sso,also will get this error
      alert('client is not installed');
  },args);

  ```
##### 登录
  ```js
  var args = {};
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  QQSDK.ssoLogin(function (result) {
      alert("token is " + result.access_token);
      alert("userid is " +result.userid);
      alert("expires_time is "+ new Date(parseInt(result.expires_time)) + " TimeStamp is " +result.expires_time);
  }, function (failReason) {
      alert(failReason);
  }.args);

  ```
##### 登出
  ```js
  QQSDK.logout(function () {
      alert('logout success');
  }, function (failReason) {
      alert(failReason);
  });

  ```
##### 分享文字
  ```js
  var args = {};
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
  args.text = "这个是Cordova QQ分享文字";
  QQSDK.shareText(function () {
      alert('shareText success');
  }, function (failReason) {
      alert(failReason);
  },args);

  ```
##### 分享图片
  ```js
  var args = {};  
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
  args.title = "这个是Cordova QQ图片分享的标题";
  args.description = "这个是Cordova QQ图片分享的描述";
  args.image = "https://cordova.apache.org/static/img/cordova_bot.png";
  QQSDK.shareImage(function () {
      alert('shareImage success');
  }, function (failReason) {
      alert(failReason);
  },args); 

  ```
##### 分享新闻
  ```js
  var args = {}; 
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
  args.url = "https://cordova.apache.org/";
  args.title = "这个是Cordova QQ新闻分享的标题";
  args.description = "这个是Cordova QQ新闻分享的描述";
  args.image = "https://cordova.apache.org/static/img/cordova_bot.png";
  QQSDK.shareNews(function () {
      alert('shareNews success');
  }, function (failReason) {
      alert(failReason);
  },args);

  ```
##### 分享音乐
  ```js
  var args = {};  
  args.client = QQSDK.ClientType.QQ;//QQSDK.ClientType.QQ,QQSDK.ClientType.TIM;
  args.scene = QQSDK.Scene.QQ;//QQSDK.Scene.QQZone,QQSDK.Scene.Favorite
  args.url = "https://y.qq.com/portal/song/001OyHbk2MSIi4.html";
  args.title = "十年";
  args.description = "陈奕迅";
  args.image = "https://y.gtimg.cn/music/photo_new/T001R300x300M000003Nz2So3XXYek.jpg";
  args.flashUrl = "http://stream20.qqmusic.qq.com/30577158.mp3";
  QQSDK.shareAudio(function () {
      alert('shareAudio success');
  }, function (failReason) {
      alert(failReason);
  },args);
  ```
##### 获取用户信息
```js
  var url = "https://graph.qq.com/user/get_user_info?access_token=" + accessToken + "&oauth_consumer_key=" + QQ_APP_ID + "&openid=" + userId;
  http.get(url)
```

## 关于SDK 
本插件 Android SDK 的版本是3.2.0，iOS SDK 的版本是3.2.0，你可以在[这里](http://wiki.open.qq.com/wiki/mobile/SDK%E4%B8%8B%E8%BD%BD)下载最新版本的 SDK          

## 注意事项  
**请认真阅读文档，请认真阅读文档，请认真阅读文档**                 
1. 这个插件要求 Cordova-android 的版本 >=4.0,推荐使用 Cordova  5.0.0 或更高的版本，因为从 Cordova 5.0 开始 Cordova-android 4.0 是默认使用的android版本
2. 请在 Cordova 的 deviceready 事件触发以后再调用本插件！！！    
3. <del>在低于5.1.1的 Cordova 版本中存在一个Bug，如果你有多个插件要修改iOS工程中的 “*-Info.plist” CFBundleURLTypes, 只有第一个安装的插件才会生效.所以安装完插件请务必在你的 Xcode 工程里面检查一下 URLTypes。 关于这个 bug 的详情你可以在 [这里](https://issues.apache.org/jira/browse/CB-8007)找到</del> 建议安装使用5.1.1及以上的 Cordova 版本   
4. Android 版本请确保你的签名是正确的 
5. 分享的 URL 长度不要过长，图片不要太大，不然会分享失败，因为 SDK 做了限制          



## Demo     
在安装完这个插件以后，把 cordova 工程中的代码替换为 example_www中的代码，安装Cordova相机插件，在build以后可以进行各个功能测试，以下为运行效果图：
<div style="text-align:center"><img src="https://github.com/iVanPan/Cordova_QQ/blob/master/ScreenShot.png?raw=true" alt="example" style="width:300px"></div>    

## 贡献代码
欢迎提交 PR，贡献你的代码，如果有新功能，请提供 Demo。

## 开源证书

**cordova-plugin-qqsdk** 遵照了 **MIT** 证书. 详情可以查看 [证书](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE) 文件
 
 		
	
				


	



