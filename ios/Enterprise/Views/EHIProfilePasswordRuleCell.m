//
//  EHIProfilePasswordRuleCell.m
//  Enterprise
//
//  Created by cgross on 12/22/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePasswordRuleCell.h"
#import "EHIProfilePasswordRuleViewModel.h"
#import "EHILabel.h"
#import "UIImageView+Utility.h"

@interface EHIProfilePasswordRuleCell ()
@property (strong, nonatomic) EHIProfilePasswordRuleViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconView;
@property (weak  , nonatomic) IBOutlet EHILabel *title;
@end

@implementation EHIProfilePasswordRuleCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePasswordRuleViewModel new];
    }
    
    return self;
}

#pragma mark - Reactions

- (void)registerReactions:(EHIProfilePasswordRuleViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateIcon:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .title.text)
    });
}

- (void)invalidateIcon:(MTRComputation *)computation
{
    NSString *imageName = self.viewModel.iconName;
    if(imageName) {
        UIImage *image      = [UIImage imageNamed:imageName];
        self.iconView.image = image;
    }
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = self.title.bounds.size.height + 5,
    };
}

@end
