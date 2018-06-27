//
//  EHIRentalsFooterCell.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsFooterCell.h"
#import "EHIRentalsFooterViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIRentalsFooterCell()
@property (strong, nonatomic) EHIRentalsFooterViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet EHIButton *lookupButton;
@property (weak, nonatomic) IBOutlet EHIButton *startRentalButton;
@property (weak, nonatomic) IBOutlet EHIButton *cannotFindButton;
@property (weak, nonatomic) IBOutlet EHIButton *contactButton;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *cannotFindContainerHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *lookupContainerHeight;
@end

@implementation EHIRentalsFooterCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.lookupButton.type      = EHIButtonTypeSecondary;
    self.cannotFindButton.type  = EHIButtonTypeSecondary;
    self.startRentalButton.type = EHIButtonTypeSecondary;
    self.contactButton.type     = EHIButtonTypeSecondary;
    self.contactButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.lookupButton.accessibilityIdentifier = EHIMyRentalsLookUpKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsFooterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.startRentalButtonText)  : dest(self, .startRentalButton.ehi_title),
        source(model.contactButtonText)      : dest(self, .contactButton.ehi_title),
        source(model.contactButtonImageName) : dest(self, .contactButton.ehi_imageName),
        source(model.cannotFindButtonText)   : dest(self, .cannotFindButton.ehi_title),
        source(model.hidesFindButton)        : dest(self, .cannotFindContainerHeight.isDisabled),
        source(model.lookupButtonText)       : dest(self, .lookupButton.ehi_title),
        source(model.hidesLookupButton)      : dest(self, .lookupContainerHeight.isDisabled),
    });
}

# pragma mark - Actions

- (IBAction)didTapLookupButton:(id)sender
{
    [self.viewModel lookupRental];
}

- (IBAction)didTapStartRentalButton:(id)sender
{
    [self.viewModel startRental];
}

- (IBAction)didTapContactUsButton:(UIButton *)button
{
    [self.viewModel callHelpNumber];
}

- (IBAction)didTapCannotFindButton:(UIButton *)button
{
    [self.viewModel cannotFindRental];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame),
    };
}

@end
