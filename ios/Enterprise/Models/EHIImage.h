//
//  EHIImage.h
//  Enterprise
//
//  Created by mplace on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHIImageQuality) {
    EHIImageQualityLow,
    EHIImageQualityMedium,
    EHIImageQualityHigh,
};

@interface EHIImage : EHIModel

- (NSString *)finalPathForWidth:(NSInteger)width quality:(EHIImageQuality)quality;

/*!
 * @discussion Template method, consume it via -finalPathForWidth:quality:
 * @return The path determined by the scaled width and quality (scaled in terms of retina vs non-retina)
 */
- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality;

@end
