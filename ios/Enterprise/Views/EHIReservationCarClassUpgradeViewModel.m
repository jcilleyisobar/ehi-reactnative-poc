//
//  EHIReservationCarClassUpgradeViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIReservationCarClassUpgradeViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationBuilder.h"

@interface EHIReservationCarClassUpgradeViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@property (copy  , nonatomic) EHIImage *vehicleImage;
@property (copy  , nonatomic) NSAttributedString *detailsTitle;
@end

@implementation EHIReservationCarClassUpgradeViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _buttonTitle = EHILocalizedString(@"review_reservation_upgrade_button_text", @"UPGRADE NOW", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self updateWithCarClass:model];
    }
}

- (void)updateWithCarClass:(EHICarClass *)carClass
{
    self.carClass = carClass;
    
    self.vehicleImage = [carClass.images firstObject];
    self.detailsTitle = [self constructDetailsTitle];
}

//
// Helpers
//

- (NSAttributedString *)constructDetailsTitle
{
    NSString *pitchText = EHILocalizedString(@"review_reservation_upgrade_text", @"Upgrade to #{name} for only #{difference} more", @"");
    NSString *rateText  = [EHIPriceFormatter format:[self.carClass upgradeDifferenceForPrepay:self.builder.reservation.prepaySelected].viewDifference].string;
    pitchText = [pitchText ehi_applyReplacementMap:@{
        @"name"        : self.carClass.name ?: @"",
        @"difference"  : rateText,
    }];
    
    return EHIAttributedStringBuilder.new.paragraphSpacing(10).text(pitchText).size(18).string;
}

- (EHIReservationBuilder*)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
