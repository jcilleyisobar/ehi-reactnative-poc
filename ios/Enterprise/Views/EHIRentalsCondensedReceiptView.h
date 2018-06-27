//
//  EHIRentalsCondensedReceiptView.h
//  Enterprise
//
//  Created by cgross on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIView.h"
#import "EHIUserRental.h"

@interface EHIRentalsCondensedReceiptView : EHIView

+ (void)captureReceiptWithRental:(EHIUserRental *)rental;

@end
