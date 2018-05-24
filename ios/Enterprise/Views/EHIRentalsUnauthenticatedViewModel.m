//
//  EHIRentalsUnauthenticatedViewModel.m
//  Enterprise
//
//  Created by fhu on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsUnauthenticatedViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserManager.h"
#import "EHISigninViewModel.h"

@implementation EHIRentalsUnauthenticatedViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _signinHeaderText  = EHILocalizedString(@"rentals_unauth_title", @"Did you make a reservation while signed into your Enterprise Plus account?", @"");
        _signinDetailText  = EHILocalizedString(@"rentals_unauth_subtitle", @"Please sign in to view your rentals.", @"");
        _signinButtonText  = EHILocalizedString(@"standard_signin_button_text", @"SIGN IN", @"standard sign in button text");
        _lookupDetailsText = EHILocalizedString(@"rentals_unauth_lookup*", @"Not an Enterprise Plus member and need to review the details of your rental?", @"");
        _lookupButtonText  = EHILocalizedString(@"standard_lookup_rental_button_text", @"LOOK UP RENTAL", @"standard lookup rental button text");
    }
    
    return self;
}

//
// Helpers
//

# pragma mark - Actions

- (void)lookup
{
    self.router.transition
        .present(EHIScreenRentalLookup).start(nil);
}

- (void)signin
{
    EHISigninViewModel *model = [EHISigninViewModel new];
    
    self.router.transition
        .present(EHIScreenMainSignin).object(model).start(nil);
}

@end
