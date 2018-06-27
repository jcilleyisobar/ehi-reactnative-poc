//
//  EHIExtrasExtraCell.h
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIExtrasExtraCell : EHICollectionViewCell 

@end

@protocol EHIExtrasExtraCellActions <NSObject> @optional
- (void)didChangeAmountForExtrasCell:(EHIExtrasExtraCell *)cell;
- (void)didInvalidateHeightForExtrasCell:(EHIExtrasExtraCell *)cell;
- (void)didSelectArrowButtonForExtrasCell:(EHIExtrasExtraCell *)cell;
@end
