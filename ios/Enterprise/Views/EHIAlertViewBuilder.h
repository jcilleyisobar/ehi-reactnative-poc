//
//  EHIAlertViewBuilder.h
//  Enterprise
//
//  Created by Ty Cobb on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef void(^EHIAlertViewConfirmHandler)(void);
typedef void(^EHIAlertViewDismissHandler)(NSInteger index, BOOL canceled);
typedef void(^EHIAlertViewInputHandler)(NSString *input, NSInteger index, BOOL canceled);

typedef NS_ENUM(NSInteger, EHIAlertViewStyle) {
    EHIAlertViewStyleDefault,
    EHIAlertViewStyleSecureTextInput
};

@interface EHIAlertViewBuilder : NSObject

- (EHIAlertViewBuilder *)withTitle:(NSString *)title;
- (EHIAlertViewBuilder *)withMessage:(NSString *)message;
- (EHIAlertViewBuilder *)withButtonTitle:(NSString *)title;
- (EHIAlertViewBuilder *)withCancelButtonTitle:(NSString *)title;
- (EHIAlertViewBuilder *)withStyle:(EHIAlertViewStyle)style;
- (void)showWithCompletion:(id)completion;

- (EHIAlertViewBuilder *(^)(NSString *))title;
- (EHIAlertViewBuilder *(^)(NSString *))message;
- (EHIAlertViewBuilder *(^)(NSString *))button;
- (EHIAlertViewBuilder *(^)(NSString *))cancelButton;
- (EHIAlertViewBuilder *(^)(EHIAlertViewStyle))style;
- (void(^)(id))show;

@end
