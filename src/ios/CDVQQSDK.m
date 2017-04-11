//
//  CDVQQSDK.m
//  QQ
//
//  Created by Van on 2016/12/16.
//
//

#import "CDVQQSDK.h"

NSString *QQ_NOT_INSTALLED = @"QQ Client is not installed";
NSString *QQ_PARAM_NOT_FOUND = @"param is not found";
NSString *QQ_IMAGE_PARAM_INCORRECT = @"image param is incorrect";
NSString *QQ_LOGIN_ERROR = @"QQ login error";
NSString *QQ_LOGIN_CANCEL = @"QQ login cancelled";
NSString *QQ_LOGIN_NETWORK_ERROR = @"QQ login network error";
NSString *QQ_SHARE_CANCEL = @"QQ share cancelled by user";
NSString *appId = @"";

@implementation CDVQQSDK {
    TencentOAuth *tencentOAuth;
}
/**
 *  插件初始化，主要用户appkey注册
 */
- (void)pluginInitialize {
    appId = [[self.commandDelegate settings] objectForKey:@"qq_app_id"];
    if (nil == tencentOAuth) {
        tencentOAuth = [[TencentOAuth alloc] initWithAppId:appId andDelegate:self];
    }
}
/**
 *  处理URL
 *
 *  @param notification cordova 传递进来的消息
 */
- (void)handleOpenURL:(NSNotification *)notification {
    NSURL *url = [notification object];
    NSString *schemaPrefix = [@"tencent" stringByAppendingString:appId];
    if ([url isKindOfClass:[NSURL class]] && [[url absoluteString] hasPrefix:[schemaPrefix stringByAppendingString:@"://response_from_qq"]]) {
        [QQApiInterface handleOpenURL:url delegate:self];
    } else {
        [TencentOAuth HandleOpenURL:url];
    }
}

/**
 *  检查QQ官方客户端是否安装
 *
 *  @param command CDVInvokedUrlCommand
 */
