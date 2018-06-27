//
//  EHIDashboardHeroImageViewModel.h
//  Enterprise
//
//  Created by fhu on 9/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardHeroImageViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *imageName;
@property (copy, nonatomic) NSString *headerText;
@property (copy, nonatomic) NSString *locationText;

@end
