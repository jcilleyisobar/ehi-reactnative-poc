//
//  EHIPromotionDetailsBulletItemCellViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIPromotionDetailsBulletItemCellViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *bulletTitle;
@end
