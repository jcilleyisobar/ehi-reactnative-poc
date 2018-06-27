//
//  EHIEnrollmentStepOneViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentStepOneViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"
#import "EHIServices+User.h"
#import "EHIUser.h"
#import "EHIEnrollProfile.h"
#import "EHIUserManager.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIFormFieldDateViewModel.h"
#import "EHIConfiguration.h"
#import "EHISigninViewModel.h"
#import "EHIEmeraldClubSignInViewModel.h"
#import "EHIDataStore.h"
#import "EHIDriverInfo.h"
#import "EHIEnrollmentStepTwoViewModel.h"
#import "EHISettings.h"

@interface EHIEnrollmentStepOneViewModel () <EHIFormFieldDelegate>
@property (strong, nonatomic) EHIUser *user;
@property (strong, nonatomic) NSDictionary *countries;
@property (strong, nonatomic) NSDictionary *regions;
@property (strong, nonatomic) EHIRegion *selectedRegion;
@property (strong, nonatomic) EHIFormFieldTextViewModel *firstNameViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *lastNameViewModel;
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *licenseStateViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *licenseNumberViewModel;
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *licenseCountryViewModel;
@property (strong, nonatomic) EHIFormFieldDateViewModel *issueDateViewModel;
@property (strong, nonatomic) EHIFormFieldDateViewModel *expirationViewModel;
@property (strong, nonatomic) EHIFormFieldDateViewModel *birthViewModel;
@property (strong, nonatomic) id<EHINetworkCancelable> regionRequest;
@end

@implementation EHIEnrollmentStepOneViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        self.step = EHIEnrollmentStepOne;
        [self buildModels];
    }
    
    return self;
}

- (void)buildModels
{
    self.firstNameViewModel = [EHIFormFieldTextViewModel new];
    self.firstNameViewModel.title      = EHILocalizedString(@"enroll_first_name", @"FIRST NAME", @"");
    self.firstNameViewModel.sensitive  = YES;
    self.firstNameViewModel.isRequired = YES;
    self.firstNameViewModel.delegate   = self;
    self.firstNameViewModel.captalizationMode = UITextAutocapitalizationTypeWords;
    
    self.lastNameViewModel = [EHIFormFieldTextViewModel new];
    self.lastNameViewModel.title      = EHILocalizedString(@"enroll_last_name", @"LAST NAME", @"");
    self.lastNameViewModel.sensitive  = YES;
    self.lastNameViewModel.isRequired = YES;
    self.lastNameViewModel.delegate   = self;
    self.lastNameViewModel.captalizationMode = UITextAutocapitalizationTypeWords;
    
    self.licenseCountryViewModel = [EHIFormFieldDropdownViewModel new];
    self.licenseCountryViewModel.title      = EHILocalizedString(@"enroll_license_issued_by", @"LICENSE ISSUED BY", @"");
    self.licenseCountryViewModel.isRequired = YES;
    self.licenseCountryViewModel.delegate   = self;
    
    self.licenseStateViewModel = [EHIFormFieldDropdownViewModel new];
    self.licenseStateViewModel.isRequired = YES;
    self.licenseStateViewModel.delegate   = self;
    
    self.licenseNumberViewModel = [EHIFormFieldTextViewModel new];
    self.licenseNumberViewModel.title      = EHILocalizedString(@"enroll_license_number", @"LICENSE NUMBER", @"");
    self.licenseNumberViewModel.sensitive  = YES;
    self.licenseNumberViewModel.isRequired = YES;
    self.licenseNumberViewModel.delegate   = self;
    
    self.issueDateViewModel = [EHIFormFieldDateViewModel new];
    self.issueDateViewModel.title       = EHILocalizedString(@"profile_edit_license_issue_date", @"ISSUE DATE", @"");
    self.issueDateViewModel.placeholder = nil;
    self.issueDateViewModel.isRequired  = YES;
    self.issueDateViewModel.maximumDate = [NSDate ehi_januaryFirstOfNextYear];
    self.issueDateViewModel.delegate    = self;
    
    self.expirationViewModel = [EHIFormFieldDateViewModel new];
    self.expirationViewModel.title       = EHILocalizedString(@"enroll_expiration_date", @"EXPIRATION DATE", @"");
    self.expirationViewModel.placeholder = nil;
    self.expirationViewModel.isRequired  = YES;
    self.expirationViewModel.minimumDate = [NSDate ehi_januaryFirstOfThisYear];
    self.expirationViewModel.delegate    = self;
    
    self.birthViewModel = [EHIFormFieldDateViewModel new];
    self.birthViewModel.title       = EHILocalizedString(@"enroll_birth", @"DATE OF BIRTH", @"");
    self.birthViewModel.placeholder = nil;
    self.birthViewModel.isRequired  = YES;
    self.birthViewModel.maximumDate = [NSDate ehi_januaryFirstOfNextYear];
    self.birthViewModel.delegate    = self;
    
    self.buttonModel = [EHIFormFieldActionButtonViewModel new];
    self.buttonModel.title = EHILocalizedString(@"next_button_title", @"NEXT", @"");
    self.buttonModel.isFauxDisabled = YES;
    self.buttonModel.delegate       = self;
    
    // show some fields during load
    [self invalidateVisibleViewModels];
    
    // fetch countries to determine what views to show
    [self fetchCountries];
    
    [self bindDriverInfo];
}

# pragma mark - Profile Binding

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];

    if([model isKindOfClass:[EHIUser class]]) {
        self.user = model;
    }
}

- (void)setUser:(EHIUser *)user
{
    _user = user;
    
    [self bindUser];
}

- (void)bindUser
{
    EHIUser *user = self.user;
    
    [self setFormFieldValue:user.profiles.basic.firstName forRow:EHIEnrollmentProfileFirstName];
    [self setFormFieldValue:user.profiles.basic.lastName forRow:EHIEnrollmentProfileLastName];
    [self setFormFieldValue:user.license.countryName forRow:EHIEnrollmentProfileLicenseIssuedCountry];
    [self setFormFieldValue:user.license.subdivisionName forRow:EHIEnrollmentProfileLicenseIssuedRegion];
    [self setFormFieldValue:user.license.licenseNumber forRow:EHIEnrollmentProfileLicenseNumber];
    [self setFormFieldValue:user.license.licenseExpiry forRow:EHIEnrollmentProfileExpirationDate];
    [self setFormFieldValue:user.license.licenseIssue forRow:EHIEnrollmentProfileIssueDate];
    [self setFormFieldValue:user.license.birthdate forRow:EHIEnrollmentProfileBirth];
}

- (void)bindDriverInfo
{
    // prefill with driver's info, if exists
    [EHIDataStore first:[EHIDriverInfo class] handler:^(EHIDriverInfo *driverInfo){
        [self setFormFieldValue:driverInfo.firstName forRow:EHIEnrollmentProfileFirstName];
        [self setFormFieldValue:driverInfo.lastName forRow:EHIEnrollmentProfileLastName];
    }];
}

- (void)setFormFieldValue:(id)value forRow:(EHIEnrollmentProfile)row
{
    id formField = [self formFieldViewModelForRow:row];
    switch(row) {
        case EHIEnrollmentProfileLicenseIssuedCountry:
        case EHIEnrollmentProfileLicenseIssuedRegion: {
            EHIFormFieldDropdownViewModel *dropDown = ((EHIFormFieldDropdownViewModel *)formField);
            NSArray *options = dropDown.options;
            NSUInteger index = (options ?: @[]).indexOf(value);
            if(index != NSNotFound) {
                dropDown.selectedOption = index;
            }
            break;
        }
        default: {
            ((EHIFormFieldViewModel *)formField).inputValue = value;
            break;
        }
    }
}

