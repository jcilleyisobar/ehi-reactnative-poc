//
//  EHICaptionedButton.m
//  Enterprise
//
//  Created by mplace on 2/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICaptionedButton.h"

#define EHICaptionedButtonSpacing (4.0f)

@implementation EHICaptionedButton

# pragma mark - View Lifecycle

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // center the text
    self.titleLabel.textAlignment = NSTextAlignmentCenter;
}

# pragma mark - Layout

- (CGRect)imageRectForContentRect:(CGRect)contentRect
{
    CGSize titleSize = [super titleRectForContentRect:contentRect].size;
    CGRect imageRect = [super imageRectForContentRect:contentRect];
   
    // center the image in the content rect
    imageRect = CGRectByCenteringSizeInRect(contentRect, imageRect.size);
    // offset the y-position by the title's height / padding
    imageRect.origin.y -= (titleSize.height  + EHICaptionedButtonSpacing) / 2.0f;
    
    return imageRect;
}

- (CGRect)titleRectForContentRect:(CGRect)contentRect
{
    CGRect imageRect = [self imageRectForContentRect:contentRect];
    CGRect titleRect = [super titleRectForContentRect:contentRect];

    titleRect.origin.x -= imageRect.size.width / 2.0f;
    
    // align against the image if it exists
    if(imageRect.size.height) {
        titleRect.origin.y = CGRectGetMaxY(imageRect) + EHICaptionedButtonSpacing;
    }

    return CGRectIntegral(titleRect);
}

@end
