//
//  EHIPromotionDetailsViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsViewController.h"
#import "EHIPromotionDetailsViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIPromotionDetailsImageCell.h"
#import "EHIPromotionDetailsTitleCell.h"
#import "EHIPromotionDetailsBulletItemCell.h"
#import "EHIPromotionDetailsActionCell.h"
#import "EHIPromotionDetailsPolicyCell.h"

@interface EHIPromotionDetailsViewController () <EHIPromotionDetailsActionCellActions>
@property (strong, nonatomic) EHIPromotionDetailsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIPromotionDetailsViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
           @(EHIPromotionDetailsSectionImage)   : EHIPromotionDetailsImageCell.class,
           @(EHIPromotionDetailsSectionTitle)   : EHIPromotionDetailsTitleCell.class,
           @(EHIPromotionDetailsSectionBullets) : EHIPromotionDetailsBulletItemCell.class,
           @(EHIPromotionDetailsSectionAction)  : EHIPromotionDetailsActionCell.class,
           @(EHIPromotionDetailsSectionPolicy)  : EHIPromotionDetailsPolicyCell.class,
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = section.index != EHIPromotionDetailsSectionImage;
    }
}

# pragma mark - EHIPromotionDetailsActionCellActions

- (void)didTapWeekendSpecialStartReservation
{
    [self.viewModel didTapWeekendSpecialStartReservation];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsViewModel *)model
{
    [super registerReactions:model];

    EHIListDataSourceSection *imageSection   = self.collectionView.sections[EHIPromotionDetailsSectionImage];
    EHIListDataSourceSection *titleSection   = self.collectionView.sections[EHIPromotionDetailsSectionTitle];
    EHIListDataSourceSection *bulletsSection = self.collectionView.sections[EHIPromotionDetailsSectionBullets];
    EHIListDataSourceSection *actionSection  = self.collectionView.sections[EHIPromotionDetailsSectionAction];
    EHIListDataSourceSection *policySection  = self.collectionView.sections[EHIPromotionDetailsSectionPolicy];
    
    model.bind.map(@{
        source(model.navigationTitle) : dest(self, .title),
        source(model.imageModel)      : dest(imageSection, .model),
        source(model.titleModel)      : dest(titleSection, .model),
        source(model.bulletModels)    : dest(bulletsSection, .models),
        source(model.actionModel)     : dest(actionSection, .model),
        source(model.policyModel)     : dest(policySection, .model),
    });
}


# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenPromotionDetails state:EHIScreenPromotionDetails];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenPromotionDetails;
}

@end
