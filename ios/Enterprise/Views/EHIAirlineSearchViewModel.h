//
//  EHIAirlineSearchViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class EHIAirline;
@interface EHIAirlineSearchViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSArray *resultModels;
@property (copy, nonatomic) void (^handler)(EHIAirline *);

- (void)filterAirlineWithQuery:(NSString *)query;
- (void)selectAirlineAtIndexPath:(NSIndexPath *)indexPath;

@end
