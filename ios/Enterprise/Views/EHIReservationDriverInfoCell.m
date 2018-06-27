//
//  EHIReservationDriverInfoCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationDriverInfoCell.h"
#import "EHIReservationDriverInfoCellViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHILabel.h"
#import "EHIButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReservationDriverInfoCell ()
@property (strong, nonatomic) EHIReservationDriverInfoCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *addButton;
@property (weak  , nonatomic) IBOutlet UIView *driverInfoContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *nameLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *emailLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *phoneLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@end

@implementation EHIReservationDriverInfoCell

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationDriverInfoCellViewModel new];
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
    
    self.nameLabel.accessibilityIdentifier  = EHIReviewDriverInfoNameKey;
    self.emailLabel.accessibilityIdentifier = EHIReviewDriverInfoEmailKey;
    self.phoneLabel.accessibilityIdentifier = EHIReviewDriverInfoPhoneKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationDriverInfoCellViewModel *)model
{
    model.bind.map(@{
        source(model.actionButtonTitle)     : dest(self, .addButton.ehi_title),
        source(model.title)                 : dest(self, .titleLabel.text),
        source(model.name)                  : dest(self, .nameLabel.text),
        source(model.email)                 : dest(self, .emailLabel.text),
        source(model.phone)                 : dest(self, .phoneLabel.text),
        source(model.shouldShowDriverInfo)  : ^{
            self.addButton.hidden = model.shouldShowDriverInfo;
            self.driverInfoContainer.hidden = !model.shouldShowDriverInfo;
        }
    });
}

# pragma mark - Actions

- (IBAction)addButtonPressed:(id)sender
{
    [self.viewModel addDriverInfo];
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *sizingView = self.viewModel.shouldShowDriverInfo ? self.driverInfoContainer : self.addButton;
    CGFloat titleHeight = CGRectGetHeight(self.titleLabel.frame);
    CGFloat height = titleHeight + CGRectGetMaxY(sizingView.frame);
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = height
    };
}

@end

NS_ASSUME_NONNULL_END