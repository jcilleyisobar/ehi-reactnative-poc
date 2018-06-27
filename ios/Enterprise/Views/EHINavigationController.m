//
//  EHINavigationController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "EHINavigationController.h"
#import "EHIMenuAnimationProgress.h"

@interface EHINavigationController () <EHIMenuAnimationProgressListener>

@end

@implementation EHINavigationController

- (void)viewDidLoad
{
    [super viewDidLoad];
  
    // listen to the menu transition so that we can hide properly
    [[EHIMenuAnimationProgress sharedInstance] addListener:self];
    // update the nav bar style
    [EHINavigationController applyCustomStyleToNavigationBar:self.navigationBar];
}

//
// Helpers
//

+ (void)applyCustomStyleToNavigationBar:(UINavigationBar *)navigationBar
{
    // update the bar color
    navigationBar.translucent   = NO;
    navigationBar.barTintColor  = [UIColor ehi_greenColor];
    // remove 1px line at the bottom of the navigation bar
    navigationBar.shadowImage   = [UIImage new];
    [navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    
    // update the title style
    navigationBar.titleTextAttributes = @{
        NSFontAttributeName : [UIFont ehi_fontWithStyle:EHIFontStyleRegular size:18.0f],
        NSForegroundColorAttributeName : UIColor.whiteColor,
    };
}

# pragma mark - EHIMenuAnimationProgressListener

- (void)menuAnimationDidUpdate:(EHIMenuAnimationProgress *)progress
{
    // if we hide the navigation bar completely, nav controller will stop laying out its content beneath it
    self.navigationBar.alpha = MAX(1.0f - progress.percentComplete, 0.1f);
}

@end
