//
//  EHIPromotionViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIPromotionViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *promotionName;
@property (copy, nonatomic, readonly) NSString *promotionButtonTitle;
@end
