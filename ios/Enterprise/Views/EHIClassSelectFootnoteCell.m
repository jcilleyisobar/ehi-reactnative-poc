//
//  EHIClassSelectFootnoteCell.m
//  Enterprise
//
//  Created by mplace on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFootnoteCell.h"
#import "EHIClassSelectFootnoteViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIClassSelectFootnoteCell ()
@property (strong, nonatomic) EHIClassSelectFootnoteViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UILabel *makeModelLabel;
@property (weak, nonatomic) IBOutlet UILabel *sourceCurrencyLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *sourceCurrencyPadding;
@end

@implementation EHIClassSelectFootnoteCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectFootnoteViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectFootnoteViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.makeModelTitle)       : dest(self, .makeModelLabel.attributedText),
        source(model.sourceCurrencyTitle)  : dest(self, .sourceCurrencyLabel.attributedText),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    // if we need to hide the source currency label, base the content size off of the make model label frame
    UIView *view = self.viewModel.hidesSourceCurrencyTitle ? self.makeModelLabel : self.sourceCurrencyLabel;
    CGRect bottomFrame = [view convertRect:view.bounds toView:self];

    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHIMediumPadding
    };
}

@end
