//
//  EHIReservationClassSelectCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassCell.h"
#import "EHICarClassViewModel.h"
#import "EHIReservationCarClassCell.h"
#import "EHINetworkImageView.h"
#import "EHIReservationPriceButton.h"
#import "EHIRestorableConstraint.h"

@interface EHICarClassCell ()
@property (strong, nonatomic) EHICarClassViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView *separator;
@property (weak  , nonatomic) IBOutlet UILabel *classNameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *makeModelLabel;
@property (weak  , nonatomic) IBOutlet UIView *rateView;
@property (weak  , nonatomic) IBOutlet UILabel *rateTitleLabel;
@property (weak  , nonatomic) IBOutlet UIButton *extendedDetailsButton;
@property (weak  , nonatomic) IBOutlet EHIButton *detailsButton;
@property (weak  , nonatomic) IBOutlet EHINetworkImageView *vehicleImageView;
@property (weak  , nonatomic) IBOutlet UILabel *extrasRateLabel;
@property (weak  , nonatomic) IBOutlet UIView *extrasRateContainer;
@property (weak  , nonatomic) IBOutlet EHIReservationPriceButton *priceButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *contentTopSpacing;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *entireContentTopSpacing;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *rateViewHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *classDetailsContainerHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bottomDividerHeightConstraint;
// transmission for car class select
@property (weak  , nonatomic) IBOutlet UILabel *selectTransmissionLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *selectTransmissionImageWidth;
// transmission for details
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *detailsTransmissionContainerHeight;
@property (weak  , nonatomic) IBOutlet UILabel *detailsTransmissionLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *detailsTransmissionImageWidth;
// redemption
@property (weak  , nonatomic) IBOutlet UIView *redemptionContainer;
@property (weak  , nonatomic) IBOutlet UILabel *freeDaysLabel;
@property (weak  , nonatomic) IBOutlet UILabel *pointsPerDayLabel;
@property (weak  , nonatomic) IBOutlet UIView *redemptionBottomBorder;
// modify
@property (weak  , nonatomic) IBOutlet UIView *previouslySelectedHeader;
@property (weak  , nonatomic) IBOutlet UIView *previouslySelectedLabelContainer;
@property (weak  , nonatomic) IBOutlet UILabel *previouslySelectedLabel;
@end

@implementation EHICarClassCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // style buttons
    [self.priceButton setBackgroundColor:[UIColor ehi_darkGreenColor]];
    [self.priceButton setBackgroundColor:[UIColor ehi_darkGreenColor] forState:UIControlStateNormal];
    
    self.previouslySelectedLabelContainer.layer.cornerRadius = 5;
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    // reset custom transforms
    self.animationContainer.layer.transform = CATransform3DIdentity;
    self.bottomContainer.layer.transform    = CATransform3DIdentity;
}

- (void)prepareToBindViewModel:(EHICarClassViewModel *)viewModel
{
    [super prepareToBindViewModel:viewModel];
    
    [self.vehicleImageView prepareForReuse];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.priceButton.accessibilityIdentifier    = EHIClassSelectCarPriceKey;
    self.classNameLabel.accessibilityIdentifier = EHIClassSelectCarTitleKey;
    self.makeModelLabel.accessibilityIdentifier = EHIClassSelectCarSubtitleKey;
    self.priceButton.titleLabel.accessibilityIdentifier = EHIClassSelectCarTotalPriceKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHICarClassViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    [MTRReactor autorun:self action:@selector(invalidateRateTitleView:)];
    [MTRReactor autorun:self action:@selector(invalidateRedemptionVisibility:)];
    [MTRReactor autorun:self action:@selector(invalidateModifyHeader:)];
    
    model.bind.map(@{
        source(model.carClassName)            : dest(self, .classNameLabel.text),
        source(model.makeModelTitle)          : dest(self, .makeModelLabel.text),
        source(model.vehicleImage)            : dest(self, .vehicleImageView.imageModel),
        source(model.price)                   : dest(self, .priceButton.price),
        source(model.detailsButtonTitle)      : dest(self, .detailsButton.ehi_title),
        source(model.transmissionTypeName)    : @[
            dest(self, .selectTransmissionLabel.text),
            dest(self, .detailsTransmissionLabel.text),
        ],
        source(model.isAutomaticTransmission) : @[
            dest(self, .selectTransmissionImageWidth.isDisabled),
            dest(self, .detailsTransmissionImageWidth.isDisabled),
        ],
        source(model.freeDaysTitle)           : dest(self, .freeDaysLabel.attributedText),
        source(model.pointsPerDayTitle)       : dest(self, .pointsPerDayLabel.attributedText),
        source(model.previousSelectionTitle)  : dest(self, .previouslySelectedLabel.text)
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    EHICarClassLayout layout   = self.viewModel.layout;
    BOOL isSelect              = layout == EHICarClassLayoutClassSelect;
    BOOL isDetails             = layout == EHICarClassLayoutClassDetails;
    BOOL isExtrasPlaceholder   = layout == EHICarClassLayoutExtrasPlaceholder;
    BOOL isExtras              = layout == EHICarClassLayoutExtras;
    BOOL isRate                = layout == EHICarClassLayoutRate;
    BOOL isCallForAvailability = self.viewModel.requiresCallForAvailability;
    BOOL isWebBook             = self.viewModel.requiresWebBook;
    EHIReservationPriceButtonType priceButtonType = self.viewModel.priceType;
    
    self.bottomDividerHeightConstraint.isDisabled      = isRate;
    self.contentTopSpacing.isDisabled                  = isSelect  || isExtrasPlaceholder || isExtras || isRate;
    self.detailsTransmissionContainerHeight.isDisabled = isSelect  || isExtrasPlaceholder || isExtras || isRate;
    self.classDetailsContainerHeight.isDisabled        = isDetails || isExtrasPlaceholder || isExtras || isRate;
    
    self.rateView.hidden          = isExtras || isRate;
    self.priceButton.hidden       = isExtras || isRate;
    self.priceButton.hidesArrow   = isDetails || isCallForAvailability || isWebBook || isRate;
    self.priceButton.priceType    = priceButtonType;
    self.priceButton.subtitleType = EHIReservationPriceButtonSubtitleTypeTotalCostOptionalNote;
    self.extrasRateLabel.hidden   = !isExtras;
    
    self.priceButton.userInteractionEnabled           = isDetails || isExtras;
    self.detailsButton.userInteractionEnabled         = !isCallForAvailability && !isWebBook;
    self.extendedDetailsButton.userInteractionEnabled = !isCallForAvailability && !isWebBook;

    self.separator.backgroundColor = isSelect ? [UIColor ehi_grayColor3] : [UIColor ehi_grayColor2];
    self.separator.hidden          = isExtrasPlaceholder || isExtras || isRate;

    self.redemptionBottomBorder.hidden = isSelect || isDetails;
}

- (void)invalidateRedemptionVisibility:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.hidesRedemption ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.redemptionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    UIView.animate(!computation.isFirstRun).duration(0.3).transform(^{
        [self layoutIfNeeded];
    }).start(nil);
}

- (void)invalidateModifyHeader:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.showPreviouslySelectedHeader ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.previouslySelectedHeader mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateRateTitleView:(MTRComputation *)computation
{
    // determine if the rate view should be hidden
    BOOL hidesRateView = !self.viewModel.rateTitle;
    
    // hide the view if necessary
    self.rateViewHeightConstraint.isDisabled = hidesRateView;
    
    // update the title if the view is going to be shown
    self.rateTitleLabel.text = !hidesRateView ? self.viewModel.rateTitle : nil;
    self.extrasRateLabel.text = !hidesRateView ? self.viewModel.rateTitle : nil;
    
    MASLayoutPriority priority = hidesRateView ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.extrasRateContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapPriceButton:(UIButton *)button
{
    [self ehi_performAction:@selector(didTapPriceButtonForCarClassCell:) withSender:self];
}

- (IBAction)didTapClassDetailsButton:(UIButton *)button
{
    [self ehi_performAction:@selector(didTapDetailsButtonForCarClassCell:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.contentContainer.frame)
    };
}

@end
