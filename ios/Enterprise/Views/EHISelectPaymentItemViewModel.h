//
//  EHISelectPaymentItemViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIUserPaymentMethod.h"

@interface EHISelectPaymentItemViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@property (copy  , nonatomic) NSString *aliasTitle;
@property (copy  , nonatomic) NSString *editTitle;
@property (copy  , nonatomic) NSString *paymentTitle;
@property (copy  , nonatomic) NSString *saveTitle;
@property (copy  , nonatomic) NSAttributedString *expiredTitle;
@property (copy  , nonatomic) NSString *cardImage;
@property (assign, nonatomic) BOOL isSelected;
@property (assign, nonatomic) BOOL isFirst;
@property (assign, nonatomic) BOOL isPreferred;
@property (assign, nonatomic) BOOL isSaved;
@property (assign, nonatomic) BOOL showSaveToggle;

- (void)editPayment;
- (void)toggleSave;

@end
