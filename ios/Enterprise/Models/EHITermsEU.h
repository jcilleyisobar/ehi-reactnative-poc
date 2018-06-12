//
//  EHITermsEU.h
//  Enterprise
//
//  Created by frhoads on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHITermsEU : EHIModel
@property (copy  , nonatomic, readonly) NSString *termsAndConditionsText;
@property (copy  , nonatomic, readonly) NSString *language;
@property (copy  , nonatomic, readonly) NSString *countryCode;
@end

EHIAnnotatable(EHITermsEU)
