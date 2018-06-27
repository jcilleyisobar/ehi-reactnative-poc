//
//  EHISettingsCellViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 1/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISettings.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsControlViewModel : EHIViewModel <MTRReactive>

/** Row information for identifying this view model */
@property (assign, nonatomic) NSUInteger row;
/** Title describing this setting */
@property (copy  , nonatomic) NSString *title;
/** Optional subtitle displayed below the title */
@property (copy  , nonatomic) NSString *subtitle;
/** Optional title for the details modal */
@property (copy  , nonatomic) NSString *detailsTitle;
/** Details displayed either under title or in modal through info icon tap*/
@property (copy  , nonatomic) NSString *details;
/** @c YES if icon to left of title is displayed. Will try and display details under title if @c NO. */
@property (assign, nonatomic) BOOL hidesDetailIcon;
/** Actionable items are highlighted in green and include no arrow */
@property (assign, nonatomic) BOOL isAction;

/** Automatically bind this setting to a @c BOOL property on @c EHISettings */
@property (copy  , nonatomic, nullable) NSString *settingsKey;
/** Called before committing toggle changes. Should return desired outcome for toggle. */
@property (copy  , nonatomic, nullable) BOOL (^toggleModifer)(BOOL enabled);
/** The current state of the toggle for this cell */
@property (assign, nonatomic, readonly) BOOL toggleEnabled;

// computed
@property (copy  , nonatomic, readonly, nullable) NSAttributedString *detailsAttributed;
@property (assign, nonatomic, readonly) BOOL hidesDetails;
@property (assign, nonatomic, readonly) BOOL hidesArrow;
@property (assign, nonatomic, readonly) BOOL hidesToggle;

/** Generator for creating all view models for a particular subclass */
+ (NSArray *)viewModels;

/** Show @c details in a custom overlay modal */
- (void)showDetailsModal;
/** Active/Deactivate this setting */
- (void)enableToggle:(BOOL)enabled;

@end

NS_ASSUME_NONNULL_END
