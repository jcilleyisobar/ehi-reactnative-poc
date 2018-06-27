//
//  EHIReviewPaymentChangeViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentChangeViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIPaymentOptionModalViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationBuilder.h"

@interface EHIReviewPaymentChangeViewModel ()
@property (assign, nonatomic) BOOL isPrepay;
@end

@implementation EHIReviewPaymentChangeViewModel

- (instancetype)initWithPriceContext:(id<EHIPriceContext>)context prepay:(BOOL)isPrepay
{
    if(self = [super init]) {
        self.isPrepay = isPrepay;
        [self updateWithPriceContext:context prepay:isPrepay];
    }
    
    return self;
}

- (void)updateWithPriceContext:(id<EHIPriceContext>)priceContext prepay:(BOOL)isPrepay
{
    _hideCardIcon = isPrepay;
    
    NSString *savingsTitle = isPrepay ? EHILocalizedString(@"reservation_review_pay_later_savings", @"Pay Later for #{amount} more", @"") : EHILocalizedString(@"reservation_review_pay_now_savings", @"Pay Now and save #{amount}", @"");
    
    EHIPrice *savingPrice = [priceContext viewPrice] ?: [priceContext paymentPrice];
    NSString *price = [EHIPriceFormatter format:savingPrice].abs(YES).string ?: @"";
    
    _title = [savingsTitle ehi_applyReplacementMap:@{
        @"amount" : price
    }];
}

- (void)didTapChangePayment
{
    NSString *action = self.isPrepay ? EHIAnalyticsResChangeHeaderPayNow : EHIAnalyticsResChangeHeaderPayLater;
    NSString *macro  = self.isPrepay ? EHIAnalyticsMacroEventPayLaterSelected : EHIAnalyticsMacroEventPayNowSelected;
    
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = macro;
        
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    }];
}

- (void)didTapMoreInfo
{
    if(self.isPrepay) {
        [EHIAnalytics trackAction:EHIAnalyticsResChangePayLaterHelp handler:^(EHIAnalyticsContext *context) {
            [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
        }];
    }
    
    [[EHIPaymentOptionModalViewModel new] present:nil];
}

@end
