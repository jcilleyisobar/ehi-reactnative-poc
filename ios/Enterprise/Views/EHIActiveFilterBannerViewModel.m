//
//  EHIActiveFilterBannerViewModel.m
//  Enterprise
//
//  Created by Michael Place on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActiveFilterBannerViewModel.h"
#import "EHIFilters.h"
#import "NSAttributedString+Construction.h"

@interface EHIActiveFilterBannerViewModel ()
@property (copy, nonatomic) NSArray *activeFilters;
@end

@implementation EHIActiveFilterBannerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // initialize static variables
        _clearButtonTitle = EHILocalizedString(@"location_filter_map_banner_clear_button_title", @"CLEAR", @"title for a clear button on the location filter banner on the map screen");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    // update our location filters
    [self setActiveFilters:model];
}

# pragma mark - Title

- (NSAttributedString *)titleForActiveFilters:(NSArray *)filters
{
    // if we don't have active filters bail out
    if(filters.count == 0) {
        return nil;
    }
    
    // placeholder for the @key macro
    EHIFilters *filter;
    // pull out the active filter titles
    NSString *titles = filters.pluck(@key(filter.displayTitle)).join(@", ");
    // localize the prefix
    NSString *prefix = EHILocalizedString(@"location_filter_banner_title_prefix", @"FILTER:", @"prefix for the location filter banner on the map screen. prefixes a list of active filters.");
    
    // build the attributed title
    EHIAttributedStringBuilder *titleBuilder = EHIAttributedStringBuilder.new
        .text(prefix).fontStyle(EHIFontStyleBold, 18.0f).space
        .appendText(titles).fontStyle(EHIFontStyleLight, 18.0f);
    
    return titleBuilder.string;
}

# pragma mark - Setters

- (void)setActiveFilters:(NSArray *)activeFilters
{
    _activeFilters = activeFilters;
    
    self.attributedTitle = [self titleForActiveFilters:activeFilters];
}

@end
