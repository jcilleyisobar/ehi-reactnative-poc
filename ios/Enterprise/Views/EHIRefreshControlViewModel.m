//
//  EHIRefreshControl.m
//  Enterprise
//
//  Created by Ty Cobb on 7/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRefreshControlViewModel.h"

@implementation EHIRefreshControlViewModel

- (void)setPercentComplete:(CGFloat)percentComplete
{
    __block CGFloat filteredPercent = percentComplete;
    
    [MTRReactor nonreactive:^{
        // nullify updates when disabled or refreshing
        if(self.isDisabled || self.isRefreshing) {
            filteredPercent = 0.0f;
        }
    }];
    
    _percentComplete = filteredPercent;
}

@end
