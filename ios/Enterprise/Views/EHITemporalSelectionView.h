//
//  EHITemporalSelectionView.h
//  Enterprise
//
//  Created by Rafael Ramos on 03/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHITemporalSelectionView : EHIView

@end

@protocol EHITemporalSelectionViewActions <NSObject>
- (void)temporalSelectionViewDidTap:(EHITemporalSelectionView *)view;
@optional
- (void)temporalSelectionViewDidTapClean:(EHITemporalSelectionView *)view;
@end
