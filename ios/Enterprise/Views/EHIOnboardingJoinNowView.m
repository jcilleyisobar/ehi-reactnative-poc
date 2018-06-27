//
//  EHIOnboardingJoinNowView.m
//  Enterprise
//
//  Created by Stu Buchbinder on 1/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIOnboardingJoinNowView.h"
#import "EHIOnboardingJoinNowViewModel.h"

@interface EHIOnboardingJoinNowView()

@property (strong, nonatomic) EHIOnboardingJoinNowViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *title;

@end

@implementation EHIOnboardingJoinNowView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIOnboardingJoinNowViewModel new];
    }
    return self;
}

- (void)registerReactions:(EHIOnboardingJoinNowViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    self.title.text = self.viewModel.title;
}
@end
