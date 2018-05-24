//
//  EHIRedemptionPointsView.m
//  Enterprise
//
//  Created by fhu on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionPointsCell.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIButton.h"
#import "EHISecondaryActionButton.h"
#import "EHIArrowLayer.h"
#import "EHIActivityIndicator.h"
#import "EHIRestorableConstraint.h"

#define EHIRedemptionToggleDuration (0.3)

@interface EHIRedemptionPointsCell()
@property (strong, nonatomic) EHIRedemptionPointsViewModel *viewModel;
@property (strong, nonatomic) EHIArrowLayer *arrowLayer;

@property (weak, nonatomic) IBOutlet UILabel *pointsHeaderLabel;
@property (weak, nonatomic) IBOutlet UILabel *pointsLabel;
@property (weak, nonatomic) IBOutlet UILabel *redeemingLabel;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UIView *backgroundImage;
@property (weak, nonatomic) IBOutlet UIView *redeemingPlacard;
@property (weak, nonatomic) IBOutlet UIView *reviewExpandedContainer;
@property (weak, nonatomic) IBOutlet UIView *reviewRedeemedContainer;
@property (weak, nonatomic) IBOutlet UIView *shadowAnchor;
@property (weak, nonatomic) IBOutlet UIView *daysRedeemedLineItemContainer;
@property (weak, nonatomic) IBOutlet EHIButton *rightButton;
@property (weak, nonatomic) IBOutlet EHISecondaryActionButton *redeemPointsButton;
@property (weak, nonatomic) IBOutlet EHIButton *savePointsButton;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *dividerWidthConstraint;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *reviewExpandedHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *reviewRedeemedHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *redeemingPlacardHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *topSpacingConstraint;
@end

@implementation EHIRedemptionPointsCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.backgroundImage.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"eplus_tilepattern"]];
    self.redeemPointsButton.isOnDarkBackground = YES;
    self.redeemPointsButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    
    self.shadowAnchor.layer.ehi_showsShadow = YES;
    self.shadowAnchor.layer.shadowRadius    = 2.0;
    self.shadowAnchor.layer.shadowOpacity   = 0.8;
    
    self.rightButton.titleLabel.numberOfLines = 0;
    
    [self.activityIndicator setType:EHIActivityIndicatorTypeGreen size:self.activityIndicator.bounds.size];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapDaysRedeemedLineItem:)];
    [self.daysRedeemedLineItemContainer addGestureRecognizer:tap];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self invalidateArrowFrame];
}

- (void)applyLayoutAttributes:(UICollectionViewLayoutAttributes *)layoutAttributes
{
    [super applyLayoutAttributes:layoutAttributes];
    
    BOOL isFirst = layoutAttributes.indexPath.section == 0;
    
    self.topSpacingConstraint.isDisabled = !isFirst;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionPointsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateButton:)];
    [MTRReactor autorun:self action:@selector(invalidateReviewExpandedContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateReviewRedeemedContainer:)];
    
    model.bind.map(@{
        source(model.redeemingString)    : dest(self, .redeemingLabel.text),
        source(model.pointsHeaderString) : dest(self, .pointsHeaderLabel.text),
        source(model.pointsString)       : dest(self, .pointsLabel.text),
        source(model.buttonAttributedString) : dest(self, .rightButton.ehi_attributedTitle),
        source(model.savePointsString)   : dest(self, .savePointsButton.ehi_title),
        source(model.redeemPointsString) : dest(self, .redeemPointsButton.ehi_title)
    });
}

- (void)invalidateButton:(MTRComputation *)computation
{
    NSAttributedString *buttonAttributedString = self.viewModel.buttonAttributedString;
    
    self.rightButton.ehi_attributedTitle = buttonAttributedString;
    self.rightButton.hidden = !buttonAttributedString.length;
    
    self.dividerWidthConstraint.isDisabled = !buttonAttributedString.length;
    
    [self.rightButton setEnabled:self.viewModel.canTapButton];
}

