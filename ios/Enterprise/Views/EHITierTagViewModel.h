//
//  EHITierTagViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHITierTagViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *tier;
@end
