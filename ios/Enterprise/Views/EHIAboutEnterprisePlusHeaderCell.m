//
//  EHIAboutEnterprisePlusHeaderCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusHeaderCell.h"
#import "EHIAboutEnterprisePlusHeaderViewModel.h"

@interface EHIAboutEnterprisePlusHeaderCell ()
@property (strong, nonatomic) EHIAboutEnterprisePlusHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailLabel;

@end

@implementation EHIAboutEnterprisePlusHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutEnterprisePlusHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutEnterprisePlusHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)  : dest(self, .titleLabel.text),
        source(model.detail) : dest(self, .detailLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailLabel.frame) + EHIHeavyPadding
    };
}

@end
