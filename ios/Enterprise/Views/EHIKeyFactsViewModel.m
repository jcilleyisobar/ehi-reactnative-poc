//
//  EHIKeyFactsViewModel.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIKeyFactsViewModel.h"
#import "EHIServices+Reservation.h"

@interface EHIKeyFactsViewModel ()
@property (strong, nonatomic) NSArray *contentList;
@property (strong, nonatomic) EHIReservation *reservation;
@end

@implementation EHIKeyFactsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"reservation_about_your_rental_section_title", @"ABOUT YOUR RENTAL", @"");
    }
    
    return self;
}

-(void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIReservation class]]) {
        [self updateWithReservation:model];
    }
}

- (void)updateWithReservation:(EHIReservation *)reservation
{
    self.reservation = reservation;
    
    self.contentList = @[
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeDamageLiability withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeProtection withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeEquipment withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeMinimumRequirements withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeAdditionalPolicies withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeAdditionalLiabilities withReservation:reservation],
        [EHIKeyFactsSectionContentViewModel modelForType:EHIKeyFactsSectionContentTypeVehicleReturnAndDamages withReservation:reservation]
    ];
}

@end
