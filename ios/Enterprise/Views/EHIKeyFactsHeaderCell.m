//
//  EHIKeyFactsHeaderCell.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsHeaderCell.h"

@interface EHIKeyFactsHeaderCell()
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailsLabel;
@end

@implementation EHIKeyFactsHeaderCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.headerLabel.text = EHILocalizedString(@"key_facts_header_title", @"Key Facts About My Rental", @"");
    self.detailsLabel.text = [NSString stringWithFormat:@"%@\n\n%@",
        EHILocalizedString(@"key_facts_intro_summary", @"This is a summary of the key facts you should know when hiring a vehicle, which will help you understand what will be included in your rental agreement.", @""),
        EHILocalizedString(@"key_facts_intro_rental_agreement", @"The rental agreement will be entered into at the time and place of vehicle hire between you and our affiliate or franchisee of the Enterprise Rent-A-Car brand that operates the branch location (referred to as “the Rental Company”). You should always read your rental agreement in full before signing it. You can view the rental agreement in your renting country at the time of your rental pickup.", @"")];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailsLabel.frame) + EHIMediumPadding
    };
}

@end
