//
//  EHIConfirmationReservationOptionsViewModel.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/21/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIConfirmationReservationOptionsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIConfiguration.h"
#import "EHIConfirmationCancelModalViewModel.h"
#import "EHIConfirmationContractCancelModalViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"

@interface EHIConfirmationReservationOptionsViewModel ()

@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (nonatomic, strong) EHIReservation *reservation;
@property (assign, nonatomic) BOOL isModifyLoading;
@property (assign, nonatomic) BOOL isCancelationLoading;

@end

@implementation EHIConfirmationReservationOptionsViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];

    if([model isKindOfClass:[EHIReservation class]]) {
        self.reservation = model;
    }
}

#pragma mark - Cancel Reservation

- (void)cancelReservation
{
    BOOL prepaySelected = self.reservation.prepaySelected;
    if(self.disableCancel) {
        if(prepaySelected) {
            [self promptPrepayCancelNotAvailable];
        } else {
            [self promptActionUnavailableWithMessage:self.cancelDisabledText];
        }
        return;
    }

    [EHIAnalytics trackAction:EHIAnalyticsResActionCancelReset handler:nil];

    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.state = EHIAnalyticsResStateCancel;
    }];

    BOOL shouldNotifyContractCancel = self.reservation.contractDetails.thirdPartyEmailRequired;
    if(shouldNotifyContractCancel) {
        [self promptContractCancellationModal];
    } else if(prepaySelected) {
        [self promptPrepayCancellationModal];
    } else {
        EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
                .message(EHILocalizedString(@"reservation_confirmation_cancel_title", @"Cancel this reservation?", @""))
                .button(EHILocalizedString(@"standard_button_yes", @"Yes", @"Standard yes button title"))
                .cancelButton(EHILocalizedString(@"standard_button_no", @"No", @"Standard no button title"));

        alertView.show(^(NSInteger index, BOOL canceled) {
            if(!canceled) {
                [self didCancelCommittedReservation];
            }
        });
    }
}

- (void)promptContractCancellationModal
{
    EHIContractDetails *contract = self.reservation.contractDetails;
    EHIConfirmationContractCancelModalViewModel *viewModel = [[EHIConfirmationContractCancelModalViewModel alloc] initWithContract:contract];
    viewModel.buttonLayout = EHIInfoModalButtonLayoutSecondaryDismiss;
    [viewModel present:^BOOL(NSInteger index, BOOL canceled) {

        if (!canceled && index == 0) {
            [self didCancelCommittedReservation];
        }

        return YES;
    }];
}

- (void)didCancelCommittedReservation
{
    // can't cancel without a confirmation number
    if (!self.reservation.confirmationNumber.length) {
        return;
    }

    self.isCancelationLoading = YES;

    __weak typeof(self) welf = self;
    [self.builder cancelReservation:self.reservation handler:^(EHIServicesError *error) {
        // perform any shared cleanup
        welf.isCancelationLoading = NO;
        [welf trackCancelationOfReservation:self.reservation didSucceed:!error];

        if (error.hasFailed) {
            return;
        }

        [welf showCancellationToast];

        // dismiss the reservation modal
        EHIMainRouter.router.transition
                .dismiss.root(EHIScreenDashboard).animated(NO)
                .start(nil);
    }];
}


- (void)promptPrepayCancellationModal
{

    BOOL useCancellationFee = [NSLocale ehi_shouldUseCancellationFees];
    EHICancellationDetails *cancelDetails = self.reservation.cancellationDetails;

    EHICancellationFee *cancellationFee = useCancellationFee ? cancelDetails.cancellationFee : nil;
    EHIPrice *originalAmount = self.originalAmount;
    EHIPrice *cancelFee = cancelDetails.feeView;
    EHIPrice *refundAmount = cancelDetails.refundView;

    EHIConfirmationCancelModalViewModel *viewModel = [[EHIConfirmationCancelModalViewModel alloc]
            initWithPrice:originalAmount
                cancelFee:cancelFee
          cancellationFee:cancellationFee
                   refund:refundAmount];
    viewModel.buttonLayout = EHIInfoModalButtonLayoutSecondaryDismiss;

    __weak typeof(self) welf = self;
    [viewModel present:^BOOL(NSInteger index, BOOL canceled) {
        if (!canceled && index == 0) {
            [welf didCancelCommittedReservation];
        }

        return YES;
    }];
}

- (void)promptPrepayCancelNotAvailable
{
    EHIInfoModalViewModel *modal = [EHIInfoModalViewModel new];
    modal.title                  = EHILocalizedString(@"reservation_cancel_unavailable_title", @"Unable to Cancel Reservation", @"");
    modal.details                = EHILocalizedString(@"reservation_cancel_unavailable_subtitle", @"You are not allowed to cancel this reservation", @"");
    modal.firstButtonTitle       = EHILocalizedString(@"standard_button_call", @"CALL US", @"");
    modal.secondButtonTitle      = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
    modal.hidesCloseButton       = YES;

    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0 && !canceled) {
            NSString * number = [EHIConfiguration configuration].primarySupportPhone.number;
            [UIApplication ehi_promptPhoneCall:number];
        }
        return YES;
    }];
}

