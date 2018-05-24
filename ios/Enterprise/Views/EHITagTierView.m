//
//  EHITagTierView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITagTierView.h"
#import "EHITierTagViewModel.h"
#import "EHITagLayer.h"

@interface EHITagTierView ()
@property (strong, nonatomic) EHITierTagViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *yourTierLabel;
@end

@implementation EHITagTierView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITierTagViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.tagLayer.fillColor = [UIColor ehi_grayColor4].CGColor;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITierTagViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.tier) : dest(self, .yourTierLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = CGRectGetMaxX(self.yourTierLabel.frame) + 12.0f,
        .height = CGRectGetHeight(self.yourTierLabel.frame) + 4.0f
    };
}

# pragma mark - Layer

- (EHITagLayer *)tagLayer
{
    return (EHITagLayer *)self.layer;
}

+ (Class)layerClass
{
    return [EHITagLayer class];
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
