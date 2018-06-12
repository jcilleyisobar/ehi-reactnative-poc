//
//  EHIConfirmationCancelModalViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 28/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIConfirmationCancelModalViewModel.h"
#import "EHICancellationFee.h"

SpecBegin(EHIConfirmationCancelModalViewModelTests)

describe(@"the confirmation cancel modal", ^{
    __block EHIConfirmationCancelModalViewModel *model = [EHIConfirmationCancelModalViewModel new];
    __block EHIPrice *original;
    __block EHICancellationFee *cancellationFee;
    __block EHIPrice *cancelFee;
    __block EHIPrice *refund;
    
    context(@"default configuration", ^{
        it(@"should have title and button actions title", ^{
            expect(model.title).to.localizeFrom(@"reservation_cancel_message_title");
            expect(model.firstButtonTitle).to.localizeFrom(@"reservation_cancel_confirmation_message");
            expect(model.secondButtonTitle).to.localizeFrom(@"reservation_cancel_dismiss_message");
        });
    });
    
    context(@"on CA to USA res", ^{
        beforeAll(^{
            cancellationFee = [EHICancellationFee modelWithDictionary:@{
                @"refund_amount": @{
                    @"code": @"USD",
                    @"symbol": @"USD",
                    @"amount": @"0.00"
                },
                @"original_amount_view": @{
                    @"code": @"CAD",
                    @"symbol": @"$",
                    @"amount": @"42.07"
                },
                @"fee_amount_view": @{
                    @"code": @"CAD",
                    @"symbol": @"$",
                    @"amount": @"100.73"
                },
                @"refund_amount_view": @{
                    @"code": @"CAD",
                    @"symbol": @"$",
                    @"amount": @"0.00"
                },
                @"original_amount_payment": @{
                    @"code": @"USD",
                    @"symbol": @"USD",
                    @"amount": @"56.38"
                },
                @"fee_amount_payment": @{
                    @"code": @"USD",
                    @"amount": @"135.00"
                },
                @"refund_amount_payment": @{
                    @"code": @"USD",
                    @"symbol": @"USD",
                    @"amount": @"30.97"
                },
                @"fee_apply": @(YES)
            }];
            
            original = [EHIPrice modelWithDictionary:@{
                @"code"   : @"CAD",
                @"symbol" : @"$",
                @"amount" : @"73.15"
            }];

            model = [[EHIConfirmationCancelModalViewModel alloc]
                     initWithPrice:original
                     cancelFee:cancellationFee.feeView
                     cancellationFee:cancellationFee
                     refund:cancellationFee.refundAmountView];
        });
        
        it(@"should show original amount", ^{
            expect(model.originalAmountTile).to.localizeFrom(@"reservation_cancel_original_amount");
            expect(model.originalAmount).to.equal(@"CA$73.15");
        });
        
        it(@"should show cancel fee", ^{
            expect(model.cancellationFeeTitle).to.localizeFrom(@"reservation_cancel_cancelation_fee");
            expect(model.cancellationFee).to.equal(@"-CA$100.73");
        });
        
        it(@"should show refund amount", ^{
            expect(model.refundedAmountTitle).to.localizeFrom(@"reservation_cancel_refunded_amount");
            expect(model.refundedAmount.string).to.equal(@"CA$0.00");
        });
        
        it(@"should show subtitle", ^{
            expect(model.subtitle.string).to.localizeFromMap(@"confirmation_cancel_reservation_message", @{
                @"terms" : [EHILocalizedString(@"general_prepay_policies", @"", @"") stringByAppendingString:@"\n"],
                @"amount" : @"CA$100.73"
            });
        });
        
        it(@"should have currency conversion subtitle", ^{
            expect(model.conversionSubtitle).localizeFromMap(@"reservation_currency_conversion_title", @{
                @"currency_code" : @"USD"
            });
        });
        
        it(@"should have converted refund", ^{
            expect(model.convertedRefund).localizeFromMap(@"reservation_cancel_currency_refund", @{
                @"refund" : @"$30.97"
            });
        });
    });
    
    context(@"on USA to CA res", ^{
        beforeAll(^{
            cancellationFee = [EHICancellationFee modelWithDictionary:@{
                @"fee_amount_payment": @{
                    @"code": @"CAD",
                    @"amount": @"135.00"
                },
                @"refund_amount": @{
                    @"code": @"CAD",
                    @"symbol": @"CAD",
                    @"amount": @"0.00"
                },
                @"original_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"42.07"
                },
                @"fee_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"100.73"
                },
                @"refund_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"0.00"
                },
                @"original_amount_payment": @{
                    @"code": @"CAD",
                    @"symbol": @"CAD",
                    @"amount": @"56.38"
                },
                @"fee_amount_payment": @{
                    @"code": @"CAD",
                    @"amount": @"135.00"
                },
                @"refund_amount_payment": @{
                    @"code": @"CAD",
                    @"symbol": @"CAD",
                    @"amount": @"30.97"
                },
                @"fee_apply": @(YES)
            }];
            
            original = [EHIPrice modelWithDictionary:@{
                @"code"   : @"USD",
                @"symbol" : @"$",
                @"amount" : @"73.15"
            }];

            model = [[EHIConfirmationCancelModalViewModel alloc]
                     initWithPrice:original
                     cancelFee:cancellationFee.feeView
                     cancellationFee:cancellationFee
                     refund:cancellationFee.refundAmountView];
        });
        
        it(@"should show original amount", ^{
            expect(model.originalAmountTile).to.localizeFrom(@"reservation_cancel_original_amount");
            expect(model.originalAmount).to.equal(@"$73.15");
        });
        
        it(@"should show cancel fee", ^{
            expect(model.cancellationFeeTitle).to.localizeFrom(@"reservation_cancel_cancelation_fee");
            expect(model.cancellationFee).to.equal(@"-$100.73");
        });
        
        it(@"should show refund amount", ^{
            expect(model.refundedAmountTitle).to.localizeFrom(@"reservation_cancel_refunded_amount");
            expect(model.refundedAmount.string).to.equal(@"$0.00");
        });
        
        it(@"should show subtitle", ^{
            expect(model.subtitle.string).to.localizeFromMap(@"confirmation_cancel_reservation_message", @{
                @"terms" : [EHILocalizedString(@"general_prepay_policies", @"", @"") stringByAppendingString:@"\n"],
                @"amount" : @"$100.73"
            });
        });
        
        it(@"should have currency conversion subtitle", ^{
            expect(model.conversionSubtitle).localizeFromMap(@"reservation_currency_conversion_title", @{
                @"currency_code" : @"CAD"
            });
        });
        
        it(@"should have converted refund", ^{
            expect(model.convertedRefund).localizeFromMap(@"reservation_cancel_currency_refund", @{
                @"refund" : @"CA$30.97"
            });
        });
    });

    context(@"on non NA res", ^{
        beforeAll(^{
            cancellationFee = [EHICancellationFee modelWithDictionary:@{
                @"fee_amount_view": @{
                    @"code"   : @"GBP",
                    @"amount" : @"50.00"
                },
                @"original_amount_view": @{
                    @"code"   : @"GBP",
                    @"symbol" : @"£",
                    @"amount" : @"97.85"
                },
                @"original_amount_payment": @{
                    @"code"   : @"GBP",
                    @"symbol" : @"£",
                    @"amount" : @"73.15"
                },
                @"refund_amount_view": @{
                    @"code"   : @"GBP",
                    @"symbol" : @"£",
                    @"amount" : @"30.97"
                },
            }];
            
            original = [EHIPrice modelWithDictionary:@{
                @"code"   : @"GBP",
                @"symbol" : @"£",
                @"amount" : @"73.15"
            }];
            
            cancelFee = [EHIPrice modelWithDictionary:@{
                @"code"   : @"GBP",
                @"amount" : @"100.00"
            }];
            
            refund = [EHIPrice modelWithDictionary:@{
                @"code"   : @"GBP",
                @"symbol" : @"£",
                @"amount" : @"50.00"
            }];
            
            model = [[EHIConfirmationCancelModalViewModel alloc]
                     initWithPrice:original
                     cancelFee:cancelFee
                     cancellationFee:cancellationFee
                     refund:refund];
        });
        
        it(@"should show original amount", ^{
            expect(model.originalAmountTile).to.localizeFrom(@"reservation_cancel_original_amount");
            expect(model.originalAmount).to.equal(@"£73.15");
        });
        
        it(@"should show cancel fee", ^{
            expect(model.cancellationFeeTitle).to.localizeFrom(@"reservation_cancel_cancelation_fee");
            expect(model.cancellationFee).to.equal(@"-£100.00");
        });
        
        it(@"should show refund amount", ^{
            expect(model.refundedAmountTitle).to.localizeFrom(@"reservation_cancel_refunded_amount");
            expect(model.refundedAmount.string).to.equal(@"£50.00");
        });
        
        it(@"should show subtitle", ^{
            expect(model.subtitle.string).to.localizeFromMap(@"confirmation_cancel_reservation_message", @{
                @"terms" : [EHILocalizedString(@"general_prepay_policies", @"", @"") stringByAppendingString:@"\n"],
                @"amount" : @"£50.00"
            });
        });
        
        it(@"shouldn't have currency conversion subtitle", ^{
            expect(model.conversionSubtitle).to.beNil();
        });
        
        it(@"shouldn't have converted refund", ^{
            expect(model.convertedRefund).to.beNil();
        });
    });
    
    context(@"on regular res", ^{
        beforeAll(^{
            original = [EHIPrice modelWithDictionary:@{
                @"code"   : @"USD",
                @"symbol" : @"$",
                @"amount" : @"73.15"
            }];
            
            cancelFee = [EHIPrice modelWithDictionary:@{
                @"code"   : @"USD",
                @"amount" : @"100.00"
            }];
            
            refund = [EHIPrice modelWithDictionary:@{
                @"code"   : @"USD",
                @"symbol" : @"$",
                @"amount" : @"0.00"
            }];
            
            model = [[EHIConfirmationCancelModalViewModel alloc]
                     initWithPrice:original
                     cancelFee:cancelFee
                     cancellationFee:nil
                     refund:refund];
        });
        
        it(@"should show original amount", ^{
            expect(model.originalAmountTile).to.localizeFrom(@"reservation_cancel_original_amount");
            expect(model.originalAmount).to.equal(@"$73.15");
        });
        
        it(@"should show cancel fee", ^{
            expect(model.cancellationFeeTitle).to.localizeFrom(@"reservation_cancel_cancelation_fee");
            expect(model.cancellationFee).to.equal(@"-$100.00");
        });
        
        it(@"should show refund amount", ^{
            expect(model.refundedAmountTitle).to.localizeFrom(@"reservation_cancel_refunded_amount");
            expect(model.refundedAmount.string).to.equal(@"$0.00");
        });
        
        it(@"should show subtitle", ^{
            expect(model.subtitle.string).to.localizeFromMap(@"reservation_cancel_message_details", @{
                    @"terms" : [EHILocalizedString(@"general_prepay_policies", @"", @"") stringByAppendingString:@"\n"]
            });
        });

    });
});

SpecEnd
