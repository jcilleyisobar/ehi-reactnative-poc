//
//  EHIExtrasExtraViewModel.m
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHIStepperControl.h"
#import "EHIInfoModalViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationBuilder.h"

@interface EHIExtrasExtraViewModel () <EHIComparable>
@property (copy, nonatomic) EHICarClassExtra *extra;
@property (copy, nonatomic) EHICarClassPriceLineItem *lineItem;
@property (copy, nonatomic) NSString *rateText;
@property (copy, nonatomic) NSString *totalText;
@end

@implementation EHIExtrasExtraViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _moreInfoText = [self constructMoreInfoText];
        _stepperTitle = EHILocalizedString(@"extras_how_many", @"How Many?", @"reservation extras item stepper title");
    }
    
    return self;
}

- (void)updateWithExtra:(EHICarClassExtra *)extra andPaymentLineItem:(EHICarClassPriceLineItem *)lineItem
{
    self.extra       = extra;
    self.rateText    = extra.rateDescriptionWithMax;
    self.details     = extra.shortDetails;
    self.maxQuantity = extra.maxQuantity;
    self.amount      = extra.selectedQuantity;
    self.lineItem    = lineItem;
    [self invalidateTotalPrice];
}

//
// Helpers
//

- (NSAttributedString *)constructMoreInfoText
{
    NSString *moreInfoText = EHILocalizedString(@"reservation_extras_item_more_info_text", @"More Info", @"reservation extras item more info text");
    
    return EHIAttributedStringBuilder.new.text(moreInfoText)
        .size(14).color([UIColor ehi_greenColor])
        .attributes(@{ NSUnderlineStyleAttributeName: @(NSUnderlineStyleSingle) }).string;
}

- (NSString *)updatedPrice
{
    EHIPrice *price =  self.lineItem.total ?: self.extra.total;
        
    return price && self.extra.isSelected && self.extra.status != EHICarClassExtraStatusIncluded ? [EHIPriceFormatter format:price].string : nil;
}

# pragma mark - Actions

- (void)selectExtra:(BOOL)selected completion:(void (^)(BOOL didToggle))completion
{
    void (^toggleExtra)(BOOL shouldContinue) = ^(BOOL shouldContinue) {
        if(shouldContinue) {
            [self enableExtra:selected];
        }
        
        // passthrough to our completion handler
        ehi_call(completion)(shouldContinue);
    };
    
    // prompt when enabling an on request extra
    if(selected && self.extra.allocation == EHICarClassExtraAllocationOnRequest) {
        [[EHIReservationBuilder sharedInstance] promptOnRequestSelectionWithHandler:toggleExtra];
    }
    
    // otherwise, just toggle the extra
    else {
        toggleExtra(YES);
    }
}

- (void)showMoreInfo
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionShowMore handler:^(EHIAnalyticsContext *context) {
        context[EHIAnalyticsResTappedExtraKey] = self.extra.code;
    }];
    
    // present an info modal from the extra
    EHIInfoModalViewModel *infoModal = [[EHIInfoModalViewModel alloc] initWithModel:self.extra];
    infoModal.secondButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
    [infoModal present:nil];
}

//
// Helpers
//

- (void)enableExtra:(BOOL)enabled
{
    // update our amount
    self.amount = enabled ? 1 : 0;
    
    // track the action
    NSString *action = enabled ? EHIAnalyticsResActionSelectExtra : EHIAnalyticsResActionDeselectExtra;
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHICarClassExtra class] encodable:self.extra];
    }];
}

# pragma mark - Setters

- (void)setAmount:(NSInteger)amount
{
    // bind amount into acceptable range
    amount = self.shouldClamp ? EHIClamp(amount, 0, self.maxQuantity) : amount;
    
    _amount = amount;
   
    // synchronize data model
    self.extra.selectedQuantity = amount;

    // enable stepper buttons properly
    self.minusButtonEnabled = amount > 1;
    self.plusButtonEnabled  = amount < self.maxQuantity;
    self.shouldExpandToggle = amount && self.maxQuantity > 1;
   
    // invalidates the price, clearing it out if amount is reduced to 0
    [self invalidateTotalPrice];
}

//
// Helpers
//

- (void)invalidateTotalPrice
{
    self.totalText = [self updatedPrice];
}

# pragma mark - Accessors

- (NSString *)title
{
    return self.extra.name;
}

- (BOOL)shouldClamp
{
    return !self.extra.isIncluded && !self.extra.isMandatory;
}

# pragma mark - Fallback

+ (instancetype)fallbackForType:(EHIExtrasType)type
{
    EHIExtrasExtraViewModel *model = [EHIExtrasExtraViewModel new];
    model.defaultText = [model fallbackTitleForType:type];
    return model;
}

- (NSString *)fallbackTitleForType:(EHIExtrasType)type
{
    switch (type) {
        case EHIExtrasTypeEquipment:
            return EHILocalizedString(@"reservation_extras_item_equipment_placeholder_text", @"You will be able to add equipment extras at the counter.", @"reservation extras equipment placeholder text");
        case EHIExtrasTypeFuel:
            return EHILocalizedString(@"reservation_extras_item_fuel_placeholder_text", @"You will be able to add fuel options at the counter.", @"reservation extras fuel placeholder text");
        case EHIExtrasTypeProtection:
            return EHILocalizedString(@"reservation_extras_item_protection_placeholder_text", @"You will be able to add protection extras at the counter.", @"reservation extras protection placeholder text");
    }
}

# pragma mark - EHIComparable

- (id)uid
{
    return self.extra.code ?: self.defaultText;
}

- (BOOL)isEqual:(id<EHIComparable>)object
{
    return [object conformsToProtocol:@protocol(EHIComparable)] && [self.uid isEqual:object.uid];
}

@end
