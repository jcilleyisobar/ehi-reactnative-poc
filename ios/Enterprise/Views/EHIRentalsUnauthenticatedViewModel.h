//
//  EHIRentalsUnauthenticatedViewModel.h
//  Enterprise
//
//  Created by fhu on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRentalsUnauthenticatedViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *signinHeaderText;
@property (copy, nonatomic) NSString *signinDetailText;
@property (copy, nonatomic) NSString *signinButtonText;
@property (copy, nonatomic) NSString *lookupDetailsText;
@property (copy, nonatomic) NSString *lookupButtonText;

- (void)signin;
- (void)lookup;

@end
