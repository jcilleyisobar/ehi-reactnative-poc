//
//  EHIPromotionDetailsActionCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsActionCellViewModel.h"

@implementation EHIPromotionDetailsActionCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _reservationButtonTitle = EHILocalizedString(@"weekend_special_start_reservation_button_title", @"START A RESERVATION", @"");
    }
    
    return self;
}

@end
