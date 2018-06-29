//
//  EHIRNFactory.m
//  Enterprise
//
//  Created by Jeff Cilley on 6/27/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRNManager.h"

@interface EHIRNManager () <RCTBridgeDelegate>

@property (strong) RCTBridge *rctBridge;
@property (strong) EHIRNFactory *factory;

@end

@implementation EHIRNManager

@synthesize rctBridge;

+ (EHIRNManager *)sharedInstance
{
    static EHIRNManager *__sharedInstance = nil;
    
    static dispatch_once_t onceToken;
    dispatch_once (&onceToken, ^{
        __sharedInstance = [[EHIRNManager alloc] init];
        __sharedInstance.rctBridge = [[RCTBridge alloc] initWithDelegate:__sharedInstance launchOptions:nil];
        __sharedInstance.factory = [[EHIRNFactory alloc] initWithRCTBridge:__sharedInstance.rctBridge];
    });

    return __sharedInstance;
}

- (void)bindViewConstraints:(UIView *)view toSuperView:(UIView *)superview
{
    view.translatesAutoresizingMaskIntoConstraints = false;
    NSLayoutConstraint *widthConstraint =
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
    NSLayoutConstraint *heightConstraint =
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeWidth multiplier:1 constant:0];

    [superview addConstraints:@[widthConstraint, heightConstraint]];
}

#pragma mark - RCTBridgeDelegate -
- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
    return [NSURL URLWithString:@"http://localhost:8081/index.bundle?platform=ios"];
}

@end
