//
//  NSLocale+Country.h
//  Enterprise
//
//  Created by cgross on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NSLocale+Utility.h"
#import "EHICountry.h"

@interface NSLocale (Country)

/** Returns the current locale's country */
+ (EHICountry *)ehi_country;

@end
