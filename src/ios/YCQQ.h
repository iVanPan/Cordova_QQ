#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>
#import <TencentOpenAPI/TencentOAuth.h>

@interface YCQQ : CDVPlugin <TencentSessionDelegate>

@property(nonatomic) TencentOAuth *tencentOAuth;
@property(nonatomic, copy) NSString *callback;
@property(nonatomic, copy) NSArray *permissions;

- (void)ssoLogin:(CDVInvokedUrlCommand *)command;

- (void)logout:(CDVInvokedUrlCommand *)command;

- (void)shareToQQ:(CDVInvokedUrlCommand *)command;

- (void)shareToQzone:(CDVInvokedUrlCommand *)command;

- (void)checkClientInstalled:(CDVInvokedUrlCommand *)command;

- (void)addToQQFavorites:(CDVInvokedUrlCommand *)command;
@end
