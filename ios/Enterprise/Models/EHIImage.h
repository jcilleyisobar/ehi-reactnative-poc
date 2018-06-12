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

@property (copy, nonatomic, readonly) NSString *name;
@property (copy, nonatomic, readonly) NSString *path;
@property (copy, nonatomic, readonly) NSArray *supportedWidths;
@property (copy, nonatomic, readonly) NSArray *supportedQualities;

/** @return The path determined by the scaled width and quality (scaled in terms of retina vs non-retina) */
- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality;

@end

EHIAnnotatable(EHIImage);