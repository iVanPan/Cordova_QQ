#import "YCQQ.h"
#import <TencentOpenAPI/QQApiInterface.h>

NSString *QQ_NOT_INSTALLED = @"QQ Client is not installed";
NSString *QQ_PARAM_NOT_FOUND = @"param is not found";
NSString *QQ_LOGIN_ERROR = @"QQ login error";
NSString *QQ_LOGIN_CANCEL = @"QQ login cancelled";
NSString *QQ_LOGIN_NETWORK_ERROR = @"QQ login network error";

@implementation YCQQ

- (void)pluginInitialize {
    NSString *appId = [[self.commandDelegate settings] objectForKey:@"qq_app_id"];
    if (nil == self.tencentOAuth) {
        self.tencentOAuth = [[TencentOAuth alloc] initWithAppId:appId andDelegate:self];
    }
}

/**
* QQ 单点登录
*/
- (void)ssoLogin:(CDVInvokedUrlCommand *)command {
    NSString *checkQQInstalled = [command.arguments objectAtIndex:0];
    if (([checkQQInstalled integerValue] == 1)) {
        if ([TencentOAuth iphoneQQInstalled]) {
            [self qqLogin:command];
        }
        else {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_NOT_INSTALLED];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }
    else {
        [self qqLogin:command];
    }
}

/**
* QQ 登出
*/
- (void)logout:(CDVInvokedUrlCommand *)command {
    if (self.tencentOAuth.isSessionValid) {
        [self.tencentOAuth logout:self];
    }
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/**
* 检查客户端是否安装
*/
- (void)checkClientInstalled:(CDVInvokedUrlCommand *)command {
    if ([TencentOAuth iphoneQQInstalled] && [TencentOAuth iphoneQQSupportSSOLogin]) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
* 分享到QQ
*/
- (void)shareToQQ:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        QQApiNewsObject *newsObj = [self makeNewsObject:args with:1];
        SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:newsObj];
        QQApiSendResultCode sent = [QQApiInterface sendReq:req];
        [self handleSendResult:sent];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
* 分享到QQ空间
*/
- (void)shareToQzone:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        QQApiNewsObject *newsObj = [self makeNewsObject:args with:2];
        SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:newsObj];
        QQApiSendResultCode sent = [QQApiInterface SendReqToQZone:req];
        [self handleSendResult:sent];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
* 添加到QQ收藏
*/
- (void)addToQQFavorites:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        QQApiNewsObject *newsObj = [self makeNewsObject:args with:1];
        [newsObj setCflag:kQQAPICtrlFlagQQShareFavorites];
        SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:newsObj];
        QQApiSendResultCode sent = [QQApiInterface sendReq:req];
        [self handleSendResult:sent];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
* 构建新闻类分享实例
*/
- (QQApiNewsObject *)makeNewsObject:(NSDictionary *)args with:(int)shareType {
    if (!args) {
        return nil;
    }
    NSString *url = [args objectForKey:@"url"];
    NSString *previewImageUrl;
    if (shareType == 1) {
        previewImageUrl = [args objectForKey:@"imageUrl"];
    }
    else if (shareType == 2) {
        previewImageUrl = [[args objectForKey:@"imageUrl"] objectAtIndex:0];
    }

    QQApiNewsObject *newsObj = [QQApiNewsObject
            objectWithURL:[NSURL URLWithString:url]
                    title:[args objectForKey:@"title"]
              description:[args objectForKey:@"description"]
          previewImageURL:[NSURL URLWithString:previewImageUrl]];
    return newsObj;
}

/**
* 处理URL
*/
- (void)handleOpenURL:(NSNotification *)notification {
    NSURL *url = [notification object];
    if ([url isKindOfClass:[NSURL class]]) {
        [TencentOAuth HandleOpenURL:url];
    }
}

#pragma mark - TencentSessionDelegate
- (void)tencentDidLogin {
    if (self.tencentOAuth.accessToken && 0 != [self.tencentOAuth.accessToken length]) {
        NSMutableDictionary *Dic = [NSMutableDictionary dictionaryWithCapacity:2];
        [Dic setObject:self.tencentOAuth.openId forKey:@"userid"];
        [Dic setObject:self.tencentOAuth.accessToken forKey:@"access_token"];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:Dic];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
}

- (void)tencentDidLogout {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
}

- (void)tencentDidNotLogin:(BOOL)cancelled {
    if (cancelled) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_CANCEL];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
}

- (void)tencentDidNotNetWork {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_NETWORK_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
}

/**
* QQ 请求回调函数
*/
- (void)handleSendResult:(QQApiSendResultCode)sendResult {
    switch (sendResult) {
        case EQQAPIAPPNOTREGISTED:
        case EQQAPIMESSAGECONTENTINVALID:
        case EQQAPIMESSAGECONTENTNULL:
        case EQQAPIMESSAGETYPEINVALID:
        case EQQAPIQQNOTINSTALLED:
        case EQQAPIQQNOTSUPPORTAPI:
        case EQQAPISENDFAILD: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }

        case EQQAPISENDSUCESS: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }

        case EQQAPIAPPSHAREASYNC: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }

        default: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
    }
}

/**
* QQ 登录
*/
- (void)qqLogin:(CDVInvokedUrlCommand *)command {
    self.permissions = [NSArray arrayWithObjects:
            kOPEN_PERMISSION_GET_USER_INFO,
            kOPEN_PERMISSION_GET_SIMPLE_USER_INFO,
            kOPEN_PERMISSION_ADD_ALBUM,
            kOPEN_PERMISSION_ADD_IDOL,
            kOPEN_PERMISSION_ADD_ONE_BLOG,
            kOPEN_PERMISSION_ADD_PIC_T,
            kOPEN_PERMISSION_ADD_SHARE,
            kOPEN_PERMISSION_ADD_TOPIC,
            kOPEN_PERMISSION_CHECK_PAGE_FANS,
            kOPEN_PERMISSION_DEL_IDOL,
            kOPEN_PERMISSION_DEL_T,
            kOPEN_PERMISSION_GET_FANSLIST,
            kOPEN_PERMISSION_GET_IDOLLIST,
            kOPEN_PERMISSION_GET_INFO,
            kOPEN_PERMISSION_GET_OTHER_INFO,
            kOPEN_PERMISSION_GET_REPOST_LIST,
            kOPEN_PERMISSION_LIST_ALBUM,
            kOPEN_PERMISSION_UPLOAD_PIC,
            kOPEN_PERMISSION_GET_VIP_INFO,
            kOPEN_PERMISSION_GET_VIP_RICH_INFO,
            kOPEN_PERMISSION_GET_INTIMATE_FRIENDS_WEIBO,
            kOPEN_PERMISSION_MATCH_NICK_TIPS_WEIBO,
                    nil];
    self.callback = command.callbackId;
    if (self.tencentOAuth.isSessionValid) {
        NSMutableDictionary *Dic = [NSMutableDictionary dictionaryWithCapacity:2];
        [Dic setObject:self.tencentOAuth.openId forKey:@"userid"];
        [Dic setObject:self.tencentOAuth.accessToken forKey:@"access_token"];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:Dic];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
    else {
        [self.tencentOAuth authorize:self.permissions inSafari:NO];
    }
}

@end
