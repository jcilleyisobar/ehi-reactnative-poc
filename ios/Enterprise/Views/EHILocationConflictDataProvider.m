//
//  EHILocationConflictDataProvider.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationConflictDataProvider.h"

@interface EHILocationConflictDataProvider ()
@property (strong, nonatomic) EHILocation *aLocation;
@property (assign, nonatomic) BOOL isOneWay;
@end

@implementation EHILocationConflictDataProvider

- (EHILocationConflictDataProvider * (^)(EHILocation *))location
{
    return ^(EHILocation *location){
        self.aLocation = location;
        return self;
    };
}

- (EHILocationConflictDataProvider *(^)(BOOL))oneWay
{
    return ^(BOOL oneWay){
        self.isOneWay = oneWay;
        return self;
    };
}

- (NSString *)title
{
	NSString *title = EHILocalizedString(@"locations_map_closed_your_pickup", @"CLOSED ON YOUR #{closed_on}", @"");

    NSString *conflict = [self conflictPairsForLocation:self.aLocation].select(^(NSDictionary *item){
        return [item[@"isClosed"] boolValue] && ![item[@"isAfterHours"] boolValue];
    }).map(^(NSDictionary *item){
        return item[@"title"];
    }).join(@" & ");
    
    return [title ehi_applyReplacementMap:@{
        @"closed_on": conflict ?: @""
    }];
}

- (NSString *)openHours
{
    NSString *(^mapSlices)(EHILocationTimesSlice *) = ^(EHILocationTimesSlice *slice) {
        NSString *open  = slice.open.ehi_localizedTimeString.lowercaseString;
        NSString *close = slice.close.ehi_localizedTimeString.lowercaseString;
        
        return [NSString stringWithFormat:@"%@ - %@", open, close];
    };
    
    NSArray<NSDictionary *> *conflicts = [self conflictPairsForLocation:self.aLocation];
    NSString *result   = conflicts.select(^(NSDictionary *item) {
        return [item[@"isClosed"] boolValue] && ![item[@"isAfterHours"] boolValue];
    }).map(^(NSDictionary *item) {
        NSString *date = item[@"date"];
        NSArray *hours = item[@"openHours"];
        NSString *closed = hours.count > 0 ? hours.map(mapSlices).join(@", ") : EHILocalizedString(@"location_details_hours_closed", @"CLOSED", @"");
        
        return [NSString stringWithFormat:@"%@: %@", date, closed];
    }).join(@"\n");
    
    return result;
}

- (NSArray<NSDictionary *> *)conflictPairsForLocation:(EHILocation *)location
{
    return @[
        @{
            @"isClosed"     : @([self isClosed:location.pickupValidity]),
            @"date"         : location.pickupDate.ehi_localizedDateString.uppercaseString ?: @"",
            @"openHours"    : location.pickupValidity.hours.standardTimes.slices ?: @[],
            @"title"        : EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"") ?: @"",
            @"isAfterHours" : @(NO)
        },
        @{
            @"isClosed"     : @([self isClosed:location.dropoffValidity]),
            @"date"         : location.dropOffDate.ehi_localizedDateString.uppercaseString ?: @"",
            @"openHours"    : location.dropoffValidity.hours.standardTimes.slices ?: @[],
            @"title"        : EHILocalizedString(@"locations_map_closed_return", @"RETURN", @"") ?: @"",
            @"isAfterHours" : @(location.hasAfterHours)
        }
    ];
}

- (NSAttributedString *)afterHours
{
    BOOL hasAfterHours = self.aLocation.hasAfterHours;
    if(!hasAfterHours || self.isOneWay) {
        return nil;
    }
    
    NSString *about 	 = EHILocalizedString(@"locations_map_after_hours_about_button", @"(about?)", @"");
    __weak __typeof(self) welf = self;
    NSAttributedString *attributedAbout =
    [NSAttributedString attributedStringWithString:about
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleItalic size:14.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            ehi_call(welf.afterHoursBlock)();
                                        }
     ];
    
    NSString *afterHours = EHILocalizedString(@"locations_map_after_hours_return_label", @"After Hours Return #{about}", @"");
    
    return EHIAttributedStringBuilder.new
        .appendText(afterHours)
        .color([UIColor ehi_grayColor4])
        .fontStyle(EHIFontStyleRegular, 14.0f)
        .replace(@"#{about}", attributedAbout)
        .string;
}

//
// Helpers
//

- (BOOL)isClosed:(EHILocationValidity *)validity
{
    return validity.status == EHILocationValidityStatusInvalidAtThatTime
        || validity.status == EHILocationValidityStatusInvalidAllDay;
}

@end
