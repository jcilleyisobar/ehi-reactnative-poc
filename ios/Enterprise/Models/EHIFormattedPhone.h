//
//  EHIFormattedPhone.h
//  Enterprise
//
//  Created by mplace on 8/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIFormattedPhone : EHIModel
/** Pre-formatted phone string input */
@property (copy, nonatomic) NSString *originalPhone;
/** Formatted phone string result */
@property (copy, nonatomic) NSString *formattedPhone;
/** Error populated in the event of a formatting error */
@property (copy, nonatomic) NSError *error;

/** Returns an instance with @c originalPhone populated */
+ (instancetype)modelWithPhone:(NSString *)phone;

@end
