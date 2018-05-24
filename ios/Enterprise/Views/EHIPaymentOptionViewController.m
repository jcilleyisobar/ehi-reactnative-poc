//
//  EHIPaymentOptionViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionViewController.h"
#import "EHIListCollectionView.h"
#import "EHIClassSelectViewController.h"
#import "EHIPaymentOptionViewModel.h"
#import "EHIPaymentOptionCellViewModel.h"
#import "EHISectionHeader.h"
#import "EHIPaymentOptionCell.h"
#import "EHICarClassCell.h"
#import "EHISectionHeaderModel.h"
#import "EHIActivityIndicator.h"
#import "EHIPlacardCell.h"
#import "EHIPaymentOptionPrepayBannerCell.h"
#import "EHIPaymentOptionFooterCell.h"

#define EHIPaymentOptionScrollInAnimationDuration (0.5)

@interface EHIPaymentOptionViewController () <UICollectionViewDelegate, EHIPlacardActions>
@property (strong, nonatomic) EHIPaymentOptionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIPaymentOptionViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionView];
}

- (BOOL)needsBottomLine
{
    return NO;
}

- (void)configureCollectionView
{
    [self.collectionView.sections construct:@{
         @(EHIPaymentOptionSectionCarClass)           : EHICarClassCell.class,
         @(EHIPaymentOptionSectionPlacard)            : EHIPlacardCell.class,
         @(EHIPaymentOptionSectionPrepayBanner)       : EHIPaymentOptionPrepayBannerCell.class,
         @(EHIPaymentOptionSectionPaymentOptions)     : EHIPaymentOptionCell.class,
         @(EHIPaymentOptionSectionFooter)             : EHIPaymentOptionFooterCell.class,
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentOptionViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateAnimation:)];

    EHIListDataSourceSection *paymentSection   = self.collectionView.sections[EHIPaymentOptionSectionPaymentOptions];
    EHIListDataSourceSection *car              = self.collectionView.sections[EHIPaymentOptionSectionCarClass];
    EHIListDataSourceSection *placard          = self.collectionView.sections[EHIPaymentOptionSectionPlacard];

    EHIListDataSourceSection *banner           = self.collectionView.sections[EHIPaymentOptionSectionPrepayBanner];
    EHIListDataSourceSection *footer           = self.collectionView.sections[EHIPaymentOptionSectionFooter];


    model.bind.map(@{
        source(model.title)               : dest(self, .title),
        source(model.placardModel)        : dest(placard, .model),
        source(model.carClassModel)       : dest(car, .model),
        source(model.paymentOptionModels) : dest(paymentSection, .models),
        source(model.prepayBannerModel)   : dest(banner, .model),
        source(model.footerModel)         : dest(footer, .model),
    });
}

- (void)invalidateAnimation:(MTRComputation *)computation
{
    BOOL shouldAnimate = self.viewModel.shouldAnimate;

    // do not animate in the cells when navigating back to this screen
    if(shouldAnimate) {
        // give collection view a chance to layout its cells
        dispatch_after_seconds(0.5, ^{

            // move cells out of the visible area
            for(EHICollectionViewCell *cell in [self animatedCells]) {
                NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
                cell.layer.zPosition = indexPath.section * -1.0f;
                [cell setTransform:CGAffineTransformMakeTranslation(0, -self.animationYTranslation)];
            }

            // animate cells back in
            [UIView animateWithDuration:EHIPaymentOptionScrollInAnimationDuration delay:1.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
                for(EHICollectionViewCell *cell in [self animatedCells]) {
                    [cell setTransform:CGAffineTransformIdentity];
                }
            } completion:nil];
        });
    }
}

# pragma mark - Helpers

- (void)didTapInfo
{
    [self.viewModel presentModalPaymentInformation];
}

- (EHIPaymentOptionSection)fixSection
{
    return EHIPaymentOptionSectionCarClass;
}

- (CGFloat)animationYTranslation
{
    EHICollectionViewCell *carClassCell = [[self.collectionView ehi_visibleCellsInSection:self.fixSection] lastObject];
    return self.collectionView.bounds.size.height - CGRectGetMaxY(carClassCell.frame);
}

- (NSArray *)animatedCells
{
    return [self.collectionView ehi_visibleCellsMatchingPredicate:^BOOL(NSIndexPath *indexPath) {
        return indexPath.section > self.fixSection;
    }];
}

# pragma mark - Actions

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == EHIPaymentOptionSectionPaymentOptions) {
        [self.viewModel selectItemAtIndex:indexPath.item];
    }
}

# pragma mark - UICollectionViewDelegate

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGFloat topInset = section == EHIPaymentOptionSectionPaymentOptions ? 5.f : 0.f;
    return (UIEdgeInsets) {
        .top = topInset
    };
}

# pragma mark - Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return [controller isKindOfClass:[EHIClassSelectViewController class]];
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    NSArray *animations = [super animationsForTransitionToViewController:controller isEntering:isEntering];

    return animations.concat(@[
        EHINavigationAnimation.target(self.view)
        .alpha(1.0)
        .duration(EHIPaymentOptionScrollInAnimationDuration)
        .delay(EHIClassSelectAnimationPhase1Duration + EHIClassSelectAnimationPhase2Duration)
    ]);
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationRateSelect state:EHIScreenReservationRateSelect];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationRateSelect;
}

@end
