//
//  EHIPromotionDetailsImageCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsImageCellViewModel.h"

@implementation EHIPromotionDetailsImageCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
    	_imageName = @"weekend_special";
    }
    
    return self;
}

@end
