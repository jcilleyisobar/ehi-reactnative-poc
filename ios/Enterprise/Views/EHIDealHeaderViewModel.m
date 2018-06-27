//
//  EHIDealHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealHeaderViewModel.h"

@interface EHIDealHeaderViewModel ()
@property (copy, nonatomic) NSString *title;
@end

@implementation EHIDealHeaderViewModel

+ (instancetype)modelWithTitle:(NSString *)title
{
    EHIDealHeaderViewModel *model = [EHIDealHeaderViewModel new];
    model.title = title;
    
    return model;
}

@end
