//
//  EHILocationCell.h
//  Enterprise
//
//  Created by mplace on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHILocationCell : EHICollectionViewCell
@end

@protocol EHILocationCellContext <NSObject> @optional
- (BOOL)shouldUseCompressedLayoutForLocationCell:(EHILocationCell *)cell;
@end
