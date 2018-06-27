//
//  EHIReservationSublistSection.m
//  Enterprise
//
//  Created by Ty Cobb on 4/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSublistSection.h"

@implementation EHIReservationSublistSection

+ (instancetype)sectionWithTitle:(NSString *)title models:(NSArray *)models
{
    // filter out sections with no models
    if(!models.count) {
        return nil;
    }
    
    EHIReservationSublistSection *section = [EHIReservationSublistSection new];
    section.title = title;
    section.models = models;
    
    return section;
}

@end
