//
//  EHIProfileBasicCell.m
//  Enterprise
//
//  Created by fhu on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileBasicCell.h"
#import "EHIProfileBasicViewModel.h"
#import "EHILabel.h"

@interface EHIProfileBasicCell()
@property (strong, nonatomic) EHIProfileBasicViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *header;
@property (weak  , nonatomic) IBOutlet EHILabel *subtitle;
@end

@implementation EHIProfileBasicCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfileBasicViewModel new];
    }
    
    return self;
}

#pragma mark - Reactions

- (void)registerReactions:(EHIProfileBasicViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .header.text),
        source(model.attributedText)    : dest(self, .subtitle.attributedText),
    });
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.divider.frame)
    };
}

@end
