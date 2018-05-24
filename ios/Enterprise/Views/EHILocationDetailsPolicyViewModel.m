//
//  EHILocationDetailsPolicyViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsPolicyViewModel.h"
#import "EHILocationPolicy.h"

@interface EHILocationDetailsPolicyViewModel ()
@property (copy, nonatomic) NSString *title;
@end

@implementation EHILocationDetailsPolicyViewModel

- (void)updateWithModel:(EHILocationPolicy *)policy
{
    [super updateWithModel:policy];
    self.title = policy.isPlaceholder
        ? EHILocalizedString(@"location_details_more_policies", @"MORE POLICIES", @"Title for 'More Policies' location detail cell")
        : policy.codeDetails;
}

@end
