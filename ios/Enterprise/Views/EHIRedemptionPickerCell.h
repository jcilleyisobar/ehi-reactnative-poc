//
//  EHIRedemptionPickerCell.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIRedemptionPickerCell : EHICollectionViewCell

@end


@protocol EHIRedemptionPickerCellActions <NSObject>
- (void)redemptionPickerDidUpdateDaysRedeemed:(EHIRedemptionPickerCell *)cell;
@end