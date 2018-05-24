//
//  EHILocationsMapListCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/29/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsMapListCell.h"
#import "EHILocationConflictCollapsableView.h"
#import "EHILocationsMapListActions.h"
#import "EHILocationsMapListViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHILocationsMapListCell ()
@property (strong, nonatomic) EHILocationsMapListViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *bottomDivider;
@property (weak  , nonatomic) IBOutlet UIView *overlayView;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *afterHoursLabel;
@property (weak  , nonatomic) IBOutlet UILabel *flexibleTravelLabel;
@property (weak  , nonatomic) IBOutlet UILabel *conflictsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *operationHoursLabel;
@property (weak  , nonatomic) IBOutlet UILabel *openHoursLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImageView;
@property (weak  , nonatomic) IBOutlet EHILocationConflictCollapsableView *conflictsView;
@property (weak  , nonatomic) IBOutlet UIView *openHoursContainerView;
@property (weak  , nonatomic) IBOutlet UIView *locationDetailsView;
@property (weak  , nonatomic) IBOutlet UIButton *selectButton;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bottomDividerHeight;

@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *buttonTrailing;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *buttonWidth;

@property (strong, nonatomic) UIView *background;

@end

@implementation EHILocationsMapListCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHILocationsMapListViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(EHILocationsMapListViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    if(model.hasConflicts) {
        CGFloat padding = (self.buttonWidth.constant / 2.0f) + self.buttonTrailing.constant;

        self.conflictsView.arrowHeight = 10;
        self.conflictsView.padding     = padding;
    }
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.background = [UIView new];
    self.background.backgroundColor = self.overlayView.backgroundColor;
    self.background.alpha = self.overlayView.alpha;
    
    [self insertSubview:self.background belowSubview:self.contentView];
    
    [self.background mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.showOverlay = NO;
}

- (void)setShowOverlay:(BOOL)showOverlay
{
    if(_showOverlay == showOverlay) {
        return;
    }
    
    _showOverlay = showOverlay;
    
    [UIView animateWithDuration:0.05f animations:^{
        self.contentView.alpha = showOverlay ? 0.0f : 1.0f;
        self.background.alpha  = showOverlay ? 0.7f : 0.0f;
    }];
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationsMapListViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectButton:)];
    [MTRReactor autorun:self action:@selector(invalidateViewState:)];
    [MTRReactor autorun:self action:@selector(forceAfterHoursRender:)];
    
    model.bind.map(@{
        source(model.title)               : dest(self, .titleLabel.attributedText),
        source(model.subtitle)            : dest(self, .subtitleLabel.text),
        source(model.afterHoursTitle)     : dest(self, .afterHoursLabel.attributedText),
        source(model.flexibleTravelTitle) : dest(self, .flexibleTravelLabel.text),
        source(model.conflictTitle)       : dest(self, .conflictsLabel.attributedText),
        source(model.openHoursTitle)      : dest(self, .operationHoursLabel.text),
        source(model.openHours)           : dest(self, .openHoursLabel.text)
    });
}

- (void)invalidateSelectButton:(MTRComputation *)computation
{
    BOOL isInvalid = self.viewModel.style == EHILocationsMapListStyleInvalid;
    NSString *imageName      = isInvalid ? @"arrow_green" : @"arrow_white";
    UIColor *backgroundColor = isInvalid ? [UIColor whiteColor] : [UIColor ehi_greenColor];
    UIColor *borderColor     = isInvalid ? [UIColor ehi_greenColor] : [UIColor clearColor];
    CGFloat borderWidth      = isInvalid ? 2.0f : 0.0f;
    
   [self.selectButton setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    self.selectButton.backgroundColor   = backgroundColor;
    self.selectButton.layer.borderColor = borderColor.CGColor;
    self.selectButton.layer.borderWidth = borderWidth;
}

- (void)invalidateViewState:(MTRComputation *)computation
{
    BOOL isExpanded = self.viewModel.isExpanded;
    
    double rads = isExpanded ? M_PI : 0;
    CGAffineTransform transform = CGAffineTransformRotate(CGAffineTransformIdentity, rads);
    [UIView animateWithDuration:0.15f animations:^{
        self.arrowImageView.transform  = transform;
        self.openHoursLabel.alpha      = isExpanded ? 1.0f : 0.0f;
        self.operationHoursLabel.alpha = isExpanded ? 1.0f : 0.0f;
    }];
}

- (void)forceAfterHoursRender:(MTRComputation *)computation
{
    BOOL hasAfterHours = self.viewModel.afterHoursTitle != nil;
    
    if(hasAfterHours) {
        [self.afterHoursLabel setNeedsLayout];
        [self.afterHoursLabel layoutIfNeeded];
    }
}

# pragma mark - Actions

- (IBAction)didTapSelect:(id)sender
{    
    [self ehi_performAction:@selector(locationsMapDidTapSelect:) withSender:self];
}

- (IBAction)showDetails:(id)sender
{
	[self ehi_performAction:@selector(locationsMapDidTapLocationTitle:) withSender:self];
}

- (IBAction)didTapChangeState:(id)sender
{
    [self.viewModel changeState];
    
	[self ehi_performAction:@selector(locationsMapDidTapChangeState:) withSender:self];
}

# pragma mark - Autolayout

- (void)updateConstraints
{
    BOOL hasConflicts = self.viewModel.hasConflicts;
    MASLayoutPriority priority = self.viewModel.isExpanded ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.openHoursContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).priority(priority);
    }];
    
    priority = hasConflicts ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.conflictsView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).priority(priority);
    }];
    
    priority = self.viewModel.shouldShowDetails ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.locationDetailsView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
    
    [super updateConstraints];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.bottomDivider.frame)
    };
}


@end
