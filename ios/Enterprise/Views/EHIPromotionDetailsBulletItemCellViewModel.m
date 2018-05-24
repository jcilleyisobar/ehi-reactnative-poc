//
//  EHIPromotionDetailsBulletItemCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsBulletItemCellViewModel.h"

@implementation EHIPromotionDetailsBulletItemCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[NSString class]]) {
            _bulletTitle = (NSString *)model;
        }
    }
    
    return self;
}

@end
