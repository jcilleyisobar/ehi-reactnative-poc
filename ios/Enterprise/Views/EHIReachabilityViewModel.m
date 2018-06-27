//
//  EHIReachabilityViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReachabilityViewModel.h"
#import "EHIReachability.h"

@interface EHIReachabilityViewModel () <EHIReachabilityListener>
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL isDelayed;
@property (assign, nonatomic) BOOL showsSplash;
@end

@implementation EHIReachabilityViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title       = EHILocalizedString(@"reachability_title", @"Sorry, we're having some network trouble.", @"Title for the reachability modal");
        _details     = EHILocalizedString(@"reachability_details", @"We can't connect to the Enterprise servers. Please check your network connection, and if it looks okay hit the 'Retry' button.", @"Details for the reachability modal");
        _retryTitle  = EHILocalizedString(@"reachability_retry_button", @"RETRY", @"Title for the reachability 'Retry' button");
        
        // show the splash when reachability is initially unknown
        _showsSplash = [EHIReachability sharedInstance].isReachabilityUnknown;
        
        // listen for reachability changes
        [[EHIReachability sharedInstance] addListener:self];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];

    // after a second, if we're still showing the splash mark ourselves
    // as delayed
    __weak typeof(self) welf = self;
    dispatch_after_seconds(1.0, ^{
        if(welf.showsSplash) {
            welf.isDelayed = YES;
        }
    });
}

# pragma mark - Actions

- (void)retryConnection
{
    // mark that we've "begun" loading
    self.isLoading = YES;
    // and attempt to retry reachability
    [[EHIReachability sharedInstance] retry];
}

# pragma mark - EHIReachabilityListener

- (void)reachability:(EHIReachability *)reachability didChange:(BOOL)isReachable
{
    // if we were showing the splash and transitioned into unreachable, then hide the splash
    if(self.showsSplash && !reachability.isReachabilityUnknown && !isReachable) {
        self.showsSplash = NO;
    }
    
    // if we were delayed and got an update, then switch off the delayed state
    self.isDelayed = NO;
    // turn off loading whenever we get an update
    self.isLoading = NO;
}

@end
