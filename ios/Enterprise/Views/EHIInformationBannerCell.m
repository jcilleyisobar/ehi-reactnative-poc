//
//  EHIInformationBannerCell
//  Enterprise
//
//  Created by Alex Koller on 6/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIInformationBannerCell.h"
#import "EHIInformationBannerViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIInformationBannerCell ()
@property (strong, nonatomic) EHIInformationBannerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *messageLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *titleSpacing;
@end

@implementation EHIInformationBannerCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // set the content view's background color so that it animates properly
    self.contentView.backgroundColor = [UIColor ehi_tanColor];
}

- (void)updateConstraints
{
    [super updateConstraints];

    // remove the spacing when there's no title
    self.titleSpacing.isDisabled = !self.viewModel.title;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInformationBannerViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.message)   : dest(self, .messageLabel.text),
        source(model.imageName) : dest(self, .iconImageView.ehi_imageName),
        
        source(model.title)     : ^(NSString *title) {
            [self.titleLabel setText:title];
            [self setNeedsUpdateConstraints];
        },
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = self.containerView.frame.size.height + 2 * EHIMediumPadding
    };
}

@end
