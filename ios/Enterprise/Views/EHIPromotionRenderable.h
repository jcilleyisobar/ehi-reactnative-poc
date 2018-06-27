//
//  EHIPromotionRenderable.h
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIImage.h"

@protocol EHIPromotionRenderable <NSObject>
@required
- (NSString *)title;
- (NSString *)shortTitle;
- (NSString *)subtitle;
- (NSString *)longDescription;
- (NSString *)cid;
- (NSString *)terms;
@optional
- (EHIImage *)imageModel;
- (NSString *)imageName;
@end
