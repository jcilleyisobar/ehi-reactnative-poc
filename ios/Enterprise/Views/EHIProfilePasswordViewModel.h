//
//  EHIProfilePasswordViewModel.h
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninFieldModel.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIProfilePasswordViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) EHISigninFieldModel *passwordNewFieldModel;
@property (copy  , nonatomic) EHISigninFieldModel *passwordConfirmFieldModel;

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *updateAlert;
@property (copy  , nonatomic) NSString *password;
@property (copy  , nonatomic) NSString *passwordConfirmation;
@property (copy  , nonatomic) NSString *passwordsDoNotMatch;
@property (copy  , nonatomic, readonly) NSArray *passwordSection;
@property (assign, nonatomic) BOOL isLoading;

@property (assign, nonatomic, readonly) BOOL hasValidPassword;
@property (assign, nonatomic, readonly) BOOL hasValidPasswordAndConfirmation;
@property (assign, nonatomic, readonly) BOOL shouldShowInlineError;
@property (assign, nonatomic, readonly) BOOL forceUpdatePassword;
@property (assign, nonatomic) BOOL shouldHighlightPasswordFieldIfNecessary;

@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredInfoViewModel;

- (void)changePassword;

@end
