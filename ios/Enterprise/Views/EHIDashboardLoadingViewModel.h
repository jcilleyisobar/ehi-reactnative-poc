//
//  EHIDashboardLoadingViewModel.h
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardLoadingViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *title;
@end
