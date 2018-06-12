//
//  EHIReservationPriceViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceViewModel.h"
#import "EHIReservationPriceItemViewModel.h"
#import "EHIPriceFormatter.h"

@implementation EHIReservationPriceViewModel

- (instancetype)initWithTitle:(NSString *)title total:(NSString *)total
{
    if(self = [super init]) {
        _title = title;
        _total = total;
    }
    
    return self;
}

@end
