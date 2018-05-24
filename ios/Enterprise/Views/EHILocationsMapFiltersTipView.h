//
//  EHILocationsMapFiltersTipView.h
//  Enterprise
//
//  Created by Rafael Machado on 03/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHILocationsMapFiltersTipView : EHIView
@property (assign, nonatomic) CGFloat arrowHeight;
@property (assign, nonatomic) CGFloat padding;
@end

@protocol EHILocationsMapTipViewActions <NSObject>
- (void)filterTipDidTapClose:(EHILocationsMapFiltersTipView *)sender;
@end
