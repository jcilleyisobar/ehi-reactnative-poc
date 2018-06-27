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

- (NSString *)finalPathForWidth:(NSInteger)width quality:(EHIImageQuality)quality
{
    width = [self scaledWidthForWidth:width];
    
    return [self pathForWidth:width quality:quality];
}

- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality
{
    NSAssert(true, @"Calling an abstract method");
    [self doesNotRecognizeSelector:_cmd];
    
    return nil;
}

- (NSInteger)scaledWidthForWidth:(NSInteger)width
{
    return width * [UIScreen mainScreen].scale;
}

@end
