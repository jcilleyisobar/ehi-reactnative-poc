//
//  EHIRentalsFallbackViewModel.m
//  Enterprise
//
//  Created by fhu on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsFallbackViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIRentalsFallbackViewModel

- (instancetype)initWithMode:(EHIRentalsMode)mode;
{
    if (self = [super initWithModel:nil]) {
        _headerText = mode == EHIRentalsModeUpcoming
            ? EHILocalizedString(@"rentals_fallback_upcoming_text", @"You don't have any upcoming rentals", @"my rentals fallback header for upcoming text")
            : EHILocalizedString(@"rentals_fallback_past_text", @"We couldn't find any past rentals", @"my rentals fallback header for past text");
    }
    return self;
}

@end
