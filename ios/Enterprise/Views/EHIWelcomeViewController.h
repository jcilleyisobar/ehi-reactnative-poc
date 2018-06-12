//
//  EHIWelcomeViewController.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 09.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewController.h"

@protocol EHIWelcomeDelegate;

@interface EHIWelcomeViewController : EHIViewController
@property (weak, nonatomic) id<EHIWelcomeDelegate>delegate;
@end

@protocol EHIWelcomeDelegate <NSObject>
- (void)welcomeViewControllerDidSelectSignin:(EHIWelcomeViewController *)viewController;
- (void)welcomeViewControllerDidSelectContinue:(EHIWelcomeViewController *)viewController;
- (void)welcomeViewControllerDidSelectJoin:(EHIWelcomeViewController *)viewController;
@end
