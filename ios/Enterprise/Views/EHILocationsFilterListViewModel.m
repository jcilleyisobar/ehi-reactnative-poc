//
//  EHILocationsFilterListViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsFilterListViewModel.h"
#import "EHISettings.h"

@interface EHILocationsFilterListViewModel ()
@property (assign, nonatomic) BOOL shouldShowTipFromSettings;
@property (assign, nonatomic) BOOL showFilterTip;
@property (assign, nonatomic) BOOL didShowFilter;
@end

@implementation EHILocationsFilterListViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _dateTimeModel = EHIDateTimeComponentMapViewModel.new;
        _bannerModel   = EHILocationsFilterBannerViewModel.new;
        
        self.shouldShowTipFromSettings = [EHISettings shouldShowLocationsMapFilterTip];
    }
    
    return self;
}

# pragma mark - Actions

- (void)setDate:(NSDate *)date inSection:(EHIDateTimeComponentSection)section
{
    [self.dateTimeModel setDate:date inSection:section];
    
    [self update];
}

- (void)updateActiveFilters:(NSArray *)filters
{
    [self.bannerModel updateWithModel:filters];
    
    [self update];
}

- (void)clearDateQuery
{
    [self.dateTimeModel setDate:nil inSection:EHIDateTimeComponentSectionPickupDate];
    [self.dateTimeModel setDate:nil inSection:EHIDateTimeComponentSectionPickupTime];
    [self.dateTimeModel setDate:nil inSection:EHIDateTimeComponentSectionReturnDate];
    [self.dateTimeModel setDate:nil inSection:EHIDateTimeComponentSectionReturnTime];
    
    [self update];
}

- (void)clearFiltersQuery
{
    [self.bannerModel updateWithModel:nil];
    
    [self update];
}

- (void)closeTip
{
    [EHISettings didShowLocationsMapFilterTip];
    
    self.shouldShowTipFromSettings = [EHISettings shouldShowLocationsMapFilterTip];
    self.didShowFilter = YES;
}

# pragma mark - Accessors

- (BOOL)hideDateComponent
{
    return !self.dateTimeModel.hasData;
}

- (BOOL)hideFiltersBanner
{
    return !self.bannerModel.hasData;
}

- (BOOL)showFilterTip
{
    BOOL show = self.shouldShowTipFromSettings;
    if(self.isFromLDT) {
        show = !self.isFiltering;
    }

    return show && !self.didShowFilter;
}

- (BOOL)isShowingFilters
{
    return !self.hideFiltersBanner || !self.hideDateComponent;
}

//
// Helpers
//

- (void)update
{
    BOOL hasData = self.bannerModel.hasData;
    self.bannerModel.hideClear   = !hasData;
    self.dateTimeModel.hideClear = hasData;
}

@end
