//
//  UIPickerView+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 5/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIPickerView+Utility.h"

@implementation UIPickerView (Utility)

- (NSInteger)ehi_selectedRow
{
    return [self selectedRowInComponent:0];
}

- (void)ehi_selectRow:(NSInteger)row animated:(BOOL)animated
{
    if([self ehi_selectedRow] != row) {
        [self selectRow:row inComponent:0 animated:animated];
    }
}

@end