# pragma mark - Country & Region Fetching

- (void)fetchCountries
{
    self.isLoading = YES;
    
    NSString *profileCountry = self.enrollmentProfile.license.countryName;
    [[EHIServices sharedInstance] fetchCountriesWithHandler:^(NSArray *countries, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error && countries) {
            EHIFormFieldDropdownViewModel *country = [self formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedCountry];
            
            // populate countries as 'Canada' -> EHICountry
            self.countries = countries.map(^(EHICountry *country) {
                return country.name ? @[country.name, country] : nil;
            }).dict;
            
            // set and preselect options
            country.options = self.countries.allKeys.sort;
            NSString *selectCountry = profileCountry ?: NSLocale.ehi_country.name;
            NSUInteger index = country.options.indexOf(selectCountry);
            country.selectedOption = index != NSNotFound ? index : EHIFormFieldDropdownValueNone;
            
            [self invalidateSelectedCountry];
        }
    }];
}

- (void)invalidateSelectedCountry
{
    NSString *inputValue = [self inputValueForRow:EHIEnrollmentProfileLicenseIssuedCountry];
    
    // set country from cache
    self.selectedCountry = self.countries[inputValue];
    
    // update region field placeholder
    EHIFormFieldDropdownViewModel *regionViewModel = [self formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedRegion];
    regionViewModel.options = nil;
    regionViewModel.selectedOption = EHIFormFieldDropdownValueNone;
    
    [self invalidateVisibleViewModels];
    
    if (self.isLicenseIssuingAuthorityRequired && [self.selectedCountry.issuingAuthorityName length] > 0) {
        [self populateRegionsDropdownWithIssuingAuthority];
    } else if(self.isCountrySubdivisionEnabled) {
        [self fetchRegions];
    }
}

- (void)populateRegionsDropdownWithIssuingAuthority {
    EHIFormFieldDropdownViewModel *licenseState = [self formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedRegion];
    licenseState.options = @[self.selectedCountry.issuingAuthorityName];
}

- (void)fetchRegions
{
    // cancel any existing request
    [self.regionRequest cancel];
    
    // fetch regions for provided country
    self.isLoading = YES;
    __weak typeof(self) welf = self;
    self.regionRequest = [[EHIServices sharedInstance] fetchRegionsForCountry:self.selectedCountry handler:^(NSArray *regions, EHIServicesError *error) {
        welf.isLoading = NO;
        welf.regionRequest = nil;
        
        if(!error && regions) {
            
            // populate regions as 'Alabama' -> EHIRegion
            welf.regions = (regions ?: @[]).map(^(EHIRegion *region) {
                return region.name ? @[region.name, region] : nil;
            }).dict;
            
            EHIFormFieldDropdownViewModel *licenseState = [welf formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedRegion];
            __block EHIRegion *userRegion;
            licenseState.options = regions.map(^(EHIRegion *region) {
                if([region.code isEqualToString:welf.selectedCountry.code]) {
                    userRegion = region;
                }
                
                return region.name;
            }) ?: @[];
            
            NSUInteger index = (licenseState.options ?: @[]).indexOf(welf.selectedRegion.name ?: userRegion.name ?: @"");
            licenseState.selectedOption = index != NSNotFound ? index : 0;
        }
    }];
}

# pragma mark - Form Field Invalidation

- (void)invalidateVisibleViewModels
{
    self.formModels = @[
        self.firstNameViewModel,
        self.lastNameViewModel,
        self.licenseCountryViewModel,
        self.licenseStateViewModel,
        self.licenseNumberViewModel,
        self.issueDateViewModel,
        self.expirationViewModel,
        self.birthViewModel
    ];
    
    self.formModels = self.formModels.map(^(EHIFormFieldViewModel *viewModel, int row) {
        return [self shouldShowViewModelInRow:row] ? viewModel : nil;
    });
    
    [self invalidateValidations];
}

