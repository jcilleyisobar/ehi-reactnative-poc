//
//  EHIDateTimeComponentViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIDateTimeComponentViewModel.h"
#import "EHITemporalSelectionViewModel.h"

@implementation EHIDateTimeComponentViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _pickupDateModel = [[EHITemporalSelectionViewModel alloc] initWithConfig:self.dateConfig];
        _pickupTimeModel = [[EHITemporalSelectionViewModel alloc] initWithConfig:self.timeConfig];
        _returnDateModel = [[EHITemporalSelectionViewModel alloc] initWithConfig:self.dateConfig];
        _returnTimeModel = [[EHITemporalSelectionViewModel alloc] initWithConfig:self.timeConfig];
    }
    
    return self;
}

# pragma mark - Accessors

- (EHITemporalSelectionConfig)timeConfig
{
    return (EHITemporalSelectionConfig) {
        .type   = EHITemporalSelectionTypeTime,
        .layout = self.temporalLayout
    };
}

- (EHITemporalSelectionConfig)dateConfig
{
    return (EHITemporalSelectionConfig) {
        .type   = EHITemporalSelectionTypeDate,
        .layout = self.temporalLayout
    };
}

- (EHITemporalSelectionLayout)temporalLayout
{
    return self.layout == EHIDateTimeComponentLayoutMap
        ? EHITemporalSelectionLayoutMap
        : EHITemporalSelectionLayoutFilter;
}

- (NSString *)pickupTitle
{
    if(self.layout == EHITemporalSelectionLayoutMap) {
        return EHILocalizedString(@"locations_map_pickup_label", @"PICK-UP:", @"");
    } else {
        return EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
    }
}

- (NSString *)returnTitle
{
    if(self.layout == EHITemporalSelectionLayoutMap) {
        return EHILocalizedString(@"locations_map_return_label", @"RETURN:", @"");
    } else {
        return EHILocalizedString(@"locations_map_closed_return", @"RETURN", @"");
    }
}

- (BOOL)hasData
{
    return self.pickupDateModel.hasValue
        || self.pickupTimeModel.hasValue
        || self.returnDateModel.hasValue
        || self.returnTimeModel.hasValue;
}

- (BOOL)hidePickupTimeSection
{
    return self.pickupDateModel.value == nil;
}

- (BOOL)hideReturnTimeSection
{
    return self.returnDateModel.value == nil;
}

# pragma mark - EHIDateTimeUpdatableProtocol

- (void)setDate:(NSDate *)date inSection:(EHIDateTimeComponentSection)section
{
    switch(section) {
        case EHIDateTimeComponentSectionPickupDate:
            self.pickupDateModel.value = date; return;
        case EHIDateTimeComponentSectionPickupTime:
            self.pickupTimeModel.value = date; return;
        case EHIDateTimeComponentSectionReturnDate:
            self.returnDateModel.value = date; return;
        case EHIDateTimeComponentSectionReturnTime:
            self.returnTimeModel.value = date; return;
    }
}

@end
