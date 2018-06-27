//
//  EHIDashboardLocationPromptViewModel.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 21/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardLocationPromptViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *mainText;
@property (copy, nonatomic, readonly) NSString *acceptTitle;
@property (copy, nonatomic, readonly) NSString *denyTitle;

- (void)acceptLocation;
- (void)denyLocation;

@end
