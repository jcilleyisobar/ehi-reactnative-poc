//
//  EHISelectPaymentFooterCell.h
//  Enterprise
//
//  Created by Stu Buchbinder on 10/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHISelectPaymentFooterCell : EHICollectionViewCell

@end

@protocol EHISelectPaymentFooterCellActions <NSObject> @optional
- (void)didTapContinue:(EHISelectPaymentFooterCell *)cell;
@end
