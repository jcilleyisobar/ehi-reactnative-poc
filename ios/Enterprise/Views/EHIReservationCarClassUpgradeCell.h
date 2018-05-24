//
//  EHIReservationCarClassUpgradeCell.h
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReservationCarClassUpgradeCell : EHICollectionViewCell

@end

@protocol EHIReservationCarClassUpgradeCellActions <NSObject>
- (void)didTapActionButtonForCarClassUpgradeCell:(id)sender;
@end