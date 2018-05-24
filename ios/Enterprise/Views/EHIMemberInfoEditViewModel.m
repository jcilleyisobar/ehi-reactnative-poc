//
//  EHIMemberInfoEditViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMemberInfoEditViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIFormFieldBasicProfileViewModel.h"
#import "EHIFormFieldTextToggleViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIServices+User.h"
#import "EHIUserManager+Analytics.h"
#import "EHIConfiguration.h"
#import "EHIRequiredInfoViewModel.h"
#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIMemberInfoEditViewModel () <EHIFormFieldTextToggleDelegate>
@property (strong, nonatomic) NSArray *allViewModels;
@property (strong, nonatomic) NSDictionary *countries;
@property (strong, nonatomic) EHICountry *selectedCountry;
@property (strong, nonatomic) id<EHINetworkCancelable> countryRequest;
@property (strong, nonatomic) id<EHINetworkCancelable> regionRequest;
// computed
@property (nonatomic, readonly) EHIUserBasicProfile *basicProfile;
@property (nonatomic, readonly) EHIUserContactProfile *contactProfile;
@property (nonatomic, readonly) EHIUserPreferencesProfile *preferencesProfile;
@property (nonatomic, readonly) EHIAddress *address;
@property (nonatomic) EHIOptionalBoolean specialOffersOptIn;
@end

@implementation EHIMemberInfoEditViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title              = EHILocalizedString(@"profile_edit_member_info_title", @"Member Information", @"");
        _saveButtonTitle    = EHILocalizedString(@"profile_edit_member_info_save_title", @"SAVE CHANGES", @"");
        _specialOffersOptIn = [NSLocale ehi_shouldCheckEmailNotificationsByDefault]
            ? EHIOptionalBooleanTrue
            : EHIOptionalBooleanNull;
    }
    return self;
} 

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    EHIFormFieldBasicProfileViewModel *name = [EHIFormFieldBasicProfileViewModel nameFieldForProfile:self.basicProfile];
    EHIFormFieldBasicProfileViewModel *memberID = [EHIFormFieldBasicProfileViewModel memberIdFieldForProfile:self.basicProfile];
    EHIFormFieldBasicProfileViewModel *account = [EHIFormFieldBasicProfileViewModel accountFieldForCorporateAccount:self.contract];
    EHIFormFieldBasicProfileViewModel *accountMissing = [EHIFormFieldBasicProfileViewModel accountFieldForMissingAccount];
    EHIFormFieldTextToggleViewModel *email = [EHIFormFieldTextToggleViewModel emailFieldWithEmail:self.contactProfile.maskedEmail forProfile:self.preferencesProfile];
    email.isRequired = YES;

    // 2 phones at most
    NSString *titlePreferred = EHILocalizedString(@"profile_edit_preferred_phone", @"PREFERRED PHONE NUMBER", @"");
    EHIFormFieldTextViewModel *phonePreferred = [EHIFormFieldTextViewModel phoneFieldForPhone:[self.contactProfile.phones ehi_safelyAccess:0] withTitle:titlePreferred];
    phonePreferred.sensitive  = YES;
    phonePreferred.isRequired = YES;
    
    NSString *titleAlternate = EHILocalizedString(@"profile_edit_alternate_phone", @"ALTERNATE PHONE NUMBER", @"");
    EHIFormFieldTextViewModel *phoneAlternate = [EHIFormFieldTextViewModel phoneFieldForPhone:[self.contactProfile.phones ehi_safelyAccess:1] withTitle:titleAlternate];
    phoneAlternate.sensitive  = YES;
    
    NSArray *addressViewModels = [self addressViewModels];
    
    // gather view models and apply validations
    self.allViewModels = @[name, memberID, account, accountMissing, email, phonePreferred, phoneAlternate].concat(addressViewModels);
    
    __weak typeof(self) welf = self;
    self.allViewModels.each(^(EHIFormFieldViewModel *viewModel, int index) {
        viewModel.delegate   = self;
        
        if([welf shouldEnsureNonEmptyForRow:index]) {
            [viewModel validates:EHIFormFieldValidationNotEmpty];
        }
    });
    
    // show some fields during load
    [self invalidateVisibleViewModels];
    
    // fetch countries to determine what views to show
    [self fetchCountries];
}

- (void)didResignActive
{
    [super didResignActive];
    
     // cancel any active requests
    [self.countryRequest cancel];
    [self.regionRequest cancel];
    
    // prevent retain cycles
    self.editHandler = nil;
}

