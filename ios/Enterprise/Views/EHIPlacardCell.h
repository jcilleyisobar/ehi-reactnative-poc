//
//  EHIPlacardCell.h
//  Enterprise
//
//  Created by Alex Koller on 8/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHIPlacardViewModel.h"

@interface EHIPlacardCell : EHICollectionViewCell

@end

@protocol EHIPlacardActions <NSObject> @optional
- (void)didTapInfo;
@end