//
//  EHIDealHeaderCell.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealHeaderCell.h"
#import "EHIDealHeaderViewModel.h"

@interface EHIDealHeaderCell ()
@property (strong, nonatomic) EHIDealHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIDealHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.titleLabel.frame) + EHIMediumPadding
    };
}

@end
