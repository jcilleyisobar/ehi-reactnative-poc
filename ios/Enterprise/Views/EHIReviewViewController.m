//
//  EHIReviewViewController.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewViewController.h"
#import "EHIReviewViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIToggleButton.h"
#import "EHIReviewBookingLoadingIndicator.h"
#import "EHIReservationConfirmationFooter.h"
#import "EHIActivityIndicator.h"
#import "EHIInformationBannerCell.h"
#import "EHIRedemptionPointsCell.h"
#import "EHIReviewLocationsCell.h"
#import "EHIReservationScheduleCell.h"
#import "EHIReservationCarClassCell.h"
#import "EHIReservationCarClassUpgradeCell.h"
#import "EHIReservationExtraCell.h"
#import "EHIReservationDriverInfoCell.h"
#import "EHIReservationRentalPriceTotalCell.h"
#import "EHIReviewPrepayPaymentCell.h"
#import "EHIFlightCell.h"
#import "EHIDeliveryCollectionCell.h"
#import "EHIReservationPoliciesCell.h"
#import "EHIReviewTravelPurposeCell.h"
#import "EHIFormFieldCell.h"
#import "EHIReviewPaymentOptionsCell.h"
#import "EHIReviewPaymentMethodCell.h"
#import "EHIConfirmationDiscountCell.h"
#import "EHIReviewContractNotificationCell.h"
#import "EHIReviewModifyPrepayBannerCell.h"
#import "EHIRestorableConstraint.h"
#import "EHIReservationPriceSublistCell.h"
#import "EHIReviewSectionHeader.h"
#import "EHIReviewAdditionalInfoCell.h"
#import "EHIReviewPaymentChangeCell.h"
#import "EHITermsAndConditionsCell.h"
#import "EHIReviewPaymentMethodLockedCell.h"


@interface EHIReviewViewController () <EHIListCollectionViewDelegate, EHIReviewPaymentOptionsActions, EHIRedemptionPointsCellActions, EHIReservationCarClassUpgradeCellActions, EHIDeliveryCollectionCellActions, EHIFormFieldCellActions, EHIReservationRentalPriceTotalCellActions, EHIReviewLocationsCellActions, EHIFlightCellActions, EHIReservationPriceSublistCellActions, EHIReviewPaymentChangeCellActions,EHIReviewPaymentMethodCellActions>
@property (strong, nonatomic) EHIReviewViewModel *viewModel;
@property (strong, nonatomic) EHIReviewBookingLoadingIndicator *reviewBookingActivityIndicator;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIReservationConfirmationFooter *bookButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property( weak  , nonatomic) IBOutlet EHIRestorableConstraint *collectionViewBottomConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bookButtonHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *termsContainerHeightConstraint;
@property (weak  , nonatomic) IBOutlet UIView *termsContainer;
@property (weak  , nonatomic) IBOutlet UILabel *quickBookTermsLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *quickBookTermsToggle;
@end

