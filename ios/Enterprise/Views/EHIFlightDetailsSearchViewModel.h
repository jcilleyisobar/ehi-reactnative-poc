//
//  EHIFlightDetailsSearchViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIFlightDetailsSearchViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *searchPlaceholder;
@property (copy, nonatomic, readonly) NSString *airlineTitle;
@property (copy, nonatomic, readonly) NSString *airlineName;
@end
