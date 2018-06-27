//
//  EHIRewardsAboutTiersListCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIRewardsAboutTiersListCell : EHICollectionViewCell

@end

@protocol EHIRewardsAboutTiersListCellActions <NSObject> @optional
- (void)rewardsAboutTierDidTapArrow:(EHIRewardsAboutTiersListCell *)sender;
@end
