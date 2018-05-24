//
//  EHIProfilePaymentStatusViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIProfilePaymentStatus) {
    EHIProfilePaymentStatusNone,
    EHIProfilePaymentStatusEmpty,
    EHIProfilePaymentStatusNoCard,
    EHIProfilePaymentStatusNumbersOfCardsExcceded,
};

@interface EHIProfilePaymentStatusViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithType:(EHIProfilePaymentStatus)type;
- (instancetype)initWithType:(EHIProfilePaymentStatus)type hideDivider:(BOOL)hide;

@property (assign, nonatomic) EHIProfilePaymentStatus type;
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSAttributedString *subtitle;
@property (assign, nonatomic) BOOL hideDivider;

@end
