//
//  EHIInformationBannerViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationBuilder.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHIInformationBannerType) {
    EHIInformationBannerTypeNone,
    EHIInformationBannerTypeEmeraldClassSelect,
    EHIInformationBannerTypeEmeraldReview,
    EHIInformationBannerTypeEmeraldConfirmation,
    EHIInformationBannerTypePrepayRates,
};

@interface EHIInformationBannerViewModel : EHIViewModel

@property (copy, nonatomic, nullable, readonly) NSString *title;
@property (copy, nonatomic, nullable, readonly) NSString *message;
@property (copy, nonatomic, readonly) NSString *imageName;

+ (nullable instancetype)modelWithType:(EHIInformationBannerType)type;

@end

NS_ASSUME_NONNULL_END
