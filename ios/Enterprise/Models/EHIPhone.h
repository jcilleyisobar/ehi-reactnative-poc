//
//  EHIUserPhoneNumber.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHIPhoneType) {
    EHIPhoneTypeUnknown,
    EHIPhoneTypeHome,
    EHIPhoneTypeMobile,
    EHIPhoneTypeWork,
    EHIPhoneTypeFax,
    EHIPhoneTypeOther,
    EHIPhoneTypeOffice,
    EHIPhoneTypeContactUs,
    EHIPhoneTypeRoadside,
    EHIPhoneTypeEPlus,
    EHIPhoneTypeDisabilities,
    EHIPhoneTypeDnr
};

typedef NS_ENUM(NSInteger, EHIPhonePriority) {
    EHIPhonePriorityNone,
    EHIPhonePriorityFirst,
    EHIPhonePrioritySecond
};

@interface EHIPhone : EHIModel

@property (assign, nonatomic) EHIPhoneType type;
@property (assign, nonatomic) EHIPhonePriority priority;
@property (copy  , nonatomic) NSString *number;
@property (copy  , nonatomic) NSString *maskedNumber;
@property (copy  , nonatomic) NSString *countryCode;
@property (copy  , nonatomic) NSString *countryName;
@property (assign, nonatomic) BOOL isDefault;

// computed
@property (nonatomic, readonly) NSString *typeTitle;

+ (NSArray *)userPhoneTypeOptions;
+ (NSArray *)userPhoneTypeOptionsStrings;

@end

EHIAnnotatable(EHIPhone);
