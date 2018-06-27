//
//  EHILocationFilterCellViewModel.m
//  Enterprise
//
//  Created by mplace on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterCellViewModel.h"
#import "EHIFilters.h"

@interface EHILocationFilterCellViewModel ()
@property (strong, nonatomic) EHIFilters *filter;
@end

@implementation EHILocationFilterCellViewModel

- (void)updateWithModel:(EHIFilters *)model
{
    [super updateWithModel:model];

    if([model isKindOfClass:[EHIFilters class]]) {
        self.filter = model;
    }
}

- (void)setFilter:(EHIFilters *)filter
{
    _filter = filter;
    
    self.title         = filter.displayTitle;
    self.isSelected    = filter.isActive;
    self.iconImageName = filter.iconImageName;
    self.shouldHideIconImage = filter.iconImageName == nil;
    self.shouldHideSelectionButton = (filter.currentFilter.type == EHIFilterTypeLocationMiscellaneous) ? YES : NO;

}

@end
