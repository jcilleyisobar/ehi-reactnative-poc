//
//  NSNotificationCenter+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 04/13/15.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#import "NSNotificationCenter+Utility.h"

@implementation NSNotificationCenter (Utility)

+ (void)ehi_observeKeyboardNotifications:(BOOL)shouldObserve forTarget:(id<EHIKeyboardResponder>)target
{
    if(shouldObserve) {
        [[NSNotificationCenter defaultCenter] addObserver:target selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:target selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    } else {
        [[NSNotificationCenter defaultCenter] removeObserver:target name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:target name:UIKeyboardWillHideNotification object:nil];
    }
}

- (id<NSObject>)ehi_observe:(NSString *)name block:(void (^)(NSNotification *))block
{
    return [self addObserverForName:name object:nil queue:nil usingBlock:block];
}

- (id<NSObject>)ehi_observeOnce:(NSString *)name block:(void (^)(NSNotification *))block
{
    __block id key;
   
    __weak typeof(self) welf = self;
    key = [self ehi_observe:name block:^(NSNotification *notification) {
        ehi_call(block)(notification);
        [welf removeObserver:key];
    }];
    
    return key;
}

@end

@implementation NSNotification (Utility)

- (void)ehi_animateWithKeyboard:(void(^)(void))animations
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:self.ehi_keyboardAnimationDuration];
    [UIView setAnimationCurve:self.ehi_keyboardAnimationCurve];
    [UIView setAnimationBeginsFromCurrentState:YES];

    ehi_call(animations)();
    
    [UIView commitAnimations];
}

- (CGRect)ehi_keyboardFrameInView:(UIView *)view
{
    UIWindow *window = [UIApplication sharedApplication].windows[0];
    if(!view) {
        view = window.rootViewController.view;
    }
    
    CGRect frame = [self.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    return [view convertRect:frame fromView:window];
}

- (CGFloat)ehi_keyboardOverlapInView:(UIView *)view
{
    CGRect keyboardFrame = [self ehi_keyboardFrameInView:view];
    
    // adjust by content offset when in scroll view
    if([view isKindOfClass:[UIScrollView class]]) {
        UIScrollView *scrollView = (UIScrollView *)view;
        keyboardFrame.origin = CGPointSubtractPoint(keyboardFrame.origin, scrollView.contentOffset);
    }
    
    return MAX(view.bounds.size.height - keyboardFrame.origin.y, 0.0f);
}

- (CGFloat)ehi_keyboardAnimationDuration
{
    return [self.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue];
}

- (UIViewAnimationCurve)ehi_keyboardAnimationCurve
{
    return [self.userInfo[UIKeyboardAnimationCurveUserInfoKey] integerValue];
}

@end

@implementation UIScrollView (Keyboard)

- (void)ehi_animateKeyboardNotification:(NSNotification *)notification additionalInset:(CGFloat)inset animations:(void(^)(void))animations
{
    BOOL shouldInset = [notification.name isEqualToString:UIKeyboardWillShowNotification];
    
    // determine content inset
    CGFloat overlap = shouldInset ? [notification ehi_keyboardOverlapInView:self] + inset : 0.f;
    
    // resize the view so that it fits in between the keyboard and the top of the screen
    [notification ehi_animateWithKeyboard:^{
        // allow OS calculations to include padding below first responder
        self.contentInset = (UIEdgeInsets){ .bottom = overlap + EHIMediumPadding };
        self.scrollIndicatorInsets = (UIEdgeInsets){ .bottom = overlap };
        ehi_call(animations)();
    }];
    
    // adjust inset so scrollview fills entire visible screen and ignores first responder padding
    dispatch_after_seconds(0.1, ^{
        self.contentInset = (UIEdgeInsets){ .bottom = overlap };
    });
}

@end
