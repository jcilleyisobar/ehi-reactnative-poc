//
//  EHIDashboardNotificationsCell.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 12/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIDashboardNotificationsCell : EHICollectionViewCell

@end

@protocol EHIDashboardNotificationsActions <NSObject>
- (void)dashboardNotificationsCellDidInteract:(EHIDashboardNotificationsCell *)sender;
@end
