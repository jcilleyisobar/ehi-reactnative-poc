//
//  EHIConfirmationManageReservationCellCollectionViewCell.h
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/14/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIConfirmationManageReservationCell : EHICollectionViewCell

@end

@protocol EHIConfirmationManageReservationCellActions <NSObject> @optional
- (void)didExpandManageReservationCell:(EHIConfirmationManageReservationCell *)cell;
@end
