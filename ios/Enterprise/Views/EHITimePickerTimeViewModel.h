//
//  EHITimePickerTimeViewModel.h
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHITimePickerTimeViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) BOOL isClosed;
@property (assign, nonatomic) BOOL isAfterHours;
@property (assign, nonatomic) BOOL isBoundaryTime;
@end
