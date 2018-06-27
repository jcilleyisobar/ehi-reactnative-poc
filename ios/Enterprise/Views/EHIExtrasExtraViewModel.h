//
//  EHIExtrasExtraViewModel.h
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICarClassExtra.h"

typedef NS_ENUM(NSUInteger, EHIExtrasType) {
    EHIExtrasTypeEquipment,
    EHIExtrasTypeFuel,
    EHIExtrasTypeProtection
};

@interface EHIExtrasExtraViewModel : EHIViewModel <MTRReactive>

/** The extra backing this view model */
@property (copy  , nonatomic, readonly) EHICarClassExtra *extra;
/** The title for this extra */
@property (copy  , nonatomic, readonly) NSString *title;
/** The title for the item count stepper */
@property (copy  , nonatomic, readonly) NSString *stepperTitle;
/** The price rate text for this extra */
@property (copy  , nonatomic, readonly) NSString *rateText;
/** The price total text for this extra */
@property (copy  , nonatomic, readonly) NSString *totalText;

@property (copy  , nonatomic) NSString *identifier;
@property (copy  , nonatomic) NSAttributedString *moreInfoText;
@property (copy  , nonatomic) NSString *details;
@property (copy  , nonatomic) NSString *defaultText;
@property (assign, nonatomic) NSInteger maxQuantity;
@property (assign, nonatomic) BOOL arrowUp;
@property (assign, nonatomic) BOOL isSelected;
@property (assign, nonatomic) BOOL shouldExpandToggle;
@property (assign, nonatomic) CGFloat height;
@property (assign, nonatomic) NSInteger amount;
@property (assign, nonatomic) BOOL plusButtonEnabled;
@property (assign, nonatomic) BOOL minusButtonEnabled;

/** Generates the fallback view model for the specified @c type */
+ (instancetype)fallbackForType:(EHIExtrasType)type;
/** Toggles the extra with a callback responding if the extra was actually toggled */
- (void)selectExtra:(BOOL)selected completion:(void (^)(BOOL didToggle))completion;
/** Shows more info for this extra */
- (void)showMoreInfo;
/** Invalidates the total price, depending on the extra state */
- (void)invalidateTotalPrice;
- (void)updateWithExtra:(EHICarClassExtra *)extra andPaymentLineItem:(EHICarClassPriceLineItem *)lineItem;

@end
