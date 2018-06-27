//
//  FSInviteDelegate.h
//  Foresee
//
//  Created by Wayne Burkett on 5/31/16.
//  Copyright © 2016 Foresee. All rights reserved.
//

/**
 Delegates registered with the ForeSee SDK may adopt the `FSInviteDelegate` protocol to receive
 updates about a survey's lifecycle. 
 
 */
@protocol FSInviteDelegate <NSObject>

@optional

/** @name Custom Surveys */

/** Tells the delegate that the ForeSee SDK is about to show an invite.
 
 @param measure The measure for the survey that the user is about to be invited to take.
 */
- (void)willShowInviteForMeasure:(TRMeasure *)measure;

/** Tells the delegate that the ForeSee SDK is about to show a survey.
 
 Implement this method to override the default survey view controller.
 
 By default, the ForeSee SDK presents the survey in a custom `UIViewController` that has a toolbar
 at the bottom of the screen (with back/forward buttons), an activity indicator, and a navigation bar
 at the top. This `UIViewController` provides additional functionality, too, such as preventing navigation
 to domains not registered in the domain white list (see the `foresee_configuration.json` that was included
 with the SDK). It also closes automatically when the survey has been submitted.
 
 So, the long and short is, be fairly certain you want to use a custom view controller to display the survey!
 
 Implementation of this method is optional. Custom surveys are not required. Return `YES` to allow the
 default behavior.
 
 @param measure The measure for which a survey is about to be displayed.
 @param url The URL of the survey.
 @return NO to prevent the default survey from appearing, YES otherwise.
 */
- (BOOL)willShowSurveyForMeasure:(TRMeasure *)measure URL:(NSURL *)url;

/** @name Survey View Controller */

/** Asks the delegate for a `UINavigationBar` object to use when displaying the survey.
 
 Implementation of this method is optional.
 
 @return A custom styled `UINavigationBar`
 */
- (UINavigationBar *)navigationBarForSurveyViewController;

/** @name Sampling and Eligibility Checks */

/** Sent when the customer does not pass the eligibility check for a survey.
 
 A user who is not eligible will not see an invite.
 
 @param measure The measure associated with this event.
 */
- (void)willNotShowInviteWithEligibilityFailedForMeasure:(TRMeasure *)measure;

/** Sent when the customer was not included in the sampling pool check.
 
 Not all users who are eligible are invited to take a survey. A sampling rate is configured for your account, which
 limits the total number of users who will see an invite. This method is called when the user was not in the
 sampling pool.
 
 @param measure The measure associated with this event.
 */
- (void)willNotShowInviteWithSamplingFailedForMeasure:(TRMeasure *)measure;

/** @name Other Invite Events */

/** Tells the delegate that the ForeSee SDK has shown an invite. 
 
 Only called when using the built-in invites (i.e. not for custom invites). It's
 assumed that custom invites already know when an invite has been shown.
 
 @param measure The measure associated with the invite that was displayed.
 */
- (void)didShowInviteForMeasure:(TRMeasure *)measure;

/** Tells the delegate that the user has accepted an invite.
 
 @param measure The measure associated with the invite.
 */
- (void)didAcceptInviteForMeasure:(TRMeasure *)measure;

/** Tells the delegate that the user has declined an invite.
 
 @param measure The measure associated with the invite.
 */
- (void)didDeclineInviteForMeasure:(TRMeasure *)measure;

/** @name Other Survey Events */

/** Tells the delegate that the ForeSee SDK has presented a survey.
 
 @warning Not sent for custom surveys
 @param measure The measure for the survey that was displayed.
 */
- (void)didShowSurveyForMeasure:(TRMeasure *)measure;

/** Tells the delegate that the user cancelled a survey.
 
 @warning Not sent for custom surveys
 @param measure The measure for the survey that was dismissed.
 */
- (void)didCancelSurveyForMeasure:(TRMeasure *)measure;

/** Tells the delegate that the user completed a survey.
 
 @warning Not sent for custom surveys
 @param measure The measure for the survey that was completed.
 */
- (void)didCompleteSurveyForMeasure:(TRMeasure *)measure;

/** @name Error Conditions */

/** Sent when the supplied contact information is not in the correct format.
 
 This error prevents successful acceptance of an invite.
 
 @param measure The measure associated with this failure
 @param error An error object containing details about the formatting issue.
 */
- (void)didFailForMeasure:(TRMeasure *)measure withContactFormatError:(NSError *)error;

/** Sent when the invite failed because of missing information.
 
 This error prevents successful acceptance of an invite.
 
 @param measure The measure associated with this failure
 @param error An error object containing details about the missing information.
 */
- (void)didFailForMeasure:(TRMeasure *)measure withMissingInformationError:(NSError *)error;

/** Sent when the invite or survey failed because of a network error.
 
 This error prevents successful acceptance or cancellation of an invite or survey.
 
 @param measure The measure associated with this failure
 @param error An error object containing details about the network failure.
 */
- (void)didFailForMeasure:(TRMeasure *)measure withNetworkError:(NSError *)error;

@end
