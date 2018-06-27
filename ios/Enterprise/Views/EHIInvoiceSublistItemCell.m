//
//  EHIInvoiceSublistItemCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSublistItemCell.h"
#import "EHIInvoiceSublistItemViewModel.h"
#import "EHILabel.h"

@interface EHIInvoiceSublistItemCell ()
@property (strong, nonatomic) EHIInvoiceSublistItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIInvoiceSublistItemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceSublistItemViewModel new];
    }

    return self;
}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
    [super willMoveToSuperview:newSuperview];
    
    self.subtitleLabel.disablesAutoShrink = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceSublistItemViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
       source(model.title)     : dest(self, .titleLabel.attributedText),
       source(model.subtitle)  : dest(self, .subtitleLabel.attributedText)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = MAX(CGRectGetMaxY(self.titleLabel.frame), CGRectGetMaxY(self.subtitleLabel.frame))
    };
}

@end
