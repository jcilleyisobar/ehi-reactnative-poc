//
//  EHIReservationCarClassCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationCarClassCell.h"
#import "EHIReservationCarClassViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHILabel.h"
#import "EHINetworkImageView.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationCarClassCell ()
@property (strong, nonatomic) EHIReservationCarClassViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *transmissionLabel;
@property (weak  , nonatomic) IBOutlet EHINetworkImageView *carImageView;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *transmissionIconWidth;
@end

@implementation EHIReservationCarClassCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationCarClassViewModel new];
    }
    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.arrowImage.hidden = metrics.tag == EHIReservationViewStyleConfirmation;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.titleLabel.accessibilityIdentifier    = EHIReviewCarClassTypeKey;
    self.subtitleLabel.accessibilityIdentifier = EHIReviewCarClassNameKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationCarClassViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)     : dest(self, .titleLabel.text),
        source(model.subtitle)  : dest(self, .subtitleLabel.text),
        source(model.carImage)  : dest(self, .carImageView.imageModel),
        source(model.transmissionType)        : dest(self, .transmissionLabel.text),
        source(model.isAutomaticTransmission) : dest(self, .transmissionIconWidth.isDisabled),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect bottomFrame = [self.contentContainer convertRect:self.contentContainer.bounds toView:self.contentView];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame)
    };
}

@end
