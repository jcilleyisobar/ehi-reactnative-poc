//
//  EHIProfileFooterViewModel.h
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIProfileFooterViewModel : EHIViewModel

/** Localized title for the footer's change password button */
@property (copy, nonatomic, readonly) NSString *changePasswordTitle;
/** Transitions to the change password screen */
- (void)changePassword;

@end
