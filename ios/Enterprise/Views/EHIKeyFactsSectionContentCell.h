//
//  EHIKeyFactsSectionContentCell.h
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIKeyFactsSectionContentCell : EHICollectionViewCell

@end

@protocol EHIKeyFactsSectionContentActions <NSObject>
- (void)didTapSectionContentHeader:(EHIKeyFactsSectionContentCell *)sender;
@end