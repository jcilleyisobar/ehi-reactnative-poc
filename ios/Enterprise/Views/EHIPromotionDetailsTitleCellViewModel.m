//
//  EHIPromotionDetailsTitleCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsTitleCellViewModel.h"

@implementation EHIPromotionDetailsTitleCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[NSString class]]) {
            NSString *title = (NSString *)model;
            _promotionTitle = title.uppercaseString;
        }
    }
    
    return self;
}

@end
