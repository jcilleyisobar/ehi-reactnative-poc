//
//  EHIWebContent.h
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHIWebContentType) {
    EHIWebContentTypeNone,
    EHIWebContentTypePrivacy,
    EHIWebContentTypeTermsOfUse,
    EHIWebContentTypeTermsAndConditions,
    EHIWebContentTypePrepayTermsAndConditions,
    EHIWebContentTypeTaxes,
    EHIWebContentTypeLicenses,
    EHIWebContentTypeWeekendSpecialTermsAndConditions,
};

@interface EHIWebContent : EHIModel

+ (instancetype)webContentWithBody:(NSString *)body;

@property (copy, nonatomic, readonly) NSString *body;
@property (copy, nonatomic, readonly) NSString *version;
@end
