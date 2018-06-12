//
//  EHIMemberInfoEditViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIMemberInfoEditSection) {
    EHIMemberInfoEditSectionRequiredInfo,
    EHIMemberInfoEditSectionForm,
    EHIMemberInfoEditSectionRequiredInfoFootnote,
};

typedef NS_ENUM(NSUInteger, EHIMemberInfoEditRow) {
    EHIMemberInfoEditRowName,
    EHIMemberInfoEditRowMemberID,
    EHIMemberInfoEditRowAccount,
    EHIMemberInfoEditRowAccountMissing,
    EHIMemberInfoEditRowEmail,
    EHIMemberInfoEditRowPhonePreferred,
    EHIMemberInfoEditRowPhoneAlternate,
    EHIMemberInfoEditRowCountry,
    EHIMemberInfoEditRowStreetOne,
    EHIMemberInfoEditRowStreetTwo,
    EHIMemberInfoEditRowStreetThree,
    EHIMemberInfoEditRowCity,
    EHIMemberInfoEditRowCountrySubdivision,
    EHIMemberInfoEditRowPostal,
};

@class EHIRequiredInfoViewModel;
@class EHIRequiredInfoFootnoteViewModel;
@interface EHIMemberInfoEditViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic) NSArray *formViewModels;
@property (strong, nonatomic) NSString *saveButtonTitle;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL shouldInvalidateConstraints;
@property (copy  , nonatomic) void (^editHandler)();

@property (strong, nonatomic, readonly) EHIRequiredInfoViewModel *requiredModel;
@property (strong, nonatomic, readonly) EHIRequiredInfoFootnoteViewModel *footnoteModel;

- (void)didSelectItemAtIndex:(NSUInteger)index;
- (void)saveChanges;

@end