- (BOOL)shouldShowViewModelInRow:(EHIEnrollmentProfile)row
{
    switch (row) {
        case EHIEnrollmentProfileLicenseIssuedRegion:
            return [self isCountrySubdivisionEnabled];
        case EHIEnrollmentProfileExpirationDate:
            return [self showLicenseExpirationDate];
        case EHIEnrollmentProfileIssueDate:
            return [self showLicenseIssueDate];
        default: return YES;
    }
}

- (id)formFieldViewModelForRow:(EHIEnrollmentProfile)row
{
    switch (row) {
        case EHIEnrollmentProfileFirstName: return self.firstNameViewModel;
        case EHIEnrollmentProfileLastName: return self.lastNameViewModel;
        case EHIEnrollmentProfileLicenseIssuedCountry: return self.licenseCountryViewModel;
        case EHIEnrollmentProfileLicenseIssuedRegion: return self.licenseStateViewModel;
        case EHIEnrollmentProfileLicenseNumber: return self.licenseNumberViewModel;
        case EHIEnrollmentProfileIssueDate: return self.issueDateViewModel;
        case EHIEnrollmentProfileExpirationDate: return self.expirationViewModel;
        case EHIEnrollmentProfileBirth: return self.birthViewModel;
    }
}

- (id)inputValueForRow:(EHIEnrollmentProfile)row
{
    return ((EHIFormFieldViewModel *)[self formFieldViewModelForRow:row]).inputValue;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    if([viewModel isEqual:[self formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedCountry]]) {
        [self invalidateSelectedCountry];
    }
    
    if([viewModel isEqual:[self formFieldViewModelForRow:EHIEnrollmentProfileLicenseIssuedRegion]]) {
        self.selectedRegion = self.regions[viewModel.inputValue];
    }
    
    [self validateForm:NO];
}

- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel
{
    BOOL invalidForm = [self validateForm:YES];
    if(!invalidForm) {
        [EHIAnalytics trackAction:EHIAnalyticsEnrollmentNext handler:nil];

        EHIUser *user = [self createUserWithBasicProfile:[self createBasicProfile] andLicenseProfile:[self createLicenseProfile]];
        [self fetchUser:user];
    }
    
    self.warning = self.warningMessages;
}

- (void)fetchUser:(EHIUser *)user
{
    EHIUserProfileFetch *fetchProfile = [EHIUserProfileFetch modelForUser:user];
    
    self.isLoading = YES;
    [[EHIServices sharedInstance] searchRenter:fetchProfile handler:^(EHIUser *user, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            [self reset];
            [self handleUser:user];
        }
    }];
}

- (void)handleUser:(EHIUser *)user
{
    self.profileMatch = EHIEnrollmentProfileMatchNoMatch;
    
    BOOL noMatch = user == nil || user.profiles == nil;
    if(noMatch) {
        EHIUser *user = [self createUserWithBasicProfile:[self createBasicProfile] andLicenseProfile:[self createLicenseProfile]];
        [self showStepTwoScreen:user];
        return;
    }
    
    BOOL branchEnrolled = user.additionalData.isBranchEnrolled;
    if(branchEnrolled) {
        [self showActivateWebView];
        return;
    }
    
    EHIUserLoyaltyProgram program = user.profiles.basic.loyalty.program;
    switch(program) {
        case EHIUserLoyaltyProgramEnterprisePlus: {
            self.profileMatch = EHIEnrollmentProfileMatchEnterprisePlus;
            [self showEnterprisePlusSignIn:user];
            break;
        }
        case EHIUserLoyaltyProgramEmeraldClub: {
            self.profileMatch = EHIEnrollmentProfileMatchEmeraldClub;
            [self showStepTwoScreen:user];
            break;
        }
        case EHIUserLoyaltyProgramNonLoyalty: {
            self.profileMatch = EHIEnrollmentProfileMatchNonLoyalty;
            [self showStepTwoScreen:user];
            break;
        }
        default: {
            [self showStepTwoScreen:nil];
            break;
        }
    }
}

