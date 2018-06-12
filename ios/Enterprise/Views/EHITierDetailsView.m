//
//  EHITierDetailsView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITierDetailsView.h"
#import "EHITierDetailsViewModel.h"

@interface EHITierDetailsView ()
@property (strong, nonatomic) EHITierDetailsViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
// goals column
@property (weak, nonatomic) IBOutlet UILabel *goalTitleLabel;
@property (weak, nonatomic) IBOutlet EHIView *goalsContainerView;

//  benefits column
@property (weak, nonatomic) IBOutlet EHIView *benefitsContainerView;
@property (weak, nonatomic) IBOutlet UILabel *benefitsTitleLabel;
@end

@implementation EHITierDetailsView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITierDetailsViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITierDetailsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.goalTitle)     : dest(self, .goalTitleLabel.text),
        source(model.goalsModel)    : dest(self, .goalsContainerView.viewModel),
        source(model.benefitsTitle) : dest(self, .benefitsTitleLabel.text),
        source(model.benefitsModel) : dest(self, .benefitsContainerView.viewModel),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    [self.goalsContainerView forceLayout];
    [self.benefitsContainerView forceLayout];
    
    CGFloat benefitsHeight = CGRectGetMaxY(self.benefitsContainerView.frame);
    CGFloat goalsHeight    = CGRectGetMaxY(self.goalsContainerView.frame);
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = MAX(benefitsHeight, goalsHeight)
    };
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
