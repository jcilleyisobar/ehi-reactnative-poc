//
//  EHIReviewAdditionalInfoAddCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewAdditionalInfoAddCell : EHICollectionViewCell

@end

@protocol EHIReviewAdditionalInfoAddCellActions <NSObject> @optional
- (void)didTapAdditionalInfoAddCell;
@end