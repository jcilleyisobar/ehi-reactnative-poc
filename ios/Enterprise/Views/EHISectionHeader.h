//
//  EHISectionHeader.h
//  Enterprise
//
//  Created by mplace on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHISectionHeaderModel.h"

@interface EHISectionHeader : EHICollectionViewCell

@end

@protocol EHISectionHeaderActions <NSObject>
- (void)sectionHeaderDidTapActionButton:(EHISectionHeader *)sectionHeader;
@end


