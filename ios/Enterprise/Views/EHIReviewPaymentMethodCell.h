//
//  EHIReviewPaymentMethodCell.h
//  Enterprise
//
//  Created by Stu Buchbinder on 11/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewPaymentMethodCell : EHICollectionViewCell

@end

// we need to mirror the toggle on book button, doing like that to avoid two-way binding glitches on reactor
@protocol EHIReviewPaymentMethodCellActions <NSObject> @optional
- (void)reviewPaymentMethodDidToggleReadTerms:(EHIReviewPaymentMethodCell *)cell;
- (void)reviewPaymentMethodDidTap:(EHIReviewPaymentMethodCell *)cell;
@end
