//
//  EHIReservationInfoButton.h
//  Enterprise
//
//  Created by Michael Place on 3/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButton.h"
#import "EHIPriceContext.h"
#import "EHIReservationPriceButtonType.h"

@interface EHIReservationPriceButton : EHIButton
@property (strong, nonatomic) id<EHIPriceContext> price;
@property (assign, nonatomic) BOOL hidesArrow;
@property (assign, nonatomic) EHIReservationPriceButtonType priceType;
@property (assign, nonatomic) EHIReservationPriceButtonSubtitleType subtitleType;
@end
