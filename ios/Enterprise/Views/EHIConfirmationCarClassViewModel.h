//
//  EHIConfirmationCarClassViewModel.h
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIConfirmationCarClassViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *headerTitle;
@property (copy  , nonatomic) NSString *carClassNameTitle;
@property (copy  , nonatomic) NSString *makeModelTitle;
@property (copy  , nonatomic) NSString *transmissionTitle;
@property (assign, nonatomic) BOOL isAutomatic;
@end
