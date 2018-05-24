//
//  EHIProfileEditDriverLicenseViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIDriverLicenseEditCellSection) {
    EHIDriverLicenseEditCellSectionRequiredInfo,
    EHIDriverLicenseEditCellSectionForm
};

typedef NS_ENUM(NSUInteger, EHIDriverLicenseEditSection) {
    EHIDriverLicenseEditSectionCountry,
    EHIDriverLicenseEditSectionIssuingAuthority,
    EHIDriverLicenseEditSectionLicenseNumber,
    EHIDriverLicenseEditSectionIssueDate,
    EHIDriverLicenseEditSectionExpiration
};

@class EHIRequiredInfoViewModel;
@interface EHIDriverLicenseEditViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic) NSArray *formViewModels;
@property (strong, nonatomic) NSString *saveButtonTitle;
@property (assign, nonatomic) BOOL invalidForm;
@property (copy  , nonatomic) void (^editHandler)();

@property (strong, nonatomic, readonly) EHIRequiredInfoViewModel *requiredModel;

- (void)saveChanges;
- (BOOL)shouldValidateLicenseExpirationDateWithFilledIssueDate:(BOOL)isIssueDateFilled;
- (BOOL)shouldValidateLicenseIssueDateWithFilledExpirationDate:(BOOL)isExpirationDateFilled;

@end

@class EHIUser;
@interface EHIDriverLicenseEditViewModel (Tests)
- (EHIUser *)createLicenseProfile;
@end
