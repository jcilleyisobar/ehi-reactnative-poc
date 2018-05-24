//
//  EHIProfilePaymentMethodModifyActionsCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIProfilePaymentMethodModifyActionsCell : EHICollectionViewCell

@end

@protocol EHIProfilePaymentMethodModifyActionsCellActions <NSObject> @optional
- (void)didTapDeletePayment:(EHIProfilePaymentMethodModifyActionsCell *)cell;
- (void)didTapEditPayment:(EHIProfilePaymentMethodModifyActionsCell *)cell;
@end