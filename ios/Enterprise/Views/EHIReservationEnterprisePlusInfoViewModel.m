//
//  EHIReservationEnterprisePlusInfoViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationEnterprisePlusInfoViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIReservationEnterprisePlusInfoViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"enterprise_plus_email_reminder_modal_title", @"Enterprise Plus®", @"");
        NSString *detailsTitle = EHILocalizedString(@"enterprise_plus_email_reminder_modal_details_title", @"Ad que nem alibus molupta tibusam, tecae nia sit, non cus doles volupis cilisimagnis.", @"");
        _detailsTitle = [EHIAttributedStringBuilder new].text(detailsTitle).lineSpacing(8).string;
        NSString *emailPlaceholder = EHILocalizedString(@"enterprise_plus_email_reminder_modal_text_placeholder", @"Email address", @"");
        _emailPlaceholder = [EHIAttributedStringBuilder new].text(emailPlaceholder).size(17.0).string;
        _actionButtonTitle = EHILocalizedString(@"enterprise_plus_email_reminder_modal_action_button_title", @"EMAIL ME A REMINDER", @"");
        _email = @"";
    }
    return self;
}

# pragma mark - Computed

- (BOOL)canSubmitEmail
{
    return !NSRangeIsNull([self.email rangeOfString:@"@"]);
}

# pragma mark - Actions

- (void)emailReminder
{
    [self dismiss];
}

- (void)dismiss
{
    self.router.transition
        .dismiss.start(nil);
}

@end
