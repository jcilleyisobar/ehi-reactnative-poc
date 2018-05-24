//
//  EHIPromotionDetailsActionCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIPromotionDetailsActionCell : EHICollectionViewCell

@end

@protocol EHIPromotionDetailsActionCellActions <NSObject> @optional
- (void)didTapWeekendSpecialStartReservation;
@end