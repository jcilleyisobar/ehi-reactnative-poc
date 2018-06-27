//
//  EHICurrencyDiffersCell.m
//  Enterprise
//
//  Created by Rafael Machado on 06/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICurrencyDiffersCell.h"
#import "EHICurrencyDiffersViewModel.h"

@interface EHICurrencyDiffersCell ()
@property (strong, nonatomic) EHICurrencyDiffersViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHICurrencyDiffersCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHICurrencyDiffersViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // for animating content view out
    self.clipsToBounds = NO;
    
    // set the content view's background color so that it animates properly
    self.contentView.backgroundColor = [UIColor ehi_tanColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHICurrencyDiffersViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.attributedText)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.titleLabel.frame) + EHIHeaviestPadding
    };
}

@end
