//
//  EHIProfileEditDriverLicenseViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDriverLicenseEditViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldDateViewModel.h"
#import "EHIUserManager+Analytics.h"
#import "EHIServices+User.h"
#import "EHISettings.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIDriverLicenseEditViewModel () <EHIFormFieldDelegate>
@property (strong, nonatomic) NSDictionary *allViewModels;
@property (strong, nonatomic) EHICountry *selectedCountry;
@property (strong, nonatomic) NSDictionary *countries;
@property (strong, nonatomic) id<EHINetworkCancelable> regionRequest;
// computed
@property (nonatomic, readonly) EHIUserLicenseProfile *licenseProfile;
@end

@implementation EHIDriverLicenseEditViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"profile_edit_driver_license_title", @"Driver Information", @"");
        _saveButtonTitle = EHILocalizedString(@"profile_edit_driver_license_save_title", @"SAVE CHANGES", @"");
    }
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];

    if ([model isKindOfClass:[EHICountry class]]) {
        self.selectedCountry = model;
    }
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    EHIFormFieldDropdownViewModel *countryViewModel = [EHIFormFieldDropdownViewModel new];
    countryViewModel.title = EHILocalizedString(@"profile_edit_country_title", @"COUNTRY", @"");
    countryViewModel.placeholder = [countryViewModel.title capitalizedString];
    
    EHIFormFieldDropdownViewModel *issuingAuthorityViewModel = [EHIFormFieldDropdownViewModel new];
    issuingAuthorityViewModel.title = EHILocalizedString(@"profile_edit_issuing_authority_title", @"ISSUING AUTHORITY", @"");
    issuingAuthorityViewModel.placeholder = [issuingAuthorityViewModel.title capitalizedString];
    
    EHIFormFieldTextViewModel *licenseNumberViewModel = [EHIFormFieldTextViewModel new];
    licenseNumberViewModel.title = EHILocalizedString(@"profile_edit_license_number_title", @"LICENSE NUMBER", @"");
    licenseNumberViewModel.placeholder = [licenseNumberViewModel.title capitalizedString];
    licenseNumberViewModel.sensitive   = YES;
    
    EHIFormFieldDateViewModel *issueDateViewModel = [EHIFormFieldDateViewModel new];
    issueDateViewModel.title = EHILocalizedString(@"profile_edit_license_issue_date", @"ISSUE DATE", @"");
    issueDateViewModel.maximumDate = [NSDate ehi_januaryFirstOfNextYear];
    
    EHIFormFieldDateViewModel *expirationViewModel = [EHIFormFieldDateViewModel new];
    expirationViewModel.title = EHILocalizedString(@"profile_edit_license_expiration_date_title", @"EXPIRATION DATE", @"");
    expirationViewModel.minimumDate = [NSDate ehi_januaryFirstOfThisYear];
    
    self.allViewModels = @{
        @(EHIDriverLicenseEditSectionCountry)          : countryViewModel,
        @(EHIDriverLicenseEditSectionIssuingAuthority) : issuingAuthorityViewModel,
        @(EHIDriverLicenseEditSectionLicenseNumber)    : licenseNumberViewModel,
        @(EHIDriverLicenseEditSectionIssueDate)        : issueDateViewModel,
        @(EHIDriverLicenseEditSectionExpiration)       : expirationViewModel
    };
    
    for(NSNumber *key in self.allViewModels) {
        EHIFormFieldViewModel *viewModel = [self.allViewModels objectForKey:key];
        viewModel.delegate      = self;
        viewModel.isLastInGroup = YES;
        viewModel.isRequired    = YES;
    }
    
    // show some fields during load
    [self invalidateVisibleViewModels];
    
    // fetch countries to determine what views to show
    [self fetchCountries];
}

- (void)didResignActive
{
    [super didResignActive];
    
    // prevent retain cycles
    self.editHandler = nil;
}

//
// Helpers
//

- (void)fetchCountries
{
    self.isLoading = YES;
    
    [[EHIServices sharedInstance] fetchCountriesWithHandler:^(NSArray *countries, EHIServicesError *error) {
        self.isLoading = NO;

        if(!error && countries) {
            EHIFormFieldDropdownViewModel *country = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionCountry];

            // populate countries as 'Canada' -> EHICountry
            self.countries = countries.map(^(EHICountry *country) {
                return country.name ? @[country.name, country] : nil;
            }).dict;
            
            // set and preselect options
            country.options = self.countries.allKeys.sort;
            NSUInteger index = country.options.indexOf(self.licenseProfile.countryName);
            if(index != NSNotFound) {
                country.selectedOption = index;
            }

            [self invalidateSelectedCountry];

            // pre-populate with user profile info
            EHIFormFieldViewModel *licenseNumber  = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionLicenseNumber];
            EHIFormFieldViewModel *issueDate      = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionIssueDate];
            EHIFormFieldViewModel *expiration     = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionExpiration];
            
            licenseNumber.inputValue = self.licenseProfile.licenseNumber;
            issueDate.inputValue     = [NSDate ehi_localizedMaskedDate:self.licenseProfile.maskedLicenseIssue] ?: self.licenseProfile.licenseIssue;
            expiration.inputValue    = self.licenseProfile.licenseExpiry ?: [NSDate ehi_localizedMaskedDate:self.licenseProfile.maskedLicenseExpiry];
        }
    }];
}


- (id)formFieldViewModelForSection:(EHIDriverLicenseEditSection)section
{
    return [self.allViewModels objectForKey:@(section)];
}

- (id)inputValueForSection:(EHIDriverLicenseEditSection)section
{
    return ((EHIFormFieldViewModel *)[self formFieldViewModelForSection:section]).inputValue;
}

#pragma mark - Validation

- (BOOL)isLicenseIssuingAuthorityRequired
{
#if defined(DEBUG) || defined(UAT)
    if([EHISettings shouldForceIssuingAuthorityRequired]) {
        return YES;
    }
#endif
    return self.selectedCountry.isLicenseIssuingAuthorityRequired;
}

# pragma mark - Field Visibility

- (BOOL)isCountrySubdivisionEnabled
{
    return self.selectedCountry.enableCountrySubdivision;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    if([viewModel isEqual:[self formFieldViewModelForSection:EHIDriverLicenseEditSectionCountry]]) {
        [self invalidateSelectedCountry];
    }
    
    [self validateForm:NO];
}

//
// Helpers
//

- (void)validateForm:(BOOL)showErrors
{
    // run validation check on all visible fields
    __block BOOL validForm = YES;
    
    self.allViewModels
        .select(^BOOL(NSNumber *index, EHIFormFieldViewModel *viewModel) {
            return [self shouldShowViewModelInSection:[index integerValue]];
        })
        .each(^(NSNumber *index, EHIFormFieldViewModel *viewModel) {
            if ([index integerValue] == EHIDriverLicenseEditSectionExpiration) {
                BOOL isIssueDateFilled = [self inputValueForSection:EHIDriverLicenseEditSectionIssueDate] != nil;
                if([self shouldValidateLicenseExpirationDateWithFilledIssueDate:isIssueDateFilled]) {
                    validForm &= [viewModel validate:showErrors];
                }
            } else if ([index integerValue] == EHIDriverLicenseEditSectionIssueDate) {
                BOOL isExpirationDateFilled = [self inputValueForSection:EHIDriverLicenseEditSectionExpiration] != nil;
                if([self shouldValidateLicenseIssueDateWithFilledExpirationDate:isExpirationDateFilled]) {
                    validForm &= [viewModel validate:showErrors];
                }
            } else {
                validForm &= [viewModel validate:showErrors];
            }
    });
    
    self.invalidForm = !validForm;
}

- (void)invalidateSelectedCountry
{
    NSString *inputValue = [self inputValueForSection:EHIDriverLicenseEditSectionCountry];
    
    // ignore repeat selections
    if(!inputValue || [inputValue isEqualToString:self.selectedCountry.name]) {
        return;
    }
    
    // set country from cache
    self.selectedCountry = self.countries[inputValue];
    
    // reset all but country inputs
    self.allViewModels.each(^(NSNumber *key, EHIFormFieldViewModel *viewModel) {
        if([key integerValue] != EHIDriverLicenseEditSectionCountry) {
            viewModel.inputValue = nil;
        }
    });
    
    // update issuing authority field placeholder
    EHIFormFieldDropdownViewModel *issuingAuthorityViewModel = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionIssuingAuthority];
    issuingAuthorityViewModel.placeholder = [self issuingAuthorityPlaceholderForCountry:self.selectedCountry];
    issuingAuthorityViewModel.options = nil;
    issuingAuthorityViewModel.selectedOption = EHIFormFieldDropdownValueNone;
    
    [self invalidateVisibleViewModels];
    
    NSString *issuingAuthorityName = self.selectedCountry.issuingAuthorityName;
    if (self.isLicenseIssuingAuthorityRequired && [issuingAuthorityName length] > 0) {
        [self populateRegionsDropdownWithIssuingAuthority];
    }  else if(self.isCountrySubdivisionEnabled) {
        [self fetchRegions];
    }
}

