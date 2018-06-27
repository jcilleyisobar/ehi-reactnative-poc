//
//  EHIConfirmationLocationViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIConfirmationLocationViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *title;
/** Name of the location */
@property (copy, nonatomic) NSString *name;
/** Address of the location */
@property (copy, nonatomic) NSString *address;
/** Phone number of the location */
@property (copy, nonatomic) NSString *phone;
/** Name of the image to be used as the icon */
@property (copy, nonatomic) NSString *iconImage;

/** Prompts the user to call the location */
- (void)callLocation;
@end
