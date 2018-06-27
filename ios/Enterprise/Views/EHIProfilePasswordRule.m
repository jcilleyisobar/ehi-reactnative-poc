//
//  EHIProfilePasswordRule.m
//  Enterprise
//
//  Created by cgross on 12/21/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePasswordRule.h"
#import "EHIUserManager.h"

@interface EHIProfilePasswordRule ()
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) BOOL (^validate)(NSString *);
@end

@implementation EHIProfilePasswordRule

+ (instancetype)ruleWithTitle:(NSString *)title validation:(BOOL (^)(NSString *))validation;
{
    EHIProfilePasswordRule *rule = [EHIProfilePasswordRule new];
    rule.title    = title;
    rule.validate = validation;
    return rule;
}

- (BOOL)passedForPassword:(NSString *)password
{
    return self.validate(password) && password.length;
}

+ (EHIProfilePasswordRule *)eightCharactersRule
{
    return [EHIProfilePasswordRule ruleWithTitle:
            EHILocalizedString(@"cp_must_be_at_least_8_characters", @"Must be at least 8 characters", @"")
                                      validation:^BOOL(NSString *password) {
                                          return password.length >= 8;
    }];
}

+ (EHIProfilePasswordRule *)containsLettersRules
{
    return [EHIProfilePasswordRule ruleWithTitle:
            EHILocalizedString(@"cp_must_contain_letter", @"Must contain a letter", @"")
                                      validation:^BOOL(NSString *password) {
                                          return ([password rangeOfCharacterFromSet:[NSCharacterSet letterCharacterSet]].location != NSNotFound);
    }];
}

+ (EHIProfilePasswordRule *)containsNumbersRules
{
    return [EHIProfilePasswordRule ruleWithTitle:
            EHILocalizedString(@"cp_must_contain_number", @"Must contain a number", @"")
                                      validation:^BOOL(NSString *password) {
                                          return ([password rangeOfCharacterFromSet:[NSCharacterSet characterSetWithCharactersInString:@"0123456789"]].location != NSNotFound);
    }];
}

+ (EHIProfilePasswordRule *)forbiddenRules
{
    return [EHIProfilePasswordRule ruleWithTitle:
            EHILocalizedString(@"cp_cannot_contain_condition", @"Cannot contain the word \"password\", an email address, or your name", @"")
                                      validation:^BOOL(NSString *password) {
                                          NSArray *blockedWords = @[@"password", @"passwort", @"contraseña", @"mot de passe", @"motdepasse", @"senha", @"wachtwoord" , @"palavra-passe", @"palavrapasse", @"kennwort"];
                                          EHIUser *user = [EHIUserManager sharedInstance].currentUser;
                                          if (user) {
                                              blockedWords = [blockedWords arrayByAddingObjectsFromArray:@[user.firstName, user.lastName]];
                                          }
                                          
                                          return password.length >= 8 && !password.ehi_validEmail && blockedWords.none(^(NSString *word) {
                                              return [password localizedCaseInsensitiveContainsString:word];
                                          });
    }];
}

@end
