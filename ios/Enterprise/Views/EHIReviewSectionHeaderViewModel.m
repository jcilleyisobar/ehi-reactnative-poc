//
//  EHIReviewSectionHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewSectionHeaderViewModel.h"

@implementation EHIReviewSectionHeaderViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[NSString class]]) {
            _title = (NSString *)model;
        }
    }
    
    return self;
}

@end
