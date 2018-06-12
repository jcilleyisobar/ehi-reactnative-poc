//
//  EHIProfilePaymentItemCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentItemCell.h"
#import "EHIProfilePaymentItemViewModel.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"

@interface EHIProfilePaymentItemCell ()
@property (strong, nonatomic) EHIProfilePaymentItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *paymentSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *preferredContainer;
@property (weak  , nonatomic) IBOutlet UILabel *preferredLabel;
@property (weak  , nonatomic) IBOutlet UIView *iconContainer;
@property (weak, nonatomic) IBOutlet UIImageView *cardImage;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *creditCardWidthConstraint;
@end

@implementation EHIProfilePaymentItemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentItemViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfilePaymentItemViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
    [MTRReactor autorun:self action:@selector(invalidatePreferred:)];
    [MTRReactor autorun:self action:@selector(invalidateExpired:)];
    [MTRReactor autorun:self action:@selector(invalidateCardImage:)];
    
    model.bind.map(@{
        source(model.title)           : dest(self, .titleLabel.text),
        source(model.paymentTitle)    : dest(self, .paymentTitleLabel.text),
        source(model.paymentSubtitle) : dest(self, .paymentSubtitleLabel.attributedText),
        source(model.preferredTitle)  : dest(self, .preferredLabel.text),
        source(model.isFirst)         : ^(NSNumber *isFirst) {
                                            self.topDivider.hidden = !isFirst.boolValue;
                                        }
    });
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    BOOL isFirst = self.viewModel.isFirst;
    
    MASLayoutPriority priority = isFirst ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.titleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidatePreferred:(MTRComputation *)computation
{
    BOOL isPreferred = self.viewModel.isPreferred;
    
    MASLayoutPriority priority = isPreferred ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.preferredContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateExpired:(MTRComputation *)computation
{
    BOOL isExpired = self.viewModel.isExpired;
    
    MASLayoutPriority priority = isExpired ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.iconContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateCardImage:(MTRComputation *)computation
{
    NSString *cardImage = self.viewModel.cardImage;
    self.cardImage.ehi_imageName = cardImage;
    self.creditCardWidthConstraint.isDisabled = (cardImage == nil);
}

# pragma mark - Layout

- (void)willMoveToSuperview:(UIView *)newSuperview
{
    [super willMoveToSuperview:newSuperview];
    
    self.paymentTitleLabel.disablesAutoShrink    = YES;
    self.paymentSubtitleLabel.disablesAutoShrink = YES;
}

- (CGSize)intrinsicContentSize
{
    CGFloat padding = self.viewModel.isLast ? EHIMediumPadding : 5.0f;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height =  CGRectGetMaxY(self.preferredContainer.frame) + padding
    };
}

@end
