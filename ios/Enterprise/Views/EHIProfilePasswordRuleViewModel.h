//
//  EHIProfilePasswordRuleViewModel.h
//  Enterprise
//
//  Created by cgross on 12/22/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIProfilePasswordRuleViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *iconName;

- (void)invalidatePassword:(NSString *)password;
- (void)invalidatePassword:(NSString *)password shouldShowFailed:(BOOL)shouldShowFailed;

@end
