//
//  EHIConfirmationJoinViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIViewModel_Calendar.h"

typedef void (^EHIConfirmationJoinHandler)(BOOL wantsJoin);

@interface EHIConfirmationJoinViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *messageText;
@property (copy, nonatomic, readonly) NSString *joinTitle;
@property (copy, nonatomic, readonly) NSString *addToCalendarTitle;
@property (copy, nonatomic, readonly) NSString *closeDescription;
@property (copy, nonatomic, readonly) NSString *closeTitle;

@property (copy, nonatomic) EHIConfirmationJoinHandler handler;

- (void)join;
- (void)close;

@end
