//
//  EHIDateTimeComponentFilterCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIDateTimeComponentFilterCell.h"
#import "EHIDateTimeComponentFilterViewModel.h"
#import "EHITemporalSelectionView.h"

@interface EHIDateTimeComponentFilterCell () <EHITemporalSelectionViewActions>
@property (strong, nonatomic) EHIDateTimeComponentFilterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;

@property (weak  , nonatomic) IBOutlet UILabel *pickupLabel;
@property (weak  , nonatomic) IBOutlet UILabel *returnLabel;

// beware: the following elements are using custom multiplier on its constraints!
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *pickupDateView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *pickupTimeView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *returnDateView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *returnTimeView;

@property (weak  , nonatomic) IBOutlet UIView *timeSectionView;

@end

@implementation EHIDateTimeComponentFilterCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDateTimeComponentFilterViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.stylize(self.pickupDateView.layer);
    self.stylize(self.pickupTimeView.layer);
    self.stylize(self.returnDateView.layer);
    self.stylize(self.returnTimeView.layer);
}

- (void (^)(CALayer *))stylize
{
    return ^(CALayer *layer) {
        layer.borderColor = [UIColor ehi_grayColor2].CGColor;
        layer.borderWidth = 1.0f;
    };
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDateTimeComponentFilterViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateTimeSection:)];
    [MTRReactor autorun:self action:@selector(invalidatePickupTimeSection:)];
    [MTRReactor autorun:self action:@selector(invalidateReturnTimeSection:)];
    
    model.bind.map(@{
        source(model.pickupTitle)     : dest(self, .pickupLabel.text),
        source(model.returnTitle)     : dest(self, .returnLabel.text),
        source(model.pickupDateModel) : dest(self, .pickupDateView.viewModel),
        source(model.pickupTimeModel) : dest(self, .pickupTimeView.viewModel),
        source(model.returnDateModel) : dest(self, .returnDateView.viewModel),
        source(model.returnTimeModel) : dest(self, .returnTimeView.viewModel)
    });
}

- (void)invalidateTimeSection:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hidePickupTimeSection && self.viewModel.hideReturnTimeSection;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.timeSectionView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidatePickupTimeSection:(MTRComputation *)computation
{
    BOOL hidePickup = self.viewModel.hidePickupTimeSection;
    
    self.pickupTimeView.alpha = hidePickup ? 0.0f : 1.0f;
}

- (void)invalidateReturnTimeSection:(MTRComputation *)computation
{
    BOOL hideReturn = self.viewModel.hideReturnTimeSection;

    self.returnTimeView.alpha = hideReturn ? 0.0f : 1.0f;
}

# pragma mark - EHITemporalSelectionViewActions

- (void)temporalSelectionViewDidTap:(EHITemporalSelectionView *)view
{
    EHIDateTimeComponentSection section = [self sectionForView:view];
    
    [self ehi_performAction:@selector(dateTimeComponentDidTapOnSection:) withSender:@(section)];
}

- (void)temporalSelectionViewDidTapClean:(EHITemporalSelectionView *)view
{
    EHIDateTimeComponentSection section = [self sectionForView:view];

    [self ehi_performAction:@selector(dateTimeComponentDidTapOnCleanSection:) withSender:@(section)];
}

- (EHIDateTimeComponentSection)sectionForView:(EHITemporalSelectionView *)view
{
    if([view isEqual:self.pickupDateView]) {
        return EHIDateTimeComponentSectionPickupDate;
    }
    if([view isEqual:self.pickupTimeView]) {
        return EHIDateTimeComponentSectionPickupTime;
    }
    if([view isEqual:self.returnDateView]) {
        return EHIDateTimeComponentSectionReturnDate;
    }
    if([view isEqual:self.returnTimeView]) {
        return EHIDateTimeComponentSectionReturnTime;
    }
    
    return EHIDateTimeComponentSectionPickupDate;
}

# pragma mark - Responder Chain

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    // this is necessary because the responder chain behaves differently when inside a collection view cell with custom subviews
    if(action == @selector(temporalSelectionViewDidTap:)
    || action == @selector(temporalSelectionViewDidTapClean:)) {
        return YES;
    } else {
        return [super canPerformAction:action withSender:sender];
    }
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
