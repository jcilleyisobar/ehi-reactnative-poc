//
//  EHIConfirmationHeaderViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationHeaderViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"
#import "EHICalendarManager.h"

@interface EHIConfirmationHeaderViewModel ()
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (nonatomic) EHIReservation *reservation;
@end

@implementation EHIConfirmationHeaderViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _headerTitle = [self generateHeaderTitle];
        _confirmationTitle = EHILocalizedString(@"confirmation_header_confirmation_number_title", @"CONFIRMATION NO.", @"title for the confirmation number on the confirmation header.");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIReservation class]]) {
        [self updateWithReservation:model];
    }
}

//
// Helpers
//

- (void)updateWithReservation:(EHIReservation *)reservation
{
    self.reservation        = reservation;
    self.emailTitle         = [self emailTitleForPickupDate:reservation.pickupTime];
    self.vehicleImage       = reservation.selectedCarClass.images.firstObject;
    self.confirmationNumber = [NSString stringWithFormat:@"#%@", reservation.confirmationNumber];
}

- (NSAttributedString *)generateHeaderTitle
{
    NSString *title = self.builder.isModifyingReservation
        ? EHILocalizedString(@"confirmation_header_modify_first_line", @"RESERVATION\nUPDATED", @"first line for conf header modify title")
        : EHILocalizedString(@"confirmation_header_title_first_line", @"YOU'RE\nALL SET", @"first line of the confirmation header title: 'YOU'RE ALL SET' ");
    
    return [NSAttributedString attributedSplitLineTitle:title font:[UIFont ehi_fontWithStyle:EHIFontStyleHeavy size:26.0f]];
}

- (NSAttributedString *)emailTitleForPickupDate:(NSDate *)pickupDate
{
    // build up localized strings
    NSString *emailText = self.builder.isModifyingReservation
        ? EHILocalizedString(@"confirmation_email_modify_message", @"We canceled your previous reservation and booked you a new one. We'll send your updated reservation details via email shortly.", @"text for the conf modify details")
        : EHILocalizedString(@"confirmation_email_message", @"We've sent an email with all your reservation details.", @"");
   
    NSString *timeText  = EHILocalizedString(@"confirmation_pickup_date_message", @"See you on #{date}", @"");
    timeText = [timeText ehi_applyReplacementMap:@{
        @"date" : [pickupDate ehi_stringForTemplate:@"d MMMM yyyy"] ?: @"",
    }];
    
    // generate the styled text
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
        .text(emailText).fontStyle(EHIFontStyleLight, 18.0f).paragraph(6, NSTextAlignmentCenter).space
        .appendText(timeText).fontStyle(EHIFontStyleBold, 18.0f);
    
    return builder.string;
}

# pragma mark - Accessors

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
