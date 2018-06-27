//
//  EHIReservationRentalPriceTotalCell.h
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReservationRentalPriceTotalCell : EHICollectionViewCell
@end

@protocol EHIReservationRentalPriceTotalCellActions <NSObject>
- (void)didTapChangePaymentTypeForPriceTotalCell:(EHIReservationRentalPriceTotalCell *)sender;
@end
