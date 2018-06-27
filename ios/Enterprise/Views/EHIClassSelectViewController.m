//
//  EHIClassSelectViewController.m
//  Enterprise
//
//  Created by mplace on 3/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectViewController.h"
#import "EHIClassSelectViewModel.h"
#import "EHIExtrasViewController.h"
#import "EHIInformationBannerCell.h"
#import "EHIRedemptionPointsCell.h"
#import "EHIClassSelectActiveFilterCell.h"
#import "EHIClassSelectDiscountCell.h"
#import "EHIClassSelectFootnoteCell.h"
#import "EHITermsAndConditionsCell.h"
#import "EHICarClassCell.h"
#import "EHIListCollectionView.h"
#import "EHIClassSelectLayout.h"
#import "EHIClassSelectLayoutDelegate.h"
#import "EHIActivityIndicator.h"
#import "EHIBarButtonItem.h"
#import "EHIPaymentOptionViewController.h"
#import "EHICurrencyDiffersCell.h"

@interface EHIClassSelectViewController () <EHIRedemptionPointsCellActions, EHIClassSelectLayoutDelegate, EHIActiveFilterBannerActions, EHIListCollectionViewDelegate, UICollectionViewDelegate>
@property (strong, nonatomic) EHIClassSelectViewModel *viewModel;
@property (assign, nonatomic) BOOL isTogglingRedemption;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
// animation properties
@property (assign, nonatomic) BOOL isEnteringViaExtras;
@property (assign, nonatomic) BOOL isEnteringViaRates;
@property (assign, nonatomic) CGFloat lastTranslationYForCells;
@end

@implementation EHIClassSelectViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // set up the collection view sections
    [self configureCollectionView];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    // reset
    self.isEnteringViaExtras = NO;
    self.isEnteringViaRates = NO;
    
    /*
     Due to -animationsForTransitionToViewController short circuit when coming back
     from extras, the transition ends before animations are complete. Let's stop the user
     from messing with the screen until the entire transition completes. This coincidentally
     prevents navigation errors from users mashing the back button while the transition is running.
     */
    dispatch_after_seconds(EHIClassSelectAnimationPhase1Duration, ^{
        self.navigationController.view.userInteractionEnabled = YES;
    });
}

- (BOOL)needsBottomLine
{
    return NO;
}

- (void)configureCollectionView
{
    self.collectionView.preservesSelectionOnReload = YES;
    
    // build sections
    [self.collectionView.sections construct:@{
        @(EHIClassSelectSectionRedemption)      : EHIRedemptionPointsCell.class,
        @(EHIClassSelectSectionBanner)          : EHIInformationBannerCell.class,
        @(EHIClassSelectSectionActiveFilter)    : EHIClassSelectActiveFilterCell.class,
        @(EHIClassSelectSectionDiscount)        : EHIClassSelectDiscountCell.class,
        @(EHIClassSelectSectionCurrencyDiffers) : EHICurrencyDiffersCell.class,
        @(EHIClassSelectSectionCarClasses)      : EHICarClassCell.class,
        @(EHIClassSelectSectionFootnotes)       : EHIClassSelectFootnoteCell.class,
        @(EHIClassSelectTermsAndConditions)     : EHITermsAndConditionsCell.class,
     }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHIClassSelectSectionCarClasses) {
        [self.viewModel selectCarClassAtIndex:indexPath.item];
    } else if (indexPath.section == EHIClassSelectTermsAndConditions) {
        [self.viewModel showTermsAndConditions];
    }
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGFloat topInset = section == (EHIClassSelectSectionCarClasses && self.viewModel.shouldInsetCarClassSection) || section == EHIClassSelectTermsAndConditions ? 10.f : 0.f;
    
    return (UIEdgeInsets) {
        .top = topInset
    };
}

# pragma mark - EHIRedemptionPointsCellActions

