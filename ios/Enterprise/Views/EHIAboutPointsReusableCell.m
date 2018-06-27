//
//  EHIAboutPointsReusableCell.m
//  Enterprise
//
//  Created by frhoads on 1/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsReusableCell.h"
#import "EHIButton.h"
#import "EHIAboutPointsReusableViewModel.h"

@interface EHIAboutPointsReusableCell()
@property (strong, nonatomic) EHIAboutPointsReusableViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UIImageView *imageView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *button;
@end

@implementation EHIAboutPointsReusableCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutPointsReusableViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutPointsReusableViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.imageName)    : dest(self, .imageView.ehi_imageName),
        source(model.titleText)    : dest(self, .titleLabel.text),
        source(model.subtitleText) : dest(self, .subtitleLabel.text),
        source(model.buttonText)   : dest(self, .button.ehi_title)
    });
}

- (IBAction)didTapButton:(id)sender
{
    [self.viewModel promptPhoneCall];
}

- (CGSize)intrinsicContentSize
{
    // collapsing cell if there is no phone number available per AC
    BOOL hasTitle = self.viewModel.buttonText.length > 0;
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = hasTitle ? CGRectGetMaxY(self.button.frame) + EHIMediumPadding : 0
    };
}

@end
