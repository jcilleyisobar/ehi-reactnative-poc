//
//  EHIFormFieldTextToggleViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextViewModel.h"
#import "EHIUserPreferencesProfile.h"

@class EHIFormFieldTextToggleViewModel;

@protocol EHIFormFieldTextToggleDelegate <EHIFormFieldDelegate> @optional
/** Called on the delegate whenver the toggle value changes */
- (void)formField:(EHIFormFieldTextToggleViewModel *)field didChangeToggleValue:(BOOL)toggleEnabled;
@end

@interface EHIFormFieldTextToggleViewModel : EHIFormFieldTextViewModel <MTRReactive>

/** Delegate for reporting state changes */
@property (weak  , nonatomic) id<EHIFormFieldTextToggleDelegate> delegate;
/** The helper text displayed next to the toggle */
@property (copy  , nonatomic) NSString *toggleTitle;
/** The helper attributed text displayed next to the toggle */
@property (copy  , nonatomic) NSAttributedString *toggleAttributesTitle;
/** The @c BOOL value held by this field's toggle */
@property (assign, nonatomic) BOOL toggleEnabled;
/** The confirmation text displayed below the toggle */
@property (copy  , nonatomic) NSString *confirmationTitle;
/** The @c BOOL value to show the confirmation text or not */
@property (assign, nonatomic) BOOL showsConfirmationTitle;

@end

@interface EHIFormFieldTextToggleViewModel (Generators)
+ (instancetype)emailFieldWithEmail:(NSString *)email forProfile:(EHIUserPreferencesProfile *)profile;
@end
