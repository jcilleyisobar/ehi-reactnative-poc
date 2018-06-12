//
//  EHIWatchConnectivityManager.m
//  Enterprise
//
//  Created by Michael Place on 10/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@import WatchConnectivity;

#import "EHIWatchConnectivityManager.h"
#import "EHIUserManager.h"
#import "EHIWatchConnectivityConstants.h"
#import "EHIAnalytics.h"

@interface EHIWatchConnectivityManager () <WCSessionDelegate>
@property (weak, nonatomic) WCSession *session;
@end

@implementation EHIWatchConnectivityManager

+ (instancetype)sharedInstance
{
    static dispatch_once_t once;
    static EHIWatchConnectivityManager *sharedInstance;
    
    dispatch_once(&once, ^{
        sharedInstance = [EHIWatchConnectivityManager new];
    });
    
    return sharedInstance;
}

+ (void)prepareToLaunch
{
    if(WCSession.isSupported && [self.sharedInstance session] == nil) {
        // grab the default session
        WCSession *session = [WCSession defaultSession];
        // become the delegate
        [session setDelegate:self.sharedInstance];
        // activation
        [session activateSession];
        
        // grab a reference
        [self.sharedInstance setSession:session];
    }
}

- (void)updateContextWithUser:(EHIUser *)user
{
    NSError *error = nil;
	
	
	NSMutableDictionary *context = [NSMutableDictionary new];
	
	// determine the significant rental
	EHIUserRental *rental = user.currentRentals.count
	? user.currentRentals.firstRental
	: user.upcomingRentals.firstRental;
	
	NSDictionary *response = [rental encodeForWatch];
	context[EHIWatchConnectivityRental] = response;
	
    [self.session updateApplicationContext:context error:&error];
    
    if(error) {
        NSLog(@"Error updating application context: %@", error.localizedDescription);
    }
}

# pragma mark - WCSessionDelegate

- (void)session:(nonnull WCSession *)session activationDidCompleteWithState:(WCSessionActivationState)activationState error:(nullable NSError *)error { }

- (void)sessionDidBecomeInactive:(nonnull WCSession *)session { }

- (void)sessionDidDeactivate:(nonnull WCSession *)session { }

- (void)sessionWatchStateDidChange:(WCSession *)session
{
    NSLog(@"Watch state did change: (Paired - %@, Reachable - %@)",
          session.paired ? @"YES" : @"NO", session.reachable ? @"YES" : @"NO");
}

- (void)sessionReachabilityDidChange:(WCSession *)session
{
    NSLog(@"Phone Session Reachability: %@",
          session.reachable ? @"YES" : @"NO");
}

- (void)session:(WCSession *)session didReceiveMessage:(NSDictionary<NSString *, id> *)message
{
    if (message[@"analytics"]) {
        NSString *screen = message[@"analytics"][@"screen"];
        NSString *state  = message[@"analytics"][@"state"];
        
        [EHIAnalytics changeWatchScreen:screen state:state];
        [EHIAnalytics trackState:nil];
        
        NSLog(@"Analytics: State tracked");
    }
}

- (void)session:(WCSession *)session didReceiveMessage:(NSDictionary<NSString *, id> *)message replyHandler:(void(^)(NSDictionary<NSString *, id> *replyMessage))replyHandler
{
    NSLog(@"Phone did receive message, expecting reply");
    
//	[[EHIUserManager sharedInstance] refreshCurrentAndUpcomingRentalsWithHandler:^(EHIUser *user, EHIServicesError *error) {
//        NSMutableDictionary *reply = [NSMutableDictionary new];
//        
//        // determine the significant rental
//        EHIUserRental *rental = user.currentRentals.count
//            ? user.currentRentals.firstRental
//            : user.upcomingRentals.firstRental;
//        
//        NSDictionary *response = [rental encodeForWatch];
//        reply[EHIConnectivityResponse] = response;
//        reply[EHIConnectivityResponseType] = NSStringFromClass([EHIUserRental class]);
//        
//        replyHandler([reply copy]);
//    }];
}

# pragma mark - Context

- (void)session:(WCSession *)session didReceiveApplicationContext:(NSDictionary<NSString *, id> *)applicationContext
{
    NSLog(@"Phone did receive application context: %@", applicationContext);
}

@end
