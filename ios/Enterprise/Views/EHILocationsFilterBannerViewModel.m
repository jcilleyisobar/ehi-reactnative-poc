//
//  EHILocationsFilterBannerViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsFilterBannerViewModel.h"

@interface EHILocationsFilterBannerViewModel ()
@property (copy, nonatomic) NSArray *activeFilters;
@end

@implementation EHILocationsFilterBannerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"locations_filter_banner_title", @"FILTERED:", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    // update our location filters
    [self setActiveFilters:model];
}

# pragma mark - Setters

- (void)setActiveFilters:(NSArray *)activeFilters
{
    _activeFilters = activeFilters;
    
    self.filters = [self filtersForActiveFilters:activeFilters];
}

# pragma mark - Title

- (NSString *)filtersForActiveFilters:(NSArray *)filters
{
    // if we don't have active filters bail out
    if(filters.count == 0) {
        return nil;
    }

    // placeholder for the @key macro
    EHIFilters *filter;
    return filters.pluck(@key(filter.displayTitle)).join(@", ");
}

- (BOOL)hasData
{
    return self.activeFilters.count > 0;
}

@end
