//
//  EHIConfirmationJoinViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIConfirmationJoinViewModel.h"
#import "EHIReservation.h"

@interface EHIConfirmationJoinViewModel () <EHIViewModelCalendarResult>
@end

@implementation EHIConfirmationJoinViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:EHIReservation.class]) {
        self.reservation = model;
    }
}

# pragma mark - Accessors

- (NSString *)messageText
{
    return EHILocalizedString(@"reservation_confirm_modal_title", @"Want to save time looking for your rentals in the future and start earning rewards?", @"");
}

- (NSString *)joinTitle
{
    return EHILocalizedString(@"login_join_now_title", @"JOIN ENTERPRISE PLUS", @"");
}

- (NSString *)addToCalendarTitle
{
    return EHILocalizedString(@"reservation_confirm_modal_cal", @"NO THANKS, JUST ADD IT TO MY CALENDAR", @"");
}

- (NSString *)closeDescription
{
    return EHILocalizedString(@"reservation_confirm_modal_close", @"Once you exist this screen, this info won't be there unless you enter all this info again.", @"");
}

- (NSString *)closeTitle
{
    return EHILocalizedString(@"standard_close_button", @"Close", @"").uppercaseString;
}

# pragma mark - EHIViewModelCalendarResult

- (void)addCalendarEventSuccess
{
    [self close];
}

# pragma mark - Actions

- (void)join
{
    [self closeAndJoin:YES];
}

- (void)close
{
    [self closeAndJoin:NO];
}

//
// Helpers
//

- (void)closeAndJoin:(BOOL)wantsJoin
{
    __weak typeof(self) welf = self;
    self.router.transition.dismiss.start(^{
        ehi_call(welf.handler)(wantsJoin);
    });
}

@end
