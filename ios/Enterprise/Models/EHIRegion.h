//
//  EHIRegion.h
//  Enterprise
//
//  Created by Alex Koller on 5/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIRegion : EHIModel

@property (strong, nonatomic, readonly) NSString *name;
@property (strong, nonatomic, readonly) NSString *code;

@end
