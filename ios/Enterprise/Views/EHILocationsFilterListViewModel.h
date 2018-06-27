//
//  EHILocationsFilterListViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIDateTimeComponentMapViewModel.h"
#import "EHIDateTimeUpdatableProtocol.h"
#import "EHILocationsFilterBannerViewModel.h"

@interface EHILocationsFilterListViewModel : EHIViewModel <MTRReactive, EHIDateTimeUpdatableProtocol>
@property (strong, nonatomic, readonly) EHIDateTimeComponentMapViewModel *dateTimeModel;
@property (strong, nonatomic, readonly) EHILocationsFilterBannerViewModel *bannerModel;
@property (assign, nonatomic, readonly) BOOL showFilterTip;
@property (assign, nonatomic, readonly) BOOL hideDateComponent;
@property (assign, nonatomic, readonly) BOOL hideFiltersBanner;
@property (assign, nonatomic, readonly) BOOL isShowingFilters;
@property (assign, nonatomic) BOOL isFromLDT;
@property (assign, nonatomic) BOOL isFiltering;

- (void)updateActiveFilters:(NSArray *)filters;

- (void)clearDateQuery;
- (void)clearFiltersQuery;
- (void)closeTip;

@end
