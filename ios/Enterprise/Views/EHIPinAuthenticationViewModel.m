//
//  EHIPinAuthenticationViewModel.m
//  Enterprise
//
//  Created by cgross on 4/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPinAuthenticationViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIFormFieldTextViewModel.h"

@interface EHIPinAuthenticationViewModel () <EHIFormFieldDelegate>

@end

@implementation EHIPinAuthenticationViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title             = EHILocalizedString(@"pin_auth_navigation_title", @"Enter Your PIN", @"");
        _submitTitle       = EHILocalizedString(@"pin_auth_submit_button", @"SUBMIT", @"");
        _instructionsTitle = EHILocalizedString(@"pin_auth_instructions_message", @"To access your corporate account please enter your PIN (the first three characters of your company name)", @"");
    
        _formModel = [self setupFormField];
        _requiredInfoViewModel = [EHIRequiredInfoViewModel modelForInfoType: EHIRequiredInfoTypeReservation];
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    // avoid any retain cycles
    self.handler = nil;
}

# pragma mark - Actions

- (void)submit
{
    self.isLoading = YES;
    
    [self.builder setPinAuth:self.formModel.inputValue];
    
    [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionSubmitPIN handler:nil];
    
    [self.builder initiateReservationWithHandler:^(EHIServicesError *error) {
        self.isLoading = NO;
        if (error.hasFailed && [error hasErrorCode:EHIServicesErrorCodePinInvalid]) {
            // if entered PIN is invalid, stay on the screen
        }
        else {
            ehi_call(_handler)(YES, error);
        }
    }];
}

- (void)close
{
    [self.builder setPinAuth:nil];
    
    ehi_call(_handler)(NO, nil);
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    [self validateForms];
}

- (void)validateForms
{
    self.isReadyToSubmit = [self.formModel validate:NO];
}

//
// Helpers
//

- (EHIFormFieldViewModel *)setupFormField
{
    EHIFormFieldTextViewModel *field = [EHIFormFieldTextViewModel new];
    field.delegate   = self;
    field.title      = EHILocalizedString(@"pin_auth_pin_header", @"PIN", @"");
    field.isRequired = YES;
    field.subtitle   = EHILocalizedString(@"pin_auth_pin_examples_message", @"Examples: St. Charles Lumber = STC\nA-1 Corporation = A1C", @"");
    field.limit      = 3;
    
    [field validates:^BOOL(NSString *input) {
        return input.length == 3;
    }];
    
    return field;
}

@end
