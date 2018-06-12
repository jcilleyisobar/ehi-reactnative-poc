//
//  EHIProfilePaymentMethodModifyViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentMethodModifyViewController.h"
#import "EHIProfilePaymentMethodModifyViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIProfilePaymentMethodModifyActionsCell.h"
#import "EHIProfilePaymentAddCell.h"
#import "EHISectionHeader.h"
#import "EHIActivityIndicator.h"
#import "EHIProfilePaymentStatusCell.h"

@interface EHIProfilePaymentMethodModifyViewController () <EHIProfilePaymentMethodModifyActionsCellActions, EHIProfilePaymentAddCellActions>
@property (strong, nonatomic) EHIProfilePaymentMethodModifyViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIProfilePaymentMethodModifyViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentMethodModifyViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIProfilePaymentMethodModifySectionBilling) : EHIProfilePaymentMethodModifyActionsCell.class,
        @(EHIProfilePaymentMethodModifySectionCard)    : EHIProfilePaymentMethodModifyActionsCell.class,
        @(EHIProfilePaymentMethodModifySectionStatus)  : EHIProfilePaymentStatusCell.class,
        @(EHIProfilePaymentMethodModifySectionAddCard) : EHIProfilePaymentAddCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
        EHISectionHeaderModel *model = [self.viewModel headerForSection:section.index];
        if(model) {
            section.header.klass   = [EHISectionHeader class];
            section.header.model   = model;
            section.header.metrics = [self metricsForSection:section.index];
        }
    }
}

- (EHILayoutMetrics *)metricsForSection:(EHIProfilePaymentMethodModifySection)section
{
    EHILayoutMetrics *metrics = [EHISectionHeader.metrics copy];
    
    metrics.backgroundColor = [UIColor ehi_grayColor2];
    metrics.primaryFont     = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:14.0f];
    if(section == EHIProfilePaymentMethodModifySectionBilling) {
        // the fist section don't have the fancy divider
        metrics.fixedSize = (CGSize){.width = EHILayoutValueNil, .height = 36.0f};
    } else {
        metrics.fixedSize = (CGSize){.width = EHILayoutValueNil, .height = 46.0f};
    }
    
    return metrics;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfilePaymentMethodModifyViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateBillings:)];
    [MTRReactor autorun:self action:@selector(invalidateCards:)];
    [MTRReactor autorun:self action:@selector(invalidateStatus:)];

    EHIListDataSourceSection *add = self.collectionView.sections[EHIProfilePaymentMethodModifySectionAddCard];

    model.bind.map(@{
        source(model.title)          : dest(self, .title),
        source(model.addModel)       : dest(add, .model),
        source(model.isLoading)      : dest(self, .loadingIndicator.isAnimating)
    });
}

- (void)invalidateBillings:(MTRComputation *)computation
{
    NSArray *billings = self.viewModel.billingsModels;
    
    __weak __typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIProfilePaymentMethodModifySectionBilling].models = billings;
    } completion:nil];
}

- (void)invalidateCards:(MTRComputation *)computation
{
    NSArray *cards = self.viewModel.cardsModels;
    
    __weak __typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIProfilePaymentMethodModifySectionCard].models = cards;
    } completion:nil];
}

- (void)invalidateStatus:(MTRComputation *)computation
{
    EHIViewModel *status = self.viewModel.statusModel;
    
    __weak __typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIProfilePaymentMethodModifySectionStatus].model = status;
    } completion:nil];
}

# pragma mark - EHIProfilePaymentMethodModifyActionsCellActions

- (void)didTapEditPayment:(EHIProfilePaymentMethodModifyActionsCell *)cell
{
    [self performAction:EHIProfilePaymentMethodModifyActionEdit forCell:cell];
}

# pragma mark - EHIProfilePaymentAddCellActions

- (void)didTapAddCreditCard:(EHIProfilePaymentAddCell *)cell
{
    [self performAction:EHIProfilePaymentMethodModifyActionAddCard forCell:cell];
}

- (void)didTapDeletePayment:(EHIProfilePaymentMethodModifyActionsCell *)cell
{
    [self performAction:EHIProfilePaymentMethodModifyActionDelete forCell:cell];
}

- (void)performAction:(EHIProfilePaymentMethodModifyAction)action forCell:(EHICollectionViewCell *)cell
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
    
    [self.viewModel updatePaymentAtIndexPath:indexPath withAction:action];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenProfileEditPaymentMethods state:EHIScreenProfileEditPaymentMethods];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenProfileEditPaymentMethods;
}

@end
