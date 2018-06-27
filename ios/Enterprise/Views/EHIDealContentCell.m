//
//  EHIDealContentCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealContentCell.h"
#import "EHIDealContentViewModel.h"
#import "EHITextView.h"

@interface EHIDealContentCell ()
@property (strong, nonatomic) EHIDealContentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHITextView *contentTextView;
@end

@implementation EHIDealContentCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealContentViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealContentViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.contentText) : dest(self, .contentTextView.attributedText),
    });
}

- (CGSize)intrinsicContentSize
{    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.contentTextView.frame)
    };
}

@end
