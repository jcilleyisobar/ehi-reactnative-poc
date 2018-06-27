//
//  EHIDebugOptionViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

#define EHIDebugOptionInvalidAuthToken @"INVALID_TOKEN"
#define EHIDebugOptionOfficeGeofenceNotificationKey @"com.ehi.EHIDebugNotificationOfficeGeofenceKey"

typedef NS_ENUM(NSUInteger, EHIDebugOptionType) {
    EHIDebugOptionTypeStringBehavior,
    EHIDebugOptionTypeGBOEnvironment,
    EHIDebugOptionTypeAEMEnvironment,
    EHIDebugOptionTypeSearchEnvironment,
    EHIDebugOptionTypeInvalidateAuthToken,
    EHIDebugOptionTypeWrongApiKey,
    EHIDebugOptionTypeMap,
    EHIDebugOptionTypeOfficeGeofence,
    EHIDebugOptionTypeNotifications,
    EHIDebugOptionTypeWeekendSpecial,
    EHIDebugOptionTypeAnalyticsReminder,
    EHIDebugOptionTypeAppleStoreRate,
    EHIDebugOptionTypeInAppAppleStoreRate,
    EHIDebugOptionTypePrepayNABanner,
    EHIDebugOptionTypeSurveyPooling,
    EHIDebugOptionTypeSurveyResetState,
    EHIDebugOptionTypeRewardsBenefitsState,
    EHIDebugOptionTypeLocationMapFilterTip,
    EHIDebugOptionTypePushNotificationEvent,
    EHIDebugOptionTypeIssuingAuthorityRequiredMock,
    EHIDebugOptionTypeUnauthJoinModal,
    EHIDebugOptionTypeGDPRState,
    EHIDebugOptionTypeGBORegion,
    EHIDebugOptionTypeClearData
};

@interface EHIDebugOptionViewModel : EHIViewModel

@property (assign, nonatomic, readonly) EHIDebugOptionType type;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;

+ (NSArray *)viewModels;

@end
