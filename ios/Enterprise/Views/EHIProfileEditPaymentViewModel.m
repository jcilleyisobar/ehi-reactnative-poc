//
//  EHIProfileEditPaymentViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIProfileEditPaymentViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHIProfilePaymentDeleteViewModel.h"
#import "EHIServices+User.h"
#import "EHIToastManager.h"
#import "EHICreditCardFormatter.h"
#import "EHIReservationBuilder.h"

typedef NS_ENUM(NSInteger, EHIProfileEditPaymentViewModelActions) {
    EHIProfileEditPaymentViewModelActionsMakeDefault,
    EHIProfileEditPaymentViewModelActionsEditDate,
    EHIProfileEditPaymentViewModelActionsSave
};

@interface EHIProfileEditPaymentViewModel ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@property (strong, nonatomic) NSArray *monthValues;
@property (strong, nonatomic) NSArray *yearValues;
@property (copy  , nonatomic) NSString *selectedYear;
@property (copy  , nonatomic) NSString *selectedMonth;
@property (copy  , nonatomic) NSArray *allMonths;
@end

@implementation EHIProfileEditPaymentViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _billingWarning       = EHILocalizedString(@"profile_payment_options_billing_edit_warn", @"Billing account cannot be changed. Please contact your account admin for changes.", @"");
        _expirationDateTitle  = EHILocalizedString(@"profile_license_expiration_date_title", @"EXPIRATION DATE", @"");
        _editDateButtonTitle  = EHILocalizedString(@"profile_payment_options_credit_card_edit_date", @"EDIT DATE", @"");
        _preferredTitle       = EHILocalizedString(@"profile_payment_options_payment_is_default", @"This is your default method of payment", @"");
        _saveTitle            = EHILocalizedString(@"profile_payment_options_edit_save_action_text", @"SAVE", @"");
        _togglePreferredTitle = EHILocalizedString(@"profile_payment_options_payment_make_default", @"Make this the default method of payment", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserPaymentMethod class]]) {
        self.paymentMethod = model;
    }
}

- (void)setPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    _paymentMethod = paymentMethod;
    
    _hideTogglePrefereed = paymentMethod.isPreferred;
    _isPreferred         = paymentMethod.isPreferred;
    _alias               = paymentMethod.alias;
    
    BOOL isCreditCard = paymentMethod.paymentType == EHIUserPaymentTypeCard;
    if(isCreditCard) {
        [self setupForCreditCard];
    } else {
        [self setupForBilling];
    }
    
    [self invalidateMonths];
    [self invalidateYear];
    [self invalidateExpirationDate];
}

- (NSString *)maskedNumber
{
    switch (self.paymentMethod.paymentType) {
        case EHIUserPaymentTypeBilling:
            return self.paymentMethod.maskedBillingNumber;
        case EHIUserPaymentTypeCard: {
            return self.paymentMethod.customDisplayName;
        }
        case EHIUserPaymentTypeUnknown: return @"";
    }
}

- (void)setupForCreditCard
{
    _title              = EHILocalizedString(@"profile_payment_options_credit_card_edit_title", @"Edit Credit Card", @"");
    _paymentAliasTitle  = EHILocalizedString(@"profile_payment_options_credit_card_nick_title", @"CREDIT CARD NICKNAME", "");
    _paymentNumberTitle = EHILocalizedString(@"payment_card_number_field_title", @"CREDIT CARD NUMBER", @"");
    
    NSCalendarUnit units = NSCalendarUnitYear | NSCalendarUnitMonth;
    NSDateComponents *components = [self.paymentMethod.expirationDate ehi_components:units];
    
    self.selectedMonth = [self formatMonth:components.month];
    self.selectedYear  = @(components.year).description;
}

- (void)setupForBilling
{
    _title              = EHILocalizedString(@"profile_payment_options_billing_edit_title", @"Edit Billing Number", @"");
    _paymentAliasTitle  = EHILocalizedString(@"profile_payment_options_billing_nick_title", @"BILLING NAME", @"");
    _paymentNumberTitle = EHILocalizedString(@"profile_payment_options_billing_number_title", @"BILLING NUMBER", @"");
}

- (NSString *)creditCardImage
{
    return [EHICreditCardFormatter cardIconForCardType:self.paymentMethod.cardType];
}

- (NSInteger)selectedMonthIndex
{
    NSInteger index = self.selectedMonth.integerValue;
    return MAX(0, index - 1);
}

- (NSInteger)selectedYearIndex
{
    NSString *year = self.selectedYear;
    NSInteger index = self.yearValues.indexOf(year);
    return index != NSNotFound ? index : 0;
}

# pragma mark - Action

- (void)togglePreferred
{
    self.isPreferred = !self.isPreferred;
    
    [self trackAction:EHIProfileEditPaymentViewModelActionsMakeDefault];
}

- (void)editDate
{
    [self trackAction:EHIProfileEditPaymentViewModelActionsEditDate];
}

- (void)deletePayment
{
    EHIProfilePaymentDeleteViewModel *modal = [EHIProfilePaymentDeleteViewModel initWithPaymentMethod:self.paymentMethod];
    
    NSString *succesMessage = nil;
    BOOL isBilling = self.isBilling;
    if(isBilling) {
        succesMessage = EHILocalizedString(@"profile_payment_options_delete_billing_success", @"Billing code deleted.", @"");
    } else {
        succesMessage = EHILocalizedString(@"profile_payment_options_delete_credit_card_success", @"Credit card deleted.", @"");
    }
    
    __weak __typeof(self) welf = self;
    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0 && !canceled) {
            welf.isLoading = YES;
            [[EHIServices sharedInstance] deletePaymentMethod:welf.paymentMethod handler:^(EHIUserPaymentProfile *payment, EHIServicesError *error) {
                welf.isLoading = NO;
                if(!error.hasFailed) {
                    [EHIToastManager showMessage:succesMessage];
                    [welf dismiss];
                }
            }];
        }
        
        return YES;
    }];
}

- (void)saveChanges
{
    [self trackAction:EHIProfileEditPaymentViewModelActionsSave];

    EHIUserPaymentMethod *newPaymentMethod = self.paymentMethod.deepCopy;

    [newPaymentMethod updateWithDictionary:@{
        @key(newPaymentMethod.isPreferred)    : @(self.isPreferred),
        @key(newPaymentMethod.alias)          : self.alias ?: newPaymentMethod.alias ?: @"",
    }];
    
    BOOL isCreditCard = self.paymentMethod.paymentType == EHIUserPaymentTypeCard;
    if(isCreditCard) {
        NSString *expirationDate = [NSString stringWithFormat:@"%@-%@", self.selectedYear, self.selectedMonth];
        [newPaymentMethod updateWithDictionary:@{
            @"expiration_date" : expirationDate
        }];
    }
    
    EHIUser *user = EHIUser.currentUser;
    EHIUser *newUser = user.deepCopy;
    [newUser attachPaymentMethod:newPaymentMethod];
    
    self.isLoading = YES;
    __weak __typeof(self) welf = self;
    [[EHIServices sharedInstance] updateUser:user withUser:newUser handler:^(EHIUser *user, EHIServicesError *error) {
        welf.isLoading = NO;
        if(!error.hasFailed) {
            [welf dismiss];
        }
    }];
}

- (void)dismiss
{
    self.router.transition.pop(1).start(nil);
}

# pragma mark - Picker View

- (NSInteger)numberOfRowsInComponent:(EHIProfileEditPaymentDatePickerComponent)component
{
    return component == EHIProfileEditPaymentDatePickerComponentMonth ? [self.monthValues count] : [self.yearValues count];
}

- (NSString *)titleForRow:(NSInteger)row inComponent:(EHIProfileEditPaymentDatePickerComponent)component
{
    NSArray *titles = component == EHIProfileEditPaymentDatePickerComponentMonth ? self.monthValues : self.yearValues;
    
    return titles[row];
}

- (void)didSelectRow:(NSInteger)row inComponent:(EHIProfileEditPaymentDatePickerComponent)component
{
    NSString *title = [self titleForRow:row inComponent:component];
    
    if(component == EHIProfileEditPaymentDatePickerComponentMonth) {
        self.selectedMonth = [self monthNumberForName:title].description;
    } else {
        self.selectedYear = title;
        
        // recompute the months, based on the selected year
        [self invalidateMonths];
    }
    
    [self invalidateExpirationDate];
}

# pragma mark - Invalidation

- (void)invalidateExpirationDate
{
    NSString *month = self.selectedMonth;
    NSString *year  = self.selectedYear;
    self.expirationDate = [NSString stringWithFormat:@"%@/%@", month, year];
}

- (void)invalidateMonths
{
    NSInteger nextMonth = 1;
    NSInteger remainingMonths = 12;
    
    NSInteger currentYear   = [self currentYear];
    NSInteger currentMonth  = [self currentMonth];
    
    // if there's no year selected yet, default it to the current year
    BOOL selectedCurrentYear = self.selectedYear == nil || self.selectedYear.integerValue == currentYear;
    if(selectedCurrentYear) {
        nextMonth = currentMonth + 1;
        remainingMonths = nextMonth - 12 > 0 ? nextMonth - 12 : 12;
    }
    
    self.monthValues = @(nextMonth).upTo(remainingMonths).map(^(NSNumber *month) {
        NSInteger monthIndex = month.integerValue;
        return [self monthNameAtIndex:monthIndex - 1];
    });
}

- (void)invalidateYear
{
    NSInteger currentYear   = [self currentYear];
    
    self.yearValues = @(currentYear).upTo(currentYear + 10).map(^(NSNumber *year) {
        return [year stringValue];
    });
}

//
// Helpers
//

- (NSString *)monthNameAtIndex:(NSInteger)index
{
    return [self.allMonths ehi_safelyAccess:index];
}

- (NSString *)monthNumberForName:(NSString *)name
{
    NSInteger month = [self.allMonths indexOfObject:name] != NSNotFound ? [self.allMonths indexOfObject:name] + 1 : 1;
    return [self formatMonth:month];
}

- (NSArray *)allMonths
{
    if(!_allMonths) {
        return [NSDate monthNames];
    }
    
    return _allMonths;
}

- (NSString *)formatMonth:(NSInteger)month
{
    return [NSString stringWithFormat:@"%02d", (int)month];
}

- (BOOL)isBilling
{
    return self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
}

- (NSInteger)currentYear
{
    NSInteger currentYear = [[NSCalendar currentCalendar] component:NSCalendarUnitYear fromDate:[NSDate date]];
    NSInteger currentMonth = [[NSCalendar currentCalendar] component:NSCalendarUnitMonth fromDate:[NSDate date]];
    
    if(currentMonth == 12) {
        currentYear += 1;
    }
    return currentYear;
}

- (NSInteger)currentMonth
{
    NSInteger currentMonth = [[NSCalendar currentCalendar] component:NSCalendarUnitMonth fromDate:[NSDate date]];
    
    if(currentMonth == 12) {
        currentMonth = 1;
    }
    return currentMonth;
}

# pragma mark - Analytics

- (void)trackAction:(EHIProfileEditPaymentViewModelActions)action
{
    NSString *actionKey = nil;
    
    switch (action) {
        case EHIProfileEditPaymentViewModelActionsMakeDefault:
            actionKey = EHIAnalyticsEditPaymentMethodActionDefault;
            break;
        case EHIProfileEditPaymentViewModelActionsEditDate:
            actionKey = EHIAnalyticsEditCreditCardActionEditDate;
            break;
        case EHIProfileEditPaymentViewModelActionsSave:
            actionKey = self.isBilling ? EHIAnalyticsEditBillingNumberActionSave : EHIAnalyticsEditCreditCardActionSave;
            break;
    }
    
    [EHIAnalytics trackAction:actionKey handler:^(EHIAnalyticsContext *context) {
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    }];
}

@end
