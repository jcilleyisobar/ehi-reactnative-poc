//
//  EHIEnrollmentStepTwoViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentStepTwoViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldDateViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"
#import "EHIServices+User.h"
#import "EHIEnrollmentStepThreeViewModel.h"

@interface EHIEnrollmentStepTwoViewModel () <EHIFormFieldDelegate>
@property (strong, nonatomic) EHIUser *user;
@property (strong, nonatomic) NSDictionary *countries;
@property (strong, nonatomic) NSDictionary *regions;
@property (strong, nonatomic) EHICountry *selectedCountry;
@property (strong, nonatomic) EHIRegion *selectedRegion;
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *countryViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *streetOneViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *streetTwoViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *cityViewModel;
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *subdivisionViewModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *postalViewModel;
@property (strong, nonatomic) id<EHINetworkCancelable> regionRequest;
@end

@implementation EHIEnrollmentStepTwoViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        self.step = EHIEnrollmentStepTwo;
        [self buildModels];
    }
    
    return self;
}

- (void)buildModels
{
    self.countryViewModel = [EHIFormFieldDropdownViewModel new];
    self.countryViewModel.title      = EHILocalizedString(@"profile_edit_country_of_residence_title", @"COUNTRY", @"");
    self.countryViewModel.isRequired = YES;
    self.countryViewModel.delegate   = self;
    
    self.streetOneViewModel = [EHIFormFieldTextViewModel new];
    self.streetOneViewModel.title      = [self streetTitleForRow:EHIEnrollmentAddressRowStreetOne];
    self.streetOneViewModel.isRequired = YES;
    self.streetOneViewModel.delegate   = self;
    self.streetOneViewModel.captalizationMode = UITextAutocapitalizationTypeWords;
    
    self.streetTwoViewModel = [EHIFormFieldTextViewModel new];
    self.streetTwoViewModel.title    = [self streetTitleForRow:EHIEnrollmentAddressRowStreetTwo];
    self.streetTwoViewModel.delegate = self;
    self.streetTwoViewModel.captalizationMode = UITextAutocapitalizationTypeWords;
    
    self.cityViewModel = [EHIFormFieldTextViewModel new];
    self.cityViewModel.title      = EHILocalizedString(@"profile_edit_city_title", @"CITY", @"");
    self.cityViewModel.isRequired = YES;
    self.cityViewModel.sensitive  = YES;
    self.cityViewModel.delegate   = self;
    self.cityViewModel.captalizationMode = UITextAutocapitalizationTypeWords;
    
    self.subdivisionViewModel = [EHIFormFieldDropdownViewModel new];
    self.subdivisionViewModel.title      = EHILocalizedString(@"profile_edit_member_info_general_subdivision_title", @"STATE", @"");
    self.subdivisionViewModel.isRequired = YES;
    self.subdivisionViewModel.delegate   = self;
    
    self.postalViewModel = [EHIFormFieldTextViewModel new];
    self.postalViewModel.title      = EHILocalizedString(@"profile_edit_zip_title", @"ZIP CODE", @"");
    self.postalViewModel.isRequired = YES;
    self.postalViewModel.sensitive  = YES;
    self.postalViewModel.delegate   = self;
    
    self.buttonModel = [EHIFormFieldActionButtonViewModel new];
    self.buttonModel.title = EHILocalizedString(@"next_button_title", @"NEXT", @"");
    self.buttonModel.isFauxDisabled = YES;
    self.buttonModel.delegate       = self;

    // show some fields during load
    [self invalidateVisibleViewModels];
    
    // fetch countries to determine what views to show
    [self fetchCountries];
}

- (NSString *)streetTitleForRow:(EHIEnrollmentAddressRow)row
{
    NSString *title = EHILocalizedString(@"street_address", @"STREET ADDRESS #{number}", @"");
    switch(row) {
        case EHIEnrollmentAddressRowStreetOne: {
            return [title ehi_applyReplacementMap:@{
                @"number": @(1).description
            }];
        }
        case EHIEnrollmentAddressRowStreetTwo: {
            title = [title ehi_applyReplacementMap:@{
                @"number": @(2).description
            }];
            NSString *optional = EHILocalizedString(@"form_title_optional_field", @"(OPTIONAL)", @"");
            return [NSString stringWithFormat:@"%@ %@", title, optional];
        }
        default: return nil;
    }
}

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
    
    [self bindAddress];
    [self updateStep];
}

