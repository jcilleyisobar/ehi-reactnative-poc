//
//  EHIConfirmationDiscountViewModel.m
//  Enterprise
//
//  Created by fhu on 6/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationDiscountViewModel.h"
#import "EHIContractDetails.h"
#import "EHIReservation.h"
#import "EHIWebViewModel.h"

@interface EHIConfirmationDiscountViewModel ()
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSString *iconName;
@property (copy  , nonatomic) NSString *terms;
@property (copy  , nonatomic) NSString *termsButtonTitle;
@property (assign, nonatomic) BOOL shouldShowTerms;
@end

@implementation EHIConfirmationDiscountViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _discountModel = [[EHIContractDiscountViewModel alloc] initWithFlow:EHIContractDiscoutFlowConfirmation];
    }
    
    return self;
}

- (void)updateWithModel:(EHIReservation *)reservation
{
    [super updateWithModel:reservation];
    
    [self.discountModel updateWithModel:reservation.contractDetails];
}

@end
