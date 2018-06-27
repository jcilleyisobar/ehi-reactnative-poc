//
//  EHILocationIconProvider.m
//  Enterprise
//
//  Created by Rafael Ramos on 03/10/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationIconProvider.h"
#import "EHILocation.h"

@implementation EHILocationIconProvider

+ (NSString *)iconForLocation:(EHILocation *)location
{
    if (location.isFavorited) {
        return @"icon_favorites_02";
    }
    if(location.isExotics) {
        return @"icon_exotics";
    }

    switch(location.type) {
        case EHILocationTypeAirport:
            return @"icon_airport_gray";
        case EHILocationTypeTrain:
            return @"icon_train_01";
        case EHILocationTypePort:
            return @"icon_portofcall_01";
        default:
            break;
    }

    return nil;
}

@end
