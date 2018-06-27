//
//  EHIReviewPaymentChangeCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewPaymentChangeCell : EHICollectionViewCell

@end

@protocol EHIReviewPaymentChangeCellActions <NSObject> @optional
- (void)didTapChangePayment:(EHIReviewPaymentChangeCell *)cell;
@end
