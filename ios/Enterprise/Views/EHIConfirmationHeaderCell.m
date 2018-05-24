//
//  EHIConfirmationHeaderCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationHeaderCell.h"
#import "EHIConfirmationHeaderViewModel.h"
#import "EHILabel.h"
#import "EHINetworkImageView.h"
#import "EHIButton.h"

@interface EHIConfirmationHeaderCell ()
@property (strong, nonatomic) EHIConfirmationHeaderViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *container;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *confirmationTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *confirmationNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel *emailTitleLabel;
@property (weak, nonatomic) IBOutlet EHINetworkImageView *vehicleImageView;
@end

@implementation EHIConfirmationHeaderCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationHeaderViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.confirmationNumberLabel.copyable = YES;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.confirmationNumberLabel.accessibilityIdentifier = EHIConfirmationNumberKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerTitle)        : dest(self, .titleLabel.attributedText),
        source(model.emailTitle)         : dest(self, .emailTitleLabel.attributedText),
        source(model.confirmationTitle)  : dest(self, .confirmationTitleLabel.text),
        source(model.confirmationNumber) : dest(self, .confirmationNumberLabel.text),
        source(model.vehicleImage)       : dest(self, .vehicleImageView.imageModel)
    });
}

- (CGSize)intrinsicContentSize
{
    CGRect frame = [self.container convertRect:self.container.frame toView:self.contentView];
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame)
    };
}

@end
