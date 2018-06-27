//
//  EHICustomerSupportHeaderCell.m
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICustomerSupportHeaderCell.h"

@interface EHICustomerSupportHeaderCell()
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@end

@implementation EHICustomerSupportHeaderCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.headerLabel.attributedText = EHIAttributedStringBuilder.new
	.text(EHILocalizedString(@"customer_support_header_prefix", @"We're here to help", @"")).fontStyle(EHIFontStyleBold, 24).color([UIColor ehi_blackColor]).string;
//        .space.appendText(EHILocalizedString(@"customer_support_header_suffix", @"help?", @"")).fontStyle(EHIFontStyleHeavy, 24).color([UIColor ehi_greenColor]).string;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.headerLabel.frame) + EHIMediumPadding
    };
}

@end
