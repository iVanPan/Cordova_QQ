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

@interface CDVQQSDK : CDVPlugin <TencentSessionDelegate, QQApiInterfaceDelegate>

@property (nonatomic, copy) NSString *callback;

- (void)checkClientInstalled:(CDVInvokedUrlCommand *)command;

- (void)ssoLogin:(CDVInvokedUrlCommand *)command;

- (void)logout:(CDVInvokedUrlCommand *)command;

- (void)shareText:(CDVInvokedUrlCommand *)command;

- (void)shareImage:(CDVInvokedUrlCommand *)command;

- (void)shareNews:(CDVInvokedUrlCommand *)command;

//- (void)shareAudio:(NSString *)previewUrl flashUrl:(NSString *)flashUrl
//             image:(NSString *)image
//         withTitle:(NSString *)title
//       description:(NSString *)description
//        shareScene:(QQShareScene)scene
//           command:(CDVInvokedUrlCommand
//                        *)command;

@end
