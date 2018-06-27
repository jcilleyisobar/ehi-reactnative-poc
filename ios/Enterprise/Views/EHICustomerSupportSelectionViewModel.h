//
//  EHICustomerSupportSelectionViewModel.h
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHISupportUrlType) {
    EHISupportUrlTypeSendMessage,
    EHISupportUrlTypeSearchAnswers
};

@interface EHICustomerSupportSelectionViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *iconImageName;
@property (copy, nonatomic) NSAttributedString *headerAttributedString;
@property (copy, nonatomic) NSString *detailsText;
@property (copy, nonatomic) NSString *phoneNumber;
@property (copy, nonatomic) NSString *url;
@property (copy, nonatomic) NSString *eventName;

+ (instancetype)modelForUrlType:(EHISupportUrlType)type url:(NSString *)url;

@end
