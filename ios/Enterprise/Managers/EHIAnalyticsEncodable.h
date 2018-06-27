//
//  EHIAnalyticsEncodable.h
//  Enterprise
//
//  Created by Ty Cobb on 5/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"
#import "EHIAnalyticsAttributes.h"

NS_ASSUME_NONNULL_BEGIN

@protocol EHIAnalyticsEncodable <NSObject>

/**
 @brief Provides a hook to encode objects of this type into an analytics context
 
 Implementers can encode values using the @c context's subscripting functionality.
 
 It's possible that @c instane is @c nil, in which case data for this type is being
 deleted from the context.
 
 @param context  The context to encode data into
 @param instance The instance whose data to encode
*/

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable id<EHIAnalyticsEncodable>)instance;

@end

NS_ASSUME_NONNULL_END
