//
//  EHIReservationInfoButton.m
//  Enterprise
//
//  Created by Michael Place on 3/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationPriceButton.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationBuilder.h"
#import "EHIActivityIndicator.h"

@interface EHIReservationPriceButton ()
@property (weak, nonatomic) EHIActivityIndicator *indicator;
@end

@implementation EHIReservationPriceButton

# pragma mark - EHIButton

- (void)applyDefaults
{
    [super applyDefaults];
    
    // configure background colors
    [self setBackgroundColor:[UIColor ehi_greenColor]];
    [self setBackgroundColor:[UIColor ehi_greenColor] forState:UIControlStateNormal];
    [self setBackgroundColor:[UIColor ehi_grayColor4] forState:UIControlStateDisabled];
    
    [self invalidateView];
}

- (void)invalidateView
{
    if(self.hidesArrow) {
        [self setImage:nil forState:UIControlStateNormal];
      
        // clear the insets so that the title can lay out properly
        self.imageEdgeInsets = UIEdgeInsetsZero;
        self.titleEdgeInsets = UIEdgeInsetsZero;
        self.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    }
    else {
        UIImage *arrowImage = [UIImage imageNamed:@"arrow_smwhite"];
        [self setImage:arrowImage forState:UIControlStateNormal];
       
        // apply insets/alignment to position the title
        self.imageEdgeInsets = (UIEdgeInsets){ .right = EHILightPadding };
        self.titleEdgeInsets = (UIEdgeInsets){ .right = EHILightPadding * 2.0f + arrowImage.size.width };
        
        self.contentHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
        self.imageHorizontalAlignment   = UIControlContentHorizontalAlignmentRight;
    }
}

# pragma mark - Setters

- (void)setPrice:(id<EHIPriceContext>)price
{
    _price = price;
    
    [self invalidatePriceTitle];
}


- (void)setPriceType:(EHIReservationPriceButtonType)priceType
{
    _priceType = priceType;
    
    [self invalidatePriceTitle];
}

- (void)setHidesArrow:(BOOL)hidesArrow
{
    _hidesArrow = hidesArrow;
    [self invalidateView];
}

//
// Helpers
//

- (void)invalidatePriceTitle
{
    // show fallback title if not type price or no price exists
    if(self.priceType != EHIReservationPriceButtonTypePrice || !self.price) {
        self.ehi_attributedTitle = [self fallbackTitleForPriceType:self.priceType];
        return;
    }
    
    NSString *subtitle;
    switch(self.subtitleType) {
        case EHIReservationPriceButtonSubtitleTypeTotalCost:
        case EHIReservationPriceButtonSubtitleTypeTotalCostOptionalNote:
            subtitle = EHILocalizedString(@"reservation_price_subtitle_total_cost", @"TOTAL COST", @"'Total Cost' subtitle for the car class price button");
            break;
        case EHIReservationPriceButtonSubtitleTypeVehicle:
            subtitle = EHILocalizedString(@"reservation_price_subtitle_vehicle", @"VEHICLE", @"'Vehicle' subtitle for the car class price button");
            break;
        case EHIReservationPriceButtonSubtitleTypeNone:
            subtitle = nil;
            break;
        case EHIReservationPriceButtonSubtitleTypeAfterPoints:
            subtitle = EHILocalizedString(@"redemption_price_subtitle_after_points", @"AFTER POINTS", @""); break;
        case EHIReservationPriceButtonSubtitleTypeModify:
            subtitle = EHILocalizedString(@"review_payment_unpaid_amount_action", @"#{amount} Unpaid Amount", @"");
            break;
        case EHIReservationPriceButtonSubtitleTypeUpdatedTotal:
            subtitle = EHILocalizedString(@"review_payment_updated_total_title", @"UPDATED TOTAL", @"'UPDATED TOTAL' subtitle for review");
            break;
    }
   
    // if the source currency differs from the view currency, denote it with a footnote
    if(self.price.viewCurrencyDiffersFromSourceCurrency && self.subtitleType == EHIReservationPriceButtonSubtitleTypeTotalCostOptionalNote) {
        subtitle = [subtitle stringByAppendingString:@"**"];
    }
    
    // generate and set the attributed title
    self.ehi_attributedTitle = [self attributedTitleForPrice:self.price subtitle:subtitle];
}

- (NSAttributedString *)attributedTitleForPrice:(id<EHIPriceContext>)price subtitle:(NSString *)subtitle
{
    EHIPriceFormatter *priceFormatter = [EHIPriceFormatter format:price.viewPrice];
    // decreasing the font size if the price exceeds 7 characters in an effort to squeeze it into the fixed button width
    priceFormatter.size(priceFormatter.string.length > 7 ? EHIPriceFontSizeMedium * .9f : EHIPriceFontSizeMedium);
    
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
        .paragraph(2, NSTextAlignmentRight).append(priceFormatter.attributedString);
    
    // append a new line with subtitle, if any
    if(subtitle) {
        builder.newline.appendText(subtitle).size(subtitle.length > 10 ? 12.0f : 14.0f);
    }
    
    return builder.string;
}

- (NSAttributedString *)fallbackTitleForPriceType:(EHIReservationPriceButtonType)type
{
    NSString *title;
    switch(self.priceType) {
        case EHIReservationPriceButtonTypePrice:
        case EHIReservationPriceButtonTypeSecretRate:
            title = EHILocalizedString(@"reservation_price_unavailable", @"No Pricing Available", @"Reservation price button fallback text when price exists"); break;
        case EHIReservationPriceButtonTypeCallForAvailability:
            title = EHILocalizedString(@"reservation_price_call_for_availability", @"Call for Availability", @"Reservation price button fallback text when car class is call for availability"); break;
        case EHIReservationPriceButtonTypeWebBook:
            title = EHILocalizedString(@"reservation_price_leave_the_app", @"LEAVE THE APP TO BOOK", @"Reservation price button fallback text when car requires booking in web"); break;
    }
    
    return [EHIAttributedStringBuilder new].text(title).size(16.0).string;
}

@end
