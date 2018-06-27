//
//  EHILocationDetailsMapViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocation.h"
#import "NSValue+MapKit.h"

@interface EHILocationDetailsMapViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic, readonly) NSArray *annotations;
@property (strong, nonatomic, readonly) NSValue *regionValue;
- (void)promptMaps;
@end
