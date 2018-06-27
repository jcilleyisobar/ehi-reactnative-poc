//
//  EHIReservationExtrasLongButton.m
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationConfirmationFooter.h"
#import "EHIReservationPriceButton.h"
#import "EHIActionButton.h"
#import "EHIActivityIndicator.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationConfirmationFooter ()
@property (weak, nonatomic) IBOutlet EHIActionButton *mainButton;
@property (weak, nonatomic) IBOutlet EHIReservationPriceButton *priceButton;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *indicator;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *separatorTopConstraint;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *separatorBottomConstraint;
@property (weak, nonatomic) IBOutlet UIView *verticalPriceSeparator;
@property (weak, nonatomic) IBOutlet UIView *bottomView;
@end

@implementation EHIReservationConfirmationFooter

- (id)awakeAfterUsingCoder:(NSCoder *)aDecoder
{
    if(self.subviews.count) {
        return self;
    }
    
    EHIReservationConfirmationFooter *replacement = [self.class ehi_instanceFromNib];
    [replacement setFrame:self.frame];
    [replacement setTranslatesAutoresizingMaskIntoConstraints:self.translatesAutoresizingMaskIntoConstraints];
    [replacement addConstraints:[self ehi_migrateConstraintsToView:replacement]];
    
    return replacement;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
       
    self.indicator.type = EHIActivityIndicatorTypeSmallWhite;
}

# pragma mark - Actions

- (BOOL)beginTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    self.highlighted = YES;
    return YES;
}

- (void)endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    self.highlighted = YES;
}

- (void)cancelTrackingWithEvent:(UIEvent *)event
{
    self.highlighted = NO;
}

# pragma mark - Setters

- (void)setPrice:(id<EHIPriceContext>)price
{
    _price = price;
    
    // only default the subtitle type if it hasn't already been set
    self.priceButton.subtitleType = self.priceButton.subtitleType != EHIReservationPriceButtonSubtitleTypeNone
        ? self.priceButton.subtitleType
        : EHIReservationPriceButtonSubtitleTypeTotalCost;
    
    self.priceButton.hidesArrow = YES;
    self.priceButton.price = price;
}

- (void)setPriceType:(EHIReservationPriceButtonType)priceType
{
    self.priceButton.priceType = priceType;
}

- (void)setPriceSubtitleType:(EHIReservationPriceButtonSubtitleType)priceSubtitleType
{
    self.priceButton.subtitleType = priceSubtitleType;
    self.priceButton.price = self.price;
}

- (void)setPriceButtonLayout:(EHIReservationConfirmationFooterPriceButtonLayout)priceButtonLayout
{
    _priceButtonLayout = priceButtonLayout;

    switch(priceButtonLayout) {
        case EHIReservationConfirmationFooterPriceButtonLayoutAlwaysShowPrice: {
            
            // re bind the price to the button and let it construct the attributed text
            self.priceButton.price = self.price;
            NSAttributedString *priceText = self.priceButton.ehi_attributedTitle;
            
            // override configuration state
            [self.priceButton setAttributedTitle:priceText forState:UIControlStateDisabled];
            break;
        }
        case EHIReservationConfirmationFooterPriceButtonLayoutDefault:
            [self.priceButton setAttributedTitle:[NSAttributedString new] forState:UIControlStateDisabled];
            break;
    }
}

- (void)setTitle:(NSString *)title
{
    self.mainButton.ehi_title = title;
}

- (void)setAttributedTitle:(NSAttributedString *)attributedTitle
{
    self.mainButton.ehi_attributedTitle = attributedTitle;
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    
    self.mainButton.highlighted  = highlighted;
    self.priceButton.highlighted = highlighted;
    
    self.bottomView.backgroundColor = self.mainButton.backgroundColor;
}

- (void)setEnabled:(BOOL)enabled
{
    [super setEnabled:enabled];
   
    [self changeButtonState:enabled];
}

- (void)setIsFauxDisabled:(BOOL)isFauxDisabled
{
    [self changeButtonState:isFauxDisabled];
}

- (void)changeButtonState:(BOOL)enabled
{
    self.mainButton.enabled  = enabled;
    self.priceButton.enabled = enabled;
    
    self.bottomView.backgroundColor = self.mainButton.backgroundColor;
    
    self.verticalPriceSeparator.backgroundColor = enabled ? [UIColor ehi_darkGreenColor] : [UIColor whiteColor];
}

- (void)setHide:(BOOL)hide
{
    _hide = hide;
    self.separatorTopConstraint.isDisabled    = hide;
    self.separatorBottomConstraint.isDisabled = hide;
}

# pragma mark - Accessors

- (EHIReservationPriceButtonSubtitleType)priceSubtitleType
{
    return self.priceButton.subtitleType;
}

# pragma mark - EHILoadable

- (BOOL)isLoading
{
    return self.indicator.isAnimating;
}

- (void)setIsLoading:(BOOL)isLoading
{
    [self setIsLoading:isLoading animated:YES];
}

- (void)setIsLoading:(BOOL)isLoading animated:(BOOL)animated
{
    self.indicator.isAnimating = isLoading;
}

@end
