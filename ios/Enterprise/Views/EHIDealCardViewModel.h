//
//  EHIDealCardViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIPromotionRenderable.h"
#import "EHIDealInfo.h"
#import "EHIDealInterfaces.h"

typedef NS_ENUM(NSInteger, EHIDealLayout) {
    EHIDealLayoutList,
    EHIDealLayoutDetail
};

@interface EHIDealCardViewModel : EHIViewModel <MTRReactive, EHIDealInteractable, EHIDealDelegator>

+ (instancetype)modelWithRenderable:(id<EHIPromotionRenderable>)renderable layout:(EHIDealLayout)layout;

@property (copy  , nonatomic, readonly) NSAttributedString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSString *terms;
@property (copy  , nonatomic, readonly) NSString *staticImageName;
@property (copy  , nonatomic, readonly) EHIImage *imageModel;
@property (assign, nonatomic, readonly) BOOL hideDivider;
@end
