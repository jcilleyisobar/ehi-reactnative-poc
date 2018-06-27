//
//  EHIReservationExtrasLongButton.h
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHIReservationPriceButtonType.h"
#import "EHILoadable.h"
#import "EHIPriceContext.h"

typedef NS_ENUM(NSInteger, EHIReservationConfirmationFooterPriceButtonLayout) {
    EHIReservationConfirmationFooterPriceButtonLayoutDefault,
    EHIReservationConfirmationFooterPriceButtonLayoutAlwaysShowPrice
};

@interface EHIReservationConfirmationFooter : UIControl <EHILoadable>
/** Price context model to populate the price button */
@property (strong, nonatomic) id<EHIPriceContext> price;
/** Title of the main button */
@property (copy  , nonatomic) NSString *title;
/** Attributed title of the main button */
@property (copy  , nonatomic) NSAttributedString *attributedTitle;
/** @c YES to show the loading indicator on the price button */
@property (assign, nonatomic) BOOL isLoading;
/** The layout of the price button */
@property (assign, nonatomic) EHIReservationConfirmationFooterPriceButtonLayout priceButtonLayout;
/** Subtitle type of the price button */
@property (assign, nonatomic) EHIReservationPriceButtonSubtitleType priceSubtitleType;
/** Price type of the price button */
@property (assign, nonatomic) EHIReservationPriceButtonType priceType;
/** @c YES to hide the button */
@property (assign, nonatomic) BOOL hide;
/** @c YES if the button should render as disabled, but still receieve touch events */
@property (assign, nonatomic) BOOL isFauxDisabled;

@end
