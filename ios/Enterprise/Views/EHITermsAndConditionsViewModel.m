//
//  TermsAndConditionsViewModel.m
//  Enterprise
//
//  Created by frhoads on 10/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHITermsAndConditionsViewModel.h"
#import "EHIServices+Reservation.h"
#import "EHIReservationBuilder.h"
#import "EHIServices+Config.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHITermsCountries.h"
#import "NSLocale+Country.h"

@interface EHITermsAndConditionsViewModel() <EHIFormFieldDelegate>
@property (strong, nonatomic) EHIWebContent *termsAndConditionsContent;
@property (strong, nonatomic) EHITermsEU *selectedLanguage;
@property (strong, nonatomic) NSArray *allViewModels;
@property (strong, nonatomic) EHITermsCountries *contriesAndTerms;
@property (strong, nonatomic) NSDictionary *terms;

@end

@implementation EHITermsAndConditionsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _dropDownModel = [EHIFormFieldDropdownViewModel new];
        _dropDownModel.title    = EHILocalizedString(@"eu_terms_screen_language_title", @"COUNTRY AND LANGUAGE", @"COUNTRY AND LANGUAGE");
        _dropDownModel.delegate = self;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIReservation class]]) {
        self.reservation = model;
    }
}

# pragma mark - Actions

- (void)didShowDropdown
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionTCChangeLanguage handler:nil];
}

- (void)dismiss
{
    NSString *countryCode = self.selectedLanguage.countryCode.copy;
    [EHIAnalytics trackAction:EHIAnalyticsResActionTCDone handler:^(EHIAnalyticsContext *context) {
        context[EHIAnalyticsEnrollmentProfileCountryCodeKey] = countryCode ?: @"";
    }];

    self.router.transition.dismiss.start(nil);
}


# pragma mark - Accessors

- (NSString *)title
{
    return EHILocalizedString(@"eu_terms_screen_title", @"Terms & Conditions", @"Terms and Conditions");
}

- (void)setReservation:(EHIReservation *)reservation
{
    _reservation = reservation;
    
    self.isLoading = YES;
    
    if (self.termsAndConditionsContent) {
        self.isLoading = NO;
        return;
    }
    
    [[EHIServices sharedInstance] fetchTermsAndConditionsForReservation:reservation handler:^(EHITermsCountries *countryTerms, EHIServicesError *error) {
        if(!error.hasFailed) {
            
            self.contriesAndTerms = countryTerms;
            
            self.terms = (countryTerms.termsLanguages ?: @[]).map(^(EHITermsEU *country) {
                return country.language ? @[country.language, country] : nil;
            }).dict;
            
            self.dropDownModel.options = self.terms.allKeys.sort;
            
            //lets auto-select the first option to display
            self.dropDownModel.selectedOption = 0;
            self.isLoading = NO;
        }
    }];
}

- (id)formFieldViewModelForSection:(EHITermsSections)section
{
    return self.allViewModels[section];
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    NSString *inputValue = viewModel.inputValue;
    
    // ignore repeat selections
    if(!inputValue || [inputValue isEqualToString:self.selectedLanguage.language]) {
        return;
    }
    
    EHITermsEU *terms = self.terms[inputValue];
    
     self.htmlString = terms.termsAndConditionsText;
}

- (id)inputValueForSection:(EHITermsSections)section
{
    return ((EHIFormFieldViewModel *)[self formFieldViewModelForSection:section]).inputValue;
}

@end
