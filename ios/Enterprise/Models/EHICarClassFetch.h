//
//  EHICarClassFetch.h
//  
//
//  Created by Michael Place on 9/29/15.
//
//

#import <Foundation/Foundation.h>
#import "EHICarClass.h"

@interface EHICarClassFetch : NSObject
/** Code of the car class */
@property (copy  , nonatomic) NSString *code;
/** Number of free days to redeem */
@property (assign, nonatomic) NSInteger daysToRedeem;

/** Generates a fetch model for the parameterized car class */
+ (instancetype)modelForCarClass:(EHICarClass *)carClass;
@end
