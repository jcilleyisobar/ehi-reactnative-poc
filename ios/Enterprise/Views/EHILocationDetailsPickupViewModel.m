//
//  EHILocationDetailsPickupViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsPickupViewModel.h"

@implementation EHILocationDetailsPickupViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"location_details_pickup_title", @"We'll pick you up!", @"Title for location details 'pickup' cell");
       
        // construct the details from components
        NSString *details = EHILocalizedString(@"location_details_pickup_body", @"Need a ride from your place to ours? Once you make your reservation, #{action} to coordinate your pick-up service details. Geographic restrictions apply.", @"Body for location details 'pickup' cell");
        NSString *action  = EHILocalizedString(@"location_details_pickup_cta", @"call this location", @"Call-to-action for location details 'pickup' cell");
        
        _details = [details ehi_applyReplacementMap:@{
            @"action" : action
        }];
        
        // find the highlight range in the constructed string
        _highlightRange = [_details rangeOfString:action];
    }
    
    return self;
}

@end
