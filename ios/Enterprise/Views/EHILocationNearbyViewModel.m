//
//  EHILocationNearbyViewModel.m
//  Enterprise
//
//  Created by mplace on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationNearbyViewModel.h"

@implementation EHILocationNearbyViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    self.title = EHILocalizedString(@"locations_empty_query_nearby_title", @"Find nearby locations", @"Title for the nearby locations cell");
}

@end
