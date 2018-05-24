//
//  UIPickerView+Utility.h
//  Enterprise
//
//  Created by Alex Koller on 5/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIPickerView (Utility)

/**
 @brief  Finds the selected row in the first component
 
 @return The selected row
 */
- (NSInteger)ehi_selectedRow;

/**
 @brief  Selects the given row in the first component
 
 If @c row is already selected, this method does nothing.
 
 @param row      The row to select
 @param animated Whether to animate the selection
 */
- (void)ehi_selectRow:(NSInteger)row animated:(BOOL)animated;

@end