@implementation EHIReviewViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // create the activity indicator
    self.reviewBookingActivityIndicator = [EHIReviewBookingLoadingIndicator ehi_instanceFromNib];
    
    self.quickBookTermsToggle.style = EHIToggleButtonStyleWhite;
    
    // configure the collection view
    [self.collectionView.sections construct:@{
        @(EHIReviewSectionPaymentChange)       : EHIReviewPaymentChangeCell.class,
        @(EHIReviewSectionModifyPrepay)        : EHIReviewModifyPrepayBannerCell.class,
        @(EHIReviewSectionRedemption)          : EHIRedemptionPointsCell.class,
        @(EHIReviewSectionRentalHeader)        : EHIReviewSectionHeader.class,
        @(EHIReviewSectionDiscount)            : EHIConfirmationDiscountCell.class,
        @(EHIReviewSectionDiscountNotify)      : EHIReviewContractNotificationCell.class,
        @(EHIReviewSectionBanner)              : EHIInformationBannerCell.class,
        @(EHIReviewSectionPickupReturn)        : EHIReservationScheduleCell.class,
        @(EHIReviewSectionCarClass)            : EHIReservationCarClassCell.class,
        @(EHIReviewSectionCarClassUpgrade)     : EHIReservationCarClassUpgradeCell.class,
        @(EHIReviewSectionIncludedExtras)      : EHIReservationExtraCell.class,
        @(EHIReviewSectionMandatoryExtras)     : EHIReservationExtraCell.class,
        @(EHIReviewSectionAddedExtras)         : EHIReservationExtraCell.class,
        @(EHIReviewSectionDriverInfo)          : EHIReservationDriverInfoCell.class,
        @(EHIReviewSectionFlightDetails)       : EHIFlightCell.class,
        @(EHIReviewSectionAdditionalInfo)      : EHIReviewAdditionalInfoCell.class,
        @(EHIReviewSectionTravelPurpose)       : EHIReviewTravelPurposeCell.class,
        @(EHIReviewSectionDeliveryCollection)  : EHIDeliveryCollectionCell.class,
        @(EHIReviewSectionPaymentOptions)      : EHIReviewPaymentOptionsCell.class,
        @(EHIReviewSectionPolicies)            : EHIReservationPoliciesCell.class,
        @(EHIReviewSectionTermsAndConditions)  : EHITermsAndConditionsCell.class,
        @(EHIReviewSectionPrepayPayment)       : EHIReviewPrepayPaymentCell.class,
        @(EHIReviewSectionPaymentMethod)       : EHIReviewPaymentMethodCell.class,
        @(EHIReviewSectionPolicies)            : EHIReservationPoliciesCell.class,
        @(EHIReviewSectionPaymentMethodLocked) : EHIReviewPaymentMethodLockedCell.class
    }];
    
    EHIListDataSourceSection *location = self.collectionView.sections[EHIReviewSectionLocation];
    location.klass = EHIReviewLocationsCell.class;
    location.model = [EHIModel placeholder];
    
    EHIListDataSourceSection *priceBreakdown = self.collectionView.sections[EHIReviewSectionPriceDetails];
    priceBreakdown.klass = EHIReservationPriceSublistCell.class;

    EHIListDataSourceSection *total = self.collectionView.sections[EHIReviewSectionPriceTotal];
    total.klass = EHIReservationRentalPriceTotalCell.class;
    
    EHIListDataSourceSection *rental = self.collectionView.sections[EHIReviewSectionRentalHeader];
    rental.model = [self.viewModel headerForSection:EHIReviewSectionRentalHeader];
    
    // configure cells
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        
        section.isDynamicallySized = YES;
        
        // ignore the rental header, we already built it above
        if(section.index != EHIReviewSectionRentalHeader) {
            EHIViewModel *model = [self.viewModel headerForSection:section.index];
            if(model) {
                section.header.isDynamicallySized = YES;
                section.header.klass = EHIReviewSectionHeader.class;
                section.header.model = model;
            }
        }
    }
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
   
    // stick the activity indicator over the view
    UIView *containerView = self.navigationController.parentViewController.view;
    
    [self.reviewBookingActivityIndicator setFrame:containerView.bounds];
    [containerView addSubview:self.reviewBookingActivityIndicator];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // animate the button in a way that the users don't have a feeling that there's a bug in the app
    [self animateBookButton];
    
    [self.collectionView reloadData];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.bookButton.accessibilityIdentifier = EHIReservationFlowBookKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *paymentChange   = self.collectionView.sections[EHIReviewSectionPaymentChange];
    EHIListDataSourceSection *modifyBanner    = self.collectionView.sections[EHIReviewSectionModifyPrepay];
    EHIListDataSourceSection *discount        = self.collectionView.sections[EHIReviewSectionDiscount];
    EHIListDataSourceSection *discountNotify  = self.collectionView.sections[EHIReviewSectionDiscountNotify];
    EHIListDataSourceSection *banner          = self.collectionView.sections[EHIReviewSectionBanner];
    EHIListDataSourceSection *pickupReturn    = self.collectionView.sections[EHIReviewSectionPickupReturn];
    EHIListDataSourceSection *carClass        = self.collectionView.sections[EHIReviewSectionCarClass];
    EHIListDataSourceSection *carClassUpgrade = self.collectionView.sections[EHIReviewSectionCarClassUpgrade];
    EHIListDataSourceSection *includedExtras  = self.collectionView.sections[EHIReviewSectionIncludedExtras];
    EHIListDataSourceSection *mandatoryExtras = self.collectionView.sections[EHIReviewSectionMandatoryExtras];
    EHIListDataSourceSection *additionalInfo  = self.collectionView.sections[EHIReviewSectionAdditionalInfo];
    EHIListDataSourceSection *driverInfo      = self.collectionView.sections[EHIReviewSectionDriverInfo];
    EHIListDataSourceSection *flightDetails   = self.collectionView.sections[EHIReviewSectionFlightDetails];
    EHIListDataSourceSection *total           = self.collectionView.sections[EHIReviewSectionPriceTotal];
    EHIListDataSourceSection *travelPurpose   = self.collectionView.sections[EHIReviewSectionTravelPurpose];
    EHIListDataSourceSection *payment         = self.collectionView.sections[EHIReviewSectionPaymentOptions];
    EHIListDataSourceSection *oneTimePayment  = self.collectionView.sections[EHIReviewSectionPrepayPayment];
    EHIListDataSourceSection *selectedPayment = self.collectionView.sections[EHIReviewSectionPaymentMethod];
    EHIListDataSourceSection *paymentLocked   = self.collectionView.sections[EHIReviewSectionPaymentMethodLocked];
    EHIListDataSourceSection *policies        = self.collectionView.sections[EHIReviewSectionPolicies];
    EHIListDataSourceSection *terms           = self.collectionView.sections[EHIReviewSectionTermsAndConditions];
    
    [MTRReactor autorun:self action:@selector(invalidateReservationLoading:)];
    [MTRReactor autorun:self action:@selector(invalidateAddedExtras:)];
    [MTRReactor autorun:self action:@selector(invalidateDeliveryAndCollections:)];
    [MTRReactor autorun:self action:@selector(invalidateScroll:)];
    [MTRReactor autorun:self action:@selector(invalidatePriceSublist:)];
    [MTRReactor autorun:self action:@selector(invalidateQuickBookTermsContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateBookButtonPrice:)];
    [MTRReactor autorun:self action:@selector(invalidateRedemptionBanner:)];
    
    model.bind.map(@{
        source(model.paymentChangeModel)      : dest(paymentChange, .model),
        source(model.isLoading)               : dest(self, .activityIndicator.isAnimating),
        source(model.title)                   : dest(self, .title),
        source(model.modifyPrepayViewModel)   : dest(modifyBanner, .model),
        source(model.discount)                : dest(discount, .model),
        source(model.discountNotifyModel)     : dest(discountNotify, .model),
        source(model.bannerModel)             : dest(banner, .model),
        source(model.scheduleViewModel)       : dest(pickupReturn, .model),
        source(model.carClass)                : dest(carClass, .model),
        source(model.carClassUpgrade)         : dest(carClassUpgrade, .model),
        source(model.includedExtras)          : dest(includedExtras, .models),
        source(model.mandatoryExtras)         : dest(mandatoryExtras, .models),
        source(model.additionalInfoModel)     : dest(additionalInfo, .model),
        source(model.driverInfoModel)         : dest(driverInfo, .model),
        source(model.flightDetailsModel)      : dest(flightDetails, .model),
        source(model.totalPriceViewModel)     : dest(total, .model),
        source(model.travelPurposeModel)      : dest(travelPurpose, .model),
        source(model.bookButtonTitle)         : dest(self, .bookButton.attributedTitle),
        source(model.bookButtonEnabled)       : dest(self, .bookButton.isFauxDisabled),
        source(model.policiesViewModel)       : dest(policies, .model),
        source(model.paymentOptionsModel)     : dest(payment, .model),
        source(model.prepayViewModel)         : dest(oneTimePayment, .model),
        source(model.selectedPaymentModel)    : dest(selectedPayment, .model),
        source(model.paymentMethodLocked)     : dest(paymentLocked, .model),
        source(model.quickBookTerms)          : dest(self, .quickBookTermsLabel.attributedText),
        source(model.quickBookTermsRead)      : dest(self, .quickBookTermsToggle.selected),
        source(model.termsModel)              : dest(terms, .model),
    });
}