- (void)showStepTwoScreen:(EHIUser *)user
{
    EHIEnrollmentStepTwoViewModel *model = [EHIEnrollmentStepTwoViewModel new];
    model.signinFlow = self.signinFlow;
    model.handler = self.handler;
    
    [model updateWithModel:user];
    
    self.router.transition.push(EHIScreenEnrollmentStepTwo).object(model).start(nil);
}

- (void)showActivateWebView
{
    self.activateAlert.show(^(NSInteger index, BOOL canceled){
        if(!canceled) {
            NSString *activateURL = [EHIConfiguration configuration].activateUrl;
            NSURL *url = [NSURL URLWithString:activateURL];
            self.router.transition.present(EHIScreenWebBrowser).object(url).start(nil);
        }
    });
}

- (EHIAlertViewBuilder *)activateAlert
{
    NSString *title   = EHILocalizedString(@"enroll_partial_profile_found_title", @"Looks like you've already gotten started.", @"");
    NSString *message = EHILocalizedString(@"enroll_partial_profile_found_message", @"To complete your enrollment, you'll need your Member Number and driver's license details.", @"");
    NSString *buttonTitle = EHILocalizedString(@"signin_partial_enrollment_action_button_title", @"COMPLETE", @"").uppercaseString;
    NSString *cancelTitle = EHILocalizedString(@"alert_cancel_title", @"CANCEL", @"").uppercaseString;
    
    return [EHIAlertViewBuilder new].title(title).message(message).button(buttonTitle).cancelButton(cancelTitle);
}

- (void)showEnterprisePlusSignIn:(EHIUser *)user
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.state = EHIAnalyticsEnrollmentProfileEPlus;
    }];
    
    EHISigninViewModel *model = [EHISigninViewModel new];
    model.layout   = EHISigninLayoutEnrollment;
    model.username = user.profiles.basic.loyalty.number;

    self.router.transition.push(EHIScreenSignin).object(model).start(nil);
}

- (void)showEmeraldClubSignIn:(EHIUser *)user
{
    EHIEmeraldClubSignInViewModel *model = [EHIEmeraldClubSignInViewModel new];
    model.layout   = EHISigninLayoutEnrollment;
    model.username = user.profiles.basic.loyalty.number;
    
    self.router.transition.push(EHIScreenSigninEmerald).object(model).start(nil);
}

- (NSString *)warningMessages
{
    NSArray *errorMessages = self.errorMessages;
    if(errorMessages.count > 0) {
        NSString *title  = EHILocalizedString(@"enroll_field_validation_message", @"Please check next if the fields are valid:", @"");
        NSString *errors = errorMessages.map(^(NSString *message){
            return [NSString stringWithFormat:@"• %@", message];
        }).join(@"\n");
        
        return [NSString stringWithFormat:@"%@\n%@",title, errors];
    }
    
    return nil;
}

# pragma mark - Actions

- (void)didTapBack
{
    [self reset];
}

# pragma mark - Profile

