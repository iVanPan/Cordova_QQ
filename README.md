# Cordova_QQ_Plugin
This is a Cordova Plugin for QQ Login (Both on android and iOS)     
# Feature
QQ SSO Login, QQ Logout
# Install
1. ```cordova plugin add https://github.com/iVanPan/Cordova_QQ.git --variable QQ_APP_ID=YOUR_QQ_APPID```
2. Add ```<preference name="QQ_APP_ID" value="YOUR_QQ_APP_ID" />``` in your config.xml                   
3. cordova build      

# Usage
### QQ SSO Login
```Javascript
YCQQ.ssoLogin(function(args){
         alert(args.access_token);
         alert(args.userid);
      },function(){
         console.log('login error');
});
```
### QQ Logout
```Javascript
YCQQ.logout(function(){
	console.log('logout success');
},function(){
	console.log('logout error');
});
```


#Notice      
When two cordova plugins are modifying “*-Info.plist” CFBundleURLTypes, only the first added plugin is getting the changes applied.so after installing plugin,please check the URLTypes in your Xcode project.You can find this issue [here](https://issues.apache.org/jira/browse/CB-8007)


# LICENSE

[MIT LICENSE](https://github.com/iVanPan/Cordova_QQ/blob/master/LICENSE)

