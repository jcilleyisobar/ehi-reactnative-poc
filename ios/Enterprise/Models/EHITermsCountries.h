//
//  EHICountryTerms.h
//  Enterprise
//
//  Created by frhoads on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHITermsEU.h"

@interface EHITermsCountries : EHIModel
@property (strong  , nonatomic, readonly) NSArray<EHITermsEU> *termsLanguages;
@end
