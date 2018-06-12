//
//  EHIPaymentInputViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentInputViewModel.h"
#import "EHICreditCardFormatter.h"
#import "EHIWebViewModel.h"
#import "EHIAnalytics.h"

@interface EHIPaymentInputViewModel ()
@property (assign, nonatomic) EHICreditCardType cardType;
@property (assign, nonatomic, readonly) NSInteger fullExpirationYear;
@end

@implementation EHIPaymentInputViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _nameTitle       = EHILocalizedString(@"payment_name_field_title", @"NAME ON CARD", @"");
        _cardNumberTitle = EHILocalizedString(@"payment_card_number_field_title", @"CARD NUMBER", @"");
        _expirationTitle = EHILocalizedString(@"payment_expiration_field_title", @"EXPIRATION DATE", @"");
        _cvvTitle        = EHILocalizedString(@"payment_cvv_code_field_title", @"CVV CODE", @"");
        
        _namePlaceholder            = EHILocalizedString(@"payment_name_field_placeholder", @"placeholder", @"");
        _cardNumberPlaceholder      = EHILocalizedString(@"payment_card_number_field_placeholder", @"placeholder", @"");
        _expirationMonthPlaceholder = EHILocalizedString(@"payment_expiration_month_field_placeholder", @"MM", @"");
        _expirationYearPlaceholder  = EHILocalizedString(@"payment_expiration_year_field_placeholder", @"YY", @"");
        _cvvPlaceholder             = EHILocalizedString(@"payment_cvv_code_field_placeholder", @"321", @"");
        
        _saveTitle  = EHILocalizedString(@"add_card_save_for_later_text", @"Save this card for later use", @"");
        _termsTitle = [self policyTermsAndConditions];
    }
    
    return self;
}

- (NSAttributedString *)policyTermsAndConditions
{
    NSString *policiesText = EHILocalizedString(@"review_prepay_policies_read", @"I have read the #{policies}", @"");
    NSString *policiesName = EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
    
    NSAttributedString *attributedPoliciesName =
    [NSAttributedString attributedStringWithString:policiesName
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleRegular size:14.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            [EHIAnalytics trackAction:EHIAnalyticsResActionPrepayPolicy handler:nil];
                                            
                                            [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] present];
                                        }];
    
    EHIAttributedStringBuilder *policiesBuilder = EHIAttributedStringBuilder.new
        .text(policiesText).fontStyle(EHIFontStyleRegular, 14.0f).replace(@"#{policies}", attributedPoliciesName);
    
    policiesBuilder.attributes(@{NSBaselineOffsetAttributeName: @1});
    
    return policiesBuilder.string;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[NSString class]]) {
        [self updateWithName:model];
    }
}

- (void)updateWithName:(NSString *)name
{
    self.name = name;
}

# pragma mark - Setters

- (void)setName:(NSString *)name
{
    _name = name;
    
    self.showNameError = NO;
}

- (void)setCardNumber:(NSString *)cardNumber
{
    _cardNumber = [EHICreditCardFormatter formatCardNumber:cardNumber];
    
    // update type
    self.cardType = [EHICreditCardFormatter typeForCardNumber:self.cardNumber];
    
    // remove error state
    self.showCardNumberError = NO;
}

- (void)setExpirationMonth:(NSString *)expirationMonth
{
    NSInteger month = expirationMonth.integerValue;
    
    // wipe
    if (expirationMonth.length == 0) {
        _expirationMonth = nil;
    }
    else if (expirationMonth.length < _expirationMonth.length) {
        _expirationMonth = expirationMonth;
    }
    else if(month == 1 && _expirationMonth != nil && _expirationMonth.integerValue < 10) {
        _expirationMonth = [NSString stringWithFormat:@"%02d", (int)month];
    }
    // assume single digit month and fill out field
    else if(1 < month && month < 10) {
        _expirationMonth = [NSString stringWithFormat:@"%02d", (int)month];
    }
    // don't allow
    else if(month > 12) {
        // no-op
    }
    else {
        _expirationMonth = @(month).description;
    }
    
    // remove error state
    self.showExpirationMonthError = NO;
}

- (void)setExpirationYear:(NSString *)expirationYear
{
    NSInteger year = expirationYear.integerValue;
    
    if(year == 0) {
        _expirationYear = nil;
    }
    // don't allow 0 or tens digit between 4 and 9
    else if((3 < year && year < 10) || year > 40) {
        // no-op
    }
    else {
        _expirationYear = @(year).description;
    }
    
    // remove error state
    self.showExpirationYearError = NO;
}

- (void)setCvv:(NSString *)ccv
{
    if(ccv.length > 4) {
        ccv = [ccv substringToIndex:4];
    }
    
    _cvv = ccv;
    
    // remove error state
    self.showCvvError = NO;
}

# pragma mark - Accessors

- (NSInteger)fullExpirationYear
{
    return self.expirationYear.integerValue + 2000;
}

- (NSString *)cardImageName
{
    return [EHICreditCardFormatter cardIconForCardType:self.cardType];
}

- (BOOL)invalidCreditCard
{
    return [self invalidCreditCard:NO];
}

- (void)setSaveCard:(BOOL)saveCard
{
    _saveCard = saveCard;
    
    [EHIAnalytics trackAction:EHIAnalyticsProfileActionSavePreferredCard handler:nil];
}

- (void)setPoliciesRead:(BOOL)policiesRead
{
    _policiesRead = policiesRead;
    
    [EHIAnalytics trackAction:EHIAnalyticsProfileActionTermsAndConditions handler:nil];
}

# pragma mark - Create credit card

- (EHICreditCard *)createCreditCard
{
    // validate while exposing errors
    if([self invalidCreditCard:YES]) {
        return nil;
    }
    
    EHICreditCard *card;
    return [EHICreditCard modelWithDictionary:@{
        @key(card.holderName)      : self.name ?: @"",
        @key(card.cardNumber)      : [self.cardNumber ehi_stripNonDecimalCharacters],
        @key(card.type)            : @(self.cardType),
        @key(card.expirationMonth) : @(self.expirationMonth.integerValue),
        @key(card.expirationYear)  : @(self.fullExpirationYear),
        @key(card.save)            : @(self.saveCard),
        @key(card.cvvNumber)       : self.cvv,
    }];
}

//
// Helpers
//

- (BOOL)invalidCreditCard:(BOOL)showErrors
{
    CGFloat cardNumberLength = self.cardNumber.ehi_stripNonDecimalCharacters.length;
    BOOL invalidName  = self.name.length == 0;
    BOOL invalidCard  = cardNumberLength < 15 || cardNumberLength > 16;
    BOOL invalidMonth = self.expirationMonth.integerValue == 0;
    BOOL invalidYear  = self.fullExpirationYear < [[NSDate date] ehi_valueForUnit:NSCalendarUnitYear];
    BOOL invalidCcv   = !(self.cvv.length == 3 || self.cvv.length == 4);
    
    if(showErrors) {
        self.showNameError            = invalidName;
        self.showCardNumberError      = invalidCard;
        self.showExpirationMonthError = invalidMonth;
        self.showExpirationYearError  = invalidYear;
        self.showCvvError             = invalidCcv;
    }
    
    return invalidName || invalidCard || invalidMonth || invalidYear || invalidCcv;
}

@end
