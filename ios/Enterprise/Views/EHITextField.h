//
//  EHITextField.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButton.h"
#import "EHIFormattedPhone.h"

typedef NS_OPTIONS(NSInteger, EHITextFieldBorder) {
    EHITextFieldBorderNone,
    EHITextFieldBorderField  = 1 << 0,
    EHITextFieldBorderButton = 1 << 1,
};

@interface EHITextField : UITextField

/** Bit-mask for the parts of the border to draw; defaults to @c EHITextFieldBorderField */
@property (assign, nonatomic) EHITextFieldBorder borderType;
/** Sets the border color of both the textfield and its action button (gray is the default) */
@property (strong, nonatomic) UIColor *borderColor;
/** Set the alert border color */
@property (strong, nonatomic) UIColor *alertBorderColor;
/** @YES disables the cursor */
@property (assign, nonatomic) BOOL hidesCursor;
/** @YES shows yellow border */
@property (assign, nonatomic) BOOL showsAlertBorder;
/** @c YES if field holds sensitive data */
@property (assign, nonatomic) BOOL sensitive;

/** The type of action button (EHIButtonTypeChevron is the default) */
@property (assign, nonatomic) EHIButtonType actionButtonType;
/** Outlet for action button; may be @c nil if no type is set */ 
@property (nonatomic, readonly) UIButton *actionButton;

/** Computed property that checks/updates the cursor position */
@property (assign, nonatomic) NSInteger cursorPosition;

/** @c YES if a toolbar with done button should be used. Pressing done will invoke @c textFieldShouldReturn: on the delegate */
@property (assign, nonatomic) BOOL usesDoneToolbar;

/** Model that allows the text field to accurately manage the text and cursor index for phone numbers */
@property (strong, nonatomic) EHIFormattedPhone *phoneModel;

@end
