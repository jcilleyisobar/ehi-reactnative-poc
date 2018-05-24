//
//  EHIButtonState.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButtonState.h"

@implementation EHIButtonState

# pragma mark - Accessors

- (BOOL)hasCustomProperties
{
    return self.backgroundColor != nil || self.tintColor != nil;
}

# pragma mark - Default States

+ (NSDictionary *)statesForType:(EHIButtonType)type
{
    switch(type) {
        case EHIButtonTypeNone: return @{
            @(UIControlStateNormal) : [EHIButtonState new],
        };
        case EHIButtonTypeBack: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"arrow_back";
            }],
        };
        case EHIButtonTypePhone: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_phone_01";
            }],
        };
        case EHIButtonTypeSignout: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"signout_bar_button_title", @"SIGN OUT", @"title for sign out bar button")];
            }],
        };
        case EHIButtonTypeChevron: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"arrow_smgreen";
            }],
        };
        case EHIButtonTypeDownChevron: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"downarrow_smgreen";
            }],
        };
        case EHIButtonTypeSearch: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_search";
            }],
        };
        case EHIButtonTypeLocation: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.tintColor = [UIColor ehi_graySpecialColor];
                state.backgroundColor = [UIColor ehi_greenColor];
                state.titleColor = [UIColor whiteColor];
            }],
        };
        case EHIButtonTypeNearby: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_locationsrch";
            }],
        };
        case EHIButtonTypeDirections: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_directions_04";
            }],
        };
        case EHIButtonTypeFavorite: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_favorites_03";
                state.tintColor = [UIColor ehi_greenColor];
                state.backgroundColor = [UIColor ehi_grayColor0];
                state.titleColor = [UIColor ehi_greenColor];
            }],
            @(UIControlStateSelected) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_favorites_04";
                state.tintColor = [UIColor whiteColor];
                state.backgroundColor = [UIColor ehi_greenColor];
                state.titleColor = [UIColor whiteColor];
            }]
        };
        case EHIButtonTypeFilter: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"location_filter_button_title", @"FILTER", @"title for a filter button")];
            }],
        };
        case EHIButtonTypeReset: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"filter_reset_button_title", @"RESET", @"title for a reset button")];
            }],
        };
        case EHIButtonTypeClear: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"clear_button_title_key", @"CLEAR", @"title for a clear button")];
            }],
        };
        case EHIButtonTypeDiscard: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"cancel_button_title_key", @"DISCARD", @"title for a discard button")];
            }],
        };
		case EHIButtonTypeCancel: return @{
				@(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
					state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"standard_button_cancel", @"CANCEL", @"title for a cancel button").uppercaseString];
				}],
			};
        case EHIButtonTypeClose: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_x_gray_01";
                state.tintColor = [UIColor whiteColor];
            }],
        };
        case EHIButtonTypeDone: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"done_button_title_key", @"DONE", @"title for a done button")];
            }],
        };
        case EHIButtonTypeDoneGreen: return @{
                @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                    state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"done_button_title_key", @"DONE", @"title for a done button") color:[UIColor ehi_greenColor]];
            }],
        };
        case EHIButtonTypeVisibility: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName = @"icon_show";
                state.tintColor = [UIColor ehi_blackColor];
            }],
            @(UIControlStateSelected) : [EHIButtonState build:^(EHIButtonState *state) {
                state.tintColor = [UIColor ehi_greenColor];
            }],
        };
        case EHIButtonTypeAlert: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName  = @"icon_alert_01";
                state.titleColor = [UIColor blackColor];
            }],
        };
        case EHIButtonTypeInfo: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.imageName  = @"icon_info";
                state.titleColor = [UIColor ehi_greenColor];
            }],
        };
        case EHIButtonTypeSecondary: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.titleColor      = [UIColor ehi_greenColor];
                state.backgroundColor = [UIColor whiteColor];
                state.borderColor     = [UIColor ehi_greenColor];
            }],
        };
        
        case EHIButtonTypeExit: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"enroll_exit_action", @"Exit", @"")];
            }]
        };
            
        case EHIButtonTypeDelete: return @{
            @(UIControlStateNormal) : [EHIButtonState build:^(EHIButtonState *state) {
                state.attributedTitle = [self attributedBarButtonString:EHILocalizedString(@"profile_payment_options_delete_action_text", @"DELETE", @"")];
            }]
        };
        
        default: return nil;
    }
}

+ (EHIButtonState *)build:(void(^)(EHIButtonState *state))builder
{
    EHIButtonState *state = [EHIButtonState new];
    builder(state);
    return state;
}

//
// Helpers
//

+ (NSAttributedString *)attributedBarButtonString:(NSString *)string
{
    return [self attributedBarButtonString:string color:[UIColor whiteColor]];
}

+ (NSAttributedString *)attributedBarButtonString:(NSString *)string color:(UIColor *)color
{
    return [NSAttributedString attributedStringWithString:string font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:16.0f] color:color];
}

@end
