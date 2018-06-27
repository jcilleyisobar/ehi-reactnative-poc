
//
//  EHIRedemptionSavingsViewModel.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionSavingsViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHICarClass.h"
#import "EHIPlaceholder.h"

@interface EHIRedemptionSavingsViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIRedemptionSavingsViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        self.carClass = model;
    }
    // nil the line item out if we got a place holder, this handles the 0 redemption day case
    else if([model isKindOfClass:[EHIPlaceholder class]]) {
        self.carClass = nil;
    }
}

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    // construct the titles
    self.title    = EHILocalizedString(@"redemption_credit_cell_title", @"Credit", @"title for the redemption savings cell");
    self.subtitle = EHILocalizedString(@"redemption_credit_cell_subtitle", @"Taxes & fees may not be credited", @"");
    
    // construct the savings value
    NSString *unit = carClass.daysToRedeem == 1
        ? EHILocalizedString(@"reservation_rate_daily_unit", @"Day", @"")
        : EHILocalizedString(@"reservation_rate_daily_unit_plural", @"Days", @"");
    
    self.value = [NSString stringWithFormat:@"(- %ld %@)", (long)carClass.daysToRedeem, unit];
}

@end
