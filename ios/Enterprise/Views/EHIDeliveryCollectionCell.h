//
//  EHIDeliveryCollectionCell.h
//  Enterprise
//
//  Created by Alex Koller on 6/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIDeliveryCollectionCell : EHICollectionViewCell

@end

@protocol EHIDeliveryCollectionCellActions <NSObject>
- (void)didTapActionButtonForDeliveryCollectionCell:(EHIDeliveryCollectionCell *)sender;
@end