//
//  EHIPolicyInfoViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPoliciesViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHIPoliciesViewModel ()
@property (copy, nonatomic) NSArray *policies;
@end

@implementation EHIPoliciesViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"policies_title", @"Policy Information", @"Title for the 'Policies List' screen");
    }
    
    return self;
}

- (void)updateWithModel:(NSArray *)policies
{
    [super updateWithModel:policies];

    self.policies = policies;
}

# pragma mark - Actions

- (void)selectPolicyAtIndex:(NSInteger)index
{
    EHILocationPolicy *policy = self.policies[index];

    self.router.transition
        .push(EHIScreenPolicyDetail).object(policy).start(nil);
}

@end
