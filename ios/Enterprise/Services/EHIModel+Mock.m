//
//  EHIModel+Mock.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 27.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel+Mock.h"

@implementation EHIModel (Mock)

+ (instancetype)mock:(NSString *)filename
{
    // load the data from file
    filename = [[NSBundle mainBundle] pathForResource:filename ofType:@"json"];
    NSData *data = [NSData dataWithContentsOfFile:filename];
    
    // creat the json data and the model from that data
    id attributes = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    id model = [self modelWithDictionary:attributes];
    
    return model;
}

@end
