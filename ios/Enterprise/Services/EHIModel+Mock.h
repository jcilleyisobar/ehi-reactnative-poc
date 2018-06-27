//
//  EHIModel+Mock.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 27.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIModel (Mock)

/**
 @brief Attempts to instantiate a model with data from disk
 
 The data is assumed to be a JSON file in the test target's bundle with the @c filename
 specified.
 
 @param filename The mock data's filename
 
 @return A model instance built from this data
*/

+ (instancetype)mock:(NSString *)filename;

@end
