//
//  EHIRewardsBenefitsAuthViewController.m
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsAuthViewController.h"
#import "EHIRewardsBenefitsAuthViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIRewardsBenefitsHeaderCell.h"
#import "EHIRewardsBenefitsPointsCell.h"
#import "EHIRewardsBenefitsStatusCell.h"
#import "EHIRewardsBenefitsTierGaugeCell.h"
#import "EHIRewardsBenefitsFooterCell.h"
#import "EHISectionHeader.h"
#import "EHIRewardsLegalCell.h"

@interface EHIRewardsBenefitsAuthViewController () <UICollectionViewDelegate>
@property (strong, nonatomic) EHIRewardsBenefitsAuthViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIRewardsBenefitsAuthViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsBenefitsAuthViewModel new];
    }
    
    return self;
}

# pragma mark- View Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionView];
}

- (void)configureCollectionView
{
    [self.collectionView.sections construct:@{
        @(EHIRewardsBenefitsSectionHeader)    : EHIRewardsBenefitsHeaderCell.class,
        @(EHIRewardsBenefitsSectionsPoints)   : EHIRewardsBenefitsPointsCell.class,
        @(EHIRewardsBenefitsSectionsStatus)   : EHIRewardsBenefitsStatusCell.class,
        @(EHIRewardsBenefitsSectionNextTier)  : EHIRewardsBenefitsTierGaugeCell.class,
        @(EHIRewardsBenefitsSectionFooter)    : EHIRewardsBenefitsFooterCell.class,
        @(EHIRewardsBenefitsSectionLegal)     : EHIRewardsLegalCell.class,
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.header.klass = EHISectionHeader.class;
        section.header.model = [self.viewModel headerForSection:section.index];
        section.isDynamicallySized = YES;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsAuthViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *headerSection  = self.collectionView.sections[EHIRewardsBenefitsSectionHeader];
    EHIListDataSourceSection *pointsSection  = self.collectionView.sections[EHIRewardsBenefitsSectionsPoints];
    EHIListDataSourceSection *statusSection  = self.collectionView.sections[EHIRewardsBenefitsSectionsStatus];
    EHIListDataSourceSection *tierSection    = self.collectionView.sections[EHIRewardsBenefitsSectionNextTier];
    EHIListDataSourceSection *footerSection  = self.collectionView.sections[EHIRewardsBenefitsSectionFooter];
    EHIListDataSourceSection *legalSection   = self.collectionView.sections[EHIRewardsBenefitsSectionLegal];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .title),
        source(model.headerModel)    : dest(headerSection, .model),
        source(model.pointsModel)    : dest(pointsSection, .model),
        source(model.statusModel)    : dest(statusSection, .model),
        source(model.tierGaugeModel) : dest(tierSection, .model),
        source(model.footerModel)    : dest(footerSection, .model),
        source(model.legalModel)     : dest(legalSection, .model)
    });
    
    [MTRReactor autorun:self action:@selector(invalidateHeaders:)];
}

- (void)invalidateHeaders:(MTRComputation *)computation
{
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        EHISectionHeaderModel *headerModel = [self.viewModel headerForSection:section.index];
        section.header.isDynamicallySized = section.index != EHIRewardsBenefitsSectionsPoints;
        
        if(headerModel) {
            section.header.klass = [EHISectionHeader class];
            section.header.model = headerModel;
            section.header.metrics = [self metricForSection:section.index];
        }
    }
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}
    
# pragma mark - Layout
 
- (EHILayoutMetrics *)metricForSection:(EHIRewardsBenefitsSection)section
{
    EHILayoutMetrics *metrics = [EHISectionHeader.metrics copy];
    
    if(section == EHIRewardsBenefitsSectionsPoints) {
        metrics.fixedSize = (CGSize){.width = EHILayoutValueNil, .height = 28.0f};
    }
    
    return metrics;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenRewardsBenefitsAuth state:EHIScreenRewardsBenefitsAuth];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRewardsBenefitsAuth;
}

@end