- (void)invalidateReviewExpandedContainer:(MTRComputation *)computation
{
    self.reviewExpandedHeight.isDisabled = !self.viewModel.showExpandedReviewFooter;
    
    UIView.animate(!computation.isFirstRun).duration(EHIRedemptionToggleDuration).transform(^{
        [self layoutIfNeeded];
    }).start(nil);
}

- (void)invalidateReviewRedeemedContainer:(MTRComputation *)computation
{
    BOOL showRedeemedReviewFooter = self.viewModel.showRedeemedReviewFooter;
    
    self.reviewRedeemedHeight.isDisabled   = !showRedeemedReviewFooter;
    self.redeemingPlacardHeight.isDisabled = !showRedeemedReviewFooter;
    
    UIView.animate(!computation.isFirstRun).duration(EHIRedemptionToggleDuration).transform(^{
        self.reviewRedeemedContainer.alpha = showRedeemedReviewFooter ? 1.0f : 0.0f;
        self.redeemingPlacard.alpha        = showRedeemedReviewFooter ? 1.0f : 0.0f;
        
        [self layoutIfNeeded];
    }).start(nil);
}

# pragma mark - Arrow

- (void)invalidateArrowFrame
{
    CGRect frame = self.arrowLayer.frame;
    frame.origin = (CGPoint){
        .x = CGRectGetMidX(self.redeemingPlacard.frame) - (frame.size.width / 2.0),
        .y = self.redeemingPlacard.bounds.size.height
    };
    
    self.arrowLayer.frame = frame;
}

- (EHIArrowLayer *)arrowLayer
{
    if(_arrowLayer) {
        return _arrowLayer;
    }
    
    // create the arrow layer with the default size
    EHIArrowLayer *arrowLayer = [EHIArrowLayer new];
    arrowLayer.fillColor = [UIColor whiteColor].CGColor;
    arrowLayer.direction = EHIArrowDirectionDown;
    
    arrowLayer.frame = (CGRect){
        .size = (CGSize){ .width = 20.0f, .height = 10.0f },
    };
    
    // insert and store it
    [self.redeemingPlacard.layer insertSublayer:arrowLayer atIndex:0];
    _arrowLayer = arrowLayer;
    
    return _arrowLayer;
}

# pragma mark - Loading

- (void)setIsLoading:(BOOL)isLoading
{
    _isLoading = isLoading;
    
    // swap button and indicator
    self.activityIndicator.isAnimating = isLoading;
    
    UIView.animate(YES).duration(0.2).option(UIViewAnimationOptionBeginFromCurrentState).transform(^{
        self.rightButton.alpha = isLoading ? 0.0 : 1.0;
    }).start(nil);
}

# pragma mark - Actions

- (IBAction)didTapToggleButton:(id)sender
{
    if (self.viewModel.reviewStatus == EHIRedemptionReviewStatusRedeemed) {
        [self ehi_performAction:@selector(didRemovePointsForRedemptionPointsCell:) withSender:self];
    }
    
    [self.viewModel toggleButtonTapped];
    
    [self ehi_performAction:@selector(didTapActionButtonForRedemptionPointsCell:) withSender:self];
    [self ehi_performAction:@selector(didSelectedRedemptionPointsCell) withSender:self];
}

- (IBAction)didTapRedeemPointsButton:(id)sender
{
    [self.viewModel redeemButtonTapped];
    
    [self ehi_performAction:@selector(didSelectedRedemptionPointsCell) withSender:self];
}

- (IBAction)didTapSavePointsButton:(id)sender
{
    [self.viewModel toggleButtonTapped];
    
    [self ehi_performAction:@selector(didTapActionButtonForRedemptionPointsCell:) withSender:self];
    [self ehi_performAction:@selector(didSelectedRedemptionPointsCell) withSender:self];
}

- (void)didTapDaysRedeemedLineItem:(id)sender
{
    [self.viewModel redeemButtonTapped];
    
    [self ehi_performAction:@selector(didSelectedRedemptionPointsCell) withSender:self];

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