//
// Helpers
//

- (NSArray *)addressViewModels
{
    EHIFormFieldDropdownViewModel *country = [EHIFormFieldDropdownViewModel new];
    country.title = EHILocalizedString(@"profile_edit_country_of_residence_title", @"COUNTRY", @"");
    country.inputValue = self.address.countryName;
    
    EHIFormFieldTextViewModel *streetOne = [EHIFormFieldTextViewModel new];
    streetOne.title = EHILocalizedString(@"profile_edit_street_title", @"STREET ADDRESS", @"");
    streetOne.inputValue = [self.address.addressLines ehi_safelyAccess:0];
    
    EHIFormFieldTextViewModel *streetTwo = [EHIFormFieldTextViewModel new];
    streetTwo.inputValue = [self.address.addressLines ehi_safelyAccess:1];
    
    EHIFormFieldTextViewModel *streetThree = [EHIFormFieldTextViewModel new];
    streetThree.inputValue = [self.address.addressLines ehi_safelyAccess:2];
    
    EHIFormFieldTextViewModel *city = [EHIFormFieldTextViewModel new];
    city.title = EHILocalizedString(@"profile_edit_city_title", @"CITY", @"");
    city.inputValue = self.address.city;
    city.sensitive  = YES;
    
    EHIFormFieldDropdownViewModel *subdivision = [EHIFormFieldDropdownViewModel new];
    subdivision.title = EHILocalizedString(@"profile_edit_state_title", @"STATE", @"");
    subdivision.inputValue = self.address.subdivisionName;
    
    EHIFormFieldTextViewModel *postal = [EHIFormFieldTextViewModel new];
    postal.title = EHILocalizedString(@"profile_edit_zip_title", @"ZIP CODE", @"");
    postal.inputValue = self.address.postalCode;
    postal.sensitive  = YES;
    postal.isLastInGroup = YES;
    
    return @[country, streetOne, streetTwo, streetThree, city, subdivision, postal]
        .each(^(EHIFormFieldViewModel *model){
            model.isRequired = YES;
        });
}

- (BOOL)shouldEnsureNonEmptyForRow:(EHIMemberInfoEditRow)row
{
    switch(row) {
        case EHIMemberInfoEditRowAccount:
        case EHIMemberInfoEditRowPhoneAlternate:
        case EHIMemberInfoEditRowStreetTwo:
        case EHIMemberInfoEditRowStreetThree:
            return NO;
        default:
            return YES;
    }
}

//
// Helpers
//

- (void)invalidateVisibleViewModels
{
    self.formViewModels = (self.allViewModels ?: @[]).map(^(EHIViewModel *viewModel, int index) {
        return [self shouldShowViewModelForRow:index] ? viewModel : nil;
    });
}

- (void)invalidateConstraints
{
    self.shouldInvalidateConstraints = YES;
}

- (BOOL)shouldShowViewModelForRow:(EHIMemberInfoEditRow)row
{
    switch(row) {
        case EHIMemberInfoEditRowCountrySubdivision:
            return self.selectedCountry.enableCountrySubdivision;
        case EHIMemberInfoEditRowAccount:
            return self.contract != nil;
        case EHIMemberInfoEditRowAccountMissing:
            return self.contract == nil;
        case EHIMemberInfoEditRowStreetThree:
            // Disable the third address line since CROS doesn't support it for now
            //return !self.selectedCountry.isUS;
            return NO;
        default:
            return YES;
    }
}

- (id)formFieldViewModelForRow:(EHIMemberInfoEditRow)row
{
    return self.allViewModels[row];
}

- (id)inputValueForRow:(EHIMemberInfoEditRow)row
{
    return ((EHIFormFieldViewModel *)[self formFieldViewModelForRow:row]).inputValue;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    if([viewModel isEqual:[self formFieldViewModelForRow:EHIMemberInfoEditRowCountry]]) {
        [self invalidateCountry];
    }
    
    [self validateForm:NO];
}

- (void)formField:(EHIFormFieldTextToggleViewModel *)field didChangeToggleValue:(BOOL)toggleEnabled
{
    if([field isEqual:[self formFieldViewModelForRow:EHIMemberInfoEditRowEmail]]) {
        [self invalidateConstraints];

        [self trackEmailPreferenceChange:toggleEnabled];
    }

    self.specialOffersOptIn = toggleEnabled ? EHIOptionalBooleanTrue : EHIOptionalBooleanFalse;
}


