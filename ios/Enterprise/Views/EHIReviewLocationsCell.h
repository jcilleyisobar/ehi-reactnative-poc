//
//  EHIReviewLocationsCell.h
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIReviewLocationsCell : EHICollectionViewCell

@end

@protocol EHIReviewLocationsCellActions <NSObject> @optional

- (void)didSelectedLocationsCell;

@end