//
//  EHIReservationLineItemCell.h
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationLineItemCell.h"
#import "EHIReservationLineItemViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationLineItemCell ()
@property (strong, nonatomic) EHIReservationLineItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *accessoryLabel;
@property (weak  , nonatomic) IBOutlet UILabel *learnMoreLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *subtitlePaddingHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *accessoryTrailingConstraint;
@end

@implementation EHIReservationLineItemCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationLineItemViewModel new];
    }
    return self;
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.accessoryLabel.attributedText = nil;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    [self.contentView setBackgroundColor:metrics.backgroundColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationLineItemViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateAccessoryLabel:)];
    [MTRReactor autorun:self action:@selector(invalidateSubtitle:)];
    [MTRReactor autorun:self action:@selector(invalidateContent:)];

    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.attributedText)
    });
}

- (void)invalidateAccessoryLabel:(MTRComputation *)computation
{
    NSAttributedString *accessoryString = self.viewModel.accessoryTitle;
    
    self.accessoryLabel.attributedText = accessoryString;
    self.accessoryTrailingConstraint.isDisabled = !accessoryString.string.length;
}

- (void)invalidateSubtitle:(MTRComputation *)computation
{
    NSString *subtitle = self.viewModel.subtitle;
    
    self.subtitleLabel.text = subtitle;
    self.subtitlePaddingHeight.isDisabled = subtitle.length == 0;
}

- (void)invalidateContent:(MTRComputation *)computation
{
    BOOL isLearnMore = self.viewModel.isLearnMore;
    self.learnMoreLabel.text = isLearnMore ? self.viewModel.title.string : nil;
    
    MASLayoutPriority constraintPriority = isLearnMore ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.containerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *contentView = self.viewModel.isLearnMore ? self.learnMoreLabel : self.containerView;
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(contentView.frame),
    };
}

@end
