//
//  EHIInformationBannerViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIInformationBannerViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIInformationBannerViewModel ()
@property (assign, nonatomic) EHIInformationBannerType type;
@end

@implementation EHIInformationBannerViewModel

+ (nullable instancetype)modelWithType:(EHIInformationBannerType)type
{
    return type == EHIInformationBannerTypeNone ? nil : [(EHIInformationBannerViewModel *)[self alloc] initWithType:type];
}

- (instancetype)initWithType:(EHIInformationBannerType)type
{
    if(self = [super init]) {
        _type = type;
    }
    
    return self;
}

# pragma mark - Accessors

- (nullable NSString *)title
{
    switch(self.type) {
        default: return nil;
    }
}

- (nullable NSString *)message
{
    switch(self.type) {
        case EHIInformationBannerTypeEmeraldClassSelect:
            return EHILocalizedString(@"reservation_review_emerald_club_active_message", @"Your Emerald Club account is currently active for this rental.", @"");
        case EHIInformationBannerTypeEmeraldReview:
        case EHIInformationBannerTypeEmeraldConfirmation:
            return EHILocalizedString(@"reservation_confirmation_emerald_club_active_message", @"When your rental is complete, you'll earn credits and tier status on your Emerald Club account.", @"");
        case EHIInformationBannerTypePrepayRates:
            return EHILocalizedString(@"info_banner_prepay_message", @"Pre-pay functionality is not yet available through this app, but fear not: You'll still get the discounted rate at pickup.", @"");
        default: return nil;
    }
}

- (NSString *)imageName
{
    switch(self.type) {
        case EHIInformationBannerTypePrepayRates:
            return @"icon_star_01";
        default:
            return @"icon_confirm_01";
    }
}

@end

NS_ASSUME_NONNULL_END
