//
//  EHIConfirmationActionsCell.h
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIConfirmationActionsCell : EHICollectionViewCell

@end

@protocol EHIConfirmationActions <NSObject>
- (void)confirmationCellDidTapReturnToDashboard:(EHIConfirmationActionsCell *)cell;
@end
