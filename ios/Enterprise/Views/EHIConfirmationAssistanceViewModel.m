//
//  EHIConfirmationAssistanceViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationAssistanceViewModel.h"
#import "EHILocation.h"

@implementation EHIConfirmationAssistanceViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _quickPickupTitle = [self quickPickupTitleString];
    }
    return self;
}

//
// Helpers
//

- (NSAttributedString *)quickPickupTitleString
{
    NSString *title = EHILocalizedString(@"reservation_confirmation_quick_pickup_button_title", @"QUICK PICK-UP", @"");
    NSString *subtitle = EHILocalizedString(@"reservation_confirmation_quick_pickup_button_subtitle", @"Spend less time at the counter", @"");
    return [self attributedButtonTextWithTitle:title subtitle:subtitle];
}

- (NSAttributedString *)attributedButtonTextWithTitle:(NSString *)title subtitle:(NSString *)subtitle
{
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new].color([UIColor ehi_greenColor]).lineSpacing(6.0)
        .text(title).fontStyle(EHIFontStyleBold, 18.0)
        .newline.appendText(subtitle).fontStyle(EHIFontStyleLight, 14.0);
    
    return builder.string;
}

@end
