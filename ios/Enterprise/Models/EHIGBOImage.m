//
//  EHIGBOImage.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIGBOImage.h"

@interface EHIGBOImage ()
@property (copy, nonatomic, readonly) NSString *name;
@property (copy, nonatomic, readonly) NSString *path;
@property (copy, nonatomic, readonly) NSArray *supportedWidths;
@property (copy, nonatomic, readonly) NSArray *supportedQualities;
@end

@implementation EHIGBOImage

- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality
{
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

+ (NSDictionary *)mappings:(EHIGBOImage *)model
{
    return @{
       @"supported_widths"    : @key(model.supportedWidths),
       @"supported_qualities" : @key(model.supportedQualities),
    };
}

@end
