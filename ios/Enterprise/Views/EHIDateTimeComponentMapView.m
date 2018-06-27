//
//  EHIDateTimeComponentMapView.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIDateTimeComponentMapView.h"
#import "EHIDateTimeComponentMapViewModel.h"
#import "EHITemporalSelectionView.h"
#import "EHIRestorableConstraint.h"

@interface EHIDateTimeComponentMapView () <EHITemporalSelectionViewActions>
@property (strong, nonatomic) EHIDateTimeComponentMapViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *pickupLabel;
@property (weak  , nonatomic) IBOutlet UILabel *returnLabel;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *pickupTop;
@property (weak  , nonatomic) IBOutlet UIView *pickupDividerView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *pickupDateView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *pickupTimeView;

@property (weak  , nonatomic) IBOutlet UIView *returnDividerView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *returnDateView;
@property (weak  , nonatomic) IBOutlet EHITemporalSelectionView *returnTimeView;

@property (weak  , nonatomic) IBOutlet UIButton *clearButton;

@end

@implementation EHIDateTimeComponentMapView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDateTimeComponentMapViewModel new];
    }
    
    return self;
}

# pragma mark - Autolayout

- (void)updateConstraints
{
    [self updateDateWidth];
    
    BOOL hideClear = self.viewModel.hideClear;
    self.pickupTop.constant = hideClear ? 5.0f : EHILightPadding;
    
    [super updateConstraints];
}

- (void)updateDateWidth
{
    [UIView animateWithDuration:0.1 animations:^{
        [self.pickupDateView invalidateIntrinsicContentSize];
        [self.returnDateView invalidateIntrinsicContentSize];
        [self.pickupTimeView invalidateIntrinsicContentSize];
        [self.returnTimeView invalidateIntrinsicContentSize];
        [self setNeedsLayout];
        [self layoutIfNeeded];
    } completion:^(BOOL finished) {
        CGFloat availableWidth = [self availableWidth];
        CGFloat timeViewMaxWidth = MAX(self.pickupTimeView.intrinsicContentSize.width, self.returnTimeView.intrinsicContentSize.width);
        CGFloat dateViewMaxWidth = MAX(self.pickupDateView.intrinsicContentSize.width, self.returnDateView.intrinsicContentSize.width);
        
        BOOL willNotFitInScreen = timeViewMaxWidth + dateViewMaxWidth > availableWidth;
        CGFloat timeViewWidth   = willNotFitInScreen ? availableWidth/2 : timeViewMaxWidth;
        CGFloat dateViewWidth   = willNotFitInScreen ? availableWidth/2 : dateViewMaxWidth;
        
        [self.pickupDateView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(dateViewWidth));
        }];
        
        [self.returnDateView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(dateViewWidth));
        }];
        
        [self.pickupTimeView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(timeViewWidth));
        }];
        
        [self.returnTimeView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(timeViewWidth));
        }];
    }];
}

/*
 @brief Returns the available width for the the dynamic views pickupDateView/returnDateView and pickupTimeView/returnTimeView
 */
- (CGFloat)availableWidth
{
    CGFloat pickupLabelFullWidth = self.pickupLabel.layoutMargins.left +self.pickupLabel.intrinsicContentSize.width + self.pickupLabel.layoutMargins.right;
    CGFloat pickupDateMargins  = self.pickupDateView.layoutMargins.left + self.pickupDateView.layoutMargins.right;
    CGFloat pickupTimeMargins  = self.pickupTimeView.layoutMargins.left + self.pickupTimeView.layoutMargins.right;
    
    CGFloat returnLabelFullWidth = self.returnLabel.layoutMargins.left + self.returnLabel.intrinsicContentSize.width + self.returnLabel.layoutMargins.right;
    CGFloat returnDateMargins    = self.returnDateView.layoutMargins.left + self.returnDateView.layoutMargins.right;
    CGFloat returnTimeMargins    = self.returnTimeView.layoutMargins.left + self.returnTimeView.layoutMargins.right;
    
    CGFloat dividerFullWidth        = self.pickupDividerView.layoutMargins.left + CGRectGetWidth(self.pickupDividerView.frame) + self.pickupDividerView.layoutMargins.right;
    CGFloat clearButtonFullWidth    = self.clearButton.layoutMargins.left + CGRectGetWidth(self.clearButton.frame)+ self.clearButton.layoutMargins.right;
    CGFloat screenWidth             = [UIScreen mainScreen].bounds.size.width;
    
    CGFloat pickupRowAvailableSpace = (screenWidth - (pickupLabelFullWidth + dividerFullWidth + clearButtonFullWidth + pickupDateMargins + pickupTimeMargins));
    CGFloat returnRowAvailableSpace = (screenWidth - (returnLabelFullWidth + dividerFullWidth + clearButtonFullWidth + returnDateMargins + returnTimeMargins));
    
    CGFloat availableWidth = MIN(pickupRowAvailableSpace, returnRowAvailableSpace);
    return availableWidth;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDateTimeComponentMapViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidatePickupTimeSection:)];
    [MTRReactor autorun:self action:@selector(invalidateReturnTimeSection:)];
    [MTRReactor autorun:self action:@selector(invalidateClearButton:)];
    [MTRReactor autorun:self action:@selector(rebindDateModels:)];
    
    model.bind.map(@{
        source(model.pickupTitle)     : dest(self, .pickupLabel.text),
        source(model.returnTitle)     : dest(self, .returnLabel.text),
        source(model.pickupTimeModel) : dest(self, .pickupTimeView.viewModel),
        source(model.returnTimeModel) : dest(self, .returnTimeView.viewModel),
    });
}

- (void)rebindDateModels:(MTRComputation *)computation
{
    self.pickupDateView.viewModel = self.viewModel.pickupDateModel;
    self.returnDateView.viewModel = self.viewModel.returnDateModel;
    
    [self setNeedsUpdateConstraints];
    [self invalidateIntrinsicContentSize];
}

- (void)invalidatePickupTimeSection:(MTRComputation *)computation
{
    BOOL hidePickup = self.viewModel.hidePickupTimeSection;
    
    CGFloat alpha = hidePickup ? 0.0f : 1.0f;
    self.pickupTimeView.alpha    = alpha;
    self.pickupDividerView.alpha = alpha;
}

- (void)invalidateReturnTimeSection:(MTRComputation *)computation
{
    BOOL hideReturn = self.viewModel.hideReturnTimeSection;
    
    CGFloat alpha = hideReturn ? 0.0f : 1.0f;
    self.returnTimeView.alpha    = alpha;
    self.returnDividerView.alpha = alpha;
}

- (void)invalidateClearButton:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideClear;
    
    [self animateBlock:^{
        self.clearButton.alpha   = hide ? 0.0f : 1.0f;
        self.clearButton.enabled = !hide;
    }];
}

- (void)animateBlock:(void (^)())block
{
    [UIView animateWithDuration:0.3f animations:^{
        ehi_call(block)();
    }];
}

# pragma mark - Actions

- (IBAction)didTap:(UIControl *)sender
{
    [self ehi_performAction:@selector(dateTimeComponentDidTap:) withSender:self];
}

- (IBAction)didTapClear:(UIControl *)sender
{
    [self ehi_performAction:@selector(dateTimeComponentDidTapClear) withSender:self];
}

# pragma mark - EHITemporalSelectionViewActions

- (void)temporalSelectionViewDidTap:(EHITemporalSelectionView *)view
{
    EHIDateTimeComponentSection section = EHIDateTimeComponentSectionPickupDate;
    if([view isEqual:self.pickupDateView]) {
        section = EHIDateTimeComponentSectionPickupDate;
    }
    if([view isEqual:self.pickupTimeView]) {
        section = EHIDateTimeComponentSectionPickupTime;
    }
    if([view isEqual:self.returnDateView]) {
        section = EHIDateTimeComponentSectionReturnDate;
    }
    if([view isEqual:self.returnTimeView]) {
        section = EHIDateTimeComponentSectionReturnTime;
    }

    [self ehi_performAction:@selector(dateTimeComponentDidTapOnSection:) withSender:@(section)];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
