//
//  EHIRNFactory.m
//  Enterprise
//
//  Created by Jeff Cilley on 6/27/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRNFactory.h"

@interface EHIRNFactory ()

@property(strong) RCTBridge *bridge;

@end

@implementation EHIRNFactory

- (instancetype)initWithRCTBridge:(RCTBridge *)bridge
{
    if (self = [super init]) {
        self.bridge = bridge;
    }
    return self;
}

- (RCTRootView *)createDealsListViewWithWeekendSpecialID:(NSString *)weekendSpecialCID;
{
    return [[RCTRootView alloc] initWithBridge:self.bridge
                                    moduleName:@"RNHighScores"
                             initialProperties:@{
                                                 @"scores" : @[
                                                         @{
                                                             @"name" : @"Alex",
                                                             @"value": @"42"
                                                             },
                                                         @{
                                                             @"name" : @"Joel",
                                                             @"value": @"10"
                                                             }
                                                         ]
                                                 }];
}


@end
