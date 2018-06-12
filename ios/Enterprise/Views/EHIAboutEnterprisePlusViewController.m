//
//  EHIAboutEnterprisePlusViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusViewController.h"
#import "EHIAboutEnterprisePlusViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIAboutEnterprisePlusHeaderCell.h"
#import "EHIAboutEnterprisePlusPointsCell.h"
#import "EHIAboutEnterprisePlusTierHeaderCell.h"
#import "EHIAboutEnterprisePlusTierCell.h"
#import "EHIAboutEnterprisePlusFooterCell.h"
#import "EHIRewardsLegalCell.h"

@interface EHIAboutEnterprisePlusViewController ()
@property (strong, nonatomic) EHIAboutEnterprisePlusViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIAboutEnterprisePlusViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutEnterprisePlusViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self.collectionView.sections construct:@{
        @(EHIAboutEnterprisePlusSectionHeader)      : EHIAboutEnterprisePlusHeaderCell.class,
        @(EHIAboutEnterprisePlusSectionPoints)      : EHIAboutEnterprisePlusPointsCell.class,
        @(EHIAboutEnterprisePlusSectionTiersHeader) : EHIAboutEnterprisePlusTierHeaderCell.class,
        @(EHIAboutEnterprisePlusSectionTiers)       : EHIAboutEnterprisePlusTierCell.class,
        @(EHIAboutEnterprisePlusSectionFooter)      : EHIAboutEnterprisePlusFooterCell.class,
        @(EHIAboutEnterprisePlusSectionLegal)       : EHIRewardsLegalCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutEnterprisePlusViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *header      = self.collectionView.sections[EHIAboutEnterprisePlusSectionHeader];
    EHIListDataSourceSection *points      = self.collectionView.sections[EHIAboutEnterprisePlusSectionPoints];
    EHIListDataSourceSection *tiersHeader = self.collectionView.sections[EHIAboutEnterprisePlusSectionTiersHeader];
    EHIListDataSourceSection *tiers       = self.collectionView.sections[EHIAboutEnterprisePlusSectionTiers];
    EHIListDataSourceSection *footer      = self.collectionView.sections[EHIAboutEnterprisePlusSectionFooter];
    EHIListDataSourceSection *legal       = self.collectionView.sections[EHIAboutEnterprisePlusSectionLegal];
    
    model.bind.map(@{
        source(model.title)            : dest(self, .title),
        source(model.headerModel)      : dest(header, .model),
        source(model.pointsModels)     : dest(points, .models),
        source(model.tiersHeaderModel) : dest(tiersHeader, .model),
        source(model.tiersModel)       : dest(tiers, .model),
        source(model.footerModel)      : dest(footer, .model),
        source(model.legalModel)       : dest(legal, .model)
    });
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenAboutEnterprisePlus state:EHIScreenAboutEnterprisePlus];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenAboutEnterprisePlus;
}

@end
