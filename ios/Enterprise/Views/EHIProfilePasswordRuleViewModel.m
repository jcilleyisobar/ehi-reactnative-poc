//
//  EHIProfilePasswordRuleViewModel.m
//  Enterprise
//
//  Created by cgross on 12/22/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePasswordRuleViewModel.h"
#import "EHIProfilePasswordRule.h"

typedef NS_ENUM(NSInteger, EHIPasswordValidationState) {
    EHIPasswordValidationStateNone,
    EHIPasswordValidationStateValid,
    EHIPasswordValidationStateInvalid
};

@interface EHIProfilePasswordRuleViewModel ()
@property (strong, nonatomic) EHIProfilePasswordRule *rule;
@property (assign, nonatomic) EHIPasswordValidationState previousState;
@end

@implementation EHIProfilePasswordRuleViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIProfilePasswordRule class]]) {
            self.rule = (EHIProfilePasswordRule *)model;
        }
    }
    
    return self;
}

- (void)updateWithModel:(EHIProfilePasswordRule *)model
{
    [super updateWithModel:model];
    
    self.rule  = model;
    self.title = model.title;
}

- (void)invalidatePassword:(NSString *)password
{
    BOOL showFailed = self.previousState != EHIPasswordValidationStateNone;
    [self invalidatePassword:password shouldShowFailed:showFailed];
}

- (void)invalidatePassword:(NSString *)password shouldShowFailed:(BOOL)shouldShowFailed
{
    BOOL passed = [self.rule passedForPassword:password];
    self.iconName = passed ? @"icon_checkmarknew-1" : shouldShowFailed ? @"icon_alert_yellow" : @"dot";
    
    self.previousState = passed ? EHIPasswordValidationStateValid : shouldShowFailed ? EHIPasswordValidationStateInvalid : EHIPasswordValidationStateNone;
}

@end
