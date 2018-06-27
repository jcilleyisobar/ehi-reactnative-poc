//
//  EHIReviewAirlineCell.h
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIFlightCell : EHICollectionViewCell

@end

@protocol EHIFlightCellActions <NSObject>

- (void)didSelectedFlightCell;

@end