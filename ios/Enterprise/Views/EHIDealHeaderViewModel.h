//
//  EHIDealHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDealHeaderViewModel : EHIViewModel <MTRReactive>

+ (instancetype)modelWithTitle:(NSString *)title;

@property (copy, nonatomic, readonly) NSString *title;

@end
