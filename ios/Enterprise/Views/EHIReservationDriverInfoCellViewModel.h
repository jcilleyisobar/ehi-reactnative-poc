//
//  EHIReservationDriverInfoCellViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReservationDriverInfoCellViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *actionButtonTitle;
@property (assign, nonatomic, readonly) BOOL shouldShowDriverInfo;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *name;
@property (copy  , nonatomic, readonly) NSString *email;
@property (copy  , nonatomic, readonly) NSString *phone;

- (void)addDriverInfo;

@end

NS_ASSUME_NONNULL_END