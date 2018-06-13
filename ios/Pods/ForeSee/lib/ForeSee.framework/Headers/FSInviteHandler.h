//
//  FSInviteHandler.h
//  Foresee
//
//  Created by Wayne Burkett on 5/23/16.
//  Copyright © 2016 Foresee. All rights reserved.
//

/**
 The `FSInviteHandler` protocol is adopted by an object that handles the display and
 dismissal of an invite view controller.
 */
@protocol FSInviteHandler <NSObject>

/** Tells the invite handler to show a survey.
 
 */
- (void)show;

/** Tells the invite handler to hide a survey.
 
 @param animate A boolean indicating whether to animate the dismissal
 */
- (void)hideWithAnimation:(BOOL)animate;

@optional

/** Tells the invite handler whether the input is valid
 
 @param isInvalid A boolean. YES, if the input is invalid; NO, otherwise
 */
- (void)setInvalidInput:(BOOL)isInvalid;

/** Tells the invite handler to load a URL
 
 The default invites use this method to load links in a new page. Custom invites need not
 implement at this time.
 
 @param url The URL to load
 @param domainWhitelist A set of URLs that are legal to follow. This parameter is ignored unless enabledWhitelist is set to true in the configuration file.
 @param title The title of the page to load
 */
- (void)show:(NSURL *)url domainWhitelist:(NSSet *)domainWhitelist title:(NSString *)title;

@end
