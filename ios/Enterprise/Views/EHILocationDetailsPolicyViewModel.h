//
//  EHILocationDetailsPolicyViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocationPolicy.h"

@interface EHILocationDetailsPolicyViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@end
