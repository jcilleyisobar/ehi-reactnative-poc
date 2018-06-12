//
//  EHIDashboardQuickstartViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardQuickstartViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *typeName;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSString *iconName;
@property (copy  , nonatomic, readonly) NSString *deletedTitle;
@property (copy  , nonatomic, readonly) NSString *undoTitle;
@property (assign, nonatomic, readonly) BOOL alignsIconLeft;
@property (assign, nonatomic, readonly) BOOL isDeleted;

- (void)deleteQuickstart;
- (void)undoDelete;

@end
