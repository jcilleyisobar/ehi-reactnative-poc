//
//  EHILocationFilterViewModel.h
//  Enterprise
//
//  Created by mplace on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationInteractorViewModel.h"
#import "EHIFilters.h"
#import "EHISectionHeaderModel.h"
#import "EHIDateTimeComponentFilterViewModel.h"

typedef NS_ENUM(NSUInteger, EHILocationFilterSection) {
    EHILocationFilterSectionOpenDuringTravel,
    EHILocationFilterSectionLocationType,
    EHILocationFilterSectionMiscellaneous,
};

@class EHILocationFilterQuery;
@interface EHILocationFilterViewModel : EHILocationInteractorViewModel <MTRReactive>

/** Title of the screen */
@property (copy, nonatomic) NSString *title;
/** Title of the apply filters button */
@property (copy, nonatomic) NSString *applyFilterButtonTitle;

@property (strong, nonatomic) EHIDateTimeComponentFilterViewModel *dateTimeFilter;
/** Models to populate the location type section with */
@property (copy, nonatomic) NSArray *locationTypeFilters;
/** Models to populate the miscellaneous section with */
@property (copy, nonatomic) NSArray *miscellaneousFilters;

@property (assign, nonatomic) BOOL shouldRefreshContent;

- (EHISectionHeaderModel *)headerModelForSection:(EHILocationFilterSection)section;
/** Sets the filter active for the parameterized indexPath */
- (void)selectFilterAtIndexPath:(NSIndexPath *)indexPath;
/** Sets all filters to be inactive */
- (void)clearFilters;
/** Navigates back to the map screen with the populated filter query */
- (void)applyFilters;
/** Navigates back to the map screen with a nil query */
- (void)cancelFiltering;

- (void)didTapOnSection:(EHIDateTimeComponentSection)section;
- (void)didTapOnCleanSection:(EHIDateTimeComponentSection)section;

@end