- (EHIPrice *)originalAmount
{
    BOOL useCancellationFee = [NSLocale ehi_shouldUseCancellationFees];
    EHIReservationPaymentMethod *paymentMethod = (EHIReservationPaymentMethod *)self.reservation.reservationPayments.firstObject;
    EHIPrice *selectedCarViewPrice = [self.reservation.selectedCarClass priceContextForPrepay:self.reservation.prepaySelected].paymentPrice;
    EHIPrice *originalAmount = self.reservation.cancellationDetails.originalAmoutView;

    return useCancellationFee ? originalAmount : (paymentMethod.amount ?: selectedCarViewPrice);
}

#pragma mark - Modify Reservation

- (void)modifyReservation
{
    if(self.disableModify) {
        [self promptActionUnavailableWithMessage:self.modifyDisabledText];
        return;
    }

    [EHIAnalytics trackAction:EHIAnalyticsResActionModify handler:nil];

    void (^modifyCompletion)() = ^{
        __weak typeof(self) welf = self;
        self.isModifyLoading = YES;
        [self.builder modifyReservation:self.reservation handler:^(EHIServicesError *error) {
            welf.isModifyLoading = NO;
        }];
    };

    BOOL isAirport  = self.reservation.pickupLocationType == EHILocationTypeAirport;
    BOOL shouldSkip = [NSLocale ehi_shouldAllowQuickBookReservation] && isAirport;
    BOOL isPrepay   = self.reservation.prepaySelected;
    if(isPrepay && !shouldSkip) {
        [self promptPrepayModifyModalWithSuccessAction:modifyCompletion];
    } else {
        modifyCompletion();
    }
}


- (void)promptPrepayModifyModalWithSuccessAction:(void (^)())completion
{
    EHIInfoModalViewModel *viewModel = [EHIInfoModalViewModel new];
    viewModel.title   = EHILocalizedString(@"modify_reservation_prepay_dialog_title", @"Are you sure you want to modify?", @"");
    viewModel.details = EHILocalizedString(@"modify_reservation_prepay_dialog_text", @"At the end of your modification you will be refunded your original amount and be charged the new total of the modified rental. Rates and prices may vary.", @"");
    viewModel.firstButtonTitle  = EHILocalizedString(@"modify_reservation_prepay_dialog_continue", @"CONTINUE", @"");
    viewModel.secondButtonTitle = EHILocalizedString(@"modify_reservation_prepay_dialog_keep_current", @"NO, KEEP CURRENT RESERVATION", @"");
    viewModel.buttonLayout = EHIInfoModalButtonLayoutSecondaryDismiss;

    [viewModel present:^BOOL(NSInteger index, BOOL canceled) {
        if(!canceled && index == 0) {
            ehi_call(completion)();
        }

        return  YES;
    }];
}

#pragma mark - Helpers

- (void)promptActionUnavailableWithMessage:(NSString *)message
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
            .message(message)
            .cancelButton(EHILocalizedString(@"standard_close_button", @"Close", @""));

    alertView.show(nil);
}

- (BOOL)hidesModifyCancel
{
    return self.reservation.status == EHIReservationStatusCanceled;
}

- (void)showCancellationToast
{
    NSString *successMessage = EHILocalizedString(@"confirmation_reservation_was_canceled", @"Reservation successfully canceled", @"");
    [EHIToastManager showMessage:successMessage];
}

- (void)trackCancelationOfReservation:(EHIReservation *)reservation didSucceed:(BOOL)didSucceed
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionConfirm handler:^(EHIAnalyticsContext *context) {
        context.state = EHIAnalyticsResStateCancel;
        context.customerValue = didSucceed ? -reservation.customerValue : 0;
    }];
}

# pragma mark - Computed

- (BOOL)disableModify
{
    BOOL hidesModify = [self hidesModifyCancel];

    // disable modify for reservations booked through ECARS
    hidesModify |= self.reservation.isReservationBookingSystemEcars;

    // respect eligibility for reservations when it comes from services
    if(self.reservation.eligibility) {
        hidesModify |= !self.reservation.eligibility.canModify;
    }

    return hidesModify;
}

- (BOOL)disableCancel
{
    BOOL hidesCancel = [self hidesModifyCancel];

    // respect eligibility for reservations when it comes from services
    if(self.reservation.eligibility) {
        hidesCancel |= !self.reservation.eligibility.canCancel;
    }

    return hidesCancel;
}

- (NSString *)modifyDisabledText
{
    return EHILocalizedString(@"ecars_reservation_modify_call_prompt_message", @"Sorry, but currently we cannot modify a rental booked in ECARS. Please cancel the reservation then rebook again or call us.", @"");
}

- (NSString *)cancelDisabledText
{
    return EHILocalizedString(@"ecars_reservation_modify_call_prompt_message", @"Sorry, but currently we cannot modify a rental booked in ECARS. Please cancel the reservation then rebook again or call us.", @"");
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIConfirmationReservationOptionsViewModel *)model
{
    return @[
        @key(model.reservation),
    ];
}

@end
