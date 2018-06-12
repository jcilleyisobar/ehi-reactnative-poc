//
//  EHIClassSelectActiveFilterCell.m
//  Enterprise
//
//  Created by mplace on 4/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectActiveFilterCell.h"
#import "EHIClassSelectActiveFilterViewModel.h"

@interface EHIClassSelectActiveFilterCell ()
@property (weak  , nonatomic) IBOutlet EHIActiveFilterBanner *customView;
@end

@implementation EHIClassSelectActiveFilterCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectActiveFilterViewModel new];
    }
    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [self.customView updateWithModel:model];
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // for animating content view out
    self.clipsToBounds = NO;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectActiveFilterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
                     
    });
}

# pragma mark - Layout

-(NSArray *)customSubviews
{
    return @[self.customView];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.customView.frame)
    };
}

@end
