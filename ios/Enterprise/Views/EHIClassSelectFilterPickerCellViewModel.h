//
//  EHIClassSelectFilterPickerCellViewModel.h
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIClassSelectFilterPickerCellViewModel : EHIViewModel <MTRReactive, UIPickerViewDataSource, UIPickerViewDelegate>
/** Title for the filter cell */
@property (copy, nonatomic) NSString *title;
/** Title of the currently selected filter value */
@property (copy, nonatomic) NSString *filterValueTitle;

/** Selects the filter value at the parameterized index */
- (void)selectFilterValueAtIndex:(NSInteger)index;
@end
