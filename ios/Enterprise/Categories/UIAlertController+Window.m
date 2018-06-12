//
//  UIAlertController+Window.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/31/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "UIAlertController+Window.h"
#import "EHIViewController.h"
#import <objc/runtime.h>

@interface UIAlertController (Private)
@property (nonatomic, strong) UIWindow *alertWindow;
@end

@implementation UIAlertController (Private)
@dynamic alertWindow;

- (void)setAlertWindow:(UIWindow *)alertWindow
{
    objc_setAssociatedObject(self, @selector(alertWindow), alertWindow, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (UIWindow *)alertWindow
{
    return objc_getAssociatedObject(self, @selector(alertWindow));
}

@end

@implementation UIAlertController (Window)

- (void)show:(BOOL)animated
{
    EHIViewController *rootViewController   = [EHIViewController new];
    rootViewController.view.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    
    self.alertWindow = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.alertWindow.rootViewController = rootViewController;
    
    // use the color defined in our delegate
    self.alertWindow.tintColor = [[UIApplication sharedApplication] delegate].window.tintColor;

    // move it to the top
    UIWindow *topWindow = [UIApplication sharedApplication].windows.lastObject;
    self.alertWindow.windowLevel = topWindow.windowLevel + 1;
    
    // and present it
    [self.alertWindow makeKeyAndVisible];
    [self.alertWindow.rootViewController presentViewController:self animated:animated completion:nil];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    // make sure to dealloc it, when dismissed
    self.alertWindow.hidden = YES;
    self.alertWindow = nil;
}

@end
