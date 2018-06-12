//
//  EHIOnboardingBenefitsView.m
//  Enterprise
//
//  Created by Stu Buchbinder on 1/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIOnboardingBenefitsView.h"
#import "EHIOnboardingBenefitsViewModel.h"
#import "EHIButton.h"

@interface EHIOnboardingBenefitsView ()

@property (strong, nonatomic) EHIOnboardingBenefitsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *title;
@property (weak  , nonatomic) IBOutlet EHIButton *benefitButton1;
@property (weak  , nonatomic) IBOutlet EHIButton *benefitButton2;
@property (weak  , nonatomic) IBOutlet EHIButton *benefitButton3;

@end

@implementation EHIOnboardingBenefitsView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIOnboardingBenefitsViewModel new];
    }
    return self;
}

- (void)registerReactions:(EHIOnboardingBenefitsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateText:)];
}

- (void)invalidateText:(MTRComputation *)computation
{
    self.title.text = self.viewModel.title;
    
    NSArray *benefits = self.viewModel.benefits;
    self.benefitButton1.ehi_title = [benefits ehi_safelyAccess:0];
    self.benefitButton2.ehi_title = [benefits ehi_safelyAccess:1];
    self.benefitButton3.ehi_title = [benefits ehi_safelyAccess:2];
}

@end
