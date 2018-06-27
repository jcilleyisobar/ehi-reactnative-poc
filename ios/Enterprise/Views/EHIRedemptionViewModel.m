//
//  EHIRedemptionViewModel.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionViewModel.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIReservationSublistViewModel.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIPrice.h"
#import "EHIExtrasViewModel.h"

@interface EHIRedemptionViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@property (copy  , nonatomic) NSArray *filteredLineItems;
@property (strong, nonatomic) NSArray *fees;
@property (strong, nonatomic) id<EHIPriceContext> originalTotal;

/** The current request kicked off by the stepper */
@property (strong, nonatomic) id <EHINetworkCancelable> activeRequest;
/** The initial fetch car class request that populates the view */
@property (strong, nonatomic) id <EHINetworkCancelable> initializationRequest;
@end

@implementation EHIRedemptionViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title              = EHILocalizedString(@"redemption_navigation_title", @"Redeem Points", @"title for the redemption screen");
        _footerTitle        = EHILocalizedString(@"redemption_footer_title", @"CONTINUE TO REVIEW", @"title for the redemption footer");
        _footerSubtitleType = EHIReservationPriceButtonSubtitleTypeAfterPoints;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        // work with a copy to avoid messing up with the actual carclass in the builder
        [self fetchDetailsForCarClass:[model copy]];
    }
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // if navigating backwards from review, resync screen content
    if(!self.isLoading) {
        [self resyncAfterEditsIfNeeded];
    }
}


# pragma mark - Services

- (void)fetchDetailsForCarClass:(EHICarClass *)carClass
{
    EHIReservation *reservation = self.builder.reservation;
    
    // if this is the same as the selected car class, we should be done
    BOOL needsToReload = ![carClass isEqual:reservation.selectedCarClass]
        || reservation.prepaySelected;

    if(!needsToReload) {
        [self initializeRedemptionWithCarClass:carClass isEditing:carClass.daysToRedeem > 0];
    }
    // otherwise, we need to select this class first
    else {
        self.isLoading = YES;
        
        EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
            if(!error.hasFailed) {
                [self initializeRedemptionWithCarClass:carClass isEditing:carClass.daysToRedeem > 0];
            }
        };
        
        self.activeRequest = [[EHIServices sharedInstance] selectCarClass:carClass
                                         reservation:reservation
                                            inModify:self.isModify
                                        selectPrepay:NO
                                             handler:handler];
    }
}

- (void)resyncAfterEditsIfNeeded
{
    EHIReservation *reservation = self.builder.reservation;
    
    if (reservation.selectedPaymentOption == EHIReservationPaymentOptionPayNow) {
        // skip the redemption screen
        self.router.transition
            .pop(1).start(nil);
        
        return;
    }
    
    if (reservation.selectedCarClass.daysToRedeem != self.carClass.daysToRedeem) {
        [self initializeRedemptionWithCarClass:reservation.selectedCarClass isEditing:YES];
    }
}

- (void)initializeRedemptionWithCarClass:(EHICarClass *)carClass isEditing:(BOOL)isEditing
{
    // determine correct flow
    self.isEditing = isEditing;
    
    // if we aren't editing, show that we are kicking off a request for max
    if(!self.isEditing) {
        carClass.daysToRedeem = carClass.maxRedemptionDays;
    }
    
    // setup ui with our redemption car class
    self.carClass = carClass;
    
    // generate a fetch model from the car class
    EHICarClassFetch *fetchModel = [EHICarClassFetch modelForCarClass:carClass];
    
    // max out days for regular redemption or zero them out if we are editting
    fetchModel.daysToRedeem = self.isEditing ? 0 : carClass.maxRedemptionDays;
    
    // fetch new model
    self.initializationRequest = [self fetchCarClassDetails:fetchModel handler:^(EHICarClass *carClass, EHIServicesError *error) {
        if(!error.hasFailed) {
            if(self.isEditing) {
                // grab the original total that we need
                self.totalModel    = [carClass vehicleRateForPrepay:self.builder.reservation.prepaySelected].priceSummary;
                self.originalTotal = [carClass vehicleRateForPrepay:self.builder.reservation.prepaySelected].priceSummary;
            } else {
                // update our car class
                self.carClass = carClass;
            }
        }
    }];
}

# pragma mark - Setters