- (void)populateRegionsDropdownWithIssuingAuthority {
    EHIFormFieldDropdownViewModel *issuingAuthorityViewModel = [self formFieldViewModelForSection:EHIDriverLicenseEditSectionIssuingAuthority];
    NSString *issuingAuthorityName = self.selectedCountry.issuingAuthorityName;

    if(self.licenseProfile.countryCode != self.selectedCountry.code) {
        issuingAuthorityViewModel.inputValue = nil;
    } else {
        issuingAuthorityViewModel.inputValue = issuingAuthorityName;
    }
    issuingAuthorityViewModel.options = @[issuingAuthorityName];
}


- (NSString *)issuingAuthorityPlaceholderForCountry:(EHICountry *)country
{
    if(country.isUS) {
        return EHILocalizedString(@"profile_edit_driver_license_us_issuing_authority_placeholder", @"State", @"");
    } else if(country.isCanada) {
        return EHILocalizedString(@"profile_edit_driver_license_ca_issuing_authority_placeholder", @"Province", @"");
    } else {
        return nil;
    }
}

- (void)invalidateVisibleViewModels
{
    NSArray *orderedKeys = [self.allViewModels.allKeys sort];

    self.formViewModels = orderedKeys.map(^(NSNumber *section) {
        return [self shouldShowViewModelInSection:[section integerValue]] ? [self.allViewModels objectForKey: section] : nil;
    });
    
    [self invalidateValidations];
}

- (void)invalidateValidations
{
    self.allViewModels.each(^(NSNumber *index, EHIFormFieldViewModel *viewModel) {
        [viewModel clearValidations];
        
        // base validation on enum
        if ([index integerValue] == EHIDriverLicenseEditSectionExpiration) {
            BOOL isIssueDateFilled = [self inputValueForSection:EHIDriverLicenseEditSectionIssueDate] != nil;
            if([self shouldValidateLicenseExpirationDateWithFilledIssueDate:isIssueDateFilled]) {
                [viewModel validates:EHIFormFieldValidationNotEmpty];
            }
        }
        // base validation on enum
        if ([index integerValue] == EHIDriverLicenseEditSectionIssueDate) {
            BOOL isExpirationDateFilled = [self inputValueForSection:EHIDriverLicenseEditSectionExpiration] != nil;
            if([self shouldValidateLicenseIssueDateWithFilledExpirationDate:isExpirationDateFilled]) {
                [viewModel validates:EHIFormFieldValidationNotEmpty];
            }
        }
        // otherwise, require the field
        else {
            [viewModel validates:EHIFormFieldValidationNotEmpty];
        }
    });
}

- (void)fetchRegions
{
    // cancel any existing request
    [self.regionRequest cancel];
    
    // fetch regions for provided country
    __weak typeof(self) welf = self;
    self.regionRequest = [[EHIServices sharedInstance] fetchRegionsForCountry:self.selectedCountry handler:^(NSArray *regions, EHIServicesError *error) {
        welf.regionRequest = nil;
        
        if(!error && regions) {
            EHIFormFieldDropdownViewModel *issuingAuthority = [welf formFieldViewModelForSection:EHIDriverLicenseEditSectionIssuingAuthority];
            __block EHIRegion *userRegion;
            issuingAuthority.options = (regions ?: @[]).map(^(EHIRegion *region) {
                if([region.code isEqualToString:welf.licenseProfile.subdivisionCode]) {
                    userRegion = region;
                }
                
                return region.name;
            });
            
            NSUInteger userIssuingAuthorityIndex = (issuingAuthority.options ?: @[]).indexOf(userRegion.name);
            issuingAuthority.selectedOption = userIssuingAuthorityIndex != NSNotFound ? userIssuingAuthorityIndex : EHIFormFieldDropdownValueNone;
            NSString *subdivisionName = welf.licenseProfile.subdivisionName;
            if(subdivisionName.ehi_isMasked) {
                if(welf.licenseProfile.countryCode != welf.selectedCountry.code) {
                    issuingAuthority.inputValue = nil;
                } else {
                    issuingAuthority.inputValue = subdivisionName;
                }
            }
        }
    }];
}

