//
//  EHIDashboardLocationPromptCell.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 21/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIDashboardLocationPromptCell : EHICollectionViewCell

@end

@protocol EHIDashboardLocationActions <NSObject>
- (void)dashboardLocationCellDidInteract:(EHIDashboardLocationPromptCell *)sender;
@end
