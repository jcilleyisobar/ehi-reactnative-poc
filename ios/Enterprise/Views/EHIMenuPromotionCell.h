//
//  EHIMenuPromotionCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIMenuCell.h"

@interface EHIMenuPromotionCell : EHIMenuCell

@end

@protocol EHIMenuPromotionCellActions <NSObject> @optional
- (void)didTapMenuPromotionCell:(EHIMenuPromotionCell *)sender;
@end
