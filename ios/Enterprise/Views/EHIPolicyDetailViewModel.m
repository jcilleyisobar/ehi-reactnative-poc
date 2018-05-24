//
//  EHIPolicyInfoDetailsViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPolicyDetailViewModel.h"
#import "EHILocationPolicy.h"
#import "EHICarClassExtra.h"

@interface EHIPolicyDetailViewModel ()
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *name;
@property (copy, nonatomic) NSString *details;
@end

@implementation EHIPolicyDetailViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"policy_details_title", @"Policy Information", @"Title for the policy detail screen");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHILocationPolicy class]]) {
        [self updateWithPolicy:(EHILocationPolicy *)model];
    } else if ([model isKindOfClass:[EHICarClassExtra class]]) {
        [self updateWithExtra:(EHICarClassExtra *)model];
    }
}

- (void)updateWithPolicy:(EHILocationPolicy *)policy
{
    self.name = policy.codeDetails;
    self.details = policy.text;
}

- (void)updateWithExtra:(EHICarClassExtra *)extra
{
    self.title = extra.name;
    self.name = extra.name;
    self.details = extra.longDetails;
}

@end
