
//
//  EHIRewardsBenefitsView.m
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsView.h"
#import "EHIRewardsBenefitsViewModel.h"
#import "EHIRewardsBannerView.h"
#import "EHIRestorableConstraint.h"

@interface EHIRewardsBenefitsView ()
@property (strong, nonatomic) EHIRewardsBenefitsViewModel *viewModel;
@property (strong, nonatomic) NSArray *benefitLabels;
@property (strong, nonatomic) NSArray *benefitHeights;
@property (weak  , nonatomic) IBOutlet EHIRewardsBannerView *plusBanner;
@property (weak  , nonatomic) IBOutlet UILabel *plusLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *plusHeight;
@property (weak  , nonatomic) IBOutlet EHIRewardsBannerView *silverBanner;
@property (weak  , nonatomic) IBOutlet UILabel *silverLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *silverHeight;
@property (weak  , nonatomic) IBOutlet EHIRewardsBannerView *goldBanner;
@property (weak  , nonatomic) IBOutlet UILabel *goldLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *goldHeight;
@property (weak  , nonatomic) IBOutlet EHIRewardsBannerView *platinumBanner;
@property (weak  , nonatomic) IBOutlet UILabel *platinumLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *platinumHeight;
@end

@implementation EHIRewardsBenefitsView

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsBenefitsViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // order by enum
    self.benefitLabels = @[self.plusLabel, self.silverLabel, self.goldLabel, self.platinumLabel];
    self.benefitHeights = @[self.plusHeight, self.silverHeight, self.goldHeight, self.platinumHeight];
    NSArray *banners = @[self.plusBanner, self.silverBanner, self.goldBanner, self.platinumBanner];
    
    // configure banner states
    banners.each(^(EHIRewardsBannerView *bannerView, int index) {
        bannerView.tier = index;
        bannerView.type = EHIRewardsBannerTypeBenefits;
        
        if(index < self.viewModel.tier) {
            bannerView.state = EHIRewardsBannerStateCompleted;
        } else if(index == self.viewModel.tier) {
            bannerView.state = EHIRewardsBannerStateCurrent;
        } else if(index == self.viewModel.tier + 1) {
            bannerView.state = EHIRewardsBannerStateNext;
        } else {
            bannerView.state = EHIRewardsBannerStateDefault;
        }
    });
    
    // setup constraints after labels have been sized properly
    dispatch_after_seconds(0.1, ^{
        [self updateConstraintsForSelectedTier:self.viewModel.selectedTier];
    });
}

- (void)updateConstraints
{
    [super updateConstraints];
    
    [self updateConstraintsForSelectedTier:self.viewModel.selectedTier];
}

//
// Helpers
//

- (void)updateConstraintsForSelectedTier:(EHIUserLoyaltyTier)tier
{
    self.benefitHeights.each(^(EHIRestorableConstraint *height, int index) {
        if(tier == index) {
            UILabel *tierLabel = self.benefitLabels[tier];
            height.constant = CGRectGetMaxY(tierLabel.frame) + EHILightPadding;
        } else {
            height.constant = EHIRestorableConstant;
        }
    });
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectedTier:)];
    
    model.bind.map(@{
        source(model.plusBenefits)     : dest(self, .plusLabel.attributedText),
        source(model.silverBenefits)   : dest(self, .silverLabel.attributedText),
        source(model.goldBenefits)     : dest(self, .goldLabel.attributedText),
        source(model.platinumBenefits) : dest(self, .platinumLabel.attributedText),
    });
}

//
// Helpers
//

- (void)invalidateSelectedTier:(MTRComputation *)computation
{
    depend(self.viewModel.selectedTier);
    
    if(!computation.isFirstRun) {
        [self setNeedsUpdateConstraints];
        [self ehi_performAction:@selector(rewardsBenefitsViewDidLayoutTier:) withSender:self];
    }
}

# pragma mark - Actions

- (IBAction)didTapBannerView:(UIGestureRecognizer *)sender
{
    // notify view model of update
    self.viewModel.selectedTier = sender.view.tag;
}

# pragma mark - Replaceable

+ (BOOL)isReplaceable
{
    return YES;
}

@end
