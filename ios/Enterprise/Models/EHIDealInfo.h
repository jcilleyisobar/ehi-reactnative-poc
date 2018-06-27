//
//  EHIDealInfo.h
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIAEMImage.h"
#import "EHIPromotionRenderable.h"

@interface EHIDealInfo : EHIModel <EHIPromotionRenderable>
@property (copy  , nonatomic, readonly) NSString *longTitle;
@property (copy  , nonatomic, readonly) NSString *shortTitle;
@property (copy  , nonatomic, readonly) NSString *smallDescription;
@property (copy  , nonatomic, readonly) NSString *longDescription;
@property (copy  , nonatomic, readonly) NSString *terms;
@property (copy  , nonatomic, readonly) NSString *cid;
@property (strong, nonatomic, readonly) EHIAEMImage *image;
@end

EHIAnnotatable(EHIDealInfo);
