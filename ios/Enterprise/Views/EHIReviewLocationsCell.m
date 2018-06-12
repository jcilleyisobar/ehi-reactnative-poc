//
//  EHIReviewLocationsCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewLocationsCell.h"
#import "EHIReviewLocationsViewModel.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"

@interface EHIReviewLocationsCell ()
@property (strong, nonatomic) EHIReviewLocationsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView *pickupContainer;
@property (weak  , nonatomic) IBOutlet UIImageView *pickupIconImageView;
@property (weak  , nonatomic) IBOutlet EHILabel *pickupSectionTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *pickupTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *pickupIconContainerWidthConstraint;

@property (weak  , nonatomic) IBOutlet UIView *returnContainer;
@property (weak  , nonatomic) IBOutlet UIImageView *returnIconImageView;
@property (weak  , nonatomic) IBOutlet EHILabel *returnSectionTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *returnTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *returnIconContainerWidthConstraint;

@property (strong, nonatomic) IBOutletCollection(EHIRestorableConstraint) NSArray *lockIconWidthConstraint;
@property (strong, nonatomic) IBOutletCollection(EHIRestorableConstraint) NSArray *arrowIconWidthConstraint;

@end

@implementation EHIReviewLocationsCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewLocationsViewModel new];
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    UITapGestureRecognizer *tapPickup = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapPickupContainer:)];
    [self.pickupContainer addGestureRecognizer:tapPickup];
    UITapGestureRecognizer *tapReturn = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapReturnContainer:)];
    [self.returnContainer addGestureRecognizer:tapReturn];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewLocationsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updatePickupImage:)];
    [MTRReactor autorun:self action:@selector(updateReturnImage:)];
    [MTRReactor autorun:self action:@selector(invalidateLockIcon:)];
    
    model.bind.map(@{
        source(model.pickupSectionTitle)    : dest(self, .pickupSectionTitleLabel.text),
        source(model.returnSectionTitle)    : dest(self, .returnSectionTitleLabel.text),
        source(model.pickupTitle)           : dest(self, .pickupTitleLabel.text),
        source(model.returnTitle)           : dest(self, .returnTitleLabel.text)
    });
}

- (void)updatePickupImage:(MTRComputation *)computation
{
    self.pickupIconImageView.ehi_imageName = self.viewModel.pickupIconImageName;
    self.pickupIconContainerWidthConstraint.isDisabled = (self.viewModel.pickupIconImageName == nil);
}

- (void)updateReturnImage:(MTRComputation *)computation
{
    self.returnIconImageView.ehi_imageName = self.viewModel.returnIconImageName;
    self.returnIconContainerWidthConstraint.isDisabled = (self.viewModel.returnIconImageName == nil);
}

- (void)invalidateLockIcon:(MTRComputation *)computation
{
    BOOL shouldHideLockIcon = self.viewModel.shouldHideLockIcon;
    
    self.arrowIconWidthConstraint.each(^(EHIRestorableConstraint *constraint) {
        constraint.isDisabled = !shouldHideLockIcon;
    });
    
    self.lockIconWidthConstraint.each(^(EHIRestorableConstraint *constraint) {
        constraint.isDisabled = shouldHideLockIcon;
    });
}

# pragma mark - Actions

- (void)didTapPickupContainer:(UITapGestureRecognizer *)tapGesture
{
    [self ehi_performAction:@selector(didSelectedLocationsCell) withSender:self];
    [self.viewModel selectPickupLocation];
}

- (void)didTapReturnContainer:(UITapGestureRecognizer *)tapGesture
{
    [self ehi_performAction:@selector(didSelectedLocationsCell) withSender:self];
    [self.viewModel selectReturnLocation];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *pickupView = self.pickupContainer;
    UIView *sizingView = self.viewModel.showsReturn ? self.returnContainer : pickupView;
    
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(sizingView.frame)
    };
}

@end
