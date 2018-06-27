//
//  EHIPriceContext.h
//  Enterprise
//
//  Created by mplace on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EHIPrice.h"

/**
 By itself, EHIPrice instances contain currency information and a currency value. This protocol is
 used to include important context information surrounding the price object, such as whether the currency 
 in the user's locale differs from the currency found in the reservation locale.
 */

@protocol EHIPriceContext <NSObject>
/** Price in the currency of the users locale */
- (EHIPrice *)viewPrice;
/** Price in the currency of the rental destination */
- (EHIPrice *)paymentPrice;
/** YES if the currency of the user's locale differs from the currency of the reservation locale */
- (BOOL)viewCurrencyDiffersFromSourceCurrency;
/** YES if currency should be converted */
- (BOOL)eligibleForCurrencyConvertion;
@end
