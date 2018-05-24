//
//  EHILocationViewModel.h
//  Enterprise
//
//  Created by mplace on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocation.h"

@interface EHILocationViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic, readonly) EHILocation *location;
@property (copy  , nonatomic, readonly) NSAttributedString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSAttributedString *tagsText;
@property (copy  , nonatomic, readonly) NSString *iconImageName;
@property (copy  , nonatomic, readonly) NSString *selectButtonTitle;
@property (assign, nonatomic, readonly) BOOL hidesIcon;
@property (assign, nonatomic, readonly) BOOL hidesSubtitle;
@property (assign, nonatomic, readonly) BOOL hidesSelectButton;

// handles the navigation and model passing
- (void)showLocationDetails;

@end
