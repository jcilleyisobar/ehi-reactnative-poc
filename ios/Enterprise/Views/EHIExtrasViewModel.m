//
//  EHIReservationExtrasViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIExtrasViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIPlacardViewModel.h"
#import "EHIUser.h"
#import "EHIFlightDetailsViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationBookStateBuilder.h"

@interface EHIExtrasViewModel ()
@property (copy, nonatomic) NSArray *equipmentExtras;
@property (copy, nonatomic) NSArray *fuelExtras;
@property (copy, nonatomic) NSArray *protectionExtras;
@property (copy, nonatomic) NSArray *includedExtras;
@property (copy, nonatomic) NSArray *mandatoryExtras;
@property (copy, nonatomic) NSDictionary *sectionHeaders;
@property (strong, nonatomic) EHICarClass *carClass;
@property (strong, nonatomic) NSMutableDictionary *cachedViewModels;
@property (strong, nonatomic) id <EHINetworkCancelable> activeRequest;
@end

@implementation EHIExtrasViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _cachedViewModels = [NSMutableDictionary new];
        _title = self.isModify
            ? EHILocalizedString(@"reservation_modify_extras_navigation_title", @"Modify Vehicle Extras", @"navigation bar title for reservation modify extras screen")
            : EHILocalizedString(@"reservation_extras_navigation_title", @"Extras", @"navigation bar title for reservation extras screen");
        _loadingTitle = EHILocalizedString(@"reservation_extras_loading_title", @"Customizing your extras...", @"text shown when loading extras");
        _placardModel = [[EHIPlacardViewModel alloc] initWithType:EHIPlacardTypeExtras carClass:nil];

        _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
            [NSNull null],
            [NSNull null],
            EHILocalizedString(@"reservation_extras_included_header", @"INCLUDED EXTRAS", @"Title for the reservation extras 'included' section"),
            EHILocalizedString(@"reservation_extras_mandatory_header", @"MANDATORY EXTRAS", @"Title for the reservation extras 'mandatorys' section"),
            EHILocalizedString(@"reservation_extras_equipment_header", @"EQUIPMENT EXTRAS", @"Title for the reservation extras 'equipment' section"),
            EHILocalizedString(@"reservation_extras_fuel_header", @"FUEL EXTRAS", @"Title for the reservation extras 'fuel' section"),
            EHILocalizedString(@"reservation_extras_protection_header", @"PROTECTION EXTRAS", @"Title for the reservation extras 'protection' section"),
        ]];
                
        _carClassModel = [EHICarClassViewModel new];
        _carClassModel.layout = EHICarClassLayoutExtras;
        _carClassPlaceholderModel = [EHICarClassViewModel new];
        _carClassPlaceholderModel.layout = EHICarClassLayoutExtrasPlaceholder;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self fetchDetailsForCarClass:model];
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

- (NSArray *)extrasFromViewModels
{
    return @[]
        .concat(self.includedExtras)
        .concat(self.mandatoryExtras)
        .concat(self.equipmentExtras)
        .concat(self.fuelExtras)
        .concat(self.protectionExtras)
        .map(^(EHIExtrasExtraViewModel *viewModel) {
            return viewModel.extra;
        });
}

//
// Helpers
//

- (void)resyncAfterEditsIfNeeded
{
    EHIReservation *reservation = self.builder.reservation;
    
    // resync if edits to extras were made
    EHICarClassVehicleRate *vehicleRate = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected];
    if(![self.carExtras isEqual:vehicleRate.extras]) {
        [self syncWithRate:vehicleRate];
        [self.carClassModel updateWithModel:self.carClass];
    }
    
    // resync if upgrade was made to change car class
    if(![self.carClass isEqual:self.builder.reservation.selectedCarClass]) {
        self.carClass = self.builder.reservation.selectedCarClass;
    }
}

- (void)syncWithRate:(EHICarClassVehicleRate *)rate
{
    self.priceSummary = rate.priceSummary;
    self.carExtras    = rate.extras;
}

# pragma mark - Services

- (void)fetchDetailsForCarClass:(EHICarClass *)carClass
{
    self.isLoading = NO;
    self.carClass  = carClass;
    
    EHIReservation *reservation = self.builder.reservation;
    
    // if this is the same as the selected car class, we should be done
    EHICarClassVehicleRate *vehicleRate = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected];
    BOOL needsToReload = ![carClass isEqual:reservation.selectedCarClass]
        || (self.isPrepay != reservation.prepaySelected);
    
    if(!needsToReload) {
        [self syncWithRate:vehicleRate];
        self.carClassModel.showSecretRate = self.isSecretRate;
    }
    // otherwise, we need to select this class first
    else {
        self.isLoading = YES;
        
        EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
            self.isLoading = NO;
            if(!error.hasFailed) {
                EHICarClassVehicleRate *vehicleRate = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected];
                [self syncWithRate:vehicleRate];
                self.carClassModel.showSecretRate = self.isSecretRate;
            } else {
                self.shouldDisableContinueButton = YES;
            }
        };
        //this is an edge case when changing from prepay location to pay later location
        //reservation still have prepay as true but all car classes don't have prepay rates
        BOOL shouldSelectPrepay = self.isPrepay && [carClass supportsPrepay];
        
        [[EHIServices sharedInstance] selectCarClass:carClass
                                         reservation:reservation
                                            inModify:self.isModify
                                        selectPrepay:shouldSelectPrepay
                                             handler:handler];
    }
}

# pragma mark - Selection

- (void)didChangeQuantityOfExtras
{
    // cancel the active request if there is one
    [self.activeRequest cancel];
        
    // the request we're about to kick off
    __block id<EHINetworkCancelable> request;
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        // cancelled request
        if(self.activeRequest != request) {
            return;
        }
        
        self.activeRequest = nil;
        
        // resync models with current reservation state (error or not)
        EHICarClassVehicleRate *vehicleRate = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected];
        [self syncWithRate:vehicleRate];
        
        // synchronize the analytics data on the selected car class after extras update successfully
        [self.builder synchronizeReservationOnContext:nil];
    };
    
    if(self.isModify) {
        request = [[EHIServices sharedInstance] modifyExtras:[self extrasFromViewModels] forReservation:self.builder.reservation handler:handler];
        self.builder.reservationIsModified = YES;
    } else {
        request = [[EHIServices sharedInstance] updateExtras:[self extrasFromViewModels] forReservation:self.builder.reservation handler:handler];
    }
    
    self.activeRequest = request;
}

- (void)selectExtraAtIndexPath:(NSIndexPath *)indexPath
{
    self.selectedPath = indexPath;

    // track the expansion of the selected extra
    [EHIAnalytics trackAction:EHIAnalyticsResActionExpandExtra handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHICarClassExtra class] encodable:[self extraAtIndexPath:indexPath]];
    }];
}

- (void)showDetailsForExtraAtIndexPath:(NSIndexPath *)indexPath
{
    [[self extraViewModelAtIndexPath:indexPath] showMoreInfo];
}

# pragma mark - Actions

- (void)finishUpdatingExtras
{
    // track a continue action here
    [EHIAnalytics trackAction:EHIAnalyticsResActionContinue handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventExtrasSelected;
    }];

    [self showNextScreen];
}

- (void)showNextScreen
{
    BOOL isMultiTerminal         = self.builder.promptsMultiTerminal;
    BOOL isLogged                = [EHIUser currentUser] != nil;
    BOOL hasDriverInfoSaved      = self.builder.driverInfo.hasRequiredFields;
    BOOL shouldShowFlightDetails = isMultiTerminal && (isLogged || hasDriverInfoSaved);
    
    // go back to review after making selected edits
    if(self.isEditing) {
        self.router.transition
        .pop(1).start(nil);
    }
    
    // if it's multi terminal location, go to the Flight Info
    else if(shouldShowFlightDetails) {
        self.router.transition
        .push(EHIScreenReservationFlightDetails).object(@(EHIFlightDetailsStateReview)).start(nil);
    } else {
        [self nextScreenForDriverInfoState];
    }
}

- (void)nextScreenForDriverInfoState
{
    // if we have complete driver info, go to the review screen
    if(self.builder.driverInfo.hasRequiredFields) {
        [self showReviewScreen];
    } else {
        // otherwise, we need to populate some part of our existing driver info
        [self showDriverInfoScreen];
    }
}

- (void)showDriverInfoScreen
{
    self.router.transition
    .push(EHIScreenReservationDriverInfo).start(nil);
}

- (void)showReviewScreen
{
    self.router.transition
    .push(EHIScreenReservationReview).start(nil);
}

# pragma mark - Setters

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    [self.carClassModel updateWithModel:carClass];
    [self.carClassPlaceholderModel updateWithModel:carClass];
}

- (void)setSelectedTogglePath:(NSIndexPath *)selectedTogglePath
{
    _selectedTogglePath = selectedTogglePath;
    self.justChangedToggle = YES;
}

-(void)setCarExtras:(EHICarClassExtras *)extras
{
    _carExtras = extras;
    
    // filter optional equipment extras and map them into vms
    self.equipmentExtras = (extras.equipment ?: @[]).map(^(EHICarClassExtra *extra) {
        return extra.isOptional || extra.isWaived ? [self viewModelForForExtra:extra withPaymentLineItem:[self.priceSummary findPriceLineItemWithCode:extra.code]] : nil;
    });

    if(!self.equipmentExtras.count) {
        self.equipmentExtras = @[[EHIExtrasExtraViewModel fallbackForType:EHIExtrasTypeEquipment]];
    }
    
    // filter optional fuel extras and map them into vms
    self.fuelExtras = (extras.fuel ?: @[]).map(^(EHICarClassExtra *extra) {
        return extra.isOptional || extra.isWaived ? [self viewModelForForExtra:extra withPaymentLineItem:[self.priceSummary findPriceLineItemWithCode:extra.code]] : nil;
    });

    // filter optional insurance extras and map them into vms
    self.protectionExtras = (extras.insurance ?: @[]).map(^(EHICarClassExtra *extra) {
        return extra.isOptional || extra.isWaived ? [self viewModelForForExtra:extra withPaymentLineItem:[self.priceSummary findPriceLineItemWithCode:extra.code]] : nil;
    });

    if(!self.protectionExtras.count) {
        self.protectionExtras = @[[EHIExtrasExtraViewModel fallbackForType:EHIExtrasTypeProtection]];
    }

    NSArray *extrasList = @[].concat(extras.equipment).concat(extras.insurance);
    // filter mandatory extras from the combined list and map them into vms
    self.mandatoryExtras = extrasList.map(^(EHICarClassExtra *extra) {
        return extra.isMandatory ? [self viewModelForForExtra:extra withPaymentLineItem:[self.priceSummary findPriceLineItemWithCode:extra.code]] : nil;
    });

    // filter included extras from the combined list and map them into vms
    self.includedExtras = extrasList.map(^(EHICarClassExtra *extra) {
        return extra.isIncluded ? [self viewModelForForExtra:extra withPaymentLineItem:[self.priceSummary findPriceLineItemWithCode:extra.code]] : nil;
    });
}

- (void)setSelectedPath:(NSIndexPath *)selectedPath
{
    EHIExtrasExtraViewModel *extraModel = [self extraViewModelAtIndexPath:_selectedPath];
    self.justChangedToggle = NO;
    
    if([_selectedPath isEqual:selectedPath]) {
        self.lastSelectedPath  = nil;
        extraModel.isSelected = !extraModel.isSelected;
    } else {
        self.lastSelectedPath = _selectedPath;
        [self extraViewModelAtIndexPath:_selectedPath].isSelected = NO;
        _selectedPath = selectedPath;
        [self extraViewModelAtIndexPath:_selectedPath].isSelected = YES;
    }
}

//
// Helpers
//

- (EHIExtrasExtraViewModel *)viewModelForForExtra:(EHICarClassExtra *)extra withPaymentLineItem:(EHICarClassPriceLineItem *)lineItem
{
    // lazy load a view model for this extra, mapped by id
    EHIExtrasExtraViewModel *result = self.cachedViewModels[extra.code];
    if(!result) {
        result = [EHIExtrasExtraViewModel new];
        self.cachedViewModels[extra.code] = result;
    }
    
    // always update the view model with our extra and payment line item
    [result updateWithExtra:extra andPaymentLineItem:lineItem];
    return result;
}

- (EHIModel *)termsModel
{
    return [EHIModel placeholder];
}

# pragma mark - Accesors

- (NSAttributedString *)buttonTitle
{
    NSString *buttonTitle = EHILocalizedString(@"reservation_extras_footer_continue_title", @"CONTINUE", @"button title for reservation extras screen to continue");

    NSString *buttonSubtitle = self.buttonSubtitle;

    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new].lineSpacing(8).color([UIColor whiteColor])
    .text(buttonTitle).fontStyle(EHIFontStyleBold, 18.0);
    
    if(buttonSubtitle.length > 0) {
        builder.lineSpacing(0).newline.appendText(buttonSubtitle).lineSpacing(0).fontStyle(EHIFontStyleRegular, 14.0);
    }
    
    return builder.string;
}

- (NSString *)buttonSubtitle
{
    BOOL isModify = self.isModify;
    if(isModify) {
        EHICarClass *carClass = self.builder.selectedCarClass;
        if(carClass.hasUnpaidRefund) {
            EHICarClassPriceDifference *difference = carClass.unpaidRefundDifference;
            BOOL hasRefundAmount = carClass.hasRefundAmount;
            NSString *format = hasRefundAmount
                ? EHILocalizedString(@"review_payment_refund_amount_action", @"#{amount} Refund Amount", @"")
                : EHILocalizedString(@"review_payment_unpaid_amount_action", @"#{amount} Unpaid Amount", @"");

            NSString *price = [EHIPriceFormatter format:difference.viewDifference].abs(YES).string;
            return [format ehi_applyReplacementMap:@{
                    @"amount" : price
                }
            ];
        }
    }
    
    return @"";
}

- (EHIReservationPriceButtonType)priceType
{
    return self.isSecretRate ? EHIReservationPriceButtonTypeSecretRate : EHIReservationPriceButtonTypePrice;
}

- (BOOL)isSecretRate
{
    return self.carClass.isSecretRate;
}

- (EHIReservationPriceButtonSubtitleType)priceSubtitleType
{
    BOOL hasUnpaid = self.carClass.hasUnpaidRefund;
    BOOL isModify  = self.isModify;
    BOOL isPrepay  = self.isPrepay;
    
    return hasUnpaid && isModify && isPrepay ? EHIReservationPriceButtonSubtitleTypeUpdatedTotal : EHIReservationPriceButtonSubtitleTypeTotalCost;
}

- (BOOL)priceIsLoading
{
    return self.activeRequest != nil;
}

- (NSNumber *)hidesInfoIcon
{
    return @YES;
}

- (EHICarClassExtra *)extraAtIndexPath:(NSIndexPath *)indexPath
{
    return [self extraViewModelAtIndexPath:indexPath].extra;
}

- (EHIExtrasExtraViewModel *)extraViewModelAtIndexPath:(NSIndexPath *)indexPath
{
    return [self extrasInSection:indexPath.section][indexPath.item];
}

- (NSArray *)extrasInSection:(EHIExtrasSection)section
{
    switch(section) {
        case EHIExtrasSectionEquipment:
            return self.equipmentExtras;
        case EHIExtrasSectionFuel:
            return self.fuelExtras;
        case EHIExtrasSectionProtection:
            return self.protectionExtras;
        case EHIExtrasSectionIncluded:
            return self.includedExtras;
        case EHIExtrasSectionMandatory:
            return self.mandatoryExtras;
        default: return nil;
    }
}

- (EHISectionHeaderModel *)headerForSection:(EHIExtrasSection)section
{
    return self.sectionHeaders[@(section)];
}

@end
