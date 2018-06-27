//
//  EHIReviewPaymentOptionsCell.h
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewPaymentOptionsCell : EHICollectionViewCell

@end

@protocol EHIReviewPaymentOptionsActions <NSObject> @optional
- (void)didResizeReviewPaymentOptionsCell:(EHIReviewPaymentOptionsCell *)cell;
@end
