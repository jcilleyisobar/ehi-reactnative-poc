//
//  UIApplication+URL.m
//  Enterprise
//
//  Created by mplace on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIApplication+URL.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>

NS_ASSUME_NONNULL_BEGIN

@implementation UIApplication (URL)

+ (void)ehi_promptPhoneCall:(nullable NSString *)phoneNumber
{
    // bail out if number is not provided
    if(!phoneNumber) {
        return;
    }
    
    // alert users if device can't make calls
    if(!self.canMakeCalls) {
        [self showCallingUnavailableAlert];
        return;
    }

    // determine invalid characters
    NSString *validCharacters = @"0123456789-+()";
    NSCharacterSet *invalidCharacters = [NSCharacterSet characterSetWithCharactersInString:validCharacters].invertedSet;
    
    // strip invalid characters
    NSString *result = [phoneNumber stringByTrimmingCharactersInSet:invalidCharacters];
    // percent escape result
    result = [result stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLHostAllowedCharacterSet]];
    // create url
    result = [NSString stringWithFormat:@"telprompt:%@", result];
    
    // prompt user
    [self ehi_openURL:[NSURL URLWithString:result]];
}

+ (void)ehi_openMapsWithSearchQuery:(NSString *)query atLocation:(EHILocation *)location
{
    if(!query || !location.position) {
        return;
    }
    
    NSString *url = @"http://maps.apple.com/?sll=#{latitude},#{longitude}&q=#{query}";
    
    url = [url ehi_applyReplacementMap:@{
        @"latitude"  : @(location.position.latitude),
        @"longitude" : @(location.position.longitude),
        @"query"     : query
    }];
    
    [self ehi_openURL:[NSURL URLWithString:url]];
}

+ (void)ehi_promptUrl:(NSString *)url
{
    NSString *title = EHILocalizedString(@"alert_open_browser_text", @"To view this, we need to leave the app to go to your web browser", @"Link out message when showing something that opens the web browser");
    
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(title)
        .button(EHILocalizedString(@"alert_open_browser_button", @"Open Browser", @"Title for alert 'open browser' button"))
        .cancelButton(nil);
    
    alert.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_openURL:[NSURL URLWithString:url]];
        }
    });
}

+ (void)ehi_openURL:(NSURL *)url
{
    UIApplication *application = [UIApplication sharedApplication];
    
    if(![application canOpenURL:url]) {
        return;
    }
    
    [application openURL:url options:@{} completionHandler:nil];
}

//
// Helpers
//

+ (BOOL)canMakeCalls
{
    // iOS 8 iPod Touch can prompt calls
    BOOL canPromptCall    = [self.sharedApplication canOpenURL:[NSURL URLWithString:@"telprompt:"]];
    
    // check for cellular network code as well
    CTCarrier *carrier    = [[CTTelephonyNetworkInfo new] subscriberCellularProvider];
    NSString *networkCode = [carrier mobileNetworkCode];
    BOOL hasNetworkCode   = networkCode.length != 0;
    
    return canPromptCall && hasNetworkCode;
}

+ (void)showCallingUnavailableAlert
{
    EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"alert_calling_unavailable_text", @"You cannot make calls on this device", @""))
        .button(EHILocalizedString(@"standard_ok_text", @"OK", @"typical ok button"))
        .show(nil);
}

@end

NS_ASSUME_NONNULL_END
