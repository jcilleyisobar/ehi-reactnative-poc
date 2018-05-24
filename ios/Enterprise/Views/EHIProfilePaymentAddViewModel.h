//
//  EHIProfilePaymentAddViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIProfilePaymentAddType) {
    EHIProfilePaymentAddTypeNone,
    EHIProfilePaymentAddTypeSelect
};

@interface EHIProfilePaymentAddViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (assign, nonatomic) EHIProfilePaymentAddType type;
@property (assign, nonatomic) CGFloat topSpacing;
@property (assign, nonatomic) BOOL hideDivider;
@end
