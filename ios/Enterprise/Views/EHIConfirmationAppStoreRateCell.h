//
//  EHIConfirmationAppStoreRateCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIConfirmationAppStoreRateCell : EHICollectionViewCell

@end

@protocol EHIConfirmationAppStoreRateCellActions <NSObject> @optional
- (void)appStoreRateCellDidTapRate;
- (void)appStoreRateCellDidTapDismiss;
@end