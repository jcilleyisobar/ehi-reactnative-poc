//
//  EHIToastManager.m
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIToastManager.h"
#import "EHIToastView.h"

@interface EHIToastManager ()
@property (weak, nonatomic) EHIToastView *activeToastView;
@end

@implementation EHIToastManager

+ (instancetype)sharedInstance
{
    static EHIToastManager *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

+ (void)showMessage:(NSString *)message
{
    if(!message) {
        return;
    }
    
    EHIToast *toast = [EHIToast new];
    toast.message = message;
    [self showToast:toast];
}

+ (void)showToast:(EHIToast *)toast
{
    [[self sharedInstance] showToast:toast];
}

- (void)showToast:(EHIToast *)toast
{
    if(!toast) {
        return;
    }
    
    // create the new toast and show it
    EHIToastView *toastView = [self insertViewForToast:toast];
    [toastView show];
   
    // update the active toast
    [self hideActiveToast];
    [self setActiveToastView:toastView];
}

- (void)hideActiveToast
{
    [self.activeToastView hide];
}

+ (void)hideActiveToast
{
    // update the active toast
    [self.sharedInstance hideActiveToast];
}

//
// Helpers
//

- (EHIToastView *)insertViewForToast:(EHIToast *)toast
{
    // construct a new toast view
    EHIToastView *view = [EHIToastView instanceWithToast:toast];
    
    // add it to the window contrained by its metrics
    UIView *container = [UIApplication sharedApplication].keyWindow;
    [view resizeDynamicallyForContainer:container.bounds.size model:toast];
    
    [container addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.metrics(view.metrics);
    }];
    
    return view;
}

@end
