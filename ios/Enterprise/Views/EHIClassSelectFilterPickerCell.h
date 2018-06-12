//
//  EHIClassSelectFilterPickerCell.h
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIClassSelectFilterPickerCell : EHICollectionViewCell

@end

@protocol EHIClassSelectFilterPickerCellActions <NSObject>
- (void)didDismissPickerForCell:(EHIClassSelectFilterPickerCell *)cell;
@end