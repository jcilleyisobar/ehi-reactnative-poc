//
//  EHIClassSelectViewModel.m
//  Enterprise
//
//  Created by mplace on 3/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_CountrySpecific.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIClassSelectViewModel.h"
#import "EHIClassSelectFilterQuery.h"
#import "EHICarClassViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIExtrasViewModel.h"
#import "EHIUserManager.h"
#import "EHIServices+Reservation.h"
#import "EHITermsViewModel.h"
#import "EHIPaymentOptionViewModel.h"

@interface EHIClassSelectViewModel () <EHIUserListener>
@property (copy  , nonatomic) NSArray *carClasses;
@property (copy  , nonatomic) NSArray *unfilteredCarClasses;
@property (strong, nonatomic) EHIClassSelectFilterQuery *filterQuery;
@end

@implementation EHIClassSelectViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = self.isModify
            ? EHILocalizedString(@"reservation_modify_class_select_navigation_title", @"Modify Vehicle Class", @"navigation bar title for modify car class selection screen")
            : EHILocalizedString(@"reservation_class_select_navigation_title", @"Select Vehicle Class", @"navigation bar title for car class selectionscreen");
       
        [self invalidateModel];
    }
    
    return self;
}

- (void)invalidateModel
{
    // filter the builder's base list, opt-in to setters
    NSArray *carClasses = (self.builder.carClasses ?: @[]).select(^(EHICarClass *carClass) {
        return !carClass.isSoldOut;
    });
    
    if (self.isModify) {
        carClasses = [self carClassesWithPreviouslySelectedClassOnTop:carClasses];
    }
    
    self.unfilteredCarClasses = carClasses;
    
    self.carClasses      = self.unfilteredCarClasses;
    self.redemptionModel = !self.hideRedemption ? [EHIRedemptionPointsViewModel modelWithType:EHIRedemptionBannerTypeClassSelect] : nil;
    self.price           = [self chargesForCarClass:carClasses.firstObject] ;
    self.bannerModel     = [EHIInformationBannerViewModel modelWithType:[self bannerTypeFromBuilder:self.builder]];
    
    NSString *pickupCountry  = self.builder.reservation.pickupLocation.countryCode;
    NSString *currentCountry = [NSLocale ehi_country].code;
    BOOL showBanner = ![pickupCountry ehi_isEqualToStringIgnoringCase:currentCountry];
    if(showBanner) {
        BOOL prepay = self.defaultPayment == EHICarClassChargeTypePrepay;
        EHICarClass *carClass = (EHICarClass *)[self.carClasses ehi_safelyAccess:0];
        id<EHIPriceContext> priceContext = [carClass chargeForPrepay:prepay] ?: [carClass vehicleRateForPrepay:prepay].priceSummary;
        if (!priceContext){
            priceContext = [carClass chargeForPrepay:!prepay] ?: [carClass vehicleRateForPrepay:!prepay].priceSummary;
        }
        self.currencyModel = [[EHICurrencyDiffersViewModel alloc] initWithModel:priceContext];
    }
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIClassSelectFilterQuery class]]) {
        self.filterQuery   = model;
        self.activeFilters = [model activeFilters];
        self.carClasses    = [model filteredCarClasses];
    }
}

//
// Helpers
//

- (EHIInformationBannerType)bannerTypeFromBuilder:(EHIReservationBuilder *)builder
{
    if(builder.isEmeraldReservation && builder.discount == nil) {
         return EHIInformationBannerTypeEmeraldClassSelect;
    }
    
    return EHIInformationBannerTypeNone;
}

# pragma mark - Previewing

- (NAVPreview *)previewForIndex:(NSUInteger)index
{
    // TODO: example until fully EHIViewController lifecycle accommodates previewing
//    EHICarClass *carClass = [self carClassAtIndex:index];
//    EHIExtrasViewModel *viewModel = [[EHIExtrasViewModel alloc] initWithModel:carClass];
//    
//    NAVTransition *peekTransition = NAVTransitionBuilder.new.push(EHIScreenReservationExtras).object(viewModel).animated(NO).build;
//    NAVPreview *preview = [[NAVPreview alloc] initWithPeekTransition:peekTransition];
    
    return nil;
}

# pragma mark - Actions

- (void)showEnterprisePlus
{
    self.router.transition
        .present(EHIScreenReservationEPlusInfo).start(nil);
}

- (void)selectCarClassAtIndex:(NSInteger)index
{
    // find the car class
    EHICarClass *carClass = [self carClassAtIndex:index];
    
    // don't progress and require phone call
    if(carClass.requiresCallForAvailability) {
        [self promptCallForAvailability:carClass];
    }
    
    // prompt user to confirm selection of `on request` class
    else if(carClass.isOnRequest) {
        [self.builder promptOnRequestSelectionWithHandler:^(BOOL shouldContinue) {
            if(shouldContinue) {
                [self showNextScreenWithCarClass:carClass];
            }
        }];
    }
    
    // show terms and conditions if required
    else if (carClass.requiresTermsAndConditions) {
        [self showTermsAndConditions:carClass];
    }
    
    // otherwise, just select the class
    else {
        [self showNextScreenWithCarClass:carClass];
    }
}

- (void)showTermsAndConditions
{
    self.router.transition.present(EHIScreenTermsAndConditions).object(self.builder.reservation).start(nil);
}

- (void)showTermsAndConditions:(EHICarClass *)carClass
{
    EHITermsViewModel *termsModel = [[EHITermsViewModel alloc] initWithContentString:carClass.termsAndConditions];
    self.router.transition
        .present(EHIScreenTerms).object(termsModel).start(nil);
    
    // handle the result of the user interaction
    termsModel.handler = ^(NSString *acceptedTermsVersion, BOOL didAccept) {
        if (didAccept) {
            [self showNextScreenWithCarClass:carClass];
        }
    };
}

- (void)showDetailsForCarClassAtIndex:(NSInteger)index
{
    // find the car class
    EHICarClass *carClass = [self carClassAtIndex:index];

    if(carClass.isOnRequest) {
        [self.builder promptOnRequestSelectionWithHandler:^(BOOL shouldContinue) {
            if(shouldContinue) {
                self.router.transition
                    .push(EHIScreenReservationClassDetails).object(carClass).start(nil);
            }
        }];
    }
    
    else {
        self.router.transition
            .push(EHIScreenReservationClassDetails).object(carClass).start(nil);
    }
}

- (void)showFilterScreen
{
    self.router.transition
        .push(EHIScreenReservationClassSelectFilter).object(self.filterQuery).start(nil);
}

- (void)clearFilters
{
    // clear the filters
    self.activeFilters = nil;
    // reset the car class list
    self.carClasses = self.unfilteredCarClasses;
    // clear out the filter query
    self.filterQuery = nil;
}

//
// Helpers
//

- (void)promptCallForAvailability:(EHICarClass *)carClass
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionAvailability handler:nil];
    
    // deeplink instead of call if link is available
    if(carClass.usesCallForAvailabilityLink) {
        [UIApplication ehi_promptUrl:carClass.truckUrl];
        return;
    }
    
    // if no number provided, default to the pickup location's phone
    NSString *number = carClass.availabilityPhoneNumber;
    if(!number) {
        number = self.builder.reservation.pickupLocation.phoneNumber;
    }
    
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"reservation_call_for_availability_prompt_call_title", @"Call?", @""))
        .message(EHILocalizedString(@"reservation_call_for_availability_prompt_call_message", @"Call this location to learn more about availability for this car class.", @""))
        .cancelButton(EHILocalizedString(@"standard_button_cancel", @"Cancel", @"Standard cancel button title"))
        .button(EHILocalizedString(@"standard_button_call", @"Call", @""));
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_promptPhoneCall:number];
        }
    });
}

- (EHIModel *)termsModel
{
    return [EHIModel placeholder];
}

# pragma mark - Setters

- (void)setCarClasses:(NSArray *)carClasses
{
    [self invalidateCarClassModels:carClasses];
    
    _carClasses = carClasses;
}

- (void)invalidateCarClassModels:(NSArray *)carClasses
{
    // inset the car class section if we are showing negotiated or promotional rates
    EHICarClass *firstCarClass = carClasses.firstObject;
    self.shouldInsetCarClassSection = firstCarClass.isNegotiatedRate || firstCarClass.isPromotionalRate;
    
    self.carClassModels = (carClasses ?: @[]).map(^(EHICarClass *carClass) {
        EHICarClassViewModel *viewModel = [[EHICarClassViewModel alloc] initWithModel:carClass];
        viewModel.layout = EHICarClassLayoutClassSelect;
        return viewModel;
    });
}

- (NSArray *)carClassesWithPreviouslySelectedClassOnTop:(NSArray *)carClasses
{
    EHICarClass *previouslySelectedClass = carClasses.find(^(EHICarClass *carClass) {
        return carClass.wasPreviouslySelected;
    });
    
    if (!previouslySelectedClass) {
        return carClasses;
    }
    
    return @[previouslySelectedClass].concat(carClasses.without(previouslySelectedClass));
}

# pragma mark - Accessors

- (EHIContractDetails *)discount
{
    if(self.builder.discount) {
        return self.builder.discount;
    } else if(self.builder.reservation.contractDetails) {
        return self.builder.reservation.contractDetails;
    } else {
        return nil;
    }
}

- (EHICarClass *)carClassAtIndex:(NSInteger)index
{
    return self.carClasses[index];
}

- (EHIClassSelectFilterQuery *)filterQuery
{
    if(!_filterQuery) {
        _filterQuery = [EHIClassSelectFilterQuery new];
    }
    
    _filterQuery.carClasses = self.unfilteredCarClasses;
    _filterQuery.filteredCarClasses = self.carClasses;
    
    return _filterQuery;
}

- (BOOL)hideRedemption
{
    return self.builder.hideRedemption;
}

@end