- (void)invalidateAddedExtras:(MTRComputation *)computation
{
    NSArray *models = self.viewModel.optionalExtras;
    
    [self.collectionView flushWithCompletion:^{
        self.collectionView.sections[EHIReviewSectionAddedExtras].models = models;
        [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

- (void)invalidateDeliveryAndCollections:(MTRComputation *)computation
{
    NSArray *models = self.viewModel.deliveryCollectionModels;
    
    [self.collectionView flushWithCompletion:^{
        self.collectionView.sections[EHIReviewSectionDeliveryCollection].models = models;
        [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

- (void)invalidateScroll:(MTRComputation *)computation
{
    BOOL shouldScroll = self.viewModel.shouldScrollToAdditionalInfo;
    
    __weak typeof(self) welf = self;
    [welf.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
    } completion:^(BOOL completed){
        if(shouldScroll) {
            [welf.collectionView reloadData];
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIReviewSectionAdditionalInfo];
            [welf.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
        }
    }];
}

- (void)invalidatePriceSublist:(MTRComputation *)computation
{
    EHIViewModel *model = self.viewModel.priceSublistModel;
    
    [self.collectionView flushWithCompletion:^{
        self.collectionView.sections[EHIReviewSectionPriceDetails].model = model;
        [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

- (void)invalidateQuickBookTermsContainer:(MTRComputation *)computation
{
    BOOL showQuickBook = self.viewModel.showQuickBook;
    
    MASLayoutPriority priority = showQuickBook ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.termsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
    
    [self invalidateBookButtonLayout];
}

- (void)invalidateBookButtonPrice:(MTRComputation *)computation
{
    self.bookButton.price = self.viewModel.totalPrice;
    self.bookButton.priceSubtitleType = self.viewModel.priceSubtitleType;
    
    [self invalidateBookButtonLayout];
}

- (void)invalidateBookButtonLayout
{
    BOOL showQuickBook = self.viewModel.showQuickBook;
    self.bookButton.priceType = self.viewModel.priceType;
    
    self.bookButton.priceButtonLayout = showQuickBook
        ? EHIReservationConfirmationFooterPriceButtonLayoutAlwaysShowPrice
        : EHIReservationConfirmationFooterPriceButtonLayoutDefault;
}

- (void)invalidateRedemptionBanner:(MTRComputation *)computation
{
    EHIViewModel *redemptionViewModel = self.viewModel.redemptionViewModel;

    __weak __typeof(self) welf = self;
    [self.collectionView flushWithCompletion:^{
        welf.collectionView.sections[EHIReviewSectionRedemption].model = redemptionViewModel;
        [welf.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

- (void)invalidateReservationLoading:(MTRComputation *)computation
{
    EHIReservationCommitState state = self.viewModel.reservationCommitState;
    
    if((state == EHIReservationCommitStateLoading) && !self.reviewBookingActivityIndicator.superview) {
        [self insertActivityIndicator];
    }
    
    UIView.animate(!computation.isFirstRun).duration(0.15).transform(^{
        self.reviewBookingActivityIndicator.isAnimating =
            (state == EHIReservationCommitStateLoading) ||
            (state == EHIReservationCommitStateSuccessful);
    }).start(nil);
    
    if(state == EHIReservationCommitStateSuccessful) {
        [[MTRReactor reactor] afterFlush:^{
            [self.viewModel showConfirmation];
            [self.reviewBookingActivityIndicator finishLoadingWithSuccess:YES completion:^(BOOL finished) {
                [self.reviewBookingActivityIndicator removeFromSuperview];
                [self.viewModel showContractToastIfNeeded];
            }];
        }];
    }
}

- (void)animateBookButton
{
    BOOL hideBookButton  = self.viewModel.hideBookButton;
    
    void (^bookButtonAnimation)() = nil;
    // enabling/disabling constraints this way to prevent autolayout's message: Unable to simultaneously satisfy constraints
    if(hideBookButton) {
        bookButtonAnimation = [^{
            self.bookButton.hide = YES;
            self.bookButtonHeightConstraint.isDisabled = YES;
            
            CGFloat translationY = CGRectGetHeight(self.bookButton.frame);
            self.bookButton.layer.transform = CATransform3DMakeTranslation(0.0, translationY, 0.0);
        } copy];
    } else {
        bookButtonAnimation = [^{
            self.bookButtonHeightConstraint.isDisabled = NO;
            self.bookButton.hide = NO;
            self.bookButton.layer.transform = CATransform3DIdentity;
        } copy];
    }
    
    UIView.animate(YES).duration(0.5).transform(^{
        bookButtonAnimation();
    }).start(nil);
}

- (void)insertActivityIndicator
{
    // stick the activity indicator over the view
    UIView *containerView = self.navigationController.parentViewController.view;
    
    [self.reviewBookingActivityIndicator setFrame:containerView.bounds];
    [containerView addSubview:self.reviewBookingActivityIndicator];
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectItemAtIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItem:indexPath.item inSection:indexPath.section];
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGFloat topInset = section == EHIReviewSectionDiscountNotify ? 1.f : 0.f;
    return (UIEdgeInsets) {
        .top = topInset
    };
}

# pragma mark - Actions

- (IBAction)didTapBookButton:(id)sender
{
    [self.viewModel footerButtonPressed];
}

- (IBAction)didTapReadTerms:(UIButton *)sender
{
    [self toggleQuickBookReadTerms];
}

# pragma mark - EHIReviewPaymentChangeCellActions

- (void)didTapChangePayment:(EHIReviewPaymentChangeCell *)cell
{
    [self changePaymentType];
}

# pragma mark - EHIFlightCellActions

- (void)didSelectedFlightCell
{
    [self didSelectedCell];
}

# pragma mark - EHIReviewLocationsCellActions

- (void)didSelectedLocationsCell
{
    [self didSelectedCell];
}

# pragma mark - EHIReservationCarClassUpgradeCellActions

- (void)didTapActionButtonForCarClassUpgradeCell:(id)sender
{
    [self didSelectedCell];
    [self animateBookButton];
    [self.viewModel upgradeCarClass];
    [self.viewModel invalidateModifyPrepayBanner];
}

# pragma mark - EHIDeliveryCollectionCellActions

- (void)didTapActionButtonForDeliveryCollectionCell:(EHIDeliveryCollectionCell *)sender
{
    [self didSelectedCell];
    [self.viewModel showDeliveryCollection];
}

# pragma mark - EHIReviewPaymentOptionsActions

- (void)didResizeReviewPaymentOptionsCell:(EHIReviewPaymentOptionsCell *)cell
{
    [self didSelectedCell];
    [self.viewModel updateBookContent];
    [self.collectionView ehi_invalidateLayoutAnimated:NO];
}

# pragma mark - EHIRedemptionPointsCellActions

- (void)didTapActionButtonForRedemptionPointsCell:(EHIRedemptionPointsCell *)sender
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

- (void)didRemovePointsForRedemptionPointsCell:(EHIRedemptionPointsCell *)sender
{
    [self.viewModel removePoints];
}

- (void)didSelectedRedemptionPointsCell
{
    [self didSelectedCell];
}

# pragma mark - EHIReservationRentalPriceTotalCellActions

- (void)didTapChangePaymentTypeForPriceTotalCell:(EHIReservationRentalPriceTotalCell *)sender
{
    [self changePaymentType];
}

# pragma mark - EHIReviewPrepayPaymentCellActions

- (void)didTapAddPrepayPaymentMethodForPrepayPaymentCell:(EHIReviewPrepayPaymentCell *)sender
{
    [self didSelectedCell];
    [self.viewModel didTapPrepayPayment];
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    // update additional information
    [self.viewModel formFieldViewModelDidChangeValue:sender.viewModel];
    
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

- (void)didSelectedCell
{
    [self.viewModel didSelectedCell];
}

# pragma mark - EHIReservationPriceSublistCellActions

- (void)didSelectExpandedReservationSublistCell:(EHIReservationPriceSublistCell *)cell
{
    [cell.contentView layoutIfNeeded];
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - EHIReviewPaymentMethodCellActions

- (void)reviewPaymentMethodDidToggleReadTerms:(EHIReviewPaymentMethodCell *)cell
{
    [self toggleQuickBookReadTerms];
}

- (void)reviewPaymentMethodDidTap:(EHIReviewPaymentMethodCell *)cell
{
    [self.viewModel showAddPayment];
}

- (void)toggleQuickBookReadTerms
{
    [self.viewModel togglePrepayQuickBookTerms];
    [self animateQuickBookTermsContainer];
}

- (void)animateQuickBookTermsContainer
{
    BOOL show = self.viewModel.quickBookTermsRead;
    CGFloat padding = 0.0f;
    CGAffineTransform transform = CGAffineTransformIdentity;

    if(show) {
        padding = CGRectGetHeight(self.termsContainer.frame);

        CGAffineTransform scale = CGAffineTransformScale(CGAffineTransformIdentity, 0.9f, 0.9f);
        transform = CGAffineTransformTranslate(scale, 0.0, padding);
    }

    void (^frameOne)() = ^{
        self.collectionViewBottomConstraint.constant = -padding;
    };

    void (^frameTwo)() = ^{
        self.termsContainerHeightConstraint.isDisabled = show;
        self.termsContainer.transform = transform;
        self.termsContainer.alpha     = show ? 0.0f : 1.0f;
    };

    EHIAnimationBuilder *animation = UIView.animate(YES).duration(0.3f).option(UIViewAnimationOptionCurveEaseInOut);

    if(show) {
        animation.transform(frameOne).start(^(BOOL completed) {
            animation.transform(frameTwo).start(nil);
        });
    } else {
        animation.transform(frameTwo).start(^(BOOL completed) {
            animation.transform(frameOne).start(nil);
        });
    }
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationReview state:EHIScreenReservationReview];
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    [self.builder synchronizeReservationOnContext:nil];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventLoadReview;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationReview;
}

//
// Helpers
//

- (void)changePaymentType
{
    [self didSelectedCell];
    [self animateBookButton];
    [self.viewModel changePaymentType];
}

@end
