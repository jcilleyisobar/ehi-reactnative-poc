//
//  EHIRewardsAboutTiersViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersViewController.h"
#import "EHIRewardsAboutTiersViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIRewardsAboutTiersHeaderCell.h"
#import "EHIRewardsAboutTiersListCell.h"
#import "EHIRewardsBenefitsFooterCell.h"
#import "EHIRewardsLegalCell.h"

@interface EHIRewardsAboutTiersViewController () <EHIListCollectionViewDelegate, EHIRewardsAboutTiersListCellActions>
@property (strong, nonatomic) EHIRewardsAboutTiersViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIRewardsAboutTiersViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsAboutTiersViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self.collectionView.sections construct:@{
        @(EHIRewardsAboutTiersSectionHeader) : EHIRewardsAboutTiersHeaderCell.class,
        @(EHIRewardsAboutTiersSectionTiers)  : EHIRewardsAboutTiersListCell.class,
        @(EHIRewardsAboutTiersSectionFooter) : EHIRewardsBenefitsFooterCell.class,
        @(EHIRewardsAboutTiersSectionLegal)  : EHIRewardsLegalCell.class,

    }];

    self.collectionView.sections.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsAboutTiersViewModel *)model
{
    [super registerReactions:model];

    EHIListDataSourceSection *header = self.collectionView.sections[EHIRewardsAboutTiersSectionHeader];
    EHIListDataSourceSection *tiers  = self.collectionView.sections[EHIRewardsAboutTiersSectionTiers];
    EHIListDataSourceSection *footer = self.collectionView.sections[EHIRewardsAboutTiersSectionFooter];
    EHIListDataSourceSection *legal  = self.collectionView.sections[EHIRewardsAboutTiersSectionLegal];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .title),
        source(model.headerModel) : dest(header, .model),
        source(model.tiersModel)  : dest(tiers, .model),
        source(model.footerModel) : dest(footer, .model),
        source(model.legalModel)  : dest(legal, .model)
    });
}

# pragma mark - EHIRewardsAboutTiersListCellActions

- (void)rewardsAboutTierDidTapArrow:(EHIRewardsAboutTiersListCell *)sender
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenRewardsAboutTiers state:EHIScreenRewardsAboutTiers];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRewardsAboutTiers;
}

@end
