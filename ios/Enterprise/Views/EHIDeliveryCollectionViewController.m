//
//  EHIDeliveryCollectionsViewController.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionViewController.h"
#import "EHIDeliveryCollectionViewModel.h"
#import "EHIDeliveryCollectionHeaderCell.h"
#import "EHIDeliveryCollectionSectionHeader.h"
#import "EHIFormFieldCell.h"
#import "EHISectionHeader.h"
#import "EHIListCollectionView.h"
#import "EHIModel.h"
#import "EHIBarButtonItem.h"
#import "EHIActionButton.h"
#import "EHIActivityIndicator.h"
#import "EHIRequiredInfoCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDeliveryCollectionViewController () <EHIFormFieldCellActions>
@property (strong, nonatomic) EHIDeliveryCollectionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *saveChangesButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIDeliveryCollectionViewController

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDeliveryCollectionViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // configure the collection view
    EHIListDataSourceSection *header = self.collectionView.sections[EHIDeliveryCollectionSectionInformation];
    header.klass = EHIDeliveryCollectionHeaderCell.class;
    header.model = [EHIModel placeholder];

    [self.collectionView.sections construct:@{
        @(EHIDeliveryCollectionSectionDeliveryToggle)       : EHIFormFieldCell.class,
        @(EHIDeliveryCollectionSectionRequiredInfo)         : EHIRequiredInfoCell.class,
        @(EHIDeliveryCollectionSectionDeliveryAddress)      : EHIFormFieldCell.class,
        @(EHIDeliveryCollectionSectionCollectionToggle)     : EHIFormFieldCell.class,
        @(EHIDeliveryCollectionSectionCollectionAddress)    : EHIFormFieldCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        Class klass = [self headerClassForSection:section.index];
        
        section.header.klass = klass;
        section.header.isDynamicallySized = klass == EHIDeliveryCollectionSectionHeader.class;
        section.header.model = [self.viewModel headerForSection:section.index];
    }
}

- (BOOL)needsBottomLine
{
    return YES;
}

//
// Helpers
//

- (nullable Class)headerClassForSection:(EHIDeliveryCollectionSection)section
{
    switch(section) {
        case EHIDeliveryCollectionSectionDeliveryToggle:
        case EHIDeliveryCollectionSectionCollectionToggle:
            return EHISectionHeader.class;
        case EHIDeliveryCollectionSectionDeliveryAddress:
        case EHIDeliveryCollectionSectionRequiredInfo:
            return EHIDeliveryCollectionSectionHeader.class;
        case EHIDeliveryCollectionSectionCollectionAddress:
        default:
            return nil;
    }
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDeliveryCollectionViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *deliveryToggle = self.collectionView.sections[EHIDeliveryCollectionSectionDeliveryToggle];
    EHIListDataSourceSection *collectionToggle = self.collectionView.sections[EHIDeliveryCollectionSectionCollectionToggle];
    
    [MTRReactor autorun:self action:@selector(invalidateDeliverySection:)];
    [MTRReactor autorun:self action:@selector(invalidateCollectionSection:)];

    EHIListDataSourceSection *required = self.collectionView.sections[EHIDeliveryCollectionSectionRequiredInfo];
    
    model.bind.map(@{
        source(model.title) : dest(self, .title),
        source(model.deliveryToggleViewModel)   : dest(deliveryToggle, .model),
        source(model.collectionToggleViewModel) : dest(collectionToggle, .model),
        source(model.saveButtonTitle)           : dest(self, .saveChangesButton.ehi_title),
        source(model.isLoading)                 : dest(self, .loadingIndicator.isAnimating),
        source(model.requiredModel)             : dest(required, .model),
        source(model.invalidForm)               : ^(NSNumber *isDisabled) {
            self.saveChangesButton.isFauxDisabled = isDisabled.boolValue;
            [self invalidateViewBelowSafeArea:isDisabled.boolValue];
        },
    });
}

//
// Helpers
//

- (void)invalidateDeliverySection:(MTRComputation *)computation
{
    NSArray *viewModels = self.viewModel.deliveryAddressSectionViewModels;
    
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        self.collectionView.sections[EHIDeliveryCollectionSectionDeliveryAddress].models = viewModels;
    } completion:nil];
}

- (void)invalidateCollectionSection:(MTRComputation *)computation
{
    NSArray *viewModels = self.viewModel.collectionAddressSectionViewModels;
    
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        self.collectionView.sections[EHIDeliveryCollectionSectionCollectionAddress].models = viewModels;
    } completion:nil];
}

# pragma mark - Actions

- (IBAction)didTapSaveChangesButton:(id)sender
{
    [self.viewModel commitDeliveryCollection];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (UIButton *)keyboardSupportedActionButton
{
    return self.saveChangesButton;
}

# pragma mark - Analytics

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionDelivery handler:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationDeliveryCollection;
}
@end

NS_ASSUME_NONNULL_END
