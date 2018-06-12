//
//  EHIPromotionView.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIPromotionView : EHIView

@end

@protocol EHIPromotionViewActions <NSObject> @optional
- (void)didTapPromotionGetStarted;
@end