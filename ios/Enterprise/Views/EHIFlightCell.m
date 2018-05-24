//
//  EHIReviewAirlineCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFlightCell.h"
#import "EHIFlightViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHIButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFlightCell ()
@property (strong, nonatomic) EHIFlightViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIView *noFlightContainer;
@property (weak  , nonatomic) IBOutlet UIView *flightInfoContainer;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@property (weak  , nonatomic) IBOutlet EHIButton *addButton;
@property (weak  , nonatomic) IBOutlet UIView *bottomDivider;
@end

@implementation EHIFlightCell

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFlightViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    BOOL inConfirmation = metrics.tag == EHIReservationViewStyleConfirmation;
    self.arrowImage.hidden    = inConfirmation;
    self.bottomDivider.hidden = !inConfirmation;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFlightViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateAddButtonVisibility:)];
    
    model.bind.map(@{
        source(model.detailsTitle) : dest(self, .detailsLabel.attributedText),
        source(model.subtitle)     : dest(self, .subtitleLabel.text),
        source(model.addTitle)     : dest(self, .addButton.ehi_attributedTitle),
        source(model.title)        : dest(self, .titleLabel.text),
    });
}

- (void)invalidateAddButtonVisibility:(MTRComputation *)computation
{
    BOOL showAdd = self.viewModel.showsAddButton;
    
    self.flightInfoContainer.hidden = showAdd;
    self.noFlightContainer.hidden   = !showAdd;
}

# pragma mark - Actions

- (IBAction)didTapAddButton:(id)sender
{
    [self ehi_performAction:@selector(didSelectedFlightCell) withSender:self];
    [self.viewModel addFlightDetails];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *bottomView  = self.viewModel.showsAddButton ? self.noFlightContainer : self.flightInfoContainer;
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomView.frame) + EHILightPadding
    };
}

@end

NS_ASSUME_NONNULL_END