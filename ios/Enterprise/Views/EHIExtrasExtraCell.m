//
//  EHIExtrasExtraCell.m
//  Enterprise
//
//  Created by fhu on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIExtrasExtraCell.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHIStepperControl.h"
#import "EHIRestorableConstraint.h"
#import "EHIToggleButton.h"
#import "EHIButton.h"

@interface EHIExtrasExtraCell () <EHIStepperControlDelegate>
@property (strong, nonatomic) EHIExtrasExtraViewModel *viewModel;
@property (assign, nonatomic) BOOL arrowUp;
@property (assign, nonatomic) NSInteger amount;
@property (weak  , nonatomic) IBOutlet UIView *defaultView;
@property (weak  , nonatomic) IBOutlet UIView *stepperView;
@property (weak  , nonatomic) IBOutlet UIView *fallbackContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *priceLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *fallbackText;
@property (weak  , nonatomic) IBOutlet UILabel *stepperTitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@property (weak  , nonatomic) IBOutlet EHIButton *moreInfoButton;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *toggleButton;
@property (weak  , nonatomic) IBOutlet EHIStepperControl *stepper;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *stepperHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *dividerTop;
@end

@implementation EHIExtrasExtraCell

- (void)updateConstraints
{
    [super updateConstraints];
    
    // collapse the stepper height when we're not expanded
    [self.stepperHeight setIsDisabled:!self.viewModel.shouldExpandToggle];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.accessibilityIdentifier = EHIExtrasExtraRowKey;
    self.titleLabel.accessibilityIdentifier    = EHIExtrasExtraTitleKey;
    self.subtitleLabel.accessibilityIdentifier = EHIExtrasExtraSubtitleKey;
    self.toggleButton.accessibilityIdentifier  = EHIExtrasExtraToggleKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIExtrasExtraViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidatePlaceholderVisibility:)];
    [MTRReactor autorun:self action:@selector(invalidateSelection:)];
    [MTRReactor autorun:self action:@selector(invalidateToggleSelection:)];
    [MTRReactor autorun:self action:@selector(invalidateTotalPrice:)];
    
    model.bind.map(@{
        source(model.title)        : dest(self, .titleLabel.text),
        source(model.rateText)     : dest(self, .subtitleLabel.text),
        source(model.details)      : dest(self, .detailsLabel.text),
        source(model.stepperTitle) : dest(self, .stepperTitleLabel.text),
        source(model.moreInfoText) : dest(self, .moreInfoButton.ehi_attributedTitle),
        source(model.amount)       : dest(self, .amount),
        source(model.maxQuantity)  : dest(self, .stepper.maxCount),
        source(model.isSelected)   : dest(self, .arrowUp),
        source(model.plusButtonEnabled)  : dest(self, .stepper.plusButtonEnabled),
        source(model.minusButtonEnabled) : dest(self, .stepper.minusButtonEnabled),
    });
}

- (void)invalidatePlaceholderVisibility:(MTRComputation *)computation
{
    NSString *placeholder = self.viewModel.defaultText;
    self.fallbackText.text = placeholder;
    self.fallbackContainer.hidden = !placeholder;
}

- (void)invalidateTotalPrice:(MTRComputation *)computation
{
    NSString *priceText = self.viewModel.totalText;
    BOOL priceIsVisible = priceText != nil;
   
    // update price text first when animating in
    if(priceIsVisible) {
        [self updateTotalPriceText:priceText];
    }
    
    UIView.animate(!computation.isFirstRun).duration(0.2).transform(^{
        self.priceLabel.alpha = priceIsVisible ? 1.0f : 0.0f;
    }).start(^(BOOL finished) {
        // clear text after animating out
        if(!priceIsVisible) {
            [self updateTotalPriceText:nil];
        }
    });
}

- (void)invalidateSelection:(MTRComputation *)computation
{
    BOOL isSelected = self.viewModel.isSelected;
//    self.titleLabel.numberOfLines = isSelected ? 0 : 1;

    UIView.animate(!computation.isFirstRun).duration(0.15).transform(^{
        self.detailsLabel.alpha   = isSelected ? 1.0f : 0.0f;
        self.moreInfoButton.alpha = isSelected ? 1.0f : 0.0f;
    }).start(nil);
}

- (void)invalidateToggleSelection:(MTRComputation *)computation
{
    BOOL isToggled = self.viewModel.shouldExpandToggle;
    [self setNeedsUpdateConstraints];
    
    UIView.animate(!computation.isFirstRun).duration(0.3).transform(^{
        [self.stepperView setAlpha:isToggled ? 1.0f : 0.0f];
        [self layoutIfNeeded];
    }).start(nil);
}

//
// Helpers
//

- (void)updateTotalPriceText:(NSString *)priceText
{
    [self.priceLabel setText:self.viewModel.totalText];
    [self.priceLabel layoutIfNeeded];
}

# pragma mark - Actions

- (IBAction)didTapToggleButton:(id)sender
{
    // notify the view model of the action
    [self.viewModel selectExtra:!self.toggleButton.isSelected completion:^(BOOL didToggle) {
        if(didToggle) {
            // invalidate the amount
            [self ehi_performAction:@selector(didChangeAmountForExtrasCell:) withSender:self];
            // cell should only resize if stepper should show (maxQuantity > 1)
            if(self.viewModel.maxQuantity > 1) {
                [self ehi_performAction:@selector(didInvalidateHeightForExtrasCell:) withSender:self];
            }
        }
    }];
}

- (IBAction)didTapArrowButton:(id)sender
{
    [self ehi_performAction:@selector(didSelectArrowButtonForExtrasCell:) withSender:self];
}

- (IBAction)didTapMoreInfo:(id)sender
{
    [self.viewModel showMoreInfo];
}

# pragma mark - Accessors

- (void)setArrowUp:(BOOL)arrowUp
{
    _arrowUp = arrowUp;
    
    double rads = (arrowUp) ? M_PI : 0;
    CGAffineTransform transform = CGAffineTransformRotate(CGAffineTransformIdentity, rads);
    self.arrowImage.transform = transform;
}

- (void)setAmount:(NSInteger)amount
{
    _amount = amount;
    
    self.stepper.count = amount;
    self.toggleButton.selected = self.amount ? YES : NO;
}

# pragma mark - Stepper Protocol

- (void)stepper:(EHIStepperControl *)stepper didUpdateValue:(NSInteger)integer
{
    [self.viewModel setAmount:integer];
    [self ehi_performAction:@selector(didChangeAmountForExtrasCell:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = [self intrinsicContentHeight]
    };
}

- (CGFloat)intrinsicContentHeight
{
    [self.detailsLabel sizeToFit];
    
    CGFloat detailsActualHeight = self.detailsLabel.frame.size.height;
    CGFloat detailsHeight = (detailsActualHeight == 0) ? detailsActualHeight : detailsActualHeight + EHIMediumPadding + 25.0f;
    CGFloat defaultHeight = self.defaultView.bounds.size.height;
    CGFloat stepperActualHeight = self.stepperView.bounds.size.height;
    CGFloat stepperHeight = (stepperActualHeight == 0) ? stepperActualHeight : stepperActualHeight + EHILightPadding;
    CGFloat totalHeight;
    
    if(self.viewModel.isSelected && self.viewModel.shouldExpandToggle) {
        totalHeight = defaultHeight + detailsHeight + stepperHeight - EHILightPadding;
    }
    
    else if(self.viewModel.shouldExpandToggle) {
        totalHeight = defaultHeight + stepperHeight;
    }
    
    else if(self.viewModel.isSelected) {
        totalHeight = defaultHeight + detailsHeight;
    }
    
    else {
        totalHeight = defaultHeight;
    }
    
    return totalHeight;
}

@end
