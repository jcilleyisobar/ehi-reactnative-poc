//
//  EHIWelcomeViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 09.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIWelcomeViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *signinTitle;
@property (copy, nonatomic, readonly) NSAttributedString *continueTitle;
@property (copy, nonatomic, readonly) NSString *joinTitle;

- (void)selectSignIn;
- (void)selectSkip;
- (void)selectJoin;

@end
