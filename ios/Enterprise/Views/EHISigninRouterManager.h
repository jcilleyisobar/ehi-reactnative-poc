//
//  EHISigninRouterManager.h
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface EHISigninRouterManager : NSObject
@property (assign, nonatomic) BOOL isActive;
+ (instancetype)sharedInstance;
@end
