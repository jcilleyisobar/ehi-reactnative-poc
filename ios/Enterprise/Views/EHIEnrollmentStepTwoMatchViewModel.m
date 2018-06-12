//
//  EHIEnrollmentStepTwoMatchViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 05/01/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentStepTwoMatchViewModel.h"
#import "EHIAddress.h"

@interface EHIEnrollmentStepTwoMatchViewModel ()
@property (copy, nonatomic) NSString *formattedAddress;
@end

@implementation EHIEnrollmentStepTwoMatchViewModel

- (void)updateWithModel:(EHIAddress *)model
{
    [super updateWithModel:model];
    
    self.formattedAddress = model.addressLines.firstObject;
}

# pragma mark - Accessors

- (NSString *)stepTitle
{
    NSString *step = EHILocalizedString(@"enroll_step", @"Step #{step} of #{step_count}", @"");
    return [step ehi_applyReplacementMap:@{
        @"step"       : @(2).description,
        @"step_count" : @(3).description
    }];
}

- (NSString *)matchMessage
{
    return EHILocalizedString(@"enroll_keep_this_address", @"It looks like you've rented from us before.\nWould you like to use the address we have on file from that rental?", @"");
}

- (NSString *)addressTitle
{
    return EHILocalizedString(@"enroll_current_address", @"ADDRESS ON FILE", @"");
}

- (NSString *)changeTitle
{
    return EHILocalizedString(@"enroll_step_2_match_address_change_title", @"CHANGE", @"");
}

- (NSString *)keepTitle
{
    return EHILocalizedString(@"enroll_step_2_match_address_keep_title", @"KEEP", @"");
}

# pragma mark - Actions

- (void)didTapChange
{
    [self trackAction:EHIAnalyticsEnrollmentChangeAddress];
}

- (void)didTapKeep
{
    [self trackAction:EHIAnalyticsEnrollmentKeepAddress];
}

# pragma mark - Analytics

- (void)trackAction:(NSString *)action
{
    [EHIAnalytics trackAction:action handler:nil];
}

@end
