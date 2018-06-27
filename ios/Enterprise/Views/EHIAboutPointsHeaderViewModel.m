//
//  EHIAboutPointsHeaderViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsHeaderViewModel.h"
#import "EHIUserLoyalty.h"

@implementation EHIAboutPointsHeaderViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIUserLoyalty class]]) {
            _pointsText = EHILocalizedString(@"rewards_points_title", @"Points", @"");
            
            NSInteger points = [(EHIUserLoyalty *)model pointsToDate];
            _points = @(points).description;
        }
    }
    
    return self;
}

@end
