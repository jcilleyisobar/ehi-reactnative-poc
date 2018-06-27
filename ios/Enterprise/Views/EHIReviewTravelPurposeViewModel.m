//
//  EHIReviewTravelPurposeViewModel.m
//  Enterprise
//
//  Created by fhu on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewTravelPurposeViewModel.h"
#import "EHIUserManager.h"
#import "EHIReservationBuilder.h"

@interface EHIReviewTravelPurposeViewModel()
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIReviewTravelPurposeViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        NSString *subtitle = EHILocalizedString(@"reservation_review_travel_purpose_question", @"Are you travelling on business on behalf of #{name} for this rental?", @"question string in travel purpose");
        _subtitle = [subtitle ehi_applyReplacementMap:@{
            @"name" : self.builder.discount.name ?: self.builder.discount.maskedId ?: @"",
        }];
        
        _title    = EHILocalizedString(@"reservation_review_travel_purpose_section_title", @"TRIP PURPOSE", @"header title for reservations review screen's travel purpose section");
        
        _segmentedControlFirstTitle = EHILocalizedString(@"review_travel_purpose_segmented_control_first_title", @"YES, BUSINESS", @"first title of segmented control for travel purpose cell in review");
        _segmentedControlSecondTitle = EHILocalizedString(@"review_travel_purpose_segmented_control_second_title", @"NO", @"second title of segmented control for travel purpose cell in review");
    }
    return self;
}

- (void)selectTravelPurposeAtIndex:(NSInteger)index
{
    if(index == 0) {
        self.builder.travelPurpose = EHIReservationTravelPurposeBusiness;
    } else {
        self.builder.travelPurpose = EHIReservationTravelPurposeLeisure;
    }
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