//
// Helpers
//

- (void)validateForm:(BOOL)showErrors
{
    // run validation check on all visible fields
    __block BOOL validForm = YES;
    
    for(EHIFormFieldViewModel *viewModel in self.formViewModels) {
        validForm &= [viewModel validate:showErrors];
    }
    
    self.invalidForm = !validForm;
}

- (void)invalidateCountry
{
    NSString *inputValue = [self inputValueForRow:EHIMemberInfoEditRowCountry];
    
    // return on no input or repeated input
    if(!inputValue || [self.selectedCountry.name isEqualToString:inputValue]) {
        return;
    }
    
    // find EHICountry via user input
    self.selectedCountry = self.countries[inputValue];
    
    // update subdivision field
    EHIFormFieldDropdownViewModel *subdivisionViewModel = [self formFieldViewModelForRow:EHIMemberInfoEditRowCountrySubdivision];
    subdivisionViewModel.title      = [self subdivisionTitleForCountry:self.selectedCountry];
    subdivisionViewModel.options    = nil;
    subdivisionViewModel.selectedOption = EHIFormFieldDropdownValueNone;
    
    [self invalidateVisibleViewModels];
    
    if(self.selectedCountry.enableCountrySubdivision) {
        [self fetchRegions];
    }
}

- (NSString *)subdivisionTitleForCountry:(EHICountry *)country
{
    return EHILocalizedString(@"profile_edit_member_info_general_subdivision_title", @"STATE/REGION/PROVINCE", @"");
}

- (void)trackEmailPreferenceChange:(BOOL)wantsEmailNotifications
{
    NSString *action = wantsEmailNotifications ? EHIAnalyticsActionEmailOptIn : EHIAnalyticsActionEmailOptOut;
    [EHIAnalytics trackAction:action handler:nil];
}

# pragma mark - Actions

- (void)didSelectItemAtIndex:(NSUInteger)index
{
    // check if selected index is the name field
    if([self fieldAtIndex:index isRow:EHIMemberInfoEditRowName]) {
        NSString *details = EHILocalizedString(@"profile_edit_member_info_non_editable_name_details", @"To update your first or last name you must call customer service.", @"");
        [self showUneditableFieldModalWithDetails:details];
    }
    // or if it's the member ID field
    else if([self fieldAtIndex:index isRow:EHIMemberInfoEditRowMemberID]) {
        NSString *details = EHILocalizedString(@"profile_edit_member_info_non_editable_member_id_details", @"To update your member ID you must call customer service.", @"");
        [self showUneditableFieldModalWithDetails:details];
    }
    else if([self fieldAtIndex:index isRow:EHIMemberInfoEditRowAccount]) {
        NSString *details = EHILocalizedString(@"profile_edit_member_info_non_editable_corp_account", @"Your account number can't be changed. Please call us if you need additional assistance.", @"");
        [self showUneditableFieldModalWithDetails:details];
    }
}

- (void)saveChanges
{
    // show errors if needed
    if(self.invalidForm) {
        [self validateForm:YES];
        return;
    }
    
    self.isLoading = YES;
   
    EHIUserContactProfile *newContact = [self createContactProfile];
    EHIUserPreferencesProfile *newPreferences = [self createPreferencesProfile];
    id specialOffers = [EHIOptionalBooleanTransformer() reverseTransformedValue:@(newPreferences.email.specialOffers)];
    EHIAddress *newAddress = [self createAddress];
   
    EHIUser *newUser = [EHIUser modelWithDictionary:@{
        @key(newUser.contact)    : @{
            @key(newContact.email)  : newContact.email ?: @"",
        },
        @key(newUser.address)    : @{
            @key(newAddress.addressLines)    : newAddress.addressLines ?: @[],
            @key(newAddress.city)            : newAddress.city ?: @"",
            @key(newAddress.countryCode)     : newAddress.countryCode ?: @"",
            @key(newAddress.countryName)     : newAddress.countryName ?: @"",
            @key(newAddress.subdivisionCode) : newAddress.subdivisionCode ?:@ "",
            @key(newAddress.subdivisionName) : newAddress.subdivisionName ?:@ "",
            @key(newAddress.postalCode)      : newAddress.postalCode ?: @"",
            @key(newAddress.addressType)     : newAddress.addressType ?: @"",
        },
        @key(newUser.preference) : @{
            @key(newPreferences.email) : @{
                @key(newPreferences.email.partnerOffers)  : @(newPreferences.email.partnerOffers),
                @key(newPreferences.email.rentalReceipts) : @(newPreferences.email.rentalReceipts),
                @key(newPreferences.email.specialOffers)  : specialOffers,
            }
        },
    }];
    
    newUser.contact.phones = newContact.phones;
    
    __weak __typeof(self) welf = self;
    [[EHIServices sharedInstance] updateUser:[EHIUser currentUser] withUser:newUser handler:^(EHIUser *user, EHIServicesError *error) {
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

- (BOOL)fieldAtIndex:(NSUInteger)index isRow:(EHIMemberInfoEditRow)row
{
    // compare visible view model ordered by an index
    EHIFormFieldViewModel *visibleViewModel = self.formViewModels[index];
    // to pre-sorted view models ordered by their type
    EHIFormFieldViewModel *inquiredViewModel = self.allViewModels[row];
    
    return [visibleViewModel isEqual:inquiredViewModel];
}

- (void)showUneditableFieldModalWithDetails:(NSString *)details
{
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title = EHILocalizedString(@"profile_edit_member_info_non_editable_title", @"Call to Update", @"");
    model.details = details;
    model.firstButtonTitle = EHILocalizedString(@"profile_edit_member_info_non_editable_action_title", @"CONTACT US", @"");
    model.secondButtonTitle = EHILocalizedString(@"standard_cancel_button_title", @"CANCEL", @"");
   
    [model present:^(NSInteger index, BOOL canceled) {
        if (index == 0) {
            [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
        }

        return YES;
    }];
}

- (EHIUserContactProfile *)createContactProfile
{
    EHIFormFieldTextToggleViewModel *email = [self formFieldViewModelForRow:EHIMemberInfoEditRowEmail];
 
    EHIUserContactProfile *profile = [EHIUserContactProfile new];
    profile.email = email.inputValue;
    
    // retrieve phones from fields
    NSArray *phonesViewModels = @[
        self.allViewModels[EHIMemberInfoEditRowPhonePreferred],
        self.allViewModels[EHIMemberInfoEditRowPhoneAlternate],
    ];
    profile.phones = (NSArray<EHIPhone> *)phonesViewModels.map(^(EHIFormFieldTextViewModel *viewModel, int index) {
        EHIPhone *phone = [EHIPhone new];
        phone.number = viewModel.inputValue;
        phone.type = [[[EHIPhone userPhoneTypeOptions] objectAtIndex:viewModel.selectedCategory] unsignedIntegerValue];
        phone.priority = index == 0 ? 1 : index + 1;
        
        return phone.number ? phone : nil;
    });

    return profile;
}

- (EHIUserPreferencesProfile *)createPreferencesProfile
{
    id specialOffers = [EHIOptionalBooleanTransformer() reverseTransformedValue:@(self.specialOffersOptIn)];

    // construct email preferences (only update special offers)
    EHIUserEmailPreferences *emailModel;
    NSDictionary *emailPreferencesDictionary = @{
        @key(emailModel.rentalReceipts) : @(self.preferencesProfile.email.rentalReceipts),
        @key(emailModel.specialOffers)  : specialOffers,
        @key(emailModel.partnerOffers)  : @(self.preferencesProfile.email.partnerOffers)
    };

    EHIUserPreferencesProfile *profileModel = [EHIUserPreferencesProfile modelWithDictionary:@{
        @key(profileModel.email) : emailPreferencesDictionary,
    }];

    return profileModel;
}

- (EHIAddress *)createAddress
{
    // retrieve all street lines with text
    NSArray *streetViewModels = @[self.allViewModels[EHIMemberInfoEditRowStreetOne],
        self.allViewModels[EHIMemberInfoEditRowStreetTwo],
        self.allViewModels[EHIMemberInfoEditRowStreetThree]
    ];
    NSArray *streetAddresses = streetViewModels.map(^(EHIFormFieldViewModel *viewModel) {
        return viewModel.inputValue;
    });
    
    EHIAddress *address = [EHIAddress modelWithDictionary:@{
        @key(address.addressLines)  : streetAddresses ?: @[],
        @key(address.countryName)   : self.selectedCountry.name ?: @"",
        @key(address.countryCode)   : self.selectedCountry.code ?: @"",
        @key(address.city)          : [self inputValueForRow:EHIMemberInfoEditRowCity] ?: @"",
        @key(address.postalCode)    : [self inputValueForRow:EHIMemberInfoEditRowPostal] ?: @"",
        @key(address.addressType)   : self.address.addressType ?: @"HOME" // reuse existing or default to `Home`
    }];
    
    // add subdivision if available
    NSString *subdivisionName = [self inputValueForRow:EHIMemberInfoEditRowCountrySubdivision];
    if(subdivisionName && self.selectedCountry.enableCountrySubdivision) {
        EHIRegion *region = self.selectedCountry.regions.find(^(EHIRegion *region) {
            return [region.name isEqualToString:subdivisionName];
        });
        
        [address updateWithDictionary:@{
            @key(address.subdivisionCode) : region.code ?: subdivisionName,
        }];

        if(region) {
            [address updateWithDictionary:@{
                 @key(address.subdivisionName) : region.name,
            }];
        }
    }
    
    return address;
}

# pragma mark - Region Services

- (void)fetchCountries
{
    self.isLoading = YES;

    __weak typeof(self) welf = self;
    self.countryRequest = [[EHIServices sharedInstance] fetchCountriesWithHandler:^(NSArray *countries, EHIServicesError *error) {
        welf.isLoading = NO;
        
        if(!error.hasFailed && countries) {
            EHIFormFieldDropdownViewModel *country = [welf formFieldViewModelForRow:EHIMemberInfoEditRowCountry];
            
            // populate countries as 'Canada' -> EHICountry
            welf.countries = countries.map(^(EHICountry *country) {
                return country.name ? @[country.name, country] : nil;
            }).dict;
            
            // set and preselect options
            country.options = welf.countries.allKeys.sort;
            NSUInteger index = country.options.indexOf(welf.address.countryName);
            country.selectedOption = index != NSNotFound ? index : EHIFormFieldDropdownValueNone;
            
            [welf invalidateCountry];
        } else {
            welf.selectedCountry = nil;
            ((EHIFormFieldViewModel *)[welf formFieldViewModelForRow:EHIMemberInfoEditRowCountry]).inputValue = nil;
        }
    }];
}

- (void)fetchRegions
{
    self.isLoading = YES;
    
    __block id<EHINetworkCancelable> request;
    __weak typeof(self) welf = self;

    BOOL changingCountry = ![self.address.countryName isEqualToString:self.selectedCountry.name];
    // fetch regions for provided country
    request = [[EHIServices sharedInstance] fetchRegionsForCountry:self.selectedCountry handler:^(NSArray *regions, EHIServicesError *error) {
        if(request != welf.regionRequest) {
            return;
        }
        
        welf.isLoading     = NO;
        welf.regionRequest = nil;
        
        if(!error.hasFailed && regions) {
            EHIFormFieldDropdownViewModel *subdivision = [welf formFieldViewModelForRow:EHIMemberInfoEditRowCountrySubdivision];
            subdivision.options = (regions ?: @[]).map(^(EHIRegion *region) {
                return region.name;
            });
            
            if(changingCountry) {
                subdivision.selectedOption = NSNotFound;
            } else {
                NSUInteger userSubdivisionIndex = (subdivision.options ?: @[]).indexOf(welf.address.subdivisionName);
                subdivision.selectedOption = userSubdivisionIndex != NSNotFound ? userSubdivisionIndex : EHIFormFieldDropdownValueNone;
            }
        }
    }];
    
    self.regionRequest = request;
}

- (void)setRegionRequest:(id<EHINetworkCancelable>)regionRequest
{
    // cancel the previous request, if any
    [_regionRequest cancel];
    
    _regionRequest = regionRequest;
}

# pragma mark - Passthrough

- (EHIUserBasicProfile *)basicProfile
{
    return [EHIUser currentUser].profiles.basic;
}

- (EHIUserProfiles *)profiles
{
    return [EHIUser currentUser].profiles;
}

- (EHIUserContactProfile *)contactProfile
{
    return [EHIUser currentUser].contact;
}

- (EHIUserPreferencesProfile *)preferencesProfile
{
    return [EHIUser currentUser].preference;
}

- (EHIAddress *)address
{
    return [EHIUser currentUser].address;
}

- (EHIContractDetails *)contract
{
    return [EHIUser currentUser].corporateContract;
}

# pragma mark - Accessors

- (EHIRequiredInfoViewModel *)requiredModel
{
    return [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeProfile];
}

- (EHIRequiredInfoFootnoteViewModel *)footnoteModel
{
    return [EHIRequiredInfoFootnoteViewModel initWithType:EHIRequiredInfoFootnoteTypeProfile];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