- (void)checkClientInstalled:(CDVInvokedUrlCommand *)command {
    NSDictionary *args = [command.arguments objectAtIndex:0];
    int type = [[args valueForKey:@"client"] intValue];
    if(type == 0) {
        [tencentOAuth setAuthShareType:AuthShareType_QQ];
        [self checkQQInstalled:command];
    } else if (type == 1) {
        [self checkTIMInstalled:command];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}
/**
 *  检查QQ官方客户端是否安装
 *
 *  @param command CDVInvokedUrlCommand
 */
- (void)checkQQInstalled:(CDVInvokedUrlCommand *)command {
    if ([TencentOAuth iphoneQQInstalled] && [TencentOAuth iphoneQQSupportSSOLogin]) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 *  检查TIM客户端是否安装
 *
 *  @param command CDVInvokedUrlCommand
 */
- (void)checkTIMInstalled:(CDVInvokedUrlCommand *)command {
    if ([TencentOAuth iphoneTIMInstalled]) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}
/**
 *  QQ 登出
 *
 *  @param command CDVInvokedUrlCommand
 */
- (void)logout:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    [tencentOAuth logout:self];
}

/**
 *  QQ 登录
 *
 *  @param command CDVInvokedUrlCommand
 */
- (void)ssoLogin:(CDVInvokedUrlCommand *)command {
    if (nil == tencentOAuth) {
        tencentOAuth = [[TencentOAuth alloc] initWithAppId:appId andDelegate:self];
    }
    NSDictionary *args = [command.arguments objectAtIndex:0];
    self.callback = command.callbackId;
    NSArray *permissions = [NSArray arrayWithObjects:
                                        kOPEN_PERMISSION_GET_USER_INFO,
                                        kOPEN_PERMISSION_GET_SIMPLE_USER_INFO,
                                        kOPEN_PERMISSION_ADD_ALBUM,
                                        kOPEN_PERMISSION_ADD_ONE_BLOG,
                                        kOPEN_PERMISSION_ADD_SHARE,
                                        kOPEN_PERMISSION_ADD_TOPIC,
                                        kOPEN_PERMISSION_CHECK_PAGE_FANS,
                                        kOPEN_PERMISSION_GET_INFO,
                                        kOPEN_PERMISSION_GET_OTHER_INFO,
                                        kOPEN_PERMISSION_LIST_ALBUM,
                                        kOPEN_PERMISSION_UPLOAD_PIC,
                                        kOPEN_PERMISSION_GET_VIP_INFO,
                                        kOPEN_PERMISSION_GET_VIP_RICH_INFO,
                                        nil];
    int type = [[args valueForKey:@"client"] intValue];
    if (type == 0) {
        [tencentOAuth setAuthShareType:AuthShareType_QQ];
    } else if (type == 1) {
        [tencentOAuth setAuthShareType:AuthShareType_TIM];
    } else {
        [tencentOAuth setAuthShareType:AuthShareType_Unknow];
    }
    [tencentOAuth authorize:permissions];

}

/**
 分享文本
 
 @param command cordova参数
 */
- (void)shareText:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if ([args objectForKey:@"text"]) {
        NSString *text = [args objectForKey:@"text"];
        int scene = [[args valueForKey:@"scene"] intValue];
        int type = [[args valueForKey:@"client"] intValue];
        [self shareObjectWithData:@{ @"text" : text } Type:TextMessage Scene:scene ClientType:type];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 分享图片
 
 @param command Cordova参数
 */
- (void)shareImage:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        NSString *title = [self check:@"title" in:args];
        NSString *image = [self check:@"image" in:args];
        NSString *description = [self check:@"description" in:args];
        int scene = [[args valueForKey:@"scene"] intValue];
        int type = [[args valueForKey:@"client"] intValue];
        NSData *imageData = [self processImage:image];
        if(!imageData) {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_IMAGE_PARAM_INCORRECT];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            [self shareObjectWithData:@{ @"image" : imageData,
                                         @"title" : title,
                                         @"description" : description }
                                 Type:ImageMesssage
                                Scene:scene
                           ClientType:type];
        }
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 分享新闻链接
 
 @param command Cordova参数
 */
- (void)shareNews:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        NSString *title = [self check:@"title" in:args];
        NSString *image = [self check:@"image" in:args];
        NSString *description = [self check:@"description" in:args];
        NSString *url = [self check:@"url" in:args];
        int scene = [[args valueForKey:@"scene"] intValue];
        int type = [[args valueForKey:@"client"] intValue];
        NSData *imageData = [self processImage:image];
        if(!imageData) {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_IMAGE_PARAM_INCORRECT];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            [self shareObjectWithData:@{ @"url" : url,
                                         @"image" : imageData,
                                         @"title" : title,
                                         @"description" : description }
                                 Type:NewsMessageWithLocalImage
                                Scene:scene
                                ClientType:type];

        }
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 分享音乐链接
 
 @param command Cordova参数
 */
- (void)shareAudio:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        NSString *title = [self check:@"title" in:args];
        NSString *image = [self check:@"image" in:args];
        NSString *description = [self check:@"description" in:args];
        NSString *url = [self check:@"url" in:args];
        NSString *flashUrl = [self check:@"flashUrl" in:args];
        int scene = [[args valueForKey:@"scene"] intValue];
        int type = [[args valueForKey:@"client"] intValue];
        NSData *imageData = [self processImage:image];
        if(!imageData) {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_IMAGE_PARAM_INCORRECT];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            [self shareObjectWithData:@{ @"url" : url,
                                         @"image" : imageData,
                                         @"flashUrl" : flashUrl,
                                         @"title" : title,
                                         @"description" : description }
                                 Type:AudioMessage
                                Scene:scene
                                ClientType:type];
        }
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 分享视频链接
 
 @param command Cordova参数
 */
