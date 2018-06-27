//
//  UIImageView+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 3/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIImageView+Utility.h"

@implementation UIImageView (Utility)

- (NSString *)ehi_imageName
{
    return nil;
}

- (void)setEhi_imageName:(NSString *)imageName
{
    // only attempt to create an image if we have a valid image name
    UIImage *image = nil;
    if(imageName.length) {
        image = [UIImage imageNamed:imageName];
    }
    
    self.image = image;
}

@end
