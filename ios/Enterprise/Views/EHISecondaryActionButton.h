//
//  EHISecondaryActionButton.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActionButton.h"

@interface EHISecondaryActionButton : EHIActionButton
/** @c YES if the button should swap its default border and background colors */
@property (assign, nonatomic) BOOL invertsColors;
/** @c YES if the button is on a dark background. The button will use a lighter green color */
@property (assign, nonatomic) BOOL isOnDarkBackground;
@end