- (EHIUser *)createUserWithBasicProfile:(EHIUserBasicProfile *)basicProfile andLicenseProfile:(EHIUserLicenseProfile *)licenseProfile
{
    EHIUser *user = [EHIUser modelWithDictionary:@{
        @key(user.profiles) : @{
            @key(user.profiles.basic) : @{
                @key(user.profiles.basic.firstName) : basicProfile.firstName ?: @"",
                @key(user.profiles.basic.lastName)  : basicProfile.lastName ?: @"",
            }
        },
        @key(user.license) : @{
            @key(user.license.countryCode)   : licenseProfile.countryCode ?: @"",
            @key(user.license.countryName)   : licenseProfile.countryName ?: @"",
            @key(user.license.licenseNumber) : licenseProfile.licenseNumber ?: @""
        },
    }];

    if (self.isLicenseIssuingAuthorityRequired) {
        NSString *issuingAuthority = licenseProfile.issuingAuthority.length > 0 ? licenseProfile.issuingAuthority : licenseProfile.subdivisionCode;
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.issuingAuthority) : issuingAuthority ?: @"",
            }
        }];
    } else if(self.isCountrySubdivisionEnabled) {
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.subdivisionCode) : licenseProfile.subdivisionCode ?: @"",
                @key(user.license.subdivisionName) : licenseProfile.subdivisionName ?: @"",
            }
        }];
    }
    
    BOOL requiresExpirationDate = [self showLicenseExpirationDate];
    if(requiresExpirationDate) {
        NSDate *expirationDate = licenseProfile.licenseExpiry;
        NSString *expiration = [expirationDate ehi_string] ?: @"";
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.licenseExpiry) : expiration,
            }
        }];
    }
    
    BOOL requiresIssueDate = [self showLicenseIssueDate];
    if(requiresIssueDate) {
        NSDate *issueDate = [self inputValueForRow:EHIEnrollmentProfileIssueDate];
        NSString *issue = [issueDate ehi_string] ?: @"";
        [user updateWithDictionary:@{
            @key(user.license) : @{
                @key(user.license.licenseIssue) : issue
             }
        }];
    }
    
    NSDate *birthDate = licenseProfile.birthdate;
    NSString *birth = [birthDate ehi_string] ?: @"";
    [user updateWithDictionary:@{
        @key(user.license) : @{
            @key(user.license.birthdate) : birth,
        },
    }];
    
    return user;
}

- (EHIUserBasicProfile *)createBasicProfile
{
    EHIUserBasicProfile *profile = [EHIUserBasicProfile modelWithDictionary:@{
        @key(profile.firstName) : [self inputValueForRow:EHIEnrollmentProfileFirstName] ?: @"",
        @key(profile.lastName)  : [self inputValueForRow:EHIEnrollmentProfileLastName] ?: @"",
    }];
    
    return profile;
}

- (EHIUserLicenseProfile *)createLicenseProfile
{
    // create with mandatory fields
    EHIUserLicenseProfile *license = [EHIUserLicenseProfile modelWithDictionary:@{
        @key(license.countryCode)                       : self.selectedCountry.code ?: @"",
        @key(license.countryName)                       : self.selectedCountry.name ?: @"",
        @key(license.subdivisionCode)                   : self.selectedRegion.code ?: @"",
        @key(license.subdivisionName)                   : self.selectedRegion.name ?: @"",
        @key(license.issuingAuthority)                  : self.selectedCountry.issuingAuthorityName ?: @"",
        @key(license.licenseNumber)                     : [self inputValueForRow:EHIEnrollmentProfileLicenseNumber] ?: @"",
    }];
    
    NSDate *expirationDate = [self inputValueForRow:EHIEnrollmentProfileExpirationDate];
    NSString *expiration = [expirationDate ehi_string] ?: @"";
    [license updateWithDictionary:@{
        @key(license.licenseExpiry) : expiration,
    }];
    
    // add issue date if required
    BOOL showIssueDate = [self showLicenseIssueDate];
    if(showIssueDate) {
        NSDate *issueDate = [self inputValueForRow:EHIEnrollmentProfileIssueDate];
        NSString *issue   = [issueDate ehi_string] ?: @"";
        [license updateWithDictionary:@{
            @key(license.licenseIssue) : issue
        }];
    }
    
    NSDate *birthDate = [self inputValueForRow:EHIEnrollmentProfileBirth];
    NSString *birth   = [birthDate ehi_string] ?: @"";
    [license updateWithDictionary:@{
        @key(license.birthdate) : birth,
    }];
    
    return license;
}

