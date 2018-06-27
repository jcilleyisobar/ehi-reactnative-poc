//
//  EHIClassDetailsClassViewModel.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIClassDetailsClassViewModel.h"
#import "EHICarClass.h"
#import "EHIAnalytics.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHIClassDetailsClassViewModel ()
@property (weak  , nonatomic) EHIReservationBuilder *builder;
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIClassDetailsClassViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _annotation = EHILocalizedString(@"class_details_make_model_annotation", @"or similar model with comparable features", @"text that explains to the user that they are renting either the car pictured or something similar and comparable");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self updateWithCarClass:model];
    }
}

- (void)updateWithCarClass:(EHICarClass *)carClass
{
    self.carClass = carClass;
    
    self.makeModel = carClass.makeModel;
    self.title = carClass.name;
    self.subtitle = carClass.transmission;
    self.vehicleImage = carClass.images.firstObject;
    self.requiresCallForAvailability = carClass.requiresCallForAvailability;
    self.price = carClass.priceContext;

    // hide the rate view if the car class is not promotion or negotiated rate
    self.shouldHideRateView = !(carClass.isPromotionalRate || carClass.isNegotiatedRate);
    self.rateTitle = [self rateTitleForCarClass:carClass];
}

//
// Helper
//

- (NSString *)rateTitleForCarClass:(EHICarClass *)carClass
{
    if (carClass.isPromotionalRate) {
        return EHILocalizedString(@"car_class_cell_promotional_rate_title", @"PROMOTIONAL RATE", @"title for the promotional rate view");
    } else if (carClass.isNegotiatedRate) {
        return EHILocalizedString(@"car_class_cell_negotiated_rate_title", @"NEGOTIATED RATE", @"title for the negotiated rate view");
    } else {
        return nil;
    }
}

# pragma mark - Actions

- (void)selectClass
{
    // push the extras screen
    self.router.transition
        .push(EHIScreenReservationExtras).object(self.carClass).start(nil);
    
    // track the total cost selection
    [EHIAnalytics trackAction:EHIAnalyticsResActionTotalCost handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeClassSelection:self.carClass context:context];
    }];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
