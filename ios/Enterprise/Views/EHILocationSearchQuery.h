//
//  EHILocationSearchQuery.h
//  Enterprise
//
//  Created by Michael Place on 3/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHILocationSearchQuery : NSObject
@property (copy  , nonatomic) NSString *query;
@property (assign, nonatomic) BOOL isOneWay;
@end
