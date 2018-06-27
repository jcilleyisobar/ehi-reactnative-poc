//
//  EHISectionHeaderModel.m
//  Enterprise
//
//  Created by mplace on 2/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISectionHeaderModel.h"

@implementation EHISectionHeaderModel

+ (instancetype)modelWithTitle:(NSString *)title
{
    EHISectionHeaderModel *model = [EHISectionHeaderModel new];
    model.title = title;
    return model;
}

+ (instancetype)modelWithAttributedTitle:(NSAttributedString *)title
{
    EHISectionHeaderModel *model = [EHISectionHeaderModel new];
    model.attributedTitle = title;
    return model;
}

+ (NSDictionary *)modelsWithTitles:(NSArray *)titles
{
    return titles
    // map each title into a [ section_index, title ] pair
    .map(^(NSObject *title, NSInteger index) {
        EHISectionHeaderModel *model;
        if([title isKindOfClass:[NSString class]]) {
            model = [EHISectionHeaderModel modelWithTitle:(NSString *)title];
        } else if ([title isKindOfClass:[NSAttributedString class]]) {
            model = [EHISectionHeaderModel modelWithAttributedTitle:(NSAttributedString *)title];
        }
        
        return model ? @[ @(index), model ] : nil;
    })
    // and unwind that into a dictionary
    .dict;
}

@end
