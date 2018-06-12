//
//  EHIMenuViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuViewController.h"
#import "EHIMenuViewModel.h"
#import "EHIMenuAnimation.h"
#import "EHIMenuCell.h"
#import "EHIListCollectionView.h"
#import "EHISettings.h"
#import "EHIUserManager.h"
#import "EHIPromotionView.h"
#import "EHIMenuScreenCell.h"
#import "EHIMenuSecondaryCell.h"
#import "EHIMenuPromotionCell.h"

@interface EHIMenuViewController () <EHIListCollectionViewDelegate, EHIMenuCellActions, EHIMenuPromotionCellActions>
@property (strong, nonatomic) EHIMenuViewModel *viewModel;
@property (assign, nonatomic) CGFloat animationPercentComplete;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIMenuViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIMenuViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
  
    self.collectionView.preservesSelectionOnReload = YES;
    [self.collectionView.sections construct:@{
           @(EHIMenuSectionPromotion) : EHIMenuPromotionCell.class,
           @(EHIMenuSectionScreen)    : EHIMenuScreenCell.class,
           @(EHIMenuSectionSecondary) : EHIMenuSecondaryCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    // force a highlight since collection view throws away our selection
    [self highlightActiveIndexPath:nil];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIMenuViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(highlightActiveIndexPath:)];
    [MTRReactor autorun:self action:@selector(menuSelectedSameIndex:)];
    
    model.bind.map(@{
        source(model.menuItems) : dest(self, .collectionView.section.models),
    });
}

- (void)highlightActiveIndexPath:(MTRComputation *)computation
{
    for(NSIndexPath *path in self.collectionView.indexPathsForSelectedItems) {
        [self.collectionView deselectItemAtIndexPath:path animated:!computation.isFirstRun];
    }
    
    [self.collectionView selectItemAtIndexPath:self.viewModel.highlightedIndexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
}

- (void)menuSelectedSameIndex:(MTRComputation *)computation
{
    BOOL sameIndex = self.viewModel.selectedSameIndex;
    
    if(sameIndex) {
        [self ehi_performAction:@selector(menuSelectedSameIndex) withSender:self];
    }
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL shouldSelectItem = [self.viewModel shouldSelectItemAtIndex:indexPath.item];

    // if we're already selected, short circuit the selection process here
    if(shouldSelectItem && [collectionView.indexPathsForSelectedItems containsObject:indexPath]) {
        shouldSelectItem = NO;
        [self.viewModel selectItemAtIndexPath:indexPath];
    }
    
    return shouldSelectItem;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

# pragma mark - Interface Actions

- (void)didTriggerActionForCell:(EHIMenuCell *)cell
{
    NSIndexPath *selectedPath = [self.collectionView indexPathForCell:cell];
    [self collectionView:self.collectionView didSelectItemAtIndexPath:selectedPath];
}

# pragma mark - EHIMenuPromotionCellActions

- (void)didTapMenuPromotionCell:(EHIMenuPromotionCell *)sender
{
    [self.viewModel didTapPromotionGetStarted];
}

# pragma mark - Analytics

- (BOOL)automaticallyInvalidatesAnalyticsContext
{
    return NO;
}

- (void)prepareToUpdateAnalyticsContext
{    
    [EHIAnalytics changeScreen:EHIScreenMenu state:EHIScreenMenu];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)storyboardName
{
    return @"EHIMainStoryboard";
}

+ (NSString *)screenName
{
    return EHIScreenMenu;
}

@end
