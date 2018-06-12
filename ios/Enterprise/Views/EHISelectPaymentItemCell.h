//
//  EHISelectPaymentItemCell.h
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHISelectPaymentItemCell : EHICollectionViewCell

@end

@protocol EHISelectPaymentItemCellActions<NSObject> @optional
- (void)didTapPaymentToggle:(EHISelectPaymentItemCell *)cell;
@end
