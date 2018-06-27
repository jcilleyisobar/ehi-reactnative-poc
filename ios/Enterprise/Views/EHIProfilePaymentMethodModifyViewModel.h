//
//  EHIProfilePaymentMethodModifyViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIProfilePaymentAddViewModel.h"
#import "EHIProfilePaymentStatusViewModel.h"

typedef NS_ENUM(NSInteger, EHIProfilePaymentMethodModifySection) {
    EHIProfilePaymentMethodModifySectionBilling,
    EHIProfilePaymentMethodModifySectionCard,
    EHIProfilePaymentMethodModifySectionStatus,
    EHIProfilePaymentMethodModifySectionAddCard
};

typedef NS_ENUM(NSInteger, EHIProfilePaymentMethodModifyAction) {
    EHIProfilePaymentMethodModifyActionAddCard,
    EHIProfilePaymentMethodModifyActionEdit,
    EHIProfilePaymentMethodModifyActionDelete,
};

@interface EHIProfilePaymentMethodModifyViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSArray *billingsModels;
@property (copy  , nonatomic) NSArray *cardsModels;
@property (strong, nonatomic) EHIProfilePaymentStatusViewModel *statusModel;
@property (strong, nonatomic) EHIProfilePaymentAddViewModel *addModel;
@property (assign, nonatomic) BOOL isLoading;

- (EHISectionHeaderModel *)headerForSection:(EHIProfilePaymentMethodModifySection)section;

- (void)updatePaymentAtIndexPath:(NSIndexPath *)indexPath withAction:(EHIProfilePaymentMethodModifyAction)action;

@end
