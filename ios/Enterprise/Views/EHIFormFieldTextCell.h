//
//  EHIFormFieldTextCell.h
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldCell.h"

@interface EHIFormFieldTextCell : EHIFormFieldCell

@end

@protocol EHIFormFieldTextCellActions <NSObject>
- (void)didTapDeleteButtonForCell:(EHIFormFieldTextCell *)sender;
@end