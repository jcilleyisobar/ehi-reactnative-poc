//
//  EHIContractDiscountViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 5/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIContractDiscountViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIWebViewModel.h"

@interface EHIContractDiscountViewModel()
@property (assign, nonatomic) BOOL promoAvailable;
@property (copy  , nonatomic) NSString *code;
@property (copy  , nonatomic) NSString *codePrefix;
@property (copy  , nonatomic) NSString *terms;
@property (copy  , nonatomic) NSString *termsButtonTitle;
@property (assign  , nonatomic) EHIContractType contractType;
@property (strong, nonatomic, readonly) EHIReservationBuilder *builder;
@property (assign, nonatomic) EHIContractDiscoutFlow flow;
@end

@implementation EHIContractDiscountViewModel

- (instancetype)initWithFlow:(EHIContractDiscoutFlow)flow
{
    if(self = [super init]) {
        self.termsButtonTitle = EHILocalizedString(@"view_terms_and_conditions_promotion", @"View Terms & Conditions", @"");
        self.flow = flow;
    }
    return self;
}

- (void)updateWithModel:(EHIContractDetails *)model
{
    [super updateWithModel:model];

    if([model isKindOfClass:[EHIContractDetails class]]) {
        [self updateWithDiscount:model];
    }
}

- (void)updateWithDiscount:(EHIContractDetails *)discount
{
    self.code         = discount.name;
    self.codePrefix   = [self codePrefixForDiscount:discount];
    self.terms        = discount.termsAndConditions;
    self.contractType = discount.contractType;
    self.title        = self.titleForDiscount;
}

- (NSAttributedString *)titleForDiscount
{
    NSString *codePrefix = self.codePrefix;
    NSString *code       = self.code;
    
    EHIAttributedStringBuilder* builder = EHIAttributedStringBuilder.new;
    
    builder.appendText(codePrefix).fontStyle(EHIFontStyleLight, 16.0f);
    builder.space.appendText(code).fontStyle(EHIFontStyleBold, 16.0f);
    
    if (self.flow == EHIContractDiscoutFlowCarClassSelect){
        if (self.contractType == EHIContractTypeCorporate
                && self.promoAvailable){
            builder.newline
            .appendText(EHILocalizedString(@"class_select_discount_subtitle", @"A custom rate has been applied to your rental.", @"Default subtitle with custom rate wording")).fontStyle(EHIFontStyleLight, 16.0f);
        }else if (!self.promoAvailable){
            builder.newline
            .appendText(EHILocalizedString(@"class_select_discount_unavailable_subtitle", @"Promotional rates are not available for this reservation.", @"Subtitle promo wasn't applied")).fontStyle(EHIFontStyleLight, 16.0f);
        }
    }
    
    return builder.string;
}

- (void)didTapPolicies
{
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeWeekendSpecialTermsAndConditions htmlString:self.terms] push];
}

//
// Helpers
//

- (NSString *)codePrefixForDiscount:(EHIContractDetails *)discount
{
    if(!self.promoAvailable) {
        return EHILocalizedString(@"class_select_discount_unavailable_prefix", @"Promotion Not Applicable:", @"");
    } else if(discount.contractType == EHIContractTypeCorporate) {
        return EHILocalizedString(@"class_select_discount_contract_prefix", @"You've added a contract: ", @"Prefix for the class select contract title");
    } else {
        return EHILocalizedString(@"class_select_discount_coupon_prefix", @"You've added a coupon: ", @"Prefix for the class select coupont title");
    }
}

# pragma mark - Accessors

- (NSString *)iconName
{
    return self.promoAvailable ? @"icon_confirm_01" : @"icon_alert_02";
}

- (BOOL)shouldShowTerms
{
    return self.terms.length && self.promoAvailable;
}


- (void)setFlow:(EHIContractDiscoutFlow)flow
{
    _flow = flow;
    [self invalidateFlow];
}

- (void)invalidateFlow
{
    switch (self.flow) {
        case EHIContractDiscoutFlowCarClassSelect:
            self.promoAvailable = (self.builder.reservation.carClasses ?: @[]).any(^(EHICarClass *carClass) {
                return !carClass.isUnpromoted;
            });
            break;
        case EHIContractDiscoutFlowConfirmation:
            self.promoAvailable = !(self.builder.reservation.carClasses.count > 0 && self.builder.reservation.selectedCarClass.isUnpromoted);
            break;
    }
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
