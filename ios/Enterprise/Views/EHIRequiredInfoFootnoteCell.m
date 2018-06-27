//
//  EHIRequiredInfoFootnoteCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoFootnoteCell.h"
#import "EHIRequiredInfoFootnoteView.h"
#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIRequiredInfoFootnoteCell ()
@property (strong, nonatomic) EHIRequiredInfoFootnoteViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *footnoteLabel;
@end

@implementation EHIRequiredInfoFootnoteCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRequiredInfoFootnoteViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRequiredInfoFootnoteViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.note) : dest(self, .footnoteLabel.attributedText)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.footnoteLabel.frame) + EHILightPadding
    };
}

@end
