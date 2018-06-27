//
//  EHIRedemptionPointsViewModel.h
//  Enterprise
//
//  Created by fhu on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIRedemptionBannerType) {
    EHIRedemptionBannerTypeClassSelect,
    EHIRedemptionBannerTypeClassDetails,
    EHIRedemptionBannerTypeReview,
    EHIRedemptionBannerTypeRedemption
};

typedef NS_ENUM(NSUInteger, EHIRedemptionReviewStatus) {
    EHIRedemptionReviewStatusNone,
    EHIRedemptionReviewStatusUnexpanded,
    EHIRedemptionReviewStatusExpanded,
    EHIRedemptionReviewStatusRedeemed,
};

@interface EHIRedemptionPointsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *pointsHeaderString;
@property (copy  , nonatomic) NSString *pointsString;
@property (copy  , nonatomic) NSAttributedString *buttonAttributedString;

/* Review Screen Properties */
@property (copy  , nonatomic) NSString *redeemingString;
@property (copy  , nonatomic) NSString *redeemPointsString;
@property (copy  , nonatomic) NSString *savePointsString;
@property (assign, nonatomic) EHIRedemptionReviewStatus reviewStatus;
@property (assign, nonatomic, readonly) BOOL showExpandedReviewFooter;
@property (assign, nonatomic, readonly) BOOL showRedeemedReviewFooter;

@property (assign, nonatomic) BOOL canTapButton;

+ (instancetype)modelWithType:(EHIRedemptionBannerType)type;
- (void)toggleButtonTapped;
- (void)redeemButtonTapped;

@end