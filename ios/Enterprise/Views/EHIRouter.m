//
//  EHIRouter.m
//  Enterprise
//
//  Created by Alex Koller on 12/8/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIRouter.h"
#import "EHIReservationBuilder.h"
#import "EHIReservationRouter.h"
#import "EHISigninRouter.h"
#import "EHISigninRouterManager.h"

@implementation EHIRouter

+ (instancetype)router
{
    NSAssert(self != EHIRouter.class, @"EHIGenericRouter is an abstract superclass. Use a subclass instead.");
    return [super router];
}

+ (EHIRouter *)currentRouter
{
    if([EHISigninRouterManager sharedInstance].isActive) {
        return EHISigninRouter.router;
    } else if([EHIReservationBuilder sharedInstance].isActive) {
        return EHIReservationRouter.router;
    } else {
        return EHIMainRouter.router;
    }
}

@end
