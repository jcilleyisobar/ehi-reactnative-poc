//
//  EHIRedemptionTotalCell.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIRedemptionTotalCell : EHICollectionViewCell

@end

@protocol EHIRedemptionTotalCellActions <NSObject>
- (void)didToggleSelectedStateForCell:(EHIRedemptionTotalCell *)cell;
@end