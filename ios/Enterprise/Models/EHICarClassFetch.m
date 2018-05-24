//
//  EHICarClassFetch.m
//  
//
//  Created by Michael Place on 9/29/15.
//
//

#import "EHICarClassFetch.h"

@implementation EHICarClassFetch

+ (instancetype)modelForCarClass:(EHICarClass *)carClass
{
    EHICarClassFetch *model = [EHICarClassFetch new];
    model.daysToRedeem = carClass.daysToRedeem;
    model.code = carClass.code;
    
    return model;
}

@end
