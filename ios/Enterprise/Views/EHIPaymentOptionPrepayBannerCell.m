//
//  EHIPaymentOptionPrepayBannerCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionPrepayBannerCell.h"
#import "EHIPaymentOptionPrepayBannerViewModel.h"

@interface EHIPaymentOptionPrepayBannerCell ()
@property (strong, nonatomic) EHIPaymentOptionPrepayBannerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIPaymentOptionPrepayBannerCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPaymentOptionPrepayBannerViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentOptionPrepayBannerViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.titleLabel.frame) + EHIHeavyPadding
    };
}

@end
