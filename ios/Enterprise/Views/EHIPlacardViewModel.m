//
//  EHIPlacardViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 8/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPlacardViewModel.h"
#import "EHIReservationBuilder.h"

@interface EHIPlacardViewModel ()
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (strong   , nonatomic) EHICarClass *carClass;
@property (assign   , nonatomic) EHIPlacardType type;
@end

@implementation EHIPlacardViewModel

- (instancetype)initWithType:(EHIPlacardType)type carClass:(EHICarClass *)carClass
{
    if(self = [super init]) {
        self.carClass = carClass ?: self.builder.selectedCarClass;
        self.type = type;
    }
    
    return self;
}

- (void)setType:(EHIPlacardType)type
{
    _type = type;
    _isPriceDetails = type == EHIPlacardTypePriceDetails;
    switch (type) {
        case EHIPlacardTypeExtras: {
            _title         = [self titleForType:type];
            _hidesInfoIcon = YES;
            break;
        }
        case EHIPlacardTypePayment: {
            _title         = [self titleForType:type];
            _hidesInfoIcon = NO;
            break;
        }
        case EHIPlacardTypePriceDetails: {
            _title = [self titleForType:type];
            _hidesInfoIcon = YES;
        }
    }
}

- (NSAttributedString *)titleForType:(EHIPlacardType)type
{
    NSString *title;
    CGFloat size = 20;
    switch (type) {
        case EHIPlacardTypeExtras: {
            title = EHILocalizedString(@"reservation_extras_customize_title", @"CUSTOMIZE YOUR RENTAL", @"");
            break;
        }
        case EHIPlacardTypePayment: {
            title = EHILocalizedString(@"choose_your_rate_section_title", @"CHOOSE A PAYMENT OPTION", @"");
            break;
        }
        case EHIPlacardTypePriceDetails: {
            title = self.rateTitle;
            size  = 13.0f;
            break;
        }
    }
    
    return EHIAttributedStringBuilder.new.appendText(title).fontStyle(EHIFontStyleBold, size).string;
}

- (NSString *)rateTitle
{
    EHICarClass *carClass = self.carClass;
    BOOL hasDiscount = self.builder.discount != nil;
    BOOL inReservationFlow = self.builder.selectedCarClass != nil;
    if(inReservationFlow && !hasDiscount) {
        return nil;
    } else if(carClass.isPromotionalRate) {
        return EHILocalizedString(@"car_class_cell_promotional_rate_title", @"PROMOTIONAL RATE", @"title for the promotional rate view");
    } else if (carClass.isNegotiatedRate) {
        return EHILocalizedString(@"car_class_cell_negotiated_rate_title", @"CUSTOM RATE", @"title for the negotiated rate view");
    } else {
        return nil;
    }
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
