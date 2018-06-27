//
//  EHIAdditionalInformationViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 4/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAdditionalInformationViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIFormFieldViewModel+AdditionalInfo.h"
#import "EHIContractDetails.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIServices+Contracts.h"
#import "EHIContractAdditionalInfo.h"
#import "EHIFormFieldTextViewViewModel.h"
#import "EHIContractAdditionalInfoValue.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIToastManager.h"

@interface EHIAdditionalInformationViewModel () <EHIFormFieldDelegate>
@property (strong, nonatomic) NSArray<EHIContractAdditionalInfo> *additionalInfos;
@end

@implementation EHIAdditionalInformationViewModel

- (instancetype)initWithFlow:(EHIAdditionalInformationFlow)flow
{
    if(self = [super init]) {
        _flow              = flow;
        _title             = EHILocalizedString(@"additional_information_navigation_title", @"Additional Information", @"");
        _submitTitle       = EHILocalizedString(@"additional_information_submit_button_title", @"SUBMIT", @"").uppercaseString;
        _instructionsTitle = EHILocalizedString(@"additional_information_instructions_message", @"Please enter your additional information. The following is required to", @"");
        [self fetchContractDetails];

        _requiredInfoModel = [EHIRequiredInfoViewModel modelForInfoType: EHIRequiredInfoTypeReservation];
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    // avoid any retain cycles
    self.handler = nil;
}

- (void)fetchContractDetails
{
    self.isLoading = YES;
    
    NSString *contractNumber = self.builder.discountCode ?: self.builder.discount.uid;
    BOOL inReview = self.inReview;
    [[EHIServices sharedInstance] fetchContractNumber:contractNumber handler:^(EHIContractDetails *contract, EHIServicesError *error) {
        if(!error.hasFailed) {
            NSArray *infos = (NSArray<EHIContractAdditionalInfo> *)(contract.additionalInformation ?: @[]);
            
            // ignore pre rate filtering when in review screen
            if(!inReview) {
                infos = infos.select(^(EHIContractAdditionalInfo *info){
                    return info.isPreRate;
                });
            }
            
            self.additionalInfos = (NSArray<EHIContractAdditionalInfo> *)infos.sortBy(^(EHIContractAdditionalInfo *additionalInfo) {
                return additionalInfo.sequence;
            });
            
            // force validations on review screen
            if(inReview) {
                self.isInvalid = [self validateFormsShowingErrors:NO];
            }
        }
        self.isLoading = NO;
    }];
}

# pragma mark - Accessors

- (void)setAdditionalInfos:(NSArray<EHIContractAdditionalInfo> *)additionalInfos
{
    if(additionalInfos) {
        self.formModels = additionalInfos.map(^(EHIContractAdditionalInfo *additionalInfo){
            return [self setupFormFieldWithAdditionalInfo:additionalInfo];
        });
        [self.formModels.lastObject setExtraPadding:EHIMediumPadding];
        
    }
    
    _additionalInfos = additionalInfos ?: @[];
}

- (BOOL)hideNavigation
{
    return self.inReview;
}

- (BOOL)inReview
{
    return self.flow == EHIAdditionalInformationFlowReview;
}

# pragma mark - Actions

- (void)submit
{
    BOOL invalidForm = [self validateFormsShowingErrors:YES];
    if(!invalidForm) {
        self.isLoading = YES;
        
        self.additionalInfos.each(^(EHIContractAdditionalInfo *additionalInfo){
            EHIFormFieldViewModel *model = [self formFieldForAdditionalInfo:additionalInfo];
            NSString *uid   = model.uid;
            NSString *value = model.inputValue;
            [self.builder setAdditionalInfo:value forKey:uid];
        });
        
        [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionSubmitAdditionInfo handler:nil];
        
        if(!self.inReview){
            [self.builder initiateReservationWithHandler:^(EHIServicesError *error) {
                self.isLoading = NO;
                if(error.hasFailed && [error hasErrorCode:EHIServicesErrorCodeAdditionalInfoInvalid]) {
                    // if entered additional info is invalid, stay on the screen
                }
                else {
                    ehi_call(_handler)(YES, error);
                }
            }];
        } else {
            ehi_call(_handler)(YES, nil);
        }
        
    } else {
        [EHIToastManager showMessage:EHILocalizedString(@"review_please_enter_additional_information", @"Please provide the required additional information.", @"")];
    }
}

- (void)close
{
    [self.builder resetAdditionalData];
    
    ehi_call(_handler)(NO, nil);
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    self.isInvalid = [self validateFormsShowingErrors:NO];
}

# pragma mark - Validation

- (EHIFormFieldViewModel *)setupFormFieldWithAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    NSString * value = [self.builder additionalInfoForKey:info.uid].value;
    EHIFormFieldViewModel *model =[EHIFormFieldViewModel viewModelForCorporateCodeInfo:info value:value];
    model.delegate = self;
 
    BOOL isDropDown = model.type == EHIFormFieldTypeDropdown;
    if(isDropDown) {
        NSString *placeholder = EHILocalizedString(@"reservation_review_additional_info_section_title", @"Additional Info", @"");
        ((EHIFormFieldDropdownViewModel *)model).placeholder = placeholder;
    }
    
    BOOL isRequired = info.isRequired;
    if(isRequired) {
        [model validates:self.validationBlock];
    }
    
    return model;
}

- (BOOL)validateFormsShowingErrors:(BOOL)showErrors
{
    __block BOOL result = YES;
    
    (self.requiredFormFields ?: @[]).each(^(EHIFormFieldViewModel *form){
        result &= [form validate:showErrors];
    });
    
    return !result;
}

- (BOOL(^)(id input))validationBlock
{
    return ^BOOL(id input) {
        BOOL valid = YES;
        if([input isKindOfClass:[NSString class]]) {
            valid &= [input length] > 0;
        }
        valid &= input != nil;
        return valid;
    };
}

# pragma mark - Form Filed Filtering

- (EHIFormFieldViewModel *)formFieldForAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    return self.formModels.find(^(EHIFormFieldViewModel *form) {
        return form.uid == info.uid;
    });
}

- (NSArray<EHIFormFieldViewModel *> *)requiredFormFields
{
    return self.additionalInfos.select(^(EHIContractAdditionalInfo *info){
        return info.isRequired;
    }).map(^(EHIContractAdditionalInfo *info){
        return [self formFieldForAdditionalInfo:info];
    });
}

@end
