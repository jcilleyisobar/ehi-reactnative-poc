//
//  EHIReservationClassSelectCell.h
//  Enterprise
//
//  Created by Alex Koller on 4/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHIButton.h"

@interface EHICarClassCell : EHICollectionViewCell
@property (weak, nonatomic) IBOutlet UIView *animationContainer;
@property (weak, nonatomic) IBOutlet UIView *bodyContainer;
@property (weak, nonatomic) IBOutlet UIView *bottomContainer;
@end

@protocol EHICarClassCellActions <NSObject> @optional
- (void)didTapPriceButtonForCarClassCell:(EHICarClassCell *)cell;
- (void)didTapDetailsButtonForCarClassCell:(EHICarClassCell *)cell;
@end
