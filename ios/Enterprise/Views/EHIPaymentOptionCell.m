//
//  EHIPaymentOptionCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionCell.h"
#import "EHIPaymentOptionCellViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIPaymentOptionCell ()
@property (strong, nonatomic) EHIPaymentOptionCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *title;
@property (weak  , nonatomic) IBOutlet UILabel *subtitle;
@property (weak  , nonatomic) IBOutlet UILabel *price;
@property (weak  , nonatomic) IBOutlet UILabel *discount;
@property (weak  , nonatomic) IBOutlet UIView *subtitleContainerView;
@property (weak  , nonatomic) IBOutlet UIView *priceContainerView;
@property (weak  , nonatomic) IBOutlet UIView *contentContainerView;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *arrowViewWidthConstraint;

@end

@implementation EHIPaymentOptionCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPaymentOptionCellViewModel new];
    }

    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
	[super updateWithModel:model metrics:metrics];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentOptionCellViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidatePaymentLayout:)];
    
    model.bind.map(@{
         source(model.title)    : dest(self, .title.text),
         source(model.discount) : dest(self, .discount.text),
         source(model.price)    : dest(self, .price.attributedText),
         source(model.subtitle) : dest(self, .subtitle.attributedText)
    });
}

- (void)invalidatePaymentLayout:(MTRComputation *)computation
{
    BOOL isDisabled = self.viewModel.layoutType == EHIPaymentOptionLayoutDisabled;
    self.arrowViewWidthConstraint.isDisabled = isDisabled;
    
    if(isDisabled) {
        [self disabledLayout];
    } else {
        [self enabledLayout];
    }
}

- (void)disabledLayout
{
    UIColor *disableColor   = [UIColor ehi_silverColor];
    self.title.textColor    = disableColor;
    self.subtitle.textColor = disableColor;
    self.price.textColor    = disableColor;
    self.layer.borderColor  = disableColor.CGColor;
}

- (void)enabledLayout
{
    UIColor *activeColor    = [UIColor ehi_greenColor];
    self.subtitle.textColor = [UIColor blackColor];
    self.title.textColor    = activeColor;
    self.price.textColor    = activeColor;
    self.layer.borderColor  = activeColor.CGColor;
    self.backgroundColor    = [UIColor whiteColor];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat containerHeight = CGRectGetHeight(self.contentContainerView.frame);
    CGFloat priceHeight     = CGRectGetHeight(self.priceContainerView.frame);
    //for disabled layout, the height has to be at least 36
    CGFloat height          = MAX(36.0f, MAX(containerHeight, priceHeight));
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + EHIHeavyPadding
    };
}

@end
