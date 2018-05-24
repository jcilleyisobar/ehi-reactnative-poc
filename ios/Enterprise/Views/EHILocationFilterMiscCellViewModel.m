//
//  EHILocationFilterMiscCellViewModel.m
//  Enterprise
//
//  Created by mplace on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterMiscCellViewModel.h"
#import "EHIFilters.h"

@implementation EHILocationFilterMiscCellViewModel

- (void)updateWithModel:(EHIFilters *)model
{
    [super updateWithModel:model];
    
    self.title = model.displayTitle;
}

@end
