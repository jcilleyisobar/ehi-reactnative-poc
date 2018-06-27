//
//  UILabel+Autoshrink.h
//  Enterprise
//
//  Created by cgross on 9/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UILabel (Autoshrink)

- (void)shrinkTextIfNeeded:(NSString *)text rect:(CGRect)rect;

@end
