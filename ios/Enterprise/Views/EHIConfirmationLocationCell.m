//
//  EHIConfirmationLocationCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationLocationCell.h"
#import "EHIConfirmationLocationViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHIConfirmationLocationCell ()
@property (strong, nonatomic) EHIConfirmationLocationViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet EHILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet EHIButton *phoneButton;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *iconWidthConstraint;
@end

@implementation EHIConfirmationLocationCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationLocationViewModel new];
    }
    return self;
}

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.addressLabel.copyable = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationLocationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateIconImage:)];
    
    model.bind.map(@{
        source(model.title)   : dest(self, .titleLabel.text),
        source(model.name)    : dest(self, .nameLabel.text),
        source(model.address) : dest(self, .addressLabel.text),
        source(model.phone)   : dest(self, .phoneButton.ehi_title)
    });
}

- (void)invalidateIconImage:(MTRComputation *)computation
{
    NSString *imageName = self.viewModel.iconImage;
    
    self.iconWidthConstraint.isDisabled = imageName == nil;
    self.iconImageView.ehi_imageName = imageName;
    self.iconImageView.tintColor = [UIColor ehi_greenColor];
}

# pragma mark - Actions

- (IBAction)didTapPhoneNumberButton:(id)sender
{
    [self.viewModel callLocation];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.contentContainer.bounds) + 2 * EHIMediumPadding
    };
}

@end
