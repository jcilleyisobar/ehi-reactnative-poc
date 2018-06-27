//
//  EHIKeyFactsContentViewModel.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsContentViewModel.h"
#import "EHILocationPolicy.h"
#import "EHICarClassExtra.h"
#import "EHIViewModel_Subclass.h"
#import "NSAttributedString+Construction.h"

@interface EHIKeyFactsContentViewModel()
@property (strong, nonatomic) EHICarClassExtra *extra;
@property (strong, nonatomic) EHILocationPolicy *policy;
@property (strong, nonatomic) EHILocationPolicy *exclusion;
@end

@implementation EHIKeyFactsContentViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _exclusionText = EHILocalizedString(@"key_facts_protections_exclusions", @"View Exclusions", @"");
        
        if ([model isKindOfClass:[EHILocationPolicy class]]) {
            [self updateWithLocationPolicy:(EHILocationPolicy *)model];
        } else if ([model isKindOfClass:[EHICarClassExtra class]]) {
            [self updateWithExtra:(EHICarClassExtra *)model];
        } else if ([model isKindOfClass:[NSString class]]) {
            [self updateWithString:(NSString *)model];
        }
    }
    return self;
}

- (void)updateWithLocationPolicy:(EHILocationPolicy *)policy
{
    EHICarClassExtra *extra = policy.extra;
    if (extra) {
        self.extra = extra;
    }
    self.linkText = EHIAttributedStringBuilder.new.text(policy.codeDetails).string;
    
    self.policy = policy;
    
    if (policy.exclusionPolicies.count) {
        self.hasExclusion = YES;
        self.exclusion = policy.exclusionPolicy;
    }
}

- (void)updateWithExtra:(EHICarClassExtra *)extra
{
    self.linkText = EHIAttributedStringBuilder.new
        .text(extra.name).fontStyle(EHIFontStyleBold, 18.0f).color([UIColor ehi_greenColor])
    .space.space.appendText(extra.rateDescriptionWithMax).fontStyle(EHIFontStyleRegular, 16.0f).color([UIColor ehi_blackColor]).string;
    
    self.extra = extra;
}

- (void)updateWithString:(NSString *)string
{
    self.contentText = string;
}

#pragma mark - Actions

- (void)selectLink
{
    self.router.transition
        .push(EHIScreenPolicyDetail).object(self.policy ?: self.extra).start(nil);
}

- (void)selectExclusions
{
    self.router.transition
        .push(EHIScreenPolicyDetail).object(self.exclusion).start(nil);
}

#pragma mark - Generators

+ (instancetype)modelWithContent:(NSString *)content
{
    EHIKeyFactsContentViewModel *model = [self new];
    model.contentText = content;
    
    return model;
}

@end
