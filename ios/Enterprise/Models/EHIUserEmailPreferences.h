//
//  EHIUserEmailPreferences.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIOptionalBoolean.h"

@interface EHIUserEmailPreferences : EHIModel
@property (assign, nonatomic, readonly) BOOL rentalReceipts;
@property (assign, nonatomic, readonly) BOOL partnerOffers;
@property (copy  , nonatomic, readonly) NSString *subscriberPreferencesUrl;
@property (assign, nonatomic) EHIOptionalBoolean specialOffers;
@end
