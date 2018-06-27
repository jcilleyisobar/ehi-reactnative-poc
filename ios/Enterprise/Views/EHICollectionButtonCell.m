//
//  EHICollectionButtonCell.m
//  Enterprise
//
//  Created by mplace on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionButtonCell.h"
#import "EHIActionButton.h"

@interface EHICollectionButtonCell ()
@property (strong, nonatomic) EHICollectionButtonAction *action;
@property (weak  , nonatomic) IBOutlet EHIActionButton *button;
@end

@implementation EHICollectionButtonCell

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    if(!self.window) {
        self.action = nil;
    }
}

- (void)updateWithModel:(EHICollectionButtonAction *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.action = model;
    
    if (model.iconName) {
        self.button.ehi_image = [UIImage imageNamed:model.iconName];
        self.button.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    }
    
    self.button.titleLabel.adjustsFontSizeToFitWidth = YES;
    self.button.titleLabel.numberOfLines = 0;
    
    // set the content alignment
    [self.button setContentHorizontalAlignment:model.alignment];
    
    self.button.titleEdgeInsets = (UIEdgeInsets) {
        .left = model.iconName ? EHIHeaviestPadding : EHILightPadding,
        .right = model.iconName ? EHIHeaviestPadding : EHILightPadding,
        .top = EHILightPadding,
        .bottom = EHILightPadding
    };
    
    // use the attributed title if we have one, otherwise just set the plain text
    if(model.attributedTitle.length > 0) {
        self.button.ehi_attributedTitle = model.attributedTitle;
    } else {
        self.button.ehi_title = model.title;
    }
}

- (IBAction)didTapButton:(UIButton *)button
{
    ehi_call(self.action.block)(button);
}

- (CGSize)intrinsicContentSize
{
    return (CGSize){ .width = EHILayoutValueNil, .height = self.button.intrinsicContentSize.height };
}

@end