- (void)updateStep
{
    EHIUserLoyaltyProgram program = self.user.profiles.basic.loyalty.program;
    switch(program) {
        case EHIUserLoyaltyProgramEmeraldClub:
            self.step = EHIEnrollmentStepProfileEmeraldClub; break;
        case EHIUserLoyaltyProgramNonLoyalty:
            self.step = EHIEnrollmentStepProfileFound; break;
        default:
            self.step = EHIEnrollmentStepTwo; break;
    }    
}

# pragma mark - Address Binding

- (void)bindAddress
{
    EHIAddress *address       = self.user.address;
    EHIAddress *enrollAddress = self.enrollmentProfile.address;
    
    NSString *countryName = address.countryName ?: enrollAddress.countryName ?: self.user.license.countryName;
    [self setFormFieldValue:countryName forRow:EHIEnrollmentAddressRowCountryOfResidence];
    
    NSString *subdivision = address.subdivisionName ?: enrollAddress.subdivisionName ?: self.user.license.subdivisionName;
    [self setFormFieldValue:subdivision forRow:EHIEnrollmentAddressRowState];
    
    NSString *streetOne = [address.addressLines ehi_safelyAccess:0] ?: [enrollAddress.addressLines ehi_safelyAccess:0];
    [self setFormFieldValue:streetOne forRow:EHIEnrollmentAddressRowStreetOne];
    
    NSString *streetTwo = [address.addressLines ehi_safelyAccess:1] ?: [enrollAddress.addressLines ehi_safelyAccess:1];
    [self setFormFieldValue:streetTwo forRow:EHIEnrollmentAddressRowStreetTwo];
    
    NSString *city = address.city ?: enrollAddress.city;
    [self setFormFieldValue:city forRow:EHIEnrollmentAddressRowCity];
    
    NSString *postalCode = address.postalCode ?: enrollAddress.postalCode;
    [self setFormFieldValue:postalCode forRow:EHIEnrollmentAddressRowZip];
    
    if(address != nil && self.didMatchProfile) {
        self.matchViewModel = [[EHIEnrollmentStepTwoMatchViewModel alloc] initWithModel:address];
    }
}

- (void)setFormFieldValue:(id)value forRow:(EHIEnrollmentAddressRow)row
{
    id formField = [self formFieldViewModelForRow:row];
    switch(row) {
        case EHIEnrollmentAddressRowCountryOfResidence:
        case EHIEnrollmentAddressRowState: {
            EHIFormFieldDropdownViewModel *dropDown = ((EHIFormFieldDropdownViewModel *)formField);
            if([value isKindOfClass:[NSString class]] && [value ehi_isMasked]) {
                dropDown.inputValue = value;
            } else {
                NSArray *options = dropDown.options;
                NSUInteger index = (options ?: @[]).indexOf(value);
                if(index != NSNotFound) {
                    dropDown.selectedOption = index;
                }
            }
            break;
        }
        default: {
            ((EHIFormFieldDateViewModel *)formField).inputValue = value;
            break;
        }
    }
}

# pragma mark - Country & Region Fetching

- (void)fetchCountries
{
    self.isLoading = YES;
    
    NSString *profileCountry = self.user.license.countryName ?: [EHIUserManager sharedInstance].enrollmentProfile.license.countryName;
    [[EHIServices sharedInstance] fetchCountriesWithHandler:^(NSArray *countries, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error && countries) {
            EHIFormFieldDropdownViewModel *country = [self formFieldViewModelForRow:EHIEnrollmentAddressRowCountryOfResidence];
            
            // populate countries as 'Canada' -> EHICountry
            self.countries = countries.map(^(EHICountry *country) {
                return country.name ? @[country.name, country] : nil;
            }).dict;
            
            // set and preselect options
            country.options = self.countries.allKeys.sort;
            NSString *selectCountry = profileCountry ?: NSLocale.ehi_country.name;
            NSUInteger index = country.options.indexOf(selectCountry);
            if(index != NSNotFound) {
                country.selectedOption = index;
            }
            
            [self invalidateSelectedCountry];
        }
    }];
}

