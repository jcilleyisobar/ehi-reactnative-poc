//
//  EHILocationFilterCellViewModel.h
//  Enterprise
//
//  Created by mplace on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationFilterCellViewModel : EHIViewModel <MTRReactive>
/** Title of the filter */
@property (copy  , nonatomic) NSString *title;
/** Color of the filter title */
@property (strong, nonatomic) UIColor *titleColor;
/** Icon image name */
@property (copy, nonatomic) NSString *iconImageName;
/** YES if the filter selection button should be hidden */
@property (assign, nonatomic) BOOL shouldHideSelectionButton;
/** YES if the icon image should be hidden */
@property (assign, nonatomic) BOOL shouldHideIconImage;
/** YES if the filter selection button is currently selected */
@property (assign, nonatomic) BOOL isSelected;
@end
