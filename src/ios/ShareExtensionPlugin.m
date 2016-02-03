//
//  ShareExtensionPlugin.m
//  Uforia
//
//  Created by Sava Savic on 12/22/14.
//
//

#import "ShareExtensionPlugin.h"

@implementation ShareExtensionPlugin

-(void)shareExtensionPluginStoreInformation:(CDVInvokedUrlCommand *)command
{
    if([[[UIDevice currentDevice] systemVersion] floatValue] >= 8) {
        NSString *oauth = [command.arguments objectAtIndex:0];
        
        NSUserDefaults *userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.com.deployinc.ent.uforia.UforiaAppGroup"];
        [userDefaults setObject:oauth forKey:@"OAUTH_TOKEN"];
        [userDefaults setBool:YES forKey:@"IS_LOGIN"];
        [userDefaults synchronize];
    }
}

-(void)shareExtensionPluginLogout:(CDVInvokedUrlCommand *)command
{
    if([[[UIDevice currentDevice] systemVersion] floatValue] >= 8) {
        NSUserDefaults *userDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.com.deployinc.ent.uforia.UforiaAppGroup"];
        [userDefaults setObject:@"" forKey:@"OAUTH_TOKEN"];
        [userDefaults setBool:NO forKey:@"IS_LOGIN"];
        [userDefaults synchronize];
    }
}

- (void)getDeepLinkingStatus:(CDVInvokedUrlCommand *)command
{
    NSLog(@"%@", [NSString stringWithFormat:@"%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_URL"]]);
    NSString *deepLinkingUrl = @"";
    if([[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_URL"] && ![[NSString stringWithFormat:@"%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_URL"]] isEqualToString:@""] && ![[NSString stringWithFormat:@"%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_URL"]] isEqualToString:@"(null)"]) {
        deepLinkingUrl = [NSString stringWithFormat:@"%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_URL"]];
    }
    
    NSDictionary *dictionary = [NSDictionary dictionaryWithObjectsAndKeys:deepLinkingUrl, @"DEEP_LINKING_URL", [NSString stringWithFormat:@"%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"DEEP_LINKING_ID"]], @"DEEP_LINKING_ID", nil];
    
    [[NSUserDefaults standardUserDefaults] setObject:@"" forKey:@"DEEP_LINKING_URL"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    //if(![deepLinkingUrl isEqualToString:@""]) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:dictionary];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    //}
}

@end
