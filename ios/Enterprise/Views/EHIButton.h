//
//  EHIButton.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButtonType.h"

@interface EHIButton : UIButton

/** Shows the appropriate border if @c YES */
@property (assign, nonatomic) BOOL showsBorder;
/** Sets the border color if showsBorder @c YES */
@property (strong, nonatomic) UIColor *borderColor;
/** Updates the type of the button, setting its image */
@property (assign, nonatomic) EHIButtonType type;

/** Computed property for normal state's title */
@property (copy, nonatomic) NSString *ehi_title;
/** Computed property for the selected & selected/highlighted state's title */
@property (copy, nonatomic) NSString *ehi_selectedTitle;
/** Computed property for normal state's attributed title */
@property (copy, nonatomic) NSAttributedString *ehi_attributedTitle;
/** Computed property for normal state's title color */
@property (copy, nonatomic) UIColor *ehi_titleColor;
/** Computed property for normal state's image */
@property (strong, nonatomic) UIImage *ehi_image;
/** Computed property for normal state's image name */
@property (strong, nonatomic) NSString *ehi_imageName;

/** @c YES if the button should render as disabled, but still receieve touch events */
@property (assign, nonatomic) BOOL isFauxDisabled;

/** Custom horizontal alignment for the button's image */
@property (assign, nonatomic) UIControlContentHorizontalAlignment imageHorizontalAlignment;
/** Custom alignment rect insets for the button's frame */
@property (assign, nonatomic) UIEdgeInsets customAlignmentRectInsets;
/** Compted property that indicates if custom image alignment has been specified */
@property (nonatomic, readonly) BOOL hasCustomImageAlignment;

/**
 @brief Creates a button with the specified type

 The type-specific styling is applied to the button automatically.

 @param type The custom button type to create
 @return A new EHIButton instance
*/

+ (instancetype)ehi_buttonWithType:(EHIButtonType)type;

/**
 Sets a dynamic background color for a control state
 
 @param color The background color to display
 @param state The state to set the background color for
*/

- (void)setBackgroundColor:(UIColor *)color forState:(UIControlState)state;

/**
 Sets a dynamic tint color for a control state
 
 @param color The tint color to display
 @param state The state to set the tint color for
*/

- (void)setTintColor:(UIColor *)color forState:(UIControlState)state;

/** Sets the fake disabled state, optionally animating it */
- (void)setIsFauxDisabled:(BOOL)isFauxDisabled animated:(BOOL)animated;
/** Forces the cell to re-apply any custom styling properties */
- (void)synchronize;

@end

@interface EHIButton (SubclassingHooks)

/** Called during instantation to apply default styles */
- (void)applyDefaults;

@end
