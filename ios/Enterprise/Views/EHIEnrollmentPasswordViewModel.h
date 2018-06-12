//
//  EHIEnrollmentPasswordViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninFieldModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentPasswordType) {
    EHIEnrollmentPasswordTypeCreate,
    EHIEnrollmentPasswordTypeConfirmation
};

@protocol EHIEnrollmentPasswordViewModelDelegate;
@interface EHIEnrollmentPasswordViewModel : EHIViewModel <MTRReactive>

@property (weak  , nonatomic) id<EHIEnrollmentPasswordViewModelDelegate> delegate;
@property (assign, nonatomic) EHIEnrollmentPasswordType type;
@property (copy  , nonatomic) NSString *password;
@property (copy  , nonatomic, readonly) NSString *passwordsDoNotMatch;
@property (copy  , nonatomic, readonly) NSAttributedString *terms;
@property (assign, nonatomic, readonly) BOOL hideTerms;
@property (strong, nonatomic, readonly) EHISigninFieldModel *signinModel;
@property (assign, nonatomic) BOOL showAlert;
@property (assign, nonatomic) BOOL showPasswordsDontMatch;
@property (assign, nonatomic) BOOL termsRead;

- (void)toggleReadTerms;

@end

@protocol EHIEnrollmentPasswordViewModelDelegate <NSObject>
- (void)enrollmentPasswordChanged:(EHIEnrollmentPasswordViewModel *)viewModel;
- (void)enrollmentPasswordToggleReadTerms:(BOOL)readTerms;
@end