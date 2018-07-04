//
//  EHIRNFactory.h
//  Enterprise
//
//  Created by Jeff Cilley on 6/27/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTRootView.h>

@interface EHIRNFactory : NSObject

- (instancetype)initWithRCTBridge:(RCTBridge *)bridge;

- (RCTRootView *)createDealsListViewWithWeekendSpecialID:(NSString *)weekendSpecialCID;


@end
