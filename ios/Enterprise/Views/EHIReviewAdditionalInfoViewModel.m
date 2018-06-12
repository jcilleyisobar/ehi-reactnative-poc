//
//  EHIReviewAdditionalInfoViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReviewAdditionalInfoViewModel.h"
#import "EHIAdditionalInformationViewModel.h"
#import "EHIReviewAdditionalInfoItemViewModel.h"

@interface EHIReviewAdditionalInfoViewModel ()
@property (copy, nonatomic) NSArray *infos;
@end

@implementation EHIReviewAdditionalInfoViewModel

- (instancetype)initWithAdditionalInfo:(NSArray *)info
{
    if(self = [super init]) {
        _infos         = (info ?: @[]).sortBy(^(EHIContractAdditionalInfo *additionalInfo) {
            return additionalInfo.sequence;
        });
        _title         = EHILocalizedString(@"reservation_confirmation_assistance_section_title", @"ADDITIONAL INFORMATION", @"");
        _addModel      = [self buildAddModel];
        _itemModels    = [self buildItemModels];
    }
    
    return self;
}

- (EHIReviewAdditionalInfoAddViewModel *)buildAddModel
{
    return self.hideAdd ? nil : [[EHIReviewAdditionalInfoAddViewModel alloc] initWithAdditionalInfo:self.infos];
}

- (NSArray *)buildItemModels
{
    return self.hideAdd ? (self.infos ?: @[]).map(^(EHIContractAdditionalInfo *info){
        return [[EHIReviewAdditionalInfoItemViewModel alloc] initWithAdditionalInfo:info];
    }) : nil;
}

- (BOOL)hideAdd
{
    __block BOOL allInfoArePreRate = YES;
    (self.infos ?: @[]).each(^(EHIContractAdditionalInfo *info){
        if (!info.isPreRate){
            allInfoArePreRate = NO;
        }
    });
    BOOL atLeastOneAfterRateProvided = (self.infos ?: @[]).any(^(EHIContractAdditionalInfo *info){
        EHIContractAdditionalInfoValue *ai =[self.builder additionalInfoForKey:info.uid];
        return (ai.value != nil && !info.isPreRate);
    });
    
    return atLeastOneAfterRateProvided || allInfoArePreRate;
}

# pragma mark - Accessors

- (BOOL)hideArrow
{
    return !self.hideAdd;
}

# pragma mark - Actions

- (void)showAdditionalInfo
{
    __weak __typeof(self) welf = self;
    self.router.transition.push(EHIScreenReservationAdditionalInfo)
        .object(@(EHIAdditionalInformationFlowReview))
        .handler(^(BOOL submitted, EHIServicesError *error){
            welf.router.transition.dismiss.start(nil);
        }).start(nil);
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
