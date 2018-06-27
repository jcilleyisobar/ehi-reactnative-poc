//
//  EHIReservationPriceInvoiceCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceInvoiceCell.h"
#import "EHIReservationPriceInvoiceViewModel.h"

@interface EHIReservationPriceInvoiceCell ()
@property (strong, nonatomic) EHIReservationPriceInvoiceViewModel *viewModel;
@end

@implementation EHIReservationPriceInvoiceCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationPriceInvoiceViewModel new];
    }

    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
	[super updateWithModel:model metrics:metrics];


}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPriceInvoiceViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
    });
}

@end
