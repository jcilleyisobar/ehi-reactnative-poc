//
//  EHISigninFieldModel.h
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHISignInFieldModelType) {
    EHISignInFieldModelTypeUsername,
    EHISignInFieldModelTypePassword
};

@interface EHISigninFieldModel : EHIModel

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *placeholder;
@property (assign, nonatomic) BOOL isSecure;
@property (assign, nonatomic) UIReturnKeyType returnType;

+ (NSArray *)enterprisePlusFields;
+ (NSArray *)emeraldClubFields;

@end
