//
//  EHISelectPaymentViewController.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISelectPaymentViewController.h"
#import "EHISelectPaymentViewModel.h"
#import "EHIListCollectionView.h"
#import "EHISelectPaymentItemCell.h"
#import "EHISelectPaymentFooterCell.h"
#import "EHIProfilePaymentAddCell.h"

@interface EHISelectPaymentViewController () <EHISelectPaymentItemCellActions, EHISelectPaymentFooterCellActions,EHIProfilePaymentAddCellActions>

@property (strong, nonatomic) EHISelectPaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHISelectPaymentViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISelectPaymentViewModel new];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHISelectPaymentSectionCards)     : EHISelectPaymentItemCell.class,
        @(EHISelectPaymentSectionAddCard)   : EHIProfilePaymentAddCell.class,
        @(EHISelectPaymentSectionFooter)    : EHISelectPaymentFooterCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.handler = attributes.handler;
}

# pragma mark - Reactions

- (void)registerReactions:(EHISelectPaymentViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidatePaymentMethods:)];
    
    EHIListDataSourceSection *addCard = self.collectionView.sections[EHISelectPaymentSectionAddCard];
    EHIListDataSourceSection *footer  = self.collectionView.sections[EHISelectPaymentSectionFooter];
    
    model.bind.map(@{
        source(model.title)           : dest(self, .title),
        source(model.addViewModel)    : dest(addCard, .model),
        source(model.footerViewModel) : dest(footer, .model)
    });
}

- (void)invalidatePaymentMethods:(MTRComputation *)computation
{
    NSArray *models = self.viewModel.cardsModels;
    
    __weak __typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHISelectPaymentSectionCards].models = models;
    } completion:nil];
}

# pragma mark - EHISelectPaymentItemCellActions

- (void)didTapPaymentToggle:(EHISelectPaymentItemCell *)cell
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
    [self.viewModel selectPaymentMethodAtIndexPath:indexPath];
}

- (void)didTapContinue:(EHISelectPaymentFooterCell *)cell
{
    [self.viewModel commitPaymentMethod];
}

# pragma mark - EHIProfilePaymentAddCellActions

- (void)didTapAddCreditCard:(EHIProfilePaymentAddCell *)cell
{
    [self.viewModel addCreditCard];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSelectPayment state:EHIScreenSelectPayment];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSelectPayment;
}

@end
