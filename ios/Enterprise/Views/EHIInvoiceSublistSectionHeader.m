//
//  EHIInvoiceSublistSectionHeader.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSublistSectionHeader.h"

@interface EHIInvoiceSublistSectionHeader ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIInvoiceSublistSectionHeader

- (void)updateWithModel:(NSString *)title metrics:(EHILayoutMetrics *)metrics
{
    self.titleLabel.text = title;
    self.backgroundColor = metrics.backgroundColor;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.titleLabel.frame)
    };
}

@end
