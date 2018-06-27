//
//  EHIInvoiceSectionHeader.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSectionHeader.h"
#import "EHIInvoiceSectionHeaderViewModel.h"
#import "EHIButton.h"

@interface EHIInvoiceSectionHeader ()
@property (strong, nonatomic) EHIInvoiceSectionHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIView *buttonContainer;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@end

@implementation EHIInvoiceSectionHeader

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceSectionHeaderViewModel new];
    }

    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // move the image to the right
    self.actionButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceSectionHeaderViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateButtonContainer:)];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.actionTitle) : dest(self, .actionButton.ehi_title)
    });
}

- (void)invalidateButtonContainer:(MTRComputation *)computation
{
    // hide button if not using it
    BOOL usingButton = self.viewModel.actionTitle.length > 0;
    MASLayoutPriority priority = usingButton ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.buttonContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0f).priority(priority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapButton:(EHIButton *)sender
{
    [self ehi_performAction:@selector(invoiceSectionHeaderDidTapActionButton:) withSender:self];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 36.0f };
    return metrics;
}

- (CGSize)intrinsicContentSize
{
    CGFloat titleHeight  = CGRectGetHeight(self.titleLabel.frame);
    CGFloat buttonHeight = CGRectGetHeight(self.actionButton.frame);
    CGFloat height = MAX(titleHeight, buttonHeight);
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + EHILightPadding
    };
}

@end
