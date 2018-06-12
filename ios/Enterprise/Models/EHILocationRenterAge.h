//
//  EHILocationRenterAge.h
//  Enterprise
//
//  Created by cgross on 7/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHILocationRenterAge : EHIModel
@property (assign, nonatomic, readonly) NSInteger value;
@property (copy  , nonatomic, readonly) NSString *text;
@property (assign, nonatomic, readonly) BOOL isDefault;
@end

EHIAnnotatable(EHILocationRenterAge)