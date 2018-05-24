//
//  EHIInfoModalViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 5/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIInfoModalModelable.h"

#define EHInfoModalIndexNone (-1)

typedef BOOL(^EHIInfoModalAction)(NSInteger index, BOOL canceled);

typedef NS_ENUM(NSInteger, EHIInfoModalButtonLayout) {
    EHIInfoModalButtonLayoutRegular,
    EHIInfoModalButtonLayoutSecondaryDismiss,
};

@interface EHIInfoModalViewModel : EHIViewModel

/** Buttons styling **/
@property (assign, nonatomic) EHIInfoModalButtonLayout buttonLayout;
/** The title to display */
@property (copy, nonatomic) NSString *title;
/** The details text to display */
@property (copy, nonatomic) NSString *details;
/** The .xib name of the custom header, NOT compatible with @c title */
@property (copy, nonatomic) NSString *headerNibName;
/** The .xib name of the custom details, NOT compatible with @c details */
@property (copy, nonatomic) NSString *detailsNibName;
/** The title for the first button */
@property (copy, nonatomic) NSString *firstButtonTitle;
/** The title for the second button */
@property (copy, nonatomic) NSString *secondButtonTitle;

@property (assign, nonatomic) BOOL needsAutoDismiss;
/** Hides close button */
@property (assign, nonatomic) BOOL hidesCloseButton;
/** If the extra action button should be displayed */
@property (assign, nonatomic, readonly) BOOL hidesActionButton;
/** The action invoked by action buttons; Returns a @c BOOL indicating if the modal should be dismissed automatically */
@property (copy  , nonatomic, readonly) EHIInfoModalAction action;

// re-declared initializer requiring conformance to EHIInfoModalModelable
- (instancetype)initWithModel:(id<EHIInfoModalModelable>)model;
// re-declared update hook requiring conformance to EHIInfoModalModelable
- (void)updateWithModel:(id<EHIInfoModalModelable>)model;

/** Present the modal, optionally passing an @c action to run with the modal finishes */
- (void)present:(EHIInfoModalAction)action;
/** Helper that dismisses the modal without canceling or calling the @c action */
- (void)dismissWithCompletion:(void(^)(void))completion;

/** Invoke the supplied @c action */
- (void)performActionForIndex:(NSInteger)index;
/** Closes (and cancels, if applicable) the info modal */
- (void)cancel;

@end
