//
//  EHIAnalyticsContext_Private.h
//  Enterprise
//
//  Created by Ty Cobb on 6/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"
#import "EHIAnalyticsEncodable.h"
#import "EHIAnalyticsAttributes.h"
#import "EHIAnalyticsContext+Serialization.h"
#import "EHIAnalyticsContext+Mappings.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalyticsContext ()
@property (strong, nonatomic) NSMutableDictionary *attributes;
@property (copy  , nonatomic, nullable) NSString *activePrefix;
@property (strong, nonatomic, nullable) NSMutableDictionary *temporaryAttributes;
@property (strong, nonatomic, nullable) NSMutableDictionary *currentAttributes;
@property (copy  , nonatomic, nullable, readonly) NSString *actionTypeString;
@end

NS_ASSUME_NONNULL_END
