# Cordova_QQ_插件
这个一个QQ SDK的Cordova 插件(支持android和iOS平台)。   For English,check [here](https://github.com/iVanPan/Cordova_QQ) please
#主要功能
QQ登录，QQ登出，QQ分享 
# 安装
1. 命令行运行      ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID```              
2. 命令行运行 cordova build      
3. 如果你在你的cordova iOS工程上面使用了这个插件,请打开Xcode检查QQ的URLTypes是否正确添加了.如果没有，请自行手动添加.    			

#重要事项			
1. 在安装过程中遇到如下错误"platforms/android/libs/android-support-v4.jar" already exists!",请将你android工程中的 android-support-v4.jar 文件删除再安装本插件 .		        		
2. 这个插件要求cordova-android 的版本 >=4.0,推荐使用 cordova  5.0.0 或更高的版本，因为从cordova 5.0 开始cordova-android 4.0 是默认使用的android版本
3.  请在cordova的deviceready事件触发以后再调用本插件！！！！   		

# 使用方式                								
					     
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


#注意事项      
cordova目前有一个bug，如果你有多个插件要修改iOS工程中的 “*-Info.plist” CFBundleURLTypes, 只有第一个安装的插件才会生效.所以安装完插件请务必在你的Xcode工程里面检查一下URLTypes。 关于这个bug的详情你可以在 [这里](https://issues.apache.org/jira/browse/CB-8007)找到


# LICENSE

[MIT LICENSE](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)

