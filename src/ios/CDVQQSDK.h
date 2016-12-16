//
//  CDVQQSDK.h
//  QQ
//
//  Created by Van on 2016/12/16.
//
//
#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>
#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
typedef NS_ENUM(NSInteger, QQShareScene) {
    QQ,
    QQZone,
    Favorite,
};

typedef NS_ENUM(NSInteger, QQShareType) {
    TextMessage,
    ImageMesssage,
    NewsMessageWithNetworkImage,
    NewsMessageWithLocalImage,
    AudioMessage,
    VideoMessage,
};
typedef NS_ENUM(NSInteger, QQShareImageType) {
    Local,
    Base64,
    Network,
};

@interface CDVQQSDK : CDVPlugin<TencentSessionDelegate,QQApiInterfaceDelegate>

@property(nonatomic, copy) NSString *callback;

- (void)checkClientInstalled:(CDVInvokedUrlCommand *)command;

- (void)ssoLogin:(CDVInvokedUrlCommand *)command;

- (void)logout:(CDVInvokedUrlCommand *)command;

- (void)shareText:(NSString *)text shareScene:(QQShareScene)scene command:(CDVInvokedUrlCommand *)command;

- (void)shareImage:(NSString *)image withImageType:(NSInteger)type title:(NSString *)title description:(NSString *)description shareScene:(QQShareScene)scene command:(CDVInvokedUrlCommand *)command;

- (void)shareNews:(NSString *)url image:(NSString *)image withImageType:(NSInteger)type title:(NSString *)title description:(NSString *)description
       shareScene:(QQShareScene)scene command:(CDVInvokedUrlCommand *)command;

- (void)shareAudio:(NSString *)previewUrl flashUrl:(NSString *)flashUrl image:(NSString *)image withImageType:(NSInteger)type title:(NSString *)title description:(NSString *)description shareScene:(QQShareScene)scene command:(CDVInvokedUrlCommand *)command;

@end
