//
//  EHICallSupportViewModel.h
//  Enterprise
//
//  Created by mplace on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHICallSupportSections) {
    EHICallSupportSectionsPhoneNumbers,
};

@interface EHICallSupportViewModel : EHIViewModel <MTRReactive>

/** Title of the call support modal */
@property (copy  , nonatomic, readonly) NSString *title;
/** Subtitle, only visible when roadside assistance button is not visible */
@property (copy  , nonatomic, readonly) NSString *subtitle;
/** Models to populate the collection view with */
@property (copy  , nonatomic, readonly) NSArray *models;

/** Dismisses the call support modal */
- (void)dismiss;

@end
