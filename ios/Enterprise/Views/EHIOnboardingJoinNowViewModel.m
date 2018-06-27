//
//  EHIOnboardingJoinNowViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 1/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIOnboardingJoinNowViewModel.h"

@implementation EHIOnboardingJoinNowViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"rewards_join_now_title", @"Join now, it just takes a few minutes", @"");
    }
    return self;
}

@end
