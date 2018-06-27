//
//  EHIRentalsLookupViewModel.m
//  Enterprise
//
//  Created by fhu on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRentalsLookupViewModel.h"
#import "EHIConfirmationViewModel.h"
#import "EHIServices+Reservation.h"
#import "EHIUserManager+Analytics.h"
#import "EHISigninRecoveryType.h"
#import "EHIConfiguration.h"
#import "EHISigninViewModel.h"

@interface EHIRentalsLookupViewModel ()
@property (assign, nonatomic) BOOL isLoading;
@property (copy  , nonatomic) NSError *error;
@end

@implementation EHIRentalsLookupViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // intiailize static content
        _title = EHILocalizedString(@"rentals_lookup_navigation_title", @"Look Up Rental", @"");
        _firstNameTitle = [EHILocalizedString(@"rentals_lookup_first_name_title", @"FIRST NAME ON RENTAL", @"") stringByAppendingString:@" *"];
        _lastNameTitle = [EHILocalizedString(@"rentals_lookup_last_name_title", @"LAST NAME ON RENTAL", @"") stringByAppendingString:@" *"];
        _actionTitle = EHILocalizedString(@"rentals_lookup_find_button_title", @"FIND RENTAL", @"");
        _callButtonTitle = EHILocalizedString(@"rentals_footer_contact_button_text", @"CALL US", @"");
       
        // prepopulate last name
        _lastName = [EHIUser currentUser].profiles.basic.lastName;
        _firstName = [EHIUser currentUser].profiles.basic.firstName;
    }

    _requiredFieldsModel = [EHIRequiredInfoViewModel modelForInfoType: EHIRequiredInfoTypeLookupRental];
    
    return self;
}

# pragma mark - Actions

- (void)findRental
{
    [EHIAnalytics trackAction:EHIAnalyticsRentalsActionFind handler:nil];
    
    self.isLoading = YES;
    [[EHIServices sharedInstance] fetchRentalForConfirmation:self.confirmation firstName:self.firstName lastName:self.lastName handler:^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        self.error = nil;
        
        // perform custom error handling if needed
        if([self handleRetrieveError:error]) {
            return;
        }
        
        // surface errors in interface
        self.error = [self promotedErrorFromReservation:reservation error:error];

        // consume valid reservation
        if(!self.error) {
            [self didRetrieveReservation:reservation];
        }
    }];
}

- (void)closeRental
{
    self.router.transition
        .dismiss.start(nil);
}

- (void)forgotRental
{
    self.router.transition
        .present(EHIScreenSigninRecovery).object(@(EHISigninRecoveryTypeForgotConfirmation)).start(nil);
}

- (void)callContactNumber
{
    [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
}

# pragma mark - Errors

- (BOOL)handleRetrieveError:(EHIServicesError *)error
{
    if ([error hasErrorCode:EHIServicesErrorCodeReservationLookupLoginRequired]) {
        [self handleReservationLookupLoginError:error];
        return YES;
    }
    
    return NO;
}

- (void)handleReservationLookupLoginError:(EHIServicesError *)error
{
    EHISigninViewModel *model = [EHISigninViewModel new];
    
    self.router.transition
        .present(EHIScreenMainSignin).object(model).handler(^{
            [self findRental];
        }).start(nil);
}

//
// Helpers
//

- (NSError *)promotedErrorFromReservation:(EHIReservation *)reservation error:(EHIServicesError *)error
{
    NSError *result = nil;
    
    // if we have a service error, generate the standard error
    if(error.internalError) {
        result = [NSError errorWithDomain:EHIErrorDomainGeneral code:-1 userInfo:@{
            EHIErrorTitleKey   : EHILocalizedString(@"rentals_fallback_lookup_text", @"Sorry, we couldn\'t find a rental matching the provided information.", @""),
            EHIErrorDetailsKey : EHILocalizedString(@"rentals_fallback_helper_text", @"Please double-check the confirmation number and last name you provided, or call us for assistance.", @""),
        }];
        
        [error consume];
    }
    // otherwise, generate an error for canceled reservations
    else if(reservation.status == EHIReservationStatusCanceled) {
        NSString *title = EHILocalizedString(@"rentals_canceled_lookup_text", @"This reservation (confirmation number #{number}) has been cancelled.", @"");
        title = [title ehi_applyReplacementMap:@{
            @"number" : reservation.confirmationNumber ?: @"",
        }];
        
        result = [NSError errorWithDomain:EHIErrorDomainGeneral code:-1 userInfo:@{
            EHIErrorTitleKey   : title,
            EHIErrorDetailsKey : EHILocalizedString(@"rentals_fallback_helper_text", @"Please double-check the confirmation number and last name you provided, or call us for assistance.", @""),
        }];
    }
    
    return result;
}

- (void)didRetrieveReservation:(EHIReservation *)reservation
{
    self.router.transition
        .dismiss
        .present(EHIScreenReservation).object(reservation).start(nil);
}

# pragma mark - Accessors

- (NSAttributedString *)confirmationTitle
{
    __weak typeof(self) welf = self;
    NSAttributedString *attributedForgotRental =
    [NSAttributedString attributedStringWithString:EHILocalizedString(@"rentals_lookup_cantfind", @"Can't find it?", @"")
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:14.0f]
                                             color:[UIColor ehi_greenColor]
                                        tapHandler:^{
                                            [welf forgotRental];
                                        }];

    NSString *confirmationTitle = [EHILocalizedString(@"rentals_lookup_confirmation_number_title", @"CONFIRMATION NUMBER", @"") stringByAppendingString:@" * "];
    
    return EHIAttributedStringBuilder.new
        .text(confirmationTitle)
        .fontStyle(EHIFontStyleBold, 14.0f)
        .space
        .append(attributedForgotRental)
        .attributes(@{ NSBaselineOffsetAttributeName : @1 }).string;
}

- (BOOL)isValid
{
    return self.confirmation.length && self.firstName.length && self.lastName.length;
}

# pragma mark - Setters

- (void)setConfirmation:(NSString *)confirmationNumber
{
    _confirmation = [self filterText:confirmationNumber];
}

- (void)setFirstName:(NSString *)firstName
{
    _firstName = [self filterText:firstName];
}

- (void)setLastName:(NSString *)lastName
{
    _lastName = [self filterText:lastName];
}

- (NSString *)filterText:(NSString *)text
{
    return text.length ? text : nil;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
