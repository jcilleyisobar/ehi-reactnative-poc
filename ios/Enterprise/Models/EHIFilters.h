//
//  EHIFilters.h
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIFilterType.h"
#import "EHICarClassFilter.h"

typedef NS_ENUM(NSUInteger, EHIFilterStyle) {
    EHIFilterStyleToggle,
    EHIFilterStylePicker,
    EHIFilterStyleLink
};

@interface EHIFilter : EHIModel <NSCopying>
/** Title for the filter */
@property (copy  , nonatomic) NSString *title;
/** Regex to filter with */
@property (copy  , nonatomic) NSString *key;
/** Type of the filter */
@property (assign, nonatomic) EHIFilterType type;
/** Value to filter against */
@property (assign, nonatomic) NSInteger value;
@end

EHIAnnotatable(EHIFilter);

@interface EHIFilters : EHIModel <NSCopying>
/** Title for the filter */
@property (copy  , nonatomic) NSString *title;
/** For toggle filters display is the current filters title, for picker filters its the concatenation of the two titles */
@property (copy  , nonatomic) NSString *displayTitle;
/** Icon image of the filter */
@property (copy, nonatomic) NSString *iconImageName;
/** Style of the filter (EHIFilterStyle) */
@property (assign, nonatomic) EHIFilterStyle style;
/** YES if the filter is active (for checkbox filters) */
@property (assign, nonatomic) BOOL isActive;
/** Array of possible filter values (for dropdown filters) */
@property (copy  , nonatomic) NSArray<EHIFilter> *possibleFilters;
/** Default filter value */
@property (strong, nonatomic) EHIFilter *defaultFilter;
/** Current filter value */
@property (strong, nonatomic) EHIFilter *currentFilter;

//
// Class Select Filters
//

/** Generates a list of vehicle filters of type @c EHIFilters */
+ (NSArray *)vehicleTypeFiltersForCarClassFilters:(NSArray *)filters;
/** Generates a list of vehicle filters of type @c EHIFilters */
+ (NSArray *)vehicleFeatureFiltersForCarClassFilters:(NSArray *)filters;

//
// Location Filters
//

/** Generates a list of location hours filters of type @c EHIFilters */
+ (NSArray *)locationHoursFilters;
/** Generates a list of location type filters of type @c EHIFilters */
+ (NSArray *)locationTypeFilters;
/** Generates a list of location miscellaneous filters of type @c EHIFilters */
+ (NSArray *)locationMiscellaneousFilters;

@end
