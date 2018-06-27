//
//  EHIAboutPointsViewController.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsViewController.h"
#import "EHIAboutPointsHeaderCell.h"
#import "EHIAboutPointsRedeemCell.h"
#import "EHIAboutPointsViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIAboutPointsReusableCell.h"
#import "EHIRewardsBenefitsFooterCell.h"

@interface EHIAboutPointsViewController () <UICollectionViewDelegate>
@property (weak, nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (strong, nonatomic) EHIAboutPointsViewModel *viewModel;
@end

@implementation EHIAboutPointsViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutPointsViewModel new];
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
        @(EHIAboutPointsSectionHeader) : EHIAboutPointsHeaderCell.class,
        @(EHIAboutPointsSectionRedeem) : EHIAboutPointsRedeemCell.class,
        @(EHIAboutPointsSectionPoints) : EHIAboutPointsReusableCell.class,
        @(EHIAboutPointsSectionFooter) : EHIRewardsBenefitsFooterCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutPointsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *headerSection = self.collectionView.sections[EHIAboutPointsSectionHeader];
    EHIListDataSourceSection *redeemSection = self.collectionView.sections[EHIAboutPointsSectionRedeem];
    EHIListDataSourceSection *pointsSection = self.collectionView.sections[EHIAboutPointsSectionPoints];
    EHIListDataSourceSection *footerSection = self.collectionView.sections[EHIAboutPointsSectionFooter];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .title),
        source(model.headerModel)    : dest(headerSection, .model),
        source(model.redeemModel)    : dest(redeemSection, .model),
        source(model.reusableModels) : dest(pointsSection, .models),
        source(model.footerModel)    : dest(footerSection, .model)
    });
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenAboutPointsScreen state:EHIScreenAboutPointsScreen];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenAboutPointsScreen;
}


@end
