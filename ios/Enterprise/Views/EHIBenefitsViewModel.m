//
//  EHIBenefitsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIBenefitsViewModel.h"

@implementation EHIBenefitsViewModel

- (instancetype)initWithTitle:(NSAttributedString *)title description:(NSAttributedString *)descriptionTitle
{
    if(self = [super init]) {
        _plusTitle = title;
        _descriptionTitle = descriptionTitle;
    }
    
    return self;
}

@end