- (BOOL)shouldShowViewModelInSection:(EHIDriverLicenseEditSection)section
{
    switch (section) {
        case EHIDriverLicenseEditSectionCountry:
        case EHIDriverLicenseEditSectionLicenseNumber:
            return YES;
        case EHIDriverLicenseEditSectionIssuingAuthority:
            return self.isCountrySubdivisionEnabled;
        case EHIDriverLicenseEditSectionExpiration:
            return [self showLicenseExpirationDate];
        case EHIDriverLicenseEditSectionIssueDate:
            return [self showLicenseIssueDate];
    }
}

# pragma mark - Actions

- (void)saveChanges
{
    // show errors if needed
    if(self.invalidForm) {
        [self validateForm:YES];
        return;
    }
    
    EHIUser *user = [EHIUser currentUser];
    EHIUser *newUser = [self createLicenseProfile];
    
    self.isLoading = YES;
    __weak typeof(self) welf = self;
    [[EHIServices sharedInstance] updateUser:user withUser:newUser handler:^(EHIUser *profile, EHIServicesError *error) {
        welf.isLoading = NO;

        if(!error.hasFailed) {
            // invoke edit handler
            ehi_call(welf.editHandler)();
            
            // dismiss view
            welf.router.transition
                .pop(1).start(nil);
        }
    }];
}

//
// Helpers
//

- (EHIUser *)createLicenseProfile
{
    EHIUser *user = [EHIUser new];
    [user updateWithDictionary:@{
       @key(user.license) : @{
            @key(user.license.countryCode)   : self.selectedCountry.code ?: @"",
            @key(user.license.countryName)   : self.selectedCountry.name ?: @"",
            @key(user.license.licenseNumber) : [self inputValueForSection:EHIDriverLicenseEditSectionLicenseNumber] ?: @""
        }
    }];

    //determine user region
    NSString *subdivisionName = [self inputValueForSection:EHIDriverLicenseEditSectionIssuingAuthority];
    EHIRegion *region = (self.selectedCountry.regions ?: @[]).find(^(EHIRegion *region) {
        return [region.name isEqualToString:subdivisionName];
    });
    
    NSString *regionCode = region.code ?: EHIUser.currentUser.license.subdivisionCode ?: @"";

    if (self.isLicenseIssuingAuthorityRequired) {
        NSString *issuingAuthority = self.selectedCountry.issuingAuthorityName ?: regionCode;
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.issuingAuthority) : issuingAuthority ?: @"",
            }
        }];
    } else if(self.isCountrySubdivisionEnabled) {
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.subdivisionName) : subdivisionName ?: region.name ?: @"",
            }
        }];

        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.subdivisionCode) : regionCode,
            }
         }];
    }
    
    // add expiration date if required
    BOOL showExpirationDate = [self showLicenseExpirationDate];
    if(showExpirationDate) {
        id inputValue = [self inputValueForSection:EHIDriverLicenseEditSectionExpiration];
        NSString *expirationDateValue = [inputValue isKindOfClass:NSDate.class] ? [inputValue ehi_string] : inputValue;
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.licenseExpiry) : expirationDateValue ?: @"",
            }
         }];
    }
    
    // add issue date if required
    BOOL showIssueDate = [self showLicenseIssueDate];
    if(showIssueDate) {
        id inputValue = [self inputValueForSection:EHIDriverLicenseEditSectionIssueDate];
        NSString *issueDateValue = [inputValue isKindOfClass:NSDate.class] ? [inputValue ehi_string] : inputValue;
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.licenseIssue) : issueDateValue ?: @"",
            }
        }];
    }
    
    return user;
}

- (BOOL)showLicenseExpirationDate
{
    return self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityOptional || self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)shouldValidateLicenseExpirationDateWithFilledIssueDate:(BOOL)isIssueDateFilled
{
    if ([self shouldValidateForOutsideNorthAmerica]) {
        return !isIssueDateFilled;
    }
    return self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)showLicenseIssueDate
{
    return self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityOptional || self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)shouldValidateLicenseIssueDateWithFilledExpirationDate:(BOOL)isExpirationDateFilled
{
    if ([self shouldValidateForOutsideNorthAmerica]) {
        return !isExpirationDateFilled;
    }
    return self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)shouldValidateForOutsideNorthAmerica
{
    BOOL areFieldsOptional = self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityOptional && self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityOptional;
    BOOL isOutsideNorthAmerica = !self.selectedCountry.isNorthAmerica;
    return areFieldsOptional && isOutsideNorthAmerica;
}

# pragma mark - Passthrough

- (EHIUserLicenseProfile *)licenseProfile
{
    return [EHIUser currentUser].license;
}

# pragma mark - Accessors

- (EHIRequiredInfoViewModel *)requiredModel
{
    return [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeProfile];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary"
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
