//
//  EHIConfirmationAssistanceCell.h
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIConfirmationAssistanceCell : EHICollectionViewCell

@end

@protocol EHIConfirmationAssistanceCellActions <NSObject>
- (void)didTapQuickPickupButtonForAssistanceCell:(EHIConfirmationAssistanceCell *)sender;
@end