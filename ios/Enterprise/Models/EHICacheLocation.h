//
//  EHILocationTimeZone.h
//  Enterprise
//
//  Created by Alex Koller on 12/2/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHICacheLocation : EHIModel

@property (copy  , nonatomic, readonly) NSString *locationId;
@property (copy  , nonatomic, readonly) NSString *timeZoneId;
@property (strong, nonatomic, readonly) EHILocationHours *hours;

- (void)updateWithLocation:(EHILocation *)location;

@end