- (void)shareVideo:(CDVInvokedUrlCommand *)command {
    self.callback = command.callbackId;
    NSDictionary *args = [command.arguments objectAtIndex:0];
    if (args) {
        NSString *title = [self check:@"title" in:args];
        NSString *image = [self check:@"image" in:args];
        NSString *description = [self check:@"description" in:args];
        NSString *url = [self check:@"url" in:args];
        NSString *flashUrl = [self check:@"flashUrl" in:args];
        int scene = [[args valueForKey:@"scene"] intValue];
        int type = [[args valueForKey:@"client"] intValue];
        NSData *imageData = [self processImage:image];
        if(!imageData) {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_IMAGE_PARAM_INCORRECT];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            [self shareObjectWithData:@{ @"url" : url,
                                         @"image" : imageData,
                                         @"flashUrl" : flashUrl,
                                         @"title" : title,
                                         @"description" : description }
                                 Type:VideoMessage
                                Scene:scene
                           ClientType:type];
        }
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_PARAM_NOT_FOUND];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

/**
 分享文本到QQ空间
 
 @param text 分享文本
 */
- (void)shareTextToQQZone:(NSString *)text Client:(int) client {
    QQApiImageArrayForQZoneObject *txtObj = [QQApiImageArrayForQZoneObject objectWithimageDataArray:nil title:text];
    if (client == 1) {
        txtObj.shareDestType = AuthShareType_TIM;
    } else {
        txtObj.shareDestType = AuthShareType_QQ;
    }
    SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:txtObj];
    QQApiSendResultCode sent = [QQApiInterface SendReqToQZone:req];
    [self handleSendResult:sent];
}

/**
 分享方法
 
 @param shareData 分享数据
 @param type 分享的类型
 @param scene 分享的场景
 */
