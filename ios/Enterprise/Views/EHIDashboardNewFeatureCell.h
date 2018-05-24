//
//  EHIDashboardNotificationsCell.h
//  Enterprise
//
//  Created by Alex Koller on 12/28/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIDashboardNewFeatureCell : EHICollectionViewCell

@end

@protocol EHIDashboardNewFeatureCellActions <NSObject>
- (void)didTapAcceptButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender;
- (void)didTapDenyButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender;
- (void)didTapCloseButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender;
@end