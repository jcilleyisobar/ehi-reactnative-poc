//
//  EHIRewardsAboutTiersListCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersListCell.h"
#import "EHIRewardsAboutTiersListViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHITierDetailsView.h"

@interface EHIRewardsAboutTiersListCell ()
@property (strong, nonatomic) EHIRewardsAboutTiersListViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UIView *plusTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *plusTitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *plusTierTagView;
@property (weak  , nonatomic) IBOutlet UIImageView *plusCheckmarkImageView;
@property (weak  , nonatomic) IBOutlet UIImageView *plusArrowImageView;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *plusDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *silverTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *silverTitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *silverTierTagView;
@property (weak  , nonatomic) IBOutlet UIImageView *silverCheckmarkImageView;
@property (weak  , nonatomic) IBOutlet UIImageView *silverArrowImageView;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *silverDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *goldTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *goldTitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *goldTierTagView;
@property (weak  , nonatomic) IBOutlet UIImageView *goldCheckmarkImageView;
@property (weak  , nonatomic) IBOutlet UIImageView *goldArrowImageView;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *goldDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *platinumTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *platinumTitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *platinumTierTagView;
@property (weak  , nonatomic) IBOutlet UIImageView *platinumCheckmarkImageView;
@property (weak  , nonatomic) IBOutlet UIImageView *platinumArrowImageView;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *platinumDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *containerView;

// helpers
@property (copy  , nonatomic) NSArray *heights;
@property (copy  , nonatomic) NSArray *tiersDetails;
@property (copy  , nonatomic) NSDictionary *tiersMap;
@property (copy  , nonatomic) NSDictionary *tiersArrowsMap;
@property (copy  , nonatomic) NSDictionary *tiersCheckmarksMap;
@property (copy  , nonatomic) NSDictionary *tiersTagsMap;
@end

@implementation EHIRewardsAboutTiersListCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsAboutTiersListViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)awakeFromNib
{
    [super awakeFromNib];

    // hacky way to force the views to recalculate its intrinsic content size
    [self.plusDetailsView forceLayout];
    [self.silverDetailsView forceLayout];
    [self.goldDetailsView forceLayout];
    [self.platinumDetailsView forceLayout];
    
    self.tiersDetails = @[
        self.plusDetailsView,
        self.silverDetailsView,
        self.goldDetailsView,
        self.platinumDetailsView
    ];
    
    self.heights = @[
        @(CGRectGetHeight(self.plusDetailsView.frame)),
        @(CGRectGetHeight(self.silverDetailsView.frame)),
        @(CGRectGetHeight(self.goldDetailsView.frame)),
        @(CGRectGetHeight(self.platinumDetailsView.frame))
    ];

    [self expandCollapseSection:EHIRewardsAboutTiersListSectionPlus];
    [self expandCollapseSection:EHIRewardsAboutTiersListSectionSilver];
    [self expandCollapseSection:EHIRewardsAboutTiersListSectionGold];
    [self expandCollapseSection:EHIRewardsAboutTiersListSectionPlatinum];
    
    self.plusTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionPlus];
    self.plusTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionPlus];
    
    self.silverTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionSilver];
    self.silverTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionSilver];
    
    self.goldTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionGold];
    self.goldTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionGold];
    
    self.platinumTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionPlatinum];
    self.platinumTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionPlatinum];
    
    [self invalidateTierTag];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsAboutTiersListViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSection:)];

	model.bind.map(@{
        source(model.plusModel)     : dest(self, .plusDetailsView.viewModel),
        source(model.silverModel)   : dest(self, .silverDetailsView.viewModel),
        source(model.goldModel)     : dest(self, .goldDetailsView.viewModel),
        source(model.platinumModel) : dest(self, .platinumDetailsView.viewModel),
	});
}

- (void)invalidateTierTag
{
    EHIRewardsAboutTiersListSection current = self.viewModel.currentSection;
    
    self.tiersTagsMap.each(^(NSNumber *tier, UIView *view){
        if(current != tier.integerValue) {
            [view mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
            }];
        }
    });
    
    self.tiersCheckmarksMap.each(^(NSNumber *tier, UIView *view){
        if(current <= tier.integerValue) {
            [view mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
            }];
        }
    });
}

- (void)invalidateSection:(MTRComputation *)computation
{
    EHIRewardsAboutTiersListSection section = self.viewModel.selectedSection;

    [UIView animateWithDuration:0.3 animations:^{
        [self animateArrowAtSection:section];
        [self expandCollapseSection:section];
        [self ehi_performAction:@selector(rewardsAboutTierDidTapArrow:) withSender:self];
        [self layoutIfNeeded];
    }];
}

# pragma mark - Invalidation

- (void)expandCollapseSection:(EHIRewardsAboutTiersListSection)section
{
    BOOL expand      = [self.viewModel.tierStates[@(section).description] boolValue];
    NSNumber *height = expand ? self.heights[section] : @(0.0);
    UIView *view     = self.tiersMap[@(section)];
    
    view.alpha = expand ? 1.0f : 0.0f;
    [view mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(height).priority(MASLayoutPriorityRequired);
    }];
}

- (void)animateArrowAtSection:(EHIRewardsAboutTiersListSection)section
{
    BOOL isDown  = [self.viewModel.tierStates[@(section).description] boolValue];
    CGFloat rads = (isDown) ? M_PI : 0.0f;
    CGAffineTransform transform = CGAffineTransformRotate(CGAffineTransformIdentity, rads);
    
    UIImageView *imageView = self.tiersArrowsMap[@(section)];
    imageView.transform = transform;
}

# pragma mark - Actions

- (IBAction)didTapArrow:(UIControl *)sender
{
    [self.viewModel selectSection:sender.tag];
}

- (NSDictionary *)tiersMap
{
    if(!_tiersMap) {
        _tiersMap = @{
            @(EHIRewardsAboutTiersListSectionPlus)     : self.plusDetailsView,
            @(EHIRewardsAboutTiersListSectionSilver)   : self.silverDetailsView,
            @(EHIRewardsAboutTiersListSectionGold)     : self.goldDetailsView,
            @(EHIRewardsAboutTiersListSectionPlatinum) : self.platinumDetailsView
        };
    }
    
    return _tiersMap;
}

# pragma mark - Accessors

- (NSDictionary *)tiersArrowsMap
{
    if(!_tiersArrowsMap) {
        _tiersArrowsMap = @{
            @(EHIRewardsAboutTiersListSectionPlus)     : self.plusArrowImageView,
            @(EHIRewardsAboutTiersListSectionSilver)   : self.silverArrowImageView,
            @(EHIRewardsAboutTiersListSectionGold)     : self.goldArrowImageView,
            @(EHIRewardsAboutTiersListSectionPlatinum) : self.platinumArrowImageView
        };
    }
    
    return _tiersArrowsMap;
}

- (NSDictionary *)tiersCheckmarksMap
{
    if(!_tiersCheckmarksMap) {
        _tiersCheckmarksMap = @{
            @(EHIRewardsAboutTiersListSectionPlus)     : self.plusCheckmarkImageView,
            @(EHIRewardsAboutTiersListSectionSilver)   : self.silverCheckmarkImageView,
            @(EHIRewardsAboutTiersListSectionGold)     : self.goldCheckmarkImageView,
            @(EHIRewardsAboutTiersListSectionPlatinum) : self.platinumCheckmarkImageView
        };
    }
    
    return _tiersCheckmarksMap;
}

- (NSDictionary *)tiersTagsMap
{
    if(!_tiersTagsMap) {
        _tiersTagsMap  = @{
            @(EHIRewardsAboutTiersListSectionPlus)     : self.plusTierTagView,
            @(EHIRewardsAboutTiersListSectionSilver)   : self.silverTierTagView,
            @(EHIRewardsAboutTiersListSectionGold)     : self.goldTierTagView,
            @(EHIRewardsAboutTiersListSectionPlatinum) : self.platinumTierTagView
        };
    }
    
    return _tiersTagsMap;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
