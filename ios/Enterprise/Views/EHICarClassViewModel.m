//
//  EHIReservationClassSelectCellViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_CountrySpecific.h"
#import "EHICarClassViewModel.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIReservationCarClassViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIUser.h"
#import "EHIAnalytics.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIPriceFormatter.h"
#import "EHICarClass.h"

@interface EHICarClassViewModel () <EHIReservationBuilderReadinessListener, EHIComparable>
@property (strong, nonatomic) EHICarClass *carClass;
@property (weak  , nonatomic) EHIReservationBuilder *builder;
@property (weak  , nonatomic) EHIUserLoyalty *loyalty;
@end

@implementation EHICarClassViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _detailsButtonTitle = EHILocalizedString(@"reservation_car_class_details_button_title", @"WHAT'S INCLUDED", @"title of button show the user details of a particular car class");
        _previousSelectionTitle = EHILocalizedString(@"reservation_car_class_previous_selection", @"YOUR CURRENT SELECTION", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        self.carClass = model;
    }
    
    // did become active (which registers our reactions) doesn't fire until after dynamic sizing takes place
    [self invalidateRedemptionVisibility:nil];
}

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    // class description
    self.carClassName = carClass.name;
    
    // make/model or transmission
    self.makeModelTitle = [self titleForMakeModel:carClass.makeModel];
    
    // transmission
    self.transmissionTypeName = self.layout != EHICarClassLayoutClassSelect && self.layout != EHICarClassLayoutExtrasPlaceholder
        ? carClass.transmission
        : carClass.transmission.uppercaseString;
    self.isAutomaticTransmission = carClass.isAutomaticTransmission;
    
    // vehicle profile image
    self.vehicleImage = carClass.images.firstObject;
    
    // reservation charge
    self.requiresCallForAvailability = carClass.requiresCallForAvailability;
    self.requiresWebBook = carClass.truckUrl != nil;
    
    id<EHIPriceContext> price;
    BOOL inExtras = self.layout == EHICarClassLayoutExtras || self.layout == EHICarClassLayoutExtrasPlaceholder;
    if(inExtras) {
        price = [carClass chargeForPrepay:self.isPrepay];
    } else {
        BOOL showPrepayPrice = self.layout == EHICarClassLayoutClassSelect ? carClass.supportsPrepay : self.isPrepay;
        price = [carClass vehicleRateForPrepay:showPrepayPrice].priceSummary
            ?: [self chargesForCarClass:carClass];
    }

    self.price = price;
    
    [self setupExtraPrice];

    // hide the rate view if the car class is not promotion or negotiated rate and there is no corporate account
    self.rateTitle = [self rateTitleForCarClass:carClass];
    
    // redemption
    self.freeDaysTitle     = [self freeDaysTitleForCarClass:carClass];
    self.pointsPerDayTitle = [self pointsPerDayTitleForCarClass:carClass];
    
    self.showPreviouslySelectedHeader = self.layout == EHICarClassLayoutClassSelect &&
                                        self.isModify &&
                                        carClass.wasPreviouslySelected;
}

//
// Helper
//

- (NSString *)rateTitleForCarClass:(EHICarClass *)carClass
{
    if(!self.builder.discount) {
        return nil;
    } else if(carClass.isPromotionalRate) {
        return EHILocalizedString(@"car_class_cell_promotional_rate_title", @"PROMOTIONAL RATE", @"title for the promotional rate view");
    } else if (carClass.isNegotiatedRate) {
        return EHILocalizedString(@"car_class_cell_negotiated_rate_title", @"NEGOTIATED RATE", @"title for the negotiated rate view");
    } else {
        return nil;
    }
}

- (void)setupExtraPrice
{
    // price for extras screen
    if (self.showSecretRate) {
        NSString *title = EHILocalizedString(@"reservation_price_unavailable", @"No Pricing Available", @"Reservation price button fallback text when price exists");
        self.extrasPrice = [EHIAttributedStringBuilder new].text(title).size(16.0).string;
    } else {
        self.extrasPrice = [EHIPriceFormatter format:self.price.viewPrice].attributedString;
    }
}

