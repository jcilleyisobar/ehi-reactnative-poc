//
//  EHIReservationEnterprisePlusInfoViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationEnterprisePlusInfoViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSAttributedString *detailsTitle;
@property (copy, nonatomic) NSString *email;
@property (copy, nonatomic) NSAttributedString *emailPlaceholder;
@property (copy, nonatomic) NSString *actionButtonTitle;

// computed
@property (nonatomic, readonly) BOOL canSubmitEmail;

- (void)emailReminder;
- (void)dismiss;

@end
