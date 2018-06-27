//
//  EHIFormFieldTextToggleViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextToggleViewModel.h"

@implementation EHIFormFieldTextToggleViewModel

- (void)setToggleEnabled:(BOOL)toggleEnabled
{
    if(_toggleEnabled != toggleEnabled) {
        _toggleEnabled = toggleEnabled;
        
        if([self.delegate respondsToSelector:@selector(formField:didChangeToggleValue:)]) {
            [self.delegate formField:self didChangeToggleValue:toggleEnabled];
        }
    }
}

- (BOOL)showsConfirmationTitle
{
    return [NSLocale ehi_shouldShowDoubleOptInForEmailSpecials] && self.confirmationTitle.length && self.toggleEnabled;
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeTextToggle;
}

@end

@implementation EHIFormFieldTextToggleViewModel (Generators)

+ (instancetype)emailFieldWithEmail:(NSString *)email forProfile:(EHIUserPreferencesProfile *)profile
{
    EHIFormFieldTextToggleViewModel *viewModel = [EHIFormFieldTextToggleViewModel new];
    viewModel.title = EHILocalizedString(@"profile_edit_email_title", @"EMAIL", @"");
    viewModel.toggleEnabled = profile.email.specialOffers == EHIOptionalBooleanTrue;
    viewModel.inputValue = email;
    viewModel.toggleAttributesTitle = EHIAttributedStringBuilder.new
        .appendText(EHILocalizedString(@"reservation_driver_info_email_toggle_title", @"Sign up to receive emails from Enterprise", @""))
        .fontStyle(EHIFontStyleLight, 14.0f)
        .space
        .appendText(EHISectionSignString)
        .fontStyle(EHIFontStyleBold, 14.0f)
        .string;
    
    viewModel.confirmationTitle = EHILocalizedString(@"email_special_german_opt_in_text", @"You will receive a confirmation email shortly. Please confirm that you would like to opt-in for email specials.", @"");
    viewModel.isLastInGroup = YES;
    [viewModel validates:EHIFormFieldValidationAtSymbol];
    
    return viewModel;
}

@end
