//
//  EHIPaymentOptionViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_CountrySpecific.h"
#import "EHIPaymentOptionViewModel.h"
#import "EHIPaymentOptionCellViewModel.h"
#import "EHIUserManager.h"
#import "EHIExtrasViewModel.h"
#import "EHIRedemptionViewModel.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIPlacardViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIPaymentOptionModalViewModel.h"
#import "EHIToastManager.h"
#import "EHISettings.h"

@interface EHIPaymentOptionViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIPaymentOptionViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"choose_your_rate_navigation_title", @"Payment Options", @"");

        [self invalidatePaymentOption];

        _placardModel = [[EHIPlacardViewModel alloc] initWithType:EHIPlacardTypePayment carClass:nil];

        _carClassModel = [EHICarClassViewModel new];
        _carClassModel.layout = EHICarClassLayoutRate;
        _footerModel = [EHIModel placeholder];
        
        BOOL isValidCountry   = [NSLocale ehi_shouldShowPrepayBanner];
        BOOL shouldShowBanner = [EHISettings shouldShowPrepayBanner];
        
        if(isValidCountry && shouldShowBanner) {
            _prepayBannerModel = [EHIModel placeholder];
            [EHISettings didShowPrepayBanner];
        }
    }

    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHICarClass class]]) {
        self.carClass = model;
    }
}

- (void)didBecomeActive
{
    [super didBecomeActive];

    // if navigating backwards from review, re sync screen content
    if(!_shouldAnimate) {
        [self invalidatePaymentOption];
        
        // reset redemption days on car class
        self.carClass.daysToRedeem = 0;
    }
}

- (void)didResignActive
{
    [super didResignActive];
    _shouldAnimate = NO;
}

- (void)updatePaymentOptions
{
    // isLoading is used for the animation and should only be true when we come straight from the car class select screen;
    // when previous screen was CarClassDetail, we don't want animation
    if(![self.carClass isEqual:self.builder.reservation.selectedCarClass]) {
        _shouldAnimate = YES;
    }

    BOOL isEmeraldReservation = self.builder.isEmeraldReservation;
    BOOL isAuthenticated      = [EHIUser currentUser] != nil;

    NSMutableArray *paymentOptionViewModels = [NSMutableArray array];

    EHIPaymentOptionCellViewModel *prepayModel   = [self rateCellViewModelWithPaymentOption:EHIReservationPaymentOptionPayNow];
    EHIPaymentOptionCellViewModel *payLaterModel = [self rateCellViewModelWithPaymentOption:EHIReservationPaymentOptionPayLater];
    
    BOOL defaultToPayLater = self.defaultPayment == EHICarClassChargeTypePayLater;
    if(defaultToPayLater) {
        [paymentOptionViewModels addObject:payLaterModel];
        [paymentOptionViewModels addObject:prepayModel];
    } else {
        [paymentOptionViewModels addObject:prepayModel];
        [paymentOptionViewModels addObject:payLaterModel];
    }
    
    // redemption
    if (isAuthenticated && !isEmeraldReservation) {
        [paymentOptionViewModels addObject:[self rateCellViewModelWithPaymentOption:EHIReservationPaymentOptionRedeemPoints]];
    }

    _paymentOptionModels = [paymentOptionViewModels copy];
}

- (EHIPaymentOptionCellViewModel *)rateCellViewModelWithPaymentOption:(EHIReservationPaymentOption)paymentOption
{
    EHIPaymentOptionCellViewModel *viewModel = [EHIPaymentOptionCellViewModel new];
    [viewModel configureWithPaymentOption:paymentOption carClass:self.carClass];
    return viewModel;
}

# pragma mark - Setters

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;

    [self.carClassModel updateWithModel:carClass];
    [self updatePaymentOptions];
}

- (void)selectItemAtIndex:(NSInteger)index
{
    EHIPaymentOptionCellViewModel *cellViewModel = [self.paymentOptionModels ehi_safelyAccess:index];
    EHIReservationPaymentOption selectedPaymentOption = [cellViewModel paymentOption];
    BOOL enabled = [cellViewModel layoutType] == EHIPaymentOptionLayoutEnabled;

    [EHIAnalytics trackAction:[self analyticsActionForPaymentOption:selectedPaymentOption] handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventRateSelected;
    }];
    
    if (selectedPaymentOption == EHIReservationPaymentOptionRedeemPoints) {
        if(!enabled) {
            return [self showRedemptionUnavailableToastIfNeeded];
        }

        EHIRedemptionViewModel *viewModel = [EHIRedemptionViewModel new];
        [viewModel updateWithModel:self.carClass];
        [viewModel setShouldGotoExtrasWhenDone:YES];

        self.router.transition.push(EHIScreenReservationRedemption).object(viewModel).start(nil);
    }
    else {
        if (!enabled) {
            // do something
            return;
        }
        
        // set the payment option on the builder before we fetch the car class details to show correct price at the top of the extras screen
        self.builder.reservation.selectedPaymentOption = selectedPaymentOption;

        EHIExtrasViewModel *viewModel = [EHIExtrasViewModel new];
        [viewModel updateWithModel:self.carClass];

        self.router.transition.push(EHIScreenReservationExtras).object(viewModel).start(nil);
    }
}

- (void)showRedemptionUnavailableToastIfNeeded
{
    BOOL canRedeemPoints = self.carClass.canRedeemPoints;
    if (!canRedeemPoints) {
        NSString *notEnoughPoints = EHILocalizedString(@"choose_your_rate_redeem_not_enough_points_subtitle", @"Not enough points for a free day", @"");
        [EHIToastManager showMessage:notEnoughPoints];
    }
}

- (void)presentModalPaymentInformation
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext * _Nonnull context) {
        context.state = EHIAnalyticsResStatePaymentOptionsModal;
    }];
    
    EHIPaymentOptionModalViewModel *viewModel = [EHIPaymentOptionModalViewModel new];
    [viewModel present:nil];
}

# pragma mark - Analytics

- (NSString *)analyticsActionForPaymentOption:(EHIReservationPaymentOption)option
{
    switch (option) {
        case EHIReservationPaymentOptionPayNow:
            return EHIAnalyticsResActionPaymentOptionPayNow;
        case EHIReservationPaymentOptionPayLater:
            return EHIAnalyticsResActionPaymentOptionPayLater;
        case EHIReservationPaymentOptionRedeemPoints:
            return EHIAnalyticsResActionPaymentOptionRedeem;
        default:
            return nil;
    }
}

//
// Helpers
//

- (void)invalidatePaymentOption
{
    if(!self.isModify) {
        self.builder.reservation.selectedPaymentOption = EHIReservationPaymentOptionUnknown;
    }
}

@end
