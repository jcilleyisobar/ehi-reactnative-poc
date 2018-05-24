//
//  EHIRentalsUpcomingRentalCellViewModel.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsUpcomingRentalViewModel.h"
#import "EHIUserRental.h"

@implementation EHIRentalsUpcomingRentalViewModel

- (void)updateWithModel:(EHIUserRental *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithRentalDetails:model];
    }
}

- (void)updateWithRentalDetails:(EHIUserRental *)model
{
    self.confirmationNumber = model.ticketNumber ?: model.confirmationNumber;
    self.confirmationNumber = [NSString stringWithFormat:@"#%@", self.confirmationNumber];
    
    self.reservationTime = model.isCurrent ? EHILocalizedString(@"current_rentals_cell_header", @"CURRENT RENTAL", @"cell header for my current rental cell")
                                           : model.pickupTimeDisplay;
    self.shouldHideArrow = model.isCurrent ? YES : NO;
    
    if(!model.returnLocation.uid || [model.pickupLocation.uid isEqualToString:model.returnLocation.uid]) {
        self.location = model.pickupLocation.displayName;
    } else {
        NSString *location = [NSString stringWithFormat:@"%@ #{to} %@", model.pickupLocation.displayName, model.returnLocation.displayName];
        location = [location ehi_applyReplacementMap:@{
            @"to" : EHILocalizedString(@"standard_to_title", @"to", @"joiner for specifying a rental's pickup and return location"),
        }];
        
        self.location = location;
    }
}

@end
