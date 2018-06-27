//
//  EHIProfilePasswordRule.h
//  Enterprise
//
//  Created by cgross on 12/21/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIProfilePasswordRule : EHIModel

@property (copy, nonatomic, readonly) NSString *title;

+ (EHIProfilePasswordRule *)eightCharactersRule;
+ (EHIProfilePasswordRule *)containsLettersRules;
+ (EHIProfilePasswordRule *)containsNumbersRules;
+ (EHIProfilePasswordRule *)forbiddenRules;

- (BOOL)passedForPassword:(NSString *)password;

@end
