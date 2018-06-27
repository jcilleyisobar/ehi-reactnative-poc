//
//  EHIToggleButton.h
//  Enterprise
//
//  Created by mplace on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, EHIToggleButtonStyle) {
    EHIToggleButtonStyleDefault,
    EHIToggleButtonStyleWhite,
};

@interface EHIToggleButton : UIButton
@property (assign, nonatomic) EHIToggleButtonStyle style;
@end
