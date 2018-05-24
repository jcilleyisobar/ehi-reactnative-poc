//
//  EHIReviewPrepayPaymentCell.h
//  Enterprise
//
//  Created by cgross on 1/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewPrepayPaymentCell : EHICollectionViewCell

@end

@protocol EHIReviewPrepayPaymentActions <NSObject> @optional
- (void)didTapAddPrepayPaymentMethodForPrepayPaymentCell:(EHIReviewPrepayPaymentCell *)sender;
@end
