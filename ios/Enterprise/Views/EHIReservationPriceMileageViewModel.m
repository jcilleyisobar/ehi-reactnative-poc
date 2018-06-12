//
//  EHIReservationPriceMileageViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceMileageViewModel.h"
#import "EHICarClassMileage.h"
#import "EHIPriceFormatter.h"

@interface EHIReservationPriceMileageViewModel ()
@property (strong, nonatomic) EHICarClassMileage *mileage;
@end

@implementation EHIReservationPriceMileageViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        EHICarClassMileage *mileage = (EHICarClassMileage *)model;
        if(mileage) {
            self.mileage = mileage;
        }
    }
    
    return self;
}

- (void)setMileage:(EHICarClassMileage *)mileage
{
    _mileage = mileage;
    
    _title    = self.mileage.unlimitedMileage ? self.unlimitedText : self.mileageIncludedText;
    _subtitle = self.subtitleText;
}

//
// Helpers
//

- (NSString *)unlimitedText
{
    return EHILocalizedString(@"price_section_mileage_unlimited", @"Unlimited Mileage Included", @"");
}

- (NSString *)mileageIncludedText
{
    NSString *title = EHILocalizedString(@"price_section_mileage_included", @"Mileage Included: #{miles}", @"");
    NSString *numberOfMiles  = @(self.mileage.totalFreeMiles).description;
    NSString *unit  = self.mileage.distanceUnit ?: @"";
    return [title ehi_applyReplacementMap:@{
        @"miles" : [NSString stringWithFormat:@"%@ %@", numberOfMiles, unit]
    }];
}

- (NSString *)subtitleText
{
    if(!self.mileage.unlimitedMileage) {
        NSString *pricePerMile = [EHIPriceFormatter format:self.mileage.excessMileageRate].string;
        NSString *subtitle = EHILocalizedString(@"price_section_mileage_price", @"#{price} per additional #{unit}", @"");
        return [subtitle ehi_applyReplacementMap:@{
            @"price" : pricePerMile,
            @"unit"           : self.mileage.distanceUnit ?: @""
        }];
    }
    
    return nil;
}

@end
