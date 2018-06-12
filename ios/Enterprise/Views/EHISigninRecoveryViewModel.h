//
//  EHISigninRecoveryViewModel.h
//  Enterprise
//
//  Created by Michael Place on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninRecoveryType.h"

@interface EHISigninRecoveryViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSAttributedString *details;
@property (copy, nonatomic) NSString *cancelButtonTitle;
@property (copy, nonatomic) NSString *actionButtonTitle;

- (void)performAction;
- (void)dismiss;

@end
