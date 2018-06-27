//
//  EHIRedemptionPointsViewModel.m
//  Enterprise
//
//  Created by fhu on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIUser.h"
#import "EHIReservationBuilder.h"
#import "EHIRedemptionViewModel.h"
#import "EHISettings.h"
#import "EHIInfoModalViewModel.h"

@interface EHIRedemptionPointsViewModel () <EHIReservationBuilderReadinessListener>
@property (assign, nonatomic) EHIRedemptionBannerType type;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIRedemptionPointsViewModel

+ (instancetype)modelWithType:(EHIRedemptionBannerType)type
{
    EHIRedemptionPointsViewModel *viewModel = [EHIRedemptionPointsViewModel new];
    viewModel.type = type;
    
    return viewModel;
}

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _pointsHeaderString = EHILocalizedString(@"redemption_header_points_header", @"YOUR POINTS", @"");
        _pointsString       = [EHIUser currentUser].loyaltyPoints;
        
        _redeemingString    = EHILocalizedString(@"redemption_enabled_title", @"You're redeeming points", @"");
        _redeemPointsString = EHILocalizedString(@"redemption_redeem_point_button_title", @"REDEEM POINTS", @"");
        _savePointsString   = EHILocalizedString(@"redemption_save_point_button_title", @"Save Points", @"");
        
        _canTapButton = YES;
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // wait for builder readiness to add reactions
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateButtonText:)];
    [MTRReactor autorun:self action:@selector(invalidateStatus:)];
}

- (void)invalidateButtonText:(MTRComputation *)computation
{
    switch(self.type) {
        case EHIRedemptionBannerTypeClassSelect:
        case EHIRedemptionBannerTypeClassDetails:
            self.buttonAttributedString = [self constructClassSelectButtonText]; break;
        case EHIRedemptionBannerTypeReview:
            break;
        case EHIRedemptionBannerTypeRedemption:
            self.buttonAttributedString = nil; break;
    }
}

- (void)invalidateStatus:(MTRComputation *)computation
{
    BOOL hasRedeemed = self.builder.pointsUsed > 0;
    
    if(hasRedeemed) {
        self.reviewStatus = EHIRedemptionReviewStatusRedeemed;
    } else {
        self.reviewStatus = EHIRedemptionReviewStatusUnexpanded;
    }
}

//
// Helpers
//

- (NSAttributedString *)constructClassSelectButtonText
{
    NSString *title = self.builder.hidePoints
        ? EHILocalizedString(@"redemption_header_points_show_label", @"VIEW POINTS", @"")
        : EHILocalizedString(@"redemption_header_points_hide_label", @"HIDE POINTS", @"");

    return [self standardAttributedStringWithString:title];
}

- (NSAttributedString *)standardAttributedStringWithString:(NSString *)string
{
    return [NSAttributedString attributedStringWithString:string font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:18]];
}

- (NSAttributedString *)enoughPointsAttributedString
{
    NSString *header = EHILocalizedString(@"redemption_free_days_title", @"ENOUGH POINTS FOR", @"");
    NSString *subHeader = EHILocalizedString(@"redemption_free_days_subtitle", @"#{number_of_days} free days", @"");
    
    subHeader = [subHeader ehi_applyReplacementMap:@{
        @"number_of_days" : @(self.builder.maxRedemptionDays)
    }];
    
    return EHIAttributedStringBuilder.new
        .text(header).fontStyle(EHIFontStyleLight, 16.0f).color([UIColor whiteColor]).newline
        .appendText(subHeader).fontStyle(EHIFontStyleRegular, 24.0f).color([UIColor whiteColor]).string;
}

- (NSAttributedString *)notEnoughPointsAttributedString
{
    NSString *header = EHILocalizedString(@"redemption_not_enough_points_title", @"Not enough points for a free day", @"");
    return EHIAttributedStringBuilder.new
        .text(header).fontStyle(EHIFontStyleLight, 18.0f).color([UIColor whiteColor]).string;
}

- (NSAttributedString *)removePointsAttributedString
{
    NSString *header = EHILocalizedString(@"redemption_remove_point_button_title", @"REMOVE POINTS FOR THIS RENTAL", @"");
    return EHIAttributedStringBuilder.new
        .text(header).fontStyle(EHIFontStyleBold, 16.0f).paragraph(1, NSTextAlignmentCenter).string;
}

# pragma mark - Setters

- (void)setType:(EHIRedemptionBannerType)type
{
    _type = type;
    
    if(type == EHIRedemptionBannerTypeReview) {
        self.reviewStatus = EHIRedemptionReviewStatusUnexpanded;
    }
}

- (void)setReviewStatus:(EHIRedemptionReviewStatus)reviewStatus
{
    // ignore if not review type
    if(self.type != EHIRedemptionBannerTypeReview) {
        reviewStatus = EHIRedemptionReviewStatusNone;
    }
    
    _reviewStatus = reviewStatus;
    
    BOOL canRedeemPoints = self.builder.selectedCarClass.canRedeemPoints;
    
    switch (_reviewStatus) {
        case EHIRedemptionReviewStatusUnexpanded:
            if(!canRedeemPoints) {
                self.buttonAttributedString = [self notEnoughPointsAttributedString];
                self.canTapButton = NO;
            } else {
                self.buttonAttributedString = [self standardAttributedStringWithString:EHILocalizedString(@"redemption_redeem_point_button_title", @"REDEEM POINTS", @"")];
                self.canTapButton = YES;
            }
            break;
        case EHIRedemptionReviewStatusExpanded:
            self.buttonAttributedString = [self enoughPointsAttributedString];
            self.canTapButton = NO;
            break;
        case EHIRedemptionReviewStatusRedeemed:
            self.buttonAttributedString = [self removePointsAttributedString];
            self.canTapButton = YES;
            break;
        case EHIRedemptionReviewStatusNone:
            break;
    }
}

# pragma mark - Getters

- (BOOL)showExpandedReviewFooter
{
    return self.reviewStatus == EHIRedemptionReviewStatusExpanded;
}

- (BOOL)showRedeemedReviewFooter
{
    return self.reviewStatus == EHIRedemptionReviewStatusRedeemed;
}

# pragma mark - Actions

- (void)toggleButtonTapped
{
    switch(self.type) {
        case EHIRedemptionBannerTypeClassSelect:
        case EHIRedemptionBannerTypeClassDetails:
            [self togglePointsVisibilityOnBuilder]; break;
        case EHIRedemptionBannerTypeReview:
            [self togglePointsVisibility]; break;
        case EHIRedemptionBannerTypeRedemption:
            break;
    }
}

- (void)redeemButtonTapped
{
    if (!self.builder.selectedCarClass.isRedemptionAllowed){
        [self showRedemptionNotAllowedModal];
        return;
    }
    [self pushToRedemptionScreen];
}

- (void)showRedemptionNotAllowedModal
{
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title = EHILocalizedString(@"currently_dont_allow_points_unsupported", @"Points are only applicable at participating locations. Points cannot be redeemed at certain locations.", @"");
    model.hidesCloseButton = YES;
    
    [model present:nil];
}

- (void)pushToRedemptionScreen
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionRedeemPoints handler:nil];
    
    EHIRedemptionViewModel *viewModel = [EHIRedemptionViewModel new];
    [viewModel updateWithModel:self.builder.reservation.selectedCarClass];

    self.router.transition
        .push(EHIScreenReservationRedemption).object(viewModel).start(nil);
}

//
// Helpers
//

- (void)togglePointsVisibility
{
    if (self.reviewStatus == EHIRedemptionReviewStatusExpanded) {
        [EHIAnalytics trackAction:EHIAnalyticsResActionSavePoints handler:nil];
    } else if (self.reviewStatus == EHIRedemptionReviewStatusRedeemed) {
        [EHIAnalytics trackAction:EHIAnalyticsResActionRemovePoints handler:nil];
    }
    
    if (self.reviewStatus == EHIRedemptionReviewStatusUnexpanded) {
        if (!self.builder.selectedCarClass.isRedemptionAllowed){
            [self showRedemptionNotAllowedModal];
            return;
        }
        [self pushToRedemptionScreen];
    }
    
    BOOL inReview = self.type == EHIRedemptionBannerTypeReview;
    if(inReview) {
        BOOL hasPoints = self.builder.pointsUsed > 0;
        self.reviewStatus = hasPoints ? EHIRedemptionReviewStatusExpanded : EHIRedemptionReviewStatusUnexpanded;
    } else {
        self.reviewStatus = (self.reviewStatus == EHIRedemptionReviewStatusExpanded || self.reviewStatus == EHIRedemptionReviewStatusRedeemed)
        ? EHIRedemptionReviewStatusUnexpanded
        : EHIRedemptionReviewStatusExpanded;
    }
}

- (void)togglePointsVisibilityOnBuilder
{
    self.builder.hidePoints = !self.builder.hidePoints;
    
    [EHIAnalytics trackAction:self.builder.hidePoints ? EHIAnalyticsResActionHidePoints : EHIAnalyticsResActionShowPoints handler:nil];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
