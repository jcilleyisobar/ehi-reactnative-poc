//
//  EHIBenefitsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIBenefitsViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithTitle:(NSAttributedString *)title description:(NSAttributedString *)descriptionTitle;

@property (copy, nonatomic, readonly) NSAttributedString *plusTitle;
@property (copy, nonatomic, readonly) NSAttributedString *descriptionTitle;

@end
