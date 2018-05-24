//
//  EHILocationTimeZone.m
//  Enterprise
//
//  Created by Alex Koller on 12/2/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICacheLocation.h"

@interface EHICacheLocation ()
@property (copy  , nonatomic) NSString *locationId;
@property (copy  , nonatomic) NSString *timeZoneId;
@property (strong, nonatomic) EHILocationHours *hours;
@end

@implementation EHICacheLocation

- (void)updateWithLocation:(EHILocation *)location
{
    self.locationId = location.uid;

    // only update with existing data
    self.timeZoneId = location.timeZoneId ?: self.timeZoneId;
    self.hours      = location.hours ?: self.hours;
}

# pragma mark - EHIModel

- (id)uid
{
    return self.locationId;
}

+ (void)prepareCollection:(EHICollection *)collection
{
    // only save 100 location : timeZone pairings
    collection.historyLimit = 100;
}

@end
