//
//  EHIRentalsPagingCell.h
//  Enterprise
//
//  Created by Ty Cobb on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIRentalsPagingCell : EHICollectionViewCell

@end

@protocol EHIRentalsPagingCellActions <NSObject>
/** Actions emitted by the cell when more rentals load successfully */
- (void)pagingCellDidLoadMoreRentals:(EHIRentalsPagingCell *)cell;
@end
