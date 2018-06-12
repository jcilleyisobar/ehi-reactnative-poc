//
//  EHIEnrollmentPasswordViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentPasswordViewModel.h"
#import "EHIWebViewModel.h"


@interface EHIEnrollmentPasswordViewModel ()
@property (strong, nonatomic) EHISigninFieldModel *signinModel;
@property (copy  , nonatomic) NSAttributedString *terms;
@property (assign, nonatomic) BOOL hideTerms;
@end

@implementation EHIEnrollmentPasswordViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model conformsToProtocol:@protocol(EHIEnrollmentPasswordViewModelDelegate)]) {
            _delegate = model;
            _passwordsDoNotMatch = EHILocalizedString(@"cp_passwords_do_not_match", @"Passwords do not match", @"");
        }
    }
    
    return self;
}

- (void)setType:(EHIEnrollmentPasswordType)type
{
    _type = type;
    
    switch (type) {
        case EHIEnrollmentPasswordTypeCreate:
            self.signinModel = [self signinCreateModel];
            self.signinModel.returnType = UIReturnKeyNext;
            self.hideTerms = YES;
            break;
        case EHIEnrollmentPasswordTypeConfirmation:
            self.signinModel = [self signinConfirmationModel];
            self.signinModel.returnType = UIReturnKeyDone;
            break;
    }
}

- (EHISigninFieldModel *)signinCreateModel
{
    NSString *title = EHILocalizedString(@"enroll_create_password_title", @"CREATE PASSWORD", @"");
    return [self signinModelWithTitle:title];
}

- (EHISigninFieldModel *)signinConfirmationModel
{
    NSString *title = EHILocalizedString(@"profile_password_confirm_title", @"CONFIRM PASSWORD", @"");
    return [self signinModelWithTitle:title];
}

- (void)toggleReadTerms
{
    self.termsRead = !self.termsRead;
    
    [_delegate enrollmentPasswordToggleReadTerms:self.termsRead];
}

- (void)setTermsRead:(BOOL)termsRead
{
    _termsRead = termsRead;
}

- (void)setPassword:(NSString *)password
{
    _password = password;
    
    [_delegate enrollmentPasswordChanged:self];
}

//
// Helpers
//

- (EHISigninFieldModel *)signinModelWithTitle:(NSString *)title
{
    EHISigninFieldModel *model = [EHISigninFieldModel new];
    
    model.title       = [title ehi_appendComponent:@" *"];
    model.placeholder = @"";
    model.isSecure    = YES;
    
    return model;
}

- (NSAttributedString *)terms
{
    NSString *policiesText = EHILocalizedString(@"enroll_terms_and_conditions_title", @"I have read and understand the #{policies}", @"");
    NSString *policiesName = EHILocalizedString(@"enroll_terms_and_conditions_string", @"Terms & Conditions", @"");
    
    NSAttributedString *attributedPoliciesName =
    [NSAttributedString attributedStringWithString:policiesName
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleRegular size:15.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            [EHIAnalytics trackAction:EHIAnalyticsEnrollmentAdditionalInfo handler:nil];
                                            
                                            [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeTermsAndConditions] push];
                                        }];
    
    return EHIAttributedStringBuilder.new
        .text(policiesText)
        .fontStyle(EHIFontStyleLight, 15.0f)
        .replace(@"#{policies}", attributedPoliciesName)
        .space
        .appendText(@"*")
        .attributes(@{ NSBaselineOffsetAttributeName: @1 })
        .string;    
}

@end
