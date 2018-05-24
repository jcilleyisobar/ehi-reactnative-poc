//
//  EHIProfileAuthenticateViewController.h
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewController.h"

@protocol EHIProfileAuthenticationDelegate;

@interface EHIProfileAuthenticationViewController : EHIViewController
@property (weak, nonatomic) id<EHIProfileAuthenticationDelegate>delegate;
@end

@protocol EHIProfileAuthenticationDelegate <NSObject>
- (void)profileAuthenticationViewControllerDidAuthenticate:(EHIProfileAuthenticationViewController *)viewController;
@end