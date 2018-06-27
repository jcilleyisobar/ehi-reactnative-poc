//
//  EHISigninField.h
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninFieldModel.h"

@protocol  EHISigninFieldActions;
@interface EHISigninField : UIControl

@property (weak    , nonatomic) IBOutlet id<EHISigninFieldActions> delegate;
@property (copy    , nonatomic) NSString *value;
@property (copy    , nonatomic) EHISigninFieldModel *model;
@property (readonly, nonatomic) UIButton *actionButton;
@property (assign  , nonatomic) BOOL showAlert;
@property (strong  , nonatomic) UIColor *alertColor;
@end

@protocol EHISigninFieldActions <NSObject> @optional
- (void)didReturnForSigninField:(EHISigninField *)signinField;
- (void)didBeginEditingForSigninField:(EHISigninField *)signinField;
- (void)didEndEditingForSigninField:(EHISigninField *)signinField;
@end
