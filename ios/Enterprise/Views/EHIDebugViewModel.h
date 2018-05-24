//
//  EHIDebugViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDebugViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSArray *viewModels;

- (void)selectItem:(NSUInteger)item;

@end
