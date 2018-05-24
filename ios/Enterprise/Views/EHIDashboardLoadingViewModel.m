//
//  EHIDashboardLoadingViewModel.m
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLoadingViewModel.h"

@implementation EHIDashboardLoadingViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"dashboard_loading_cell_title", @"Loading Reservation...", @"title for the dashboard loading indicator cell.");
    }
    
    return self;
}

@end
