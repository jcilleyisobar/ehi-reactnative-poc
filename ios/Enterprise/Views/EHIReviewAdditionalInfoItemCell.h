//
//  EHIReviewAdditionalInfoItemCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewAdditionalInfoItemCell : EHICollectionViewCell

@end

@protocol EHIReviewAdditionalInfoItemCellActions <NSObject> @optional
- (void)didTapAdditionalInfoCell;
@end