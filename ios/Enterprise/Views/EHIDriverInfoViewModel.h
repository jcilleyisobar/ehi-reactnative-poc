//
//  EHIDriverInfoViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIDriverInfo.h"
#import "EHIFormattedPhone.h"
#import "EHIOptionalBoolean.h"
#import "EHIRequiredInfoViewModel.h"
#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIDriverInfoViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *nameTitle;
@property (copy  , nonatomic, readonly) NSString *phoneTitle;
@property (copy  , nonatomic, readonly) NSString *nextTitle;
@property (copy  , nonatomic, readonly) NSString *emailTitle;
@property (copy  , nonatomic, readonly) NSString *emailHelpTitle;
@property (copy  , nonatomic, readonly) NSString *emailToggleTitle;
@property (copy  , nonatomic, readonly) NSString *confirmationTitle;
@property (copy  , nonatomic, readonly) NSString *actionButtonTitle;
@property (copy  , nonatomic, readonly) NSString *outsideVendorMessage;
@property (copy  , nonatomic, readonly) NSString *signInBannerTitle;
@property (copy  , nonatomic, readonly) NSString *signInButtonTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *saveToggleTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *firstNamePlaceholder;
@property (copy  , nonatomic, readonly) NSAttributedString *lastNamePlaceholder;
@property (copy  , nonatomic, readonly) NSAttributedString *phonePlaceholder;
@property (copy  , nonatomic, readonly) NSAttributedString *emailPlaceholder;
@property (copy  , nonatomic, readonly) NSString *displayNameTitle;
@property (assign, nonatomic, readonly) BOOL showFirstNameError;
@property (assign, nonatomic, readonly) BOOL showLastNameError;
@property (assign, nonatomic, readonly) BOOL showPhoneError;
@property (assign, nonatomic, readonly) BOOL showEmailError;
@property (assign, nonatomic, readonly) BOOL showConfirmationTitle;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic, readonly) BOOL hideNameContainer;
@property (assign, nonatomic, readonly) BOOL hideSaveInfoContainer;
@property (assign, nonatomic, readonly) BOOL showSignInContainer;
@property (assign, nonatomic, readonly) BOOL showOutsideVendor;
@property (assign, nonatomic, readonly) BOOL invalidDriverInfo;
@property (assign, nonatomic, readonly) BOOL isAuthenticated;

@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredFieldsModel;
@property (strong, nonatomic) EHIRequiredInfoFootnoteViewModel *footnoteModel;

// driver info fields
@property (copy  , nonatomic) NSString *firstName;
@property (copy  , nonatomic) NSString *lastName;
@property (copy  , nonatomic) NSString *phone;
@property (strong, nonatomic) EHIFormattedPhone *formattedPhone;
@property (copy  , nonatomic) NSString *email;
@property (assign, nonatomic) BOOL wantsEmailNotifications;
@property (assign, nonatomic) EHIOptionalBoolean specialOffersOptIn;
@property (assign, nonatomic) BOOL shouldSaveDriverInfo;

- (void)toggleEmailNotifications;
- (void)commitDriverInfo; 
- (void)presentSignIn;

@end
