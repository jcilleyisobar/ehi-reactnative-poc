//
//  EHIProfilePaymentAddCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIProfilePaymentAddCell : EHICollectionViewCell

@end

@protocol EHIProfilePaymentAddCellActions <NSObject> @optional
- (void)didTapAddCreditCard:(EHIProfilePaymentAddCell *)cell;
@end