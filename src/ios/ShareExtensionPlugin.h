//
//  ShareExtensionPlugin.h
//  Uforia
//
//  Created by Sava Savic on 12/22/14.
//
//

#import <Cordova/CDV.h>

@interface ShareExtensionPlugin : CDVPlugin

- (void)shareExtensionPluginStoreInformation:(CDVInvokedUrlCommand*)command;

- (void)getDeepLinkingStatus:(CDVInvokedUrlCommand *)command;

-(void)shareExtensionPluginLogout:(CDVInvokedUrlCommand *)command;

@end
