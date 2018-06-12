//
//  EHIConfirmationAppStoreRateViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationAppStoreRateViewModel.h"

@implementation EHIConfirmationAppStoreRateViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title              = EHILocalizedString(@"confirmation_rating_title", @"ENJOYING THE APP?", @"");
        _subtitle           = EHILocalizedString(@"confirmation_rating_subtitle", @"Do you mind rating us on the App Store?", @"");
        _rateButtonTile     = EHILocalizedString(@"confirmation_rating_rate_us_button_title", @"RATE US", @"");
        _dismissButtontitle = EHILocalizedString(@"confirmation_rating_dismiss_button_title", @"DISMISS", @"");
    }
    
    return self;
}

@end
