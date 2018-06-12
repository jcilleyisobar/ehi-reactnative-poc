//
//  EHIImage.m
//  Enterprise
//
//  Created by mplace on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIImage.h"

@implementation EHIImage

- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality
{
    width = [self scaledWidthForWidth:width];
    
    NSString *finalPath     = self.path;
    NSString *qualityString = [self qualityStringForType:quality];
    NSString *widthString   = [self supportedWidthForImageOfWidth:width];
    
    finalPath = [finalPath stringByReplacingOccurrencesOfString:@"{width}" withString:widthString];
    finalPath = [finalPath stringByReplacingOccurrencesOfString:@"{quality}" withString:qualityString];

    return finalPath;
}

//
// Helper
//

- (NSInteger)scaledWidthForWidth:(NSInteger)width
{
    return width * [UIScreen mainScreen].scale;
}

- (NSString *)qualityStringForType:(EHIImageQuality)quality
{
    switch (quality) {
        case EHIImageQualityLow:
            return @"low";
        case EHIImageQualityMedium:
            return @"medium";
        case EHIImageQualityHigh:
            return @"high";
    }
}

- (NSString *)supportedWidthForImageOfWidth:(NSInteger)width
{
    NSString *idealWidth = self.supportedWidths.find(^(NSString *supportedWidth) {
        return width <= [supportedWidth integerValue];
    });
    
    return idealWidth ?: [self.supportedWidths lastObject];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIImage *)model
{
    return @{
        @"supported_widths"    : @key(model.supportedWidths),
        @"supported_qualities" : @key(model.supportedQualities),
    };
}

@end
