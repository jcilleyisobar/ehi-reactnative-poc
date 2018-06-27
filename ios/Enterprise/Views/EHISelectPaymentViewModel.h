//
//  EHISelectPaymentViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIProfilePaymentAddViewModel.h"
#import "EHISelectPaymentFooterViewModel.h"

typedef void (^EHISelectPaymentHandler)(NSString *);

typedef NS_ENUM(NSInteger, EHISelectPaymentSection) {
    EHISelectPaymentSectionCards,
    EHISelectPaymentSectionAddCard,
    EHISelectPaymentSectionFooter
};

@interface EHISelectPaymentViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (strong, nonatomic) NSArray *cardsModels;
@property (strong, nonatomic) EHIProfilePaymentAddViewModel *addViewModel;
@property (strong, nonatomic) EHISelectPaymentFooterViewModel *footerViewModel;
@property (assign, nonatomic) BOOL isTermsRead;
@property (copy  , nonatomic) EHISelectPaymentHandler handler;

- (EHISectionHeaderModel *)headerForSection:(EHISelectPaymentSection)section;
- (void)addCreditCard;
- (void)selectPaymentMethodAtIndexPath:(NSIndexPath *)indexPath;
- (void)commitPaymentMethod;
@end