- (void)shareObjectWithData:(NSDictionary *)shareData Type:(QQShareType)type Scene:(QQShareScene)scene ClientType:(int) client {
    switch (type) {
        case TextMessage: {
            NSString *msg = [shareData objectForKey:@"text"];
            QQApiTextObject *txtObj = [QQApiTextObject objectWithText:msg];
            [txtObj setCflag:kQQAPICtrlFlagQZoneShareOnStart];
            switch (scene) {
                case QQZone:
                    [self shareTextToQQZone:msg Client:client];
                    return;
                case Favorite:
                    [txtObj setCflag:kQQAPICtrlFlagQQShareFavorites];
                    break;
                default:
                    [txtObj setCflag:kQQAPICtrlFlagQQShare];
                    break;
            }
            if (client == 1) {
                txtObj.shareDestType = AuthShareType_TIM;
            } else {
                txtObj.shareDestType = AuthShareType_QQ;
            }
            SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:txtObj];
            QQApiSendResultCode sent = [QQApiInterface sendReq:req];
            [self handleSendResult:sent];
        } break;
        case ImageMesssage: {
            NSData *data = [shareData objectForKey:@"image"];
            NSString *title = [shareData objectForKey:@"title"];
            NSString *description = [shareData objectForKey:@"description"];
            QQApiImageObject *imgObj = [QQApiImageObject objectWithData:data
                                                       previewImageData:data
                                                                  title:title
                                                            description:description];
            switch (scene) {
                case QQZone:
                    [imgObj setCflag:kQQAPICtrlFlagQZoneShareOnStart];
                    break;
                case Favorite:
                    [imgObj setCflag:kQQAPICtrlFlagQQShareFavorites];
                    break;
                default:
                    [imgObj setCflag:kQQAPICtrlFlagQQShare];
                    break;
            }
            if (client == 1) {
                imgObj.shareDestType = AuthShareType_TIM;
            } else {
                imgObj.shareDestType = AuthShareType_QQ;
            }
            SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:imgObj];
            QQApiSendResultCode sent = [QQApiInterface sendReq:req];
            [self handleSendResult:sent];
        } break;
        case NewsMessageWithLocalImage: {
            NSData *data = [shareData objectForKey:@"image"];
            NSURL *url = [NSURL URLWithString:[shareData objectForKey:@"url"]];
            NSString *title = [shareData objectForKey:@"title"];
            NSString *description = [shareData objectForKey:@"description"];
            QQApiNewsObject *newsObj = [QQApiNewsObject objectWithURL:url
                                                                title:title
                                                          description:description
                                                     previewImageData:data];
            switch (scene) {
                case QQZone:
                    [newsObj setCflag:kQQAPICtrlFlagQZoneShareOnStart];
                    break;
                case Favorite:
                    [newsObj setCflag:kQQAPICtrlFlagQQShareFavorites];
                    break;
                default:
                    [newsObj setCflag:kQQAPICtrlFlagQQShare];
                    break;
            }
            if (client == 1) {
                newsObj.shareDestType = AuthShareType_TIM;
            } else {
                newsObj.shareDestType = AuthShareType_QQ;
            }
            SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:newsObj];
            QQApiSendResultCode sent = [QQApiInterface sendReq:req];
            [self handleSendResult:sent];
        } break;
        case AudioMessage: {
            NSData *data = [shareData objectForKey:@"image"];
            NSURL *url = [NSURL URLWithString:[shareData objectForKey:@"url"]];
            NSString *title = [shareData objectForKey:@"title"];
            NSString *description = [shareData objectForKey:@"description"];
            NSURL *flashUrl = [NSURL URLWithString:[shareData objectForKey:@"url"]];
            QQApiAudioObject *audioObj = [QQApiAudioObject objectWithURL:url
                                                                   title:title
                                                             description:description
                                                        previewImageData:data];
            [audioObj setFlashURL:flashUrl];
            switch (scene) {
                case QQZone:
                    [audioObj setCflag:kQQAPICtrlFlagQZoneShareOnStart];
                    break;
                case Favorite:
                    [audioObj setCflag:kQQAPICtrlFlagQQShareFavorites];
                    break;
                default:
                    [audioObj setCflag:kQQAPICtrlFlagQQShare];
                    break;
            }
            if (client == 1) {
                audioObj.shareDestType = AuthShareType_TIM;
            } else {
                audioObj.shareDestType = AuthShareType_QQ;
            }
            SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:audioObj];
            QQApiSendResultCode sent = [QQApiInterface sendReq:req];
            [self handleSendResult:sent];
        } break;
        case VideoMessage: {
            NSData *data = [shareData objectForKey:@"image"];
            NSURL *url = [NSURL URLWithString:[shareData objectForKey:@"url"]];
            NSString *title = [shareData objectForKey:@"title"];
            NSString *description = [shareData objectForKey:@"description"];
            NSURL *flashUrl = [NSURL URLWithString:[shareData objectForKey:@"url"]];
            QQApiVideoObject *videoObj = [QQApiVideoObject objectWithURL:url
                                                                   title:title
                                                             description:description
                                                        previewImageData:data];
            [videoObj setFlashURL:flashUrl];
            switch (scene) {
                case QQZone:
                    [videoObj setCflag:kQQAPICtrlFlagQZoneShareOnStart];
                    break;
                case Favorite:
                    [videoObj setCflag:kQQAPICtrlFlagQQShareFavorites];
                    break;
                default:
                    [videoObj setCflag:kQQAPICtrlFlagQQShare];
                    break;
            }
            if (client == 1) {
                videoObj.shareDestType = AuthShareType_TIM;
            } else {
                videoObj.shareDestType = AuthShareType_QQ;
            }
            SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:videoObj];
            QQApiSendResultCode sent = [QQApiInterface sendReq:req];
            [self handleSendResult:sent];
        }
        default:
            break;
    }
}

/**
 分析那个结果处理
 
 @param sendResult 分享结果
 */
- (void)handleSendResult:(QQApiSendResultCode)sendResult {
    switch (sendResult) {
        case EQQAPISENDSUCESS:
            break;
        case EQQAPIAPPSHAREASYNC:
            break;
        case EQQAPIAPPNOTREGISTED: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"App 未注册"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPIMESSAGECONTENTINVALID:
        case EQQAPIMESSAGECONTENTNULL:
        case EQQAPIMESSAGETYPEINVALID: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"发送参数错误"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPITIMNOTINSTALLED: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"没有安装 TIM"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPIQQNOTINSTALLED: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"没有安装手机 QQ"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPITIMNOTSUPPORTAPI:
        case EQQAPIQQNOTSUPPORTAPI: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"API 接口不支持"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPISENDFAILD: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"发送失败"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPIVERSIONNEEDUPDATE: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"当前 QQ 版本太低"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case ETIMAPIVERSIONNEEDUPDATE: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"当前 TIM 版本太低"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPIQZONENOTSUPPORTTEXT: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"QQZone 不支持 QQApiTextObject 分享"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPIQZONENOTSUPPORTIMAGE: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"QQZone 不支持 QQApiImageObject 分享"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case EQQAPISHAREDESTUNKNOWN: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"未指定分享到 QQ 或 TIM"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        default: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"发生其他错误"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
    }
}

#pragma mark - QQApiInterfaceDelegate
- (void)onReq:(QQBaseReq *)req {
}

- (void)onResp:(QQBaseResp *)resp {
    switch ([resp.result integerValue]) {
        case 0: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        case -4: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_SHARE_CANCEL];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
        default: {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
            break;
        }
    }
}

- (void)isOnlineResponse:(NSDictionary *)response {
}

#pragma mark - TencentSessionDelegate
- (void)tencentDidLogin {
    if (tencentOAuth.accessToken && 0 != [tencentOAuth.accessToken length]) {
        NSMutableDictionary *Dic = [NSMutableDictionary dictionaryWithCapacity:2];
        [Dic setObject:tencentOAuth.openId forKey:@"userid"];
        [Dic setObject:tencentOAuth.accessToken forKey:@"access_token"];
        [Dic setObject:[NSString stringWithFormat:@"%f", [tencentOAuth.expirationDate timeIntervalSince1970] * 1000] forKey:@"expires_time"];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:Dic];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
}

- (void)tencentDidLogout {
    tencentOAuth = nil;
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
}

- (void)tencentDidNotLogin:(BOOL)cancelled {
    if (cancelled) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_CANCEL];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
    }
}

- (void)tencentDidNotNetWork {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:QQ_LOGIN_NETWORK_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
}

/**
 图片处理
 
 @param image 图片数据
 @return 图片NSdata数据
 */
- (NSData *)processImage:(NSString *)image {
    if ([self isBase64Data:image]) {
        return [[NSData alloc] initWithBase64EncodedString:image options:0];
    } else if ([image hasPrefix:@"http://"] || [image hasPrefix:@"https://"]) {
        NSURL *url = [NSURL URLWithString:image];
        return [NSData dataWithContentsOfURL:url];
    } else {
        return [NSData dataWithContentsOfFile:image];
    }
}

/**
 检查图片是不是Base64
 
 @param data 图片数据
 @return 结果true or false
 */
- (BOOL)isBase64Data:(NSString *)data {
    data = [[data componentsSeparatedByCharactersInSet:
             [NSCharacterSet whitespaceAndNewlineCharacterSet]]
            componentsJoinedByString:@""];
    if ([data length] % 4 == 0) {
        static NSCharacterSet *invertedBase64CharacterSet = nil;
        if (invertedBase64CharacterSet == nil) {
            invertedBase64CharacterSet = [[NSCharacterSet characterSetWithCharactersInString:@"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="] invertedSet];
        }
        return [data rangeOfCharacterFromSet:invertedBase64CharacterSet options:NSLiteralSearch].location == NSNotFound;
    }
    return NO;
}

/**
 检查参数是否存在
 
 @param param 要检查的参数
 @param args 参数字典
 @return 参数
 */
- (NSString *)check:(NSString *)param in:(NSDictionary *)args {
    NSString *data = [args objectForKey:param];
    return data?data:@"";
}

@end
