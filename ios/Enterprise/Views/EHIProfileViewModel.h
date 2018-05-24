//
//  EHIProfileViewModel.h
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIProfilePaymentViewModel.h"
#import "EHISectionHeader.h"
#import "EHIBarButtonItem.h"
#import "EHIProfilePaymentAddViewModel.h"

typedef NS_ENUM(NSUInteger, EHIProfileSection) {
    EHIProfileSectionBasic,
    EHIProfileSectionDriver,
    EHIProfileSectionPayment,
    EHIProfileSectionAddCard,
    EHIProfileSectionFooter
};

@interface EHIProfileViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (strong, nonatomic) EHIBarButtonItem *signoutButton;

@property (assign, nonatomic) BOOL authenticated;
@property (strong, nonatomic) NSArray *basicInformation;
@property (strong, nonatomic) NSArray *licenseInformation;
@property (strong, nonatomic) EHIProfilePaymentViewModel *paymentInformation;
@property (strong, nonatomic) EHIProfilePaymentAddViewModel *addCardModel;

@property (assign, nonatomic) BOOL isLoading;

- (EHISectionHeaderModel *)headerForSection:(EHIProfileSection)section;
- (void)didTapActionForHeaderForSection:(NSUInteger)section;
- (void)didTapAddCreditCard;

@end