- (void)didTapActionButtonForRedemptionPointsCell:(EHIRedemptionPointsCell *)cell
{
    cell.isLoading = YES;
    self.isTogglingRedemption = YES;
    
    [self.collectionView ehi_invalidateLayoutAnimated:YES completion:^(BOOL finished) {
        cell.isLoading = NO;
        self.isTogglingRedemption = NO;
    }];
}

# pragma mark - EHIActiveFilterBannerActions

- (void)didTapClearButtonForFilterBanner:(EHIActiveFilterBanner *)banner
{
    [self.viewModel clearFilters];
}

# pragma mark - EHICarClassCellActions

- (void)didTapDetailsButtonForCarClassCell:(EHICarClassCell *)cell
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
    
    [self.viewModel showDetailsForCarClassAtIndex:indexPath.item];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectViewModel *)model
{
    [super registerReactions:model];

    // retrieve collection view sections
    EHIListDataSourceSection *redemption   = self.collectionView.sections[EHIClassSelectSectionRedemption];
    EHIListDataSourceSection *banner       = self.collectionView.sections[EHIClassSelectSectionBanner];
    EHIListDataSourceSection *carClasses   = self.collectionView.sections[EHIClassSelectSectionCarClasses];
    EHIListDataSourceSection *activeFilter = self.collectionView.sections[EHIClassSelectSectionActiveFilter];
    EHIListDataSourceSection *discount     = self.collectionView.sections[EHIClassSelectSectionDiscount];
    EHIListDataSourceSection *currency     = self.collectionView.sections[EHIClassSelectSectionCurrencyDiffers];
    EHIListDataSourceSection *footnote     = self.collectionView.sections[EHIClassSelectSectionFootnotes];
    EHIListDataSourceSection *terms        = self.collectionView.sections[EHIClassSelectTermsAndConditions];

    [MTRReactor autorun:^(MTRComputation *computation) {
        // animate the active filter insertion / deletion
        [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
            activeFilter.model = model.activeFilters;
        } completion:nil];
    }];
    
    model.bind.map(@{
        source(model.title)           : dest(self, .title),
        source(model.redemptionModel) : dest(redemption, .model),
        source(model.bannerModel)     : dest(banner, .model),
        source(model.discount)        : dest(discount, .model),
        source(model.currencyModel)   : dest(currency, .model),
        source(model.carClassModels)  : dest(carClasses, .models),
        source(model.isLoading)       : dest(self, .loadingIndicator.isAnimating),
        source(model.price)           : dest(footnote, .model),
        source(model.termsModel)      : dest(terms, .model)
    });
}

# pragma mark - Actions

- (void)didTapFilterButton:(UIBarButtonItem *)sender
{
    [self.viewModel showFilterScreen];
}

# pragma mark - EHIClassSelectLayoutDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldDisplayRedemptionAtIndexPath:(NSIndexPath *)indexPath
{
    return !self.viewModel.hideRedemption;
}

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldSlideRedemptionHeaderAtIndexPath:(NSIndexPath *)indexPath
{
    return !self.isTogglingRedemption;
}

# pragma mark- EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    /**
     When returning, cells are dequeued after animations are applied in -executesCustomAnimationsForTransition...
     This means their ordering changes and the existing animations are applied to the wrong cells. Must reset and apply
     correct animations here.
    */

    if(self.isEnteringViaExtras || self.isEnteringViaRates) {
        const NSTimeInterval delay = EHITransitionAnimationDuration;
        
        // selected cell
        if([indexPath isEqual:self.collectionView.ehi_indexPathForSelectedItem]) {
            EHICarClassCell *selectedCell = (EHICarClassCell *)cell;
            
            // ensure any layout changed have been processed (like the cell's header height)
            [cell layoutIfNeeded];
            
            // prepare for animations
            selectedCell.contentView.alpha = 1.0;
            selectedCell.animationContainer.layer.transform = CATransform3DMakeTranslation(0.0, [self translationYForSelectedCell:selectedCell], 0.0);
            selectedCell.bottomContainer.layer.transform = CATransform3DMakeTranslation(0.0, self.lastTranslationYForCells, 0.0);
            
            // animate
            UIView.animate(YES).duration(EHIClassSelectAnimationPhase2Duration).delay(delay)
                .options(UIViewAnimationOptionCurveEaseInOut).transform(^{
                    selectedCell.animationContainer.layer.transform = CATransform3DIdentity;
                }).start(nil);
            
            UIView.animate(YES).duration(EHIClassSelectAnimationPhase1Duration).delay(EHIClassSelectAnimationPhase2Duration + delay)
                .options(UIViewAnimationOptionCurveEaseOut).transform(^{
                    selectedCell.bottomContainer.layer.transform = CATransform3DIdentity;
                }).start(nil);
        }
        // all other cells
        else {

            CGFloat translation = [self translationYForCellAtIndexPath:indexPath returning:YES];
            
            // prepare for animations
            cell.contentView.alpha = 0.0;
            cell.contentView.layer.transform = CATransform3DMakeTranslation(0.0, translation, 0.0);
            
            // animate
            UIView.animate(YES).duration(EHIClassSelectAnimationPhase1Duration).delay(EHIClassSelectAnimationPhase2Duration + delay)
                .options(UIViewAnimationOptionCurveEaseOut).transform(^{
                    cell.contentView.alpha = 1.0;
                    cell.contentView.layer.transform = CATransform3DIdentity;
                }).start(nil);
        }
    }
}

# pragma mark - Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    self.isEnteringViaRates    = isEntering && [controller isKindOfClass:[EHIPaymentOptionViewController class]];
    self.isEnteringViaExtras   = isEntering && [controller isKindOfClass:[EHIExtrasViewController class]];
    return [controller executesCustomAnimationsForTransitionToViewController:self isEntering:!isEntering];
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    NSArray *animations  = [super animationsForTransitionToViewController:controller isEntering:isEntering];
    
    // always animate background color
    animations = animations.concat(@[
        EHINavigationAnimation.target(self.collectionView)
            .color([UIColor ehi_grayColor2])
            .duration(EHIClassSelectAnimationPhase2Duration).delay(EHIClassSelectAnimationPhase1Duration),
    ]);
    
    // manual build animations after cells are dequeued
    if(self.isEnteringViaExtras || self.isEnteringViaRates) {
        [self resetForTransition];
        // disable navigation controls during custom transition to prevent buggy navigation state
        [self.navigationController.view setUserInteractionEnabled:NO];
        
        return animations;
    }
    
    // pull out the selected cell from the list view
    EHICarClassCell *selectedCell = (EHICarClassCell *)[self.collectionView cellForItemAtIndexPath:self.collectionView.ehi_indexPathForSelectedItem];
    NSMutableArray *visibleIndexPaths = [[NSMutableArray alloc] initWithArray:[self.collectionView indexPathsForVisibleItems]];
    [visibleIndexPaths removeObject:self.collectionView.ehi_indexPathForSelectedItem];
    
    // phase 1 - secondary cells
    for(NSIndexPath *indexPath in visibleIndexPaths) {
        
        UICollectionViewCell *cell = [self.collectionView cellForItemAtIndexPath:indexPath];
        CGFloat translation = [self translationYForCellAtIndexPath:indexPath returning:NO];
        
        animations = animations.concat(@[
            EHINavigationAnimation.target(cell.contentView)
                .options(UIViewAnimationOptionCurveEaseIn)
                .reverseTranslation((EHIFloatVector){ .y = translation })
                .duration(EHIClassSelectAnimationPhase1Duration)
        ]);
    }
    
    // phase 1 - secondary components
    animations = animations.concat(@[
        EHINavigationAnimation.target(selectedCell.bottomContainer)
            .options(UIViewAnimationOptionCurveEaseIn)
            .reverseTranslation((EHIFloatVector){ .y = self.translationYForCells})
            .duration(EHIClassSelectAnimationPhase1Duration)
    ]);

    // phase 2 - selected cell
    animations = animations.concat(@[
        EHINavigationAnimation.target(selectedCell.animationContainer)
            .options(UIViewAnimationOptionCurveEaseInOut)
            .reverseTranslation((EHIFloatVector){ .y = [self translationYForSelectedCell:selectedCell] })
            .duration(EHIClassSelectAnimationPhase2Duration).delay(EHIClassSelectAnimationPhase1Duration)
    ]);
    
    return animations;
}

# pragma mark - Helpers

- (CGFloat)translationYForSelectedCell:(EHICarClassCell *)cell
{
    // find distance to top of view
    CGRect  cellRect = [cell.animationContainer convertRect:cell.bodyContainer.frame toView:self.view];
    CGFloat selectedYOffset = -1 * CGRectGetMinY(cellRect);
    
    return selectedYOffset;
}

- (CGFloat)translationYForCellAtIndexPath:(NSIndexPath *)indexPath returning:(BOOL)isReturning
{
    // dismiss cells below the selected class downwards
    BOOL dismissDownwards = (indexPath.section == EHIClassSelectSectionCarClasses
                             && indexPath.item > self.collectionView.ehi_indexPathForSelectedItem.item)
                             || indexPath.section > EHIClassSelectSectionCarClasses;
    
    
    // apply a translation in the correct direction
    CGFloat translation = isReturning ? self.lastTranslationYForCells : self.translationYForCells;
    translation = (dismissDownwards ? 1.0f : -1.0f) * translation;

    return translation;
}

- (CGFloat)translationYForCells
{
    // selected cell's main animation container frame to compare to collection view
    EHICarClassCell *selectedCell = (EHICarClassCell *)[self.collectionView cellForItemAtIndexPath:[[self.collectionView indexPathsForSelectedItems] firstObject]];
    CGRect frame = [selectedCell convertRect:selectedCell.animationContainer.frame toView:self.view];
    
    // compare space above and below selected cell's main animation container
    CGFloat translation = MAX(frame.origin.y, self.collectionView.bounds.size.height - CGRectGetMaxY(frame));
    
    // save for return animation
    self.lastTranslationYForCells = translation;
    
    // minimum to remove secondary cells/components off screen
    return translation;
}

- (void)resetForTransition
{
    NSMutableArray *visibleIndexPaths = [[self.collectionView indexPathsForVisibleItems] mutableCopy];
    
    // configure cells until screen is refreshed
    for(NSIndexPath *indexPath in visibleIndexPaths) {
        NSIndexPath *selectedIndexPath = [[self.collectionView indexPathsForSelectedItems] firstObject];
        EHICarClassCell *cell = (EHICarClassCell *)[self.collectionView cellForItemAtIndexPath:indexPath];
        
        // place correctly under extras
        if([indexPath isEqual:selectedIndexPath]) {
            cell.contentView.alpha = 1.0;
            cell.animationContainer.layer.transform = CATransform3DMakeTranslation(0.0, [self translationYForSelectedCell:cell], 0.0);
            cell.bottomContainer.layer.transform = CATransform3DMakeTranslation(0.0, self.lastTranslationYForCells, 0.0);
        }
        // otherwise, hide until dequeued
        else {
            cell.contentView.alpha = 0.0;
        }
    }
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeFilter target:self action:@selector(didTapFilterButton:)];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationClassSelect state:EHIScreenReservationClassSelect];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventLoadClasses;
    }];
}

# pragma mark - NAVViewController

- (NSArray *)previewingSourceViews
{
    return @[
        self.collectionView
    ];
}

- (NAVPreview *)previewingContext:(id<UIViewControllerPreviewing>)previewingContext previewForLocation:(CGPoint)location
{
    NSIndexPath *indexPath = [self.collectionView indexPathForItemAtPoint:location];
    
    return [self.viewModel previewForIndex:indexPath.item];
}

+ (NSString *)screenName
{
    return EHIScreenReservationClassSelect;
}

@end