- (void)setAddExtraPadding:(BOOL)addExtraPadding
{
    self.birthViewModel.extraPadding = addExtraPadding ? EHIMediumPadding : 0;
}

# pragma mark - Validation

- (BOOL)showLicenseExpirationDate
{
    return self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityOptional
        || self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)isCountrySubdivisionEnabled
{
    return self.selectedCountry.enableCountrySubdivision;
}

- (BOOL)isLicenseIssuingAuthorityRequired
{
    #if defined(DEBUG) || defined(UAT)
    if([EHISettings shouldForceIssuingAuthorityRequired]) {
        return YES;
    }
    #endif
    return self.selectedCountry.isLicenseIssuingAuthorityRequired;
}

- (BOOL)showLicenseIssueDate
{
    return self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityOptional
        || self.selectedCountry.licenseIssueDate == EHICountryFieldVisibilityMandatory;
}

- (BOOL)shouldValidateLicenseExpirationDateWithFilledIssueDate:(BOOL)isIssueDateFilled
{
    if ([self shouldValidateForOutsideNorthAmerica]) {
        return !isIssueDateFilled;
    }
    return self.selectedCountry.licenseExpiryDate == EHICountryFieldVisibilityMandatory;
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

- (BOOL)validateForm:(BOOL)showErrors;
{
    // run validation check on all visible fields
    __block BOOL validForm = YES;
    
    BOOL isExpirationDateFilled = [self inputValueForRow:EHIEnrollmentProfileExpirationDate] != nil;
    BOOL isIssueDateFilled  = [self inputValueForRow:EHIEnrollmentProfileIssueDate] != nil;
    
    for(EHIFormFieldViewModel *viewModel in self.formModels) {
        BOOL shouldValidate = YES;
        if (viewModel == self.expirationViewModel) {
            shouldValidate = [self shouldValidateLicenseExpirationDateWithFilledIssueDate:isIssueDateFilled];
        } else if (viewModel == self.issueDateViewModel) {
            shouldValidate = [self shouldValidateLicenseIssueDateWithFilledExpirationDate:isExpirationDateFilled];
        }
        if (shouldValidate) {
            validForm &= [viewModel validate:showErrors];
        }
    }
    
    self.invalidForm = !validForm;
    
    return !validForm;
}

- (void)setInvalidForm:(BOOL)invalidForm
{
    _invalidForm = invalidForm;
    
    self.buttonModel.isFauxDisabled = invalidForm;
}

- (void)invalidateValidations
{
    self.formModels.each(^(EHIFormFieldViewModel *viewModel) {
        [viewModel clearValidations];
        [viewModel validates:EHIFormFieldValidationNotEmptyOrSpaces];
    });
}

- (NSArray *)errorMessages
{
    BOOL isExpirationDateFilled = [self inputValueForRow:EHIEnrollmentProfileExpirationDate] != nil;
    BOOL isIssueDateFilled  = [self inputValueForRow:EHIEnrollmentProfileIssueDate] != nil;
    
    return self.formModels
        .reject(^(EHIFormFieldViewModel *model) {
            BOOL shouldValidate = YES;
            if (model == self.expirationViewModel) {
                shouldValidate = [self shouldValidateLicenseExpirationDateWithFilledIssueDate:isIssueDateFilled];
            } else if (model == self.issueDateViewModel) {
                shouldValidate = [self shouldValidateLicenseIssueDateWithFilledExpirationDate:isExpirationDateFilled];
            }
            BOOL shouldReject = !shouldValidate || [model validate:NO];
            return shouldReject;
        }).map(^(EHIFormFieldViewModel *model){
            return model.title;
        });
}

- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors
{
    [self validateForm:showErrors];
    
    return self.errorMessages;
}

- (EHIUser *)currentUser
{
    return [self createUserWithBasicProfile:[self createBasicProfile] andLicenseProfile:[self createLicenseProfile]];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    context.state = EHIAnalyticsEnrollmentNone;
}

@end
