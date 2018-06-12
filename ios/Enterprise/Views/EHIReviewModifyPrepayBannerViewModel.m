//
//  EHIReviewModifyPrepayBannerViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewModifyPrepayBannerViewModel.h"
#import "EHIPriceFormatter.h"

@interface EHIReviewModifyPrepayBannerViewModel()
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *subtitle;
@property (copy, nonatomic) NSString *totalAmount;
@property (copy, nonatomic) NSString *price;
@end

@implementation EHIReviewModifyPrepayBannerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIPrice class]]) {
            _title = EHILocalizedString(@"modify_reservation_prepay_default_warning_text", @"Select the information below that you would like to update", @"");
            _price = [EHIPriceFormatter format:(EHIPrice *)model].string;
        }
    }
    return self;
}

- (void)setUpdated:(BOOL)updated
{
    _updated = updated;
    
    if(updated) {
        self.title       = self.isNAAirport
            ? EHILocalizedString(@"modify_reservation_prepay_naa_warning_text", @"Please review your modified rental details.", @"")
            : EHILocalizedString(@"modify_reservation_prepay_warning_text", @"Upon updating your reservation, you will receive a refund for your original reservation and be charged the new total.", @"");
        
        self.subtitle    = !self.isNAAirport
            ? EHILocalizedString(@"modify_reservation_prepay_original_amount", @"Original Amount:", @"")
            : nil;
        
        self.totalAmount = !self.isNAAirport
            ? self.price
            : nil;
    }
}

@end