- (void)invalidateSelectedCountry
{
    NSString *inputValue = [self inputValueForRow:EHIEnrollmentAddressRowCountryOfResidence];
    
    // set country from cache
    self.selectedCountry = self.countries[inputValue];
    
    // update state field placeholder
    EHIFormFieldDropdownViewModel *stateViewModel = [self formFieldViewModelForRow:EHIEnrollmentAddressRowState];
    stateViewModel.options = nil;
    stateViewModel.selectedOption = EHIFormFieldDropdownValueNone;
    
    [self invalidateVisibleViewModels];
    
    // grab regions if needed
    if(self.requiresIssuingAuthority) {
        [self fetchRegions];
    }
}

- (void)fetchRegions
{
    // cancel any existing request
    [self.regionRequest cancel];
    
    // fetch regions for provided country
    self.isLoading = YES;
    NSString *profileSubdivision = self.user.license.subdivisionName ?: [EHIUserManager sharedInstance].enrollmentProfile.license.subdivisionName;
    __weak typeof(self) welf = self;
    self.regionRequest = [[EHIServices sharedInstance] fetchRegionsForCountry:self.selectedCountry handler:^(NSArray *regions, EHIServicesError *error) {
        welf.isLoading = NO;
        welf.regionRequest = nil;
        
        if(!error && regions) {
            
            // populate regions as 'Alabama' -> EHIRegion
            welf.regions = (regions ?: @[]).map(^(EHIRegion *region) {
                return region.name ? @[region.name, region] : nil;
            }).dict;
            
            EHIFormFieldDropdownViewModel *state = [welf formFieldViewModelForRow:EHIEnrollmentAddressRowState];
            __block EHIRegion *userRegion;
            state.options = (regions ?: @[]).map(^(EHIRegion *region) {
                if([region.code isEqualToString:welf.selectedCountry.code]) {
                    userRegion = region;
                }
                
                return region.name;
            });
            
            NSString *selectSubdivision = profileSubdivision ?: welf.selectedRegion.name;
            NSUInteger stateIndex = (state.options ?: @[]).indexOf(selectSubdivision);
            state.selectedOption = stateIndex != NSNotFound ? stateIndex : EHIFormFieldDropdownValueNone;
        }
    }];
}

# pragma mark - Form Field Invalidation

- (void)invalidateVisibleViewModels
{
    self.formModels = @[
        self.countryViewModel,
        self.streetOneViewModel,
        self.streetTwoViewModel,
        self.cityViewModel,
        self.subdivisionViewModel,
        self.postalViewModel,
    ];
    
    self.formModels = self.formModels.map(^(EHIFormFieldViewModel *viewModel, int row) {
        return [self shouldShowViewModelInRow:row] ? viewModel : nil;
    });
    
    [self invalidateValidations];
}

- (BOOL)shouldShowViewModelInRow:(EHIEnrollmentAddressRow)row
{
    switch(row) {
        case EHIEnrollmentAddressRowState: return [self requiresIssuingAuthority];
        default: return YES;
    }
}

- (id)formFieldViewModelForRow:(EHIEnrollmentAddressRow)row
{
    switch(row) {
        case EHIEnrollmentAddressRowCountryOfResidence: return self.countryViewModel;
        case EHIEnrollmentAddressRowStreetOne: return self.streetOneViewModel;
        case EHIEnrollmentAddressRowStreetTwo: return self.streetTwoViewModel;
        case EHIEnrollmentAddressRowCity: return self.cityViewModel;
        case EHIEnrollmentAddressRowState: return self.subdivisionViewModel;
        case EHIEnrollmentAddressRowZip: return self.postalViewModel;
    }
}

- (id)inputValueForRow:(EHIEnrollmentAddressRow)row
{
    return ((EHIFormFieldViewModel *)[self formFieldViewModelForRow:row]).inputValue;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    if([viewModel isEqual:[self formFieldViewModelForRow:EHIEnrollmentAddressRowCountryOfResidence]]) {
        [self invalidateSelectedCountry];
    }
    
    if([viewModel isEqual:[self formFieldViewModelForRow:EHIEnrollmentAddressRowState]]) {
        self.selectedRegion = self.regions[viewModel.inputValue];
    }
    
    [self validateForm:NO];
}

- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel
{
    BOOL invalidForm = [self validateForm:YES];
    if(!invalidForm) {
        [EHIAnalytics trackAction:EHIAnalyticsEnrollmentNext handler:nil];

        // update current profile
        [self.user updateAddress:[self createAddress]];
        [self performNextStepWith:self.user];
    }
    
    self.warning = self.warningMessages;
}

- (NSString *)warningMessages
{
    NSArray *errorMessages = self.errorMessages;
    if(errorMessages.count > 0) {
        NSString *title = EHILocalizedString(@"enroll_field_validation_message", @"Please check next if the fields are valid:", @"");
        NSString *errors = errorMessages.map(^(NSString *message){
            return [NSString stringWithFormat:@"• %@", message];
        }).join(@"\n");
        
        return [NSString stringWithFormat:@"%@\n%@",title, errors];
    }
    
    return nil;
}

# pragma mark - Actions

- (void)performNextStepWith:(EHIUser *)user
{
    [self persistUser:self.user];

    EHIEnrollmentStepThreeViewModel *model = [EHIEnrollmentStepThreeViewModel new];
    model.signinFlow = self.signinFlow;
    model.handler    = self.handler;
    
    [model updateWithModel:user];
    
    self.router.transition.push(EHIScreenEnrollmentStepThree).object(model).start(nil);
}

- (void)changeAddress
{
    (self.formModels ?: @[]).each(^(EHIFormFieldViewModel *viewModel){
        viewModel.inputValue = nil;
    });
}

- (void)keepAddress
{
    [self performNextStepWith:self.user];
}

# pragma mark - Address

- (EHIAddress *)address
{
    return [self createAddress];
}

- (EHIAddress *)createAddress
{
    // retrieve all street lines with text
    NSArray *streetViewModels = @[
        self.formModels[EHIEnrollmentAddressRowStreetOne] ?: @"",
        self.formModels[EHIEnrollmentAddressRowStreetTwo] ?: @""
    ];
    
    NSArray *streetAddresses = streetViewModels.map(^(EHIFormFieldViewModel *viewModel) {
        return viewModel.inputValue;
    });
    
    EHIAddress *address = [EHIAddress modelWithDictionary:@{
        @key(address.addressLines) : streetAddresses,
        @key(address.countryName)  : self.selectedCountry.name ?: @"",
        @key(address.countryCode)  : self.selectedCountry.code ?: @"",
        @key(address.city)         : [self inputValueForRow:EHIEnrollmentAddressRowCity] ?: @"",
        @key(address.postalCode)   : [self inputValueForRow:EHIEnrollmentAddressRowZip] ?: @"",
        @key(address.addressType)  : @"HOME"
    }];
    
    // add subdivision if required
    BOOL requiresSubdivision = [self requiresIssuingAuthority];
    if(requiresSubdivision) {
        [address updateWithDictionary:@{
            @key(address.subdivisionName) : self.selectedRegion.name ?: self.user.address.subdivisionName ?: @"",
            @key(address.subdivisionCode) : self.selectedRegion.code ?: self.user.address.subdivisionCode ?: @"",
        }];
    }
    
    return address;
}

- (void)setAddExtraPadding:(BOOL)addExtraPadding
{
    self.postalViewModel.extraPadding = addExtraPadding ? EHIMediumPadding : 0.0f;
}

# pragma mark - Validation

- (BOOL)requiresIssuingAuthority
{
    return self.selectedCountry.enableCountrySubdivision;
}

- (BOOL)validateForm:(BOOL)showErrors
{
    // run validation check on all visible fields
    __block BOOL validForm = YES;
    
    for(EHIFormFieldViewModel *viewModel in self.formModels) {
        validForm &= [viewModel validate:showErrors];
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
    self.formModels.each(^(EHIFormFieldViewModel *viewModel, int index) {
        [viewModel clearValidations];
        // skip street two since it's optional
        if(index != EHIEnrollmentAddressRowStreetTwo) {
            [viewModel validates:EHIFormFieldValidationNotEmptyOrSpaces];
        }
    });
}

- (NSArray *)errorMessages
{
    return self.formModels.reject(^(EHIFormFieldViewModel *model){
        return [model validate:NO];
    }).map(^(EHIFormFieldViewModel *model){
        return model.title;
    });
}

- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors
{
    [self validateForm:showErrors];
    
    return self.errorMessages;
}

- (EHIAddress *)currentAddress
{
    return [self createAddress];
}

@end