- (void)setCarClass:(EHICarClass *)carClass
{
    if(!carClass) {
        return;
    }
    
    _carClass = carClass;
    
    EHICarClassPriceSummary *priceSummary = [carClass vehicleRateForPrepay:self.builder.reservation.prepaySelected].priceSummary;
    
    // extract the fees manually from the line items
    self.fees = (priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.type == EHIReservationLineItemTypeFee;
    });
    
    // header model
    self.headerModel = [EHIRedemptionPointsViewModel modelWithType:EHIRedemptionBannerTypeRedemption];
    
    // points model
    self.pointsModel = carClass;

    // if we are editing, use the original total that we get from services, or placeholder if we are still waiting
    if(self.isEditing) {
        self.totalModel = (id)self.originalTotal ?: [EHIModel placeholder];
    }
    // otherwise use the original total from the reservation whose car class has not had redemption applied
    else {
        self.totalModel = self.builder.totalPrice;
    }
    
    // if we don't have a redemption savings line item, pass in a place holder so the section doesn't disappear
    self.savingsModel = priceSummary.redemptionSavings ? carClass : [EHIModel placeholder];
    
    // footer model
    self.footerModel = priceSummary;
    
    // line items
    self.filteredLineItems = (self.builder.selectedLineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.type != EHIReservationLineItemTypeFee
        && lineItem.total.amount != 0.0f;
    });
    
    self.filteredLineItems = self.filteredLineItems ?: @[]
    
    // add fee total to the line items
    .concat(@[
        [self.builder.selectedCarClass vehicleRateForPrepay:self.builder.reservation.prepaySelected].priceSummary.feeSummmary,
        [EHIReservationSublistViewModel lineItemExtrasForClassClass:self.builder.selectedCarClass prepay:self.builder.reservation.prepaySelected],
        (self.builder.selectedCarClass.mileage ? [EHICarClassPriceLineItem lineItemForMileage:self.builder.selectedCarClass.mileage] : @[])
    ]).flatten;
}

# pragma mark - Actions

- (void)selectLineItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section != EHIRedemptionSectionLineItems) {
        return;
    }
    
    EHICarClassPriceLineItem *lineItem = [self lineItemAtIndex:indexPath.item];
    
    if(lineItem.type == EHIReservationLineItemTypeFeeSummary) {
        self.router.transition
            .present(EHIScreenReservationFees).object(self.fees).start(nil);
    }
}

- (void)toggleLineItems
{
    self.lineItemsModel = self.lineItemsModel ? nil : [self filteredLineItems];
    
    [EHIAnalytics trackAction:self.lineItemsModel == nil ? EHIAnalyticsResActionRedemptionHideDetails : EHIAnalyticsResActionRedemptionShowDetails handler:nil];
}

- (void)updateReservationWithDaysRedeemed
{
    // cancel the previous request if there is one
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(fetchCarClassDetails) object:nil];
    
    // schedule the new request to run in half a second
    [self performSelector:@selector(fetchCarClassDetails) withObject:nil afterDelay:.5f];
}

# pragma mark - Services

- (id<EHINetworkCancelable>)fetchCarClassDetails
{
    return [self fetchCarClassDetails:[EHICarClassFetch modelForCarClass:self.carClass] handler:^(EHICarClass *carClass, EHIServicesError *error) {
        if(!error.hasFailed) {
            self.carClass = carClass;
        }
    }];
}

- (id<EHINetworkCancelable>)fetchCarClassDetails:(EHICarClassFetch *)fetchModel handler:(void (^)(EHICarClass *, EHIServicesError *))handler
{
    // don't allow cancellation of the init request (we need that response)
    if(self.activeRequest != self.initializationRequest) {
        [self.activeRequest cancel];
    }
    
    // the request we're about to kick off
    __block id<EHINetworkCancelable> request;
    
    request = [[EHIServices sharedInstance] fetchCarClass:fetchModel reservation:self.builder.reservation handler:^(EHICarClass *carClass, EHIServicesError *error) {
        
        if([self.activeRequest isEqual:request]) {
            [self setActiveRequest:nil];
        }
        
        ehi_call(handler)(carClass, error);
    }];
    
    self.activeRequest = request;
    
    return request;
}

- (void)commitRedemption
{
    // track a continue action here
    [EHIAnalytics trackAction:EHIAnalyticsResActionContinue handler:nil];
    
    // show the activity indicator
    self.isCommitting = YES;
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        self.isCommitting = NO;
        
        if(!error.hasFailed) {
            if (self.shouldGotoExtrasWhenDone) {
                // transition to extras screen
                EHIExtrasViewModel *viewModel = [EHIExtrasViewModel new];
                [viewModel updateWithModel:self.builder.reservation.selectedCarClass];
                
                self.router.transition
                    .push(EHIScreenReservationExtras).object(viewModel).start(nil);
            } else {
                // transition to the review screen
                self.router.transition
                    .pop(1).start(nil);
            }
        }
        
        // synchronize the analytics data on the selected car class after extras update successfully
        [self.builder synchronizeReservationOnContext:nil];
    };
    
    [[EHIServices sharedInstance] selectCarClass:self.carClass
                                     reservation:self.builder.reservation
                                        inModify:self.isModify
                                    selectPrepay:NO
                                         handler:handler];
}

# pragma mark - Accessor

- (EHICarClassPriceLineItem *)lineItemAtIndex:(NSInteger)index
{
    return index < self.filteredLineItems.count ? self.filteredLineItems[index] : nil;
}
    
- (BOOL)isLoading
{
    return self.activeRequest != nil;
}

@end
