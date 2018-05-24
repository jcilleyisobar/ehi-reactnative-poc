//
//  EHIDashboardSearchFieldBorder.m
//  Enterprise
//
//  Created by Ty Cobb on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardSearchFieldBorder.h"
#import "EHIAnimatedShapeLayer.h"

@implementation EHIDashboardSearchFieldBorder

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        self.userInteractionEnabled = NO;
    
        // create the view for the rectangular border
        UIView *border = [[UIView alloc] initWithFrame:self.bounds];
        border.autoresizingMask  = UIViewAutoresizingFill;
        border.layer.borderColor = [UIColor ehi_grayColor2].CGColor;
        border.layer.borderWidth = 1.0f;

        // adjuste the frame for the green border so that it pins the bottom
        CGFloat bottomHeight = 2.0f;
        frame.origin.y    = frame.size.height - bottomHeight;
        frame.size.height = bottomHeight;
        
        UIView *bottomBorder = [[UIView alloc] initWithFrame:frame];
        bottomBorder.backgroundColor  = [UIColor ehi_greenColor];
        bottomBorder.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
      
        // composite the views
        [self addSubview:border];
        [self addSubview:bottomBorder];
    }
    
    return self;
}

@end
