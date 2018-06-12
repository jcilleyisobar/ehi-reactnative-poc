//
//  EHIReservationPriceSublistCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReservationPriceSublistCell : EHICollectionViewCell

@end

@protocol EHIReservationPriceSublistCellActions <NSObject> @optional
- (void)didSelectExpandedReservationSublistCell:(EHIReservationPriceSublistCell *)cell;
@end