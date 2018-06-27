//
//  EHIProfileEditPaymentViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIProfileEditPaymentDatePickerComponent) {
    EHIProfileEditPaymentDatePickerComponentMonth,
    EHIProfileEditPaymentDatePickerComponentYear
};

@interface EHIProfileEditPaymentViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *paymentAliasTitle;
@property (copy  , nonatomic, readonly) NSString *paymentNumberTitle;
@property (copy  , nonatomic, readonly) NSString *maskedNumber;

@property (copy  , nonatomic, readonly) NSString *billingWarning;

@property (copy  , nonatomic, readonly) NSString *expirationDateTitle;
@property (copy  , nonatomic, readonly) NSString *editDateButtonTitle;
@property (copy  , nonatomic) NSString *expirationDate;

@property (copy  , nonatomic, readonly) NSString *togglePreferredTitle;
@property (copy  , nonatomic, readonly) NSString *preferredTitle;
@property (copy  , nonatomic, readonly) NSString *saveTitle;

@property (copy  , nonatomic, readonly) NSString *creditCardImage;

@property (assign, nonatomic, readonly) BOOL hideTogglePrefereed;

@property (copy  , nonatomic) NSString *alias;
@property (assign, nonatomic) BOOL isPreferred;

@property (assign, nonatomic) NSInteger selectedMonthIndex;
@property (assign, nonatomic) NSInteger selectedYearIndex;

@property (assign, nonatomic) BOOL isLoading;

- (NSInteger)numberOfRowsInComponent:(EHIProfileEditPaymentDatePickerComponent)component;
- (NSString *)titleForRow:(NSInteger)row inComponent:(EHIProfileEditPaymentDatePickerComponent)component;
- (void)didSelectRow:(NSInteger)row inComponent:(EHIProfileEditPaymentDatePickerComponent)component;

- (void)togglePreferred;
- (void)editDate;
- (void)deletePayment;
- (void)saveChanges;

@end
