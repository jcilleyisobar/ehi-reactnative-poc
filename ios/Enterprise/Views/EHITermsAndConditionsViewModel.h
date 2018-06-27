//
//  TermsAndConditionsViewModel.h
//  Enterprise
//
//  Created by frhoads on 10/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservation.h"
#import "EHIViewModel_Subclass.h"
#import "EHITermsEU.h"
#import "EHIFormFieldDropdownViewModel.h"

typedef NS_ENUM(NSUInteger, EHITermsSections) {
    EHITermsLanguageSection
};

@interface EHITermsAndConditionsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *htmlString;
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic) EHIReservation *reservation;
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *dropDownModel;

- (void)didShowDropdown;
- (void)dismiss;

@end
