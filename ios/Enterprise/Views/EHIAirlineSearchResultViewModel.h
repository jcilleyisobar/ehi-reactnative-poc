//
//  EHIAirlineSearchResultViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class EHIAirline;
@interface EHIAirlineSearchResultViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *airlineName;
@property (strong, nonatomic, readonly) EHIAirline *airline;
+ (instancetype)initWithAirline:(EHIAirline *)airline;

- (BOOL)contains:(NSString *)name;

@end
