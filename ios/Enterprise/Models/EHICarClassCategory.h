//
//  EHICarClassCategory.h
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHICarClassCategory : EHIModel
@property (copy, nonatomic, readonly) NSString *code;
@property (copy, nonatomic, readonly) NSString *name;

- (BOOL)isVan;

@end

EHIAnnotatable(EHICarClassCategory);