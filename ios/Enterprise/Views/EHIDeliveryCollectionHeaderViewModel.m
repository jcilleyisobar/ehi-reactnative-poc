//
//  EHIDeliveryCollectionHeaderViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionHeaderViewModel.h"

@interface EHIDeliveryCollectionHeaderViewModel ()
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSAttributedString *detailsTitle;
@property (copy, nonatomic) NSAttributedString *chargesTitle;
@end

@implementation EHIDeliveryCollectionHeaderViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"delivery_collection_header_title", @"Delivery & Collection", @"");
        _detailsTitle = [self constructDetailsTitle];
        _chargesTitle = [self constructChargesTitle];
    }
    
    return self;
}

//
// Helpers
//

- (NSAttributedString *)constructDetailsTitle
{
    NSString *detailsTitle = EHILocalizedString(@"delivery_collection_header_details_title", @"Your contract allows you to specify a specific location where you'd like Enterprise to delivery or collect your rental.", @"");
    
    return EHIAttributedStringBuilder.new.size(18.0).lineSpacing(13.0).text(detailsTitle).string;
}

- (NSAttributedString *)constructChargesTitle
{
    NSString *chargesTitle = EHILocalizedString(@"delivery_collection_header_charges_title", @"Additional charges may apply for Delivery & Collection services.", @"");
    
    return EHIAttributedStringBuilder.new.size(14.0).lineSpacing(8.0).text(chargesTitle).string;
}

@end
