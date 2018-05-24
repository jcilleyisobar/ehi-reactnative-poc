//
//  EHIRedemptionPointsView.h
//  Enterprise
//
//  Created by fhu on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIRedemptionPointsCell : EHICollectionViewCell
@property (assign, nonatomic) BOOL isLoading;
@end

@protocol EHIRedemptionPointsCellActions <NSObject>
- (void)didTapActionButtonForRedemptionPointsCell:(EHIRedemptionPointsCell *)sender;
@optional
- (void)didRemovePointsForRedemptionPointsCell:(EHIRedemptionPointsCell *)sender;
- (void)didSelectedRedemptionPointsCell;
@end