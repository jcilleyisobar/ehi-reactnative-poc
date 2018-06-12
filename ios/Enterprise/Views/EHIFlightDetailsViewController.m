//
//  EHIFlightDetailsViewController.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFlightDetailsViewController.h"
#import "EHIFlightDetailsViewModel.h"
#import "EHIFormFieldCell.h"
#import "EHIListCollectionView.h"
#import "EHIActionButton.h"
#import "EHIBarButtonItem.h"
#import "EHIFlightDetailsSearchCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFlightDetailsViewController () <EHIFormFieldCellActions, EHIListCollectionViewDelegate, EHIFlightDetailsSearchActions>
@property (strong, nonatomic) EHIFlightDetailsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) EHIFlightDetailsSearchCell *searchCell;
@property (weak  , nonatomic) IBOutlet EHIActionButton *submitButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIFlightDetailsViewController

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFlightDetailsViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIFlightDetailsSectionHelp)           : EHIFormFieldCell.class,
        @(EHIFlightDetailsSectionSearch)         : EHIFlightDetailsSearchCell.class,
        @(EHIFlightDetailsSectionNoFlightButton) : EHIFormFieldCell.class,
        @(EHIFlightDetailsSectionFlightNumber)   : EHIFormFieldCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFlightDetailsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *help = self.collectionView.sections[EHIFlightDetailsSectionHelp];
    EHIListDataSourceSection *search = self.collectionView.sections[EHIFlightDetailsSectionSearch];
    EHIListDataSourceSection *noFlight = self.collectionView.sections[EHIFlightDetailsSectionNoFlightButton];
    EHIListDataSourceSection *flightNumber = self.collectionView.sections[EHIFlightDetailsSectionFlightNumber];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.helpModel)         : dest(help, .model),
        source(model.searchModel)       : dest(search, .model),
        source(model.noFlightModel)     : dest(noFlight, .model),
        source(model.flightNumberModel) : dest(flightNumber, .model),
        source(model.submitTitle)       : dest(self, .submitButton.ehi_title),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
        source(model.invalidForm)       : ^(NSNumber *invalid) {
            self.submitButton.isFauxDisabled = invalid.boolValue;
            [self invalidateViewBelowSafeArea:invalid.boolValue];
        },
    });
}

# pragma mark - EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHIFlightDetailsSectionSearch) {
        self.searchCell = (EHIFlightDetailsSearchCell *)cell;
    }
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - EHIFlightDetailsSearchActions

- (void)searchCellDidTap
{
    [self.viewModel showSearchAirlines];
}

# pragma mark - Actions

- (IBAction)didTapSubmitButton:(id)sender
{
    [self.viewModel submitFlightDetails];
}

//# pragma mark - Transitions
//
//- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
//{
//    return [controller executesCustomAnimationsForTransitionToViewController:self isEntering:!isEntering];
//}
//
//- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
//{
//    NSArray *animations = [super animationsForTransitionToViewController:controller isEntering:isEntering];
//    
//    // pull out the navigation bar we're animating the search view to
//    UIView *navigationBar = self.navigationController.navigationBar;
//    
//    // calculate frames in the window space for various views we're animating
//    CGRect barFrame   = [navigationBar convertRect:navigationBar.bounds toView:navigationBar.window];
//    CGRect titleFrame = [self.searchCell.titleLabel convertRect:self.searchCell.titleLabel.bounds toView:navigationBar.window];
//    
//    // build up our animation sequence
//    animations = animations.concat(@[
//    EHINavigationAnimation.target(self.view)
//        .alpha(1.0f)
//        .duration(0.35),
//        
//    EHINavigationAnimation.proxy(self.searchCell.searchField)
//        .reverseFrame(CGRectOffset(CGRectInsetWithOffset(barFrame, EHISearchFieldInsets), 0.0f, -6.0f))
//        .block(^(EHITextField *textField, CGFloat percent) {
//            textField.actionButton.alpha  = percent;
//        })
//        .delay(0.35).duration(0.35),
//    
//    EHINavigationAnimation.proxy(self.searchCell.searchField)
//        .reverseTranslation((EHIFloatVector){ .y = -CGRectGetMaxY(titleFrame) })
//        .alpha(1.0f)
//        .delay(0.3).duration(0.4),
//    ]);
//    
//    if(!isEntering) {
//        animations = animations.concat(@[
//           EHINavigationAnimation.target(navigationBar)
//           .autoreversingNavigationBar
//        ]);
//    }
//    
//    return animations;
//}


# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (UIButton *)keyboardSupportedActionButton
{
    return self.submitButton;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationReview state:EHIScreenReservationFlightDetails];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationFlightDetails;
}
@end

NS_ASSUME_NONNULL_END
