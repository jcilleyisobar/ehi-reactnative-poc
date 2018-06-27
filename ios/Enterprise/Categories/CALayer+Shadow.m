//
//  CALayer+Shadow.m
//  Enterprise
//
//  Created by Ty Cobb on 2/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "CALayer+Shadow.h"

@implementation CALayer (Shadow)

- (void)setEhi_showsShadow:(BOOL)ehi_showsShadow
{
    // update the standard shaodw properties
    self.shadowOpacity = ehi_showsShadow ? 0.15f : 0.0f;
    self.shadowColor   = [UIColor blackColor].CGColor;
    self.shadowRadius  = 8.0f;
    self.shadowOffset  = CGSizeZero;
    self.masksToBounds = !ehi_showsShadow;
    
    // update the path so that it doesn't chug
    if(ehi_showsShadow) {
        [self ehi_recalculateShadowPath];
    }
}

- (void)ehi_recalculateShadowPath
{
    CGPathRef shadowPath = CGPathCreateWithRect(self.bounds, NULL); {
        self.shadowPath = shadowPath;
    } CGPathRelease(shadowPath); 
}

- (BOOL)ehi_showsShadow
{
    return self.shadowOpacity != 0.0;
}

@end
