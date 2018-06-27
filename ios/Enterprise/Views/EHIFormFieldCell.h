//
//  EHIFormFieldCell.h
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIFormFieldCell : EHICollectionViewCell

@end

@protocol EHIFormFieldCellActions <NSObject>
@optional
- (void)didBeginEditingPrimaryInputForCell:(EHIFormFieldCell *)sender;
- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender;
@end