//
//  EHIPolicyInfoViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocationPolicy.h"
#import "EHISectionHeaderModel.h"

@interface EHIPoliciesViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSArray *policies;
@property (copy  , nonatomic, readonly) NSString *title;

- (void)selectPolicyAtIndex:(NSInteger)index;

@end