- (NSString *)titleForMakeModel:(NSString *)makeModel
{
    // because the make model strings from the service have trailing white space of varying length
    NSString *sanitizedMakeModel = [makeModel stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    NSString *format = EHILocalizedString(@"reservation_car_class_make_model_title", @"#{make_model} or similar", @"");
    NSString *makeModelTitle = [format ehi_applyReplacementMap:@{
        @"make_model" : sanitizedMakeModel,
    }];
    
    switch (self.layout) {
        case EHICarClassLayoutClassDetails:
            return makeModelTitle;
        case EHICarClassLayoutClassSelect:
        case EHICarClassLayoutExtrasPlaceholder:
        case EHICarClassLayoutExtras:
        case EHICarClassLayoutRate:
            return [makeModelTitle stringByAppendingString:@"*"];
    }
}

- (NSAttributedString *)freeDaysTitleForCarClass:(EHICarClass *)carClass
{
    // alert user if they cannot redeem any days at all
    if(carClass.maxRedemptionDays == 0) {
        NSString *noFreeDaysTitle = EHILocalizedString(@"redemption_not_enough_points_title", @"Not enough points for a free day", @"");
        return [NSAttributedString attributedStringWithString:noFreeDaysTitle font:[UIFont ehi_fontWithStyle:EHIFontStyleItalic size:14.0]];
    }
    
    NSString *earnTitle = EHILocalizedString(@"redemption_free_days_title", @"Enough points for", @"");
    NSString *freeTitle = carClass.maxRedemptionDays == 1
        ? EHILocalizedString(@"redemption_free_day_subtitle", @"#{number_of_days} free day", @"")
        : EHILocalizedString(@"redemption_free_days_subtitle", @"#{number_of_days} free days", @"");
    
    freeTitle = [freeTitle ehi_applyReplacementMap:@{
        @"number_of_days" : @(carClass.maxRedemptionDays)
    }];
    
    return EHIAttributedStringBuilder.new.text(earnTitle).size(14)
        .newline.appendText(freeTitle).fontStyle(EHIFontStyleBold, 18).string;
}

- (NSAttributedString *)pointsPerDayTitleForCarClass:(EHICarClass *)carClass
{
    NSString *points    = @(carClass.redemptionPoints).ehi_localizedDecimalString;
    NSString *rateTitle = EHILocalizedString(@"redemption_points_per_day", @"POINTS/DAY", @"");
    
    return EHIAttributedStringBuilder.new.text(points).fontStyle(EHIFontStyleRegular, 24)
        .space.appendText(rateTitle).size(14).string;
}

- (EHIReservationPriceButtonType)priceTypeForCallForAvailability:(BOOL)callForAvailability webBook:(BOOL)webBook
{
    if(webBook) {
        return EHIReservationPriceButtonTypeWebBook;
    } else if(callForAvailability) {
        return EHIReservationPriceButtonTypeCallForAvailability;
    } else {
        return EHIReservationPriceButtonTypePrice;
    }
}


# pragma mark - EHIViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // synchronize with the builder when ready
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateRedemptionVisibility:)];
}

- (void)invalidateRedemptionVisibility:(MTRComputation *)computation
{
    self.hidesRedemption = self.builder.hidePoints
    || (self.builder.reservation.selectedPaymentOption == EHIReservationPaymentOptionPayNow && self.layout > EHICarClassLayoutClassSelect)
    || self.requiresCallForAvailability
    || !self.carClass.isRedemptionAllowed
    || self.layout == EHICarClassLayoutRate;
}

# pragma mark - Computed

- (BOOL)hidesRedemption
{
    return self.builder.hidePoints
    || (self.builder.reservation.selectedPaymentOption == EHIReservationPaymentOptionPayNow && self.layout > EHICarClassLayoutClassSelect)
    || self.requiresCallForAvailability
    || !self.carClass.isRedemptionAllowed
    || self.layout == EHICarClassLayoutRate;
}

- (EHIReservationPriceButtonType)priceType
{
    return self.showSecretRate ? EHIReservationPriceButtonTypeSecretRate : [self priceTypeForCallForAvailability:self.requiresCallForAvailability webBook:self.requiresWebBook];
}

- (void)setShowSecretRate:(BOOL)showSecretRate
{
    _showSecretRate = showSecretRate;
    [self setupExtraPrice];
}

- (BOOL)showsDetailsView
{
    return _layout != EHICarClassLayoutExtras;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (EHIUserLoyalty *)loyalty
{
    return [EHIUser currentUser].profiles.basic.loyalty;
}

# pragma mark - Setters

- (void)setLayout:(EHICarClassLayout)layout
{
    _layout = layout;
    
    // invalidate the car class when the layout changes (if we have one)
    if(self.carClass) {
        self.carClass = self.carClass;
    }
}

# pragma mark - EHIComparable

- (id)uid
{
    return self.carClass.uid;
}

@end
