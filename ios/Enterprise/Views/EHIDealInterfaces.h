//
//  EHIDealActions.h
//  Enterprise
//
//  Created by Rafael Ramos on 22/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIPromotionRenderable.h"

@protocol EHIDealActionable <NSObject>
- (void)show:(id<EHIPromotionRenderable>)renderable;
@end

@protocol EHIDealInteractable <NSObject>
- (void)tapDeal;
@end

@protocol EHIDealDelegator <NSObject>
@property (weak, nonatomic) id<EHIDealActionable> delegate;
@end
