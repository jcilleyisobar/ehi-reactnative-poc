//
//  EHIConfirmationCarClassCell.m
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationCarClassCell.h"
#import "EHIConfirmationCarClassViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIConfirmationCarClassCell ()
@property (strong, nonatomic) EHIConfirmationCarClassViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *transmissionLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *transmissionIconWidth;
@end

@implementation EHIConfirmationCarClassCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationCarClassViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationCarClassViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerTitle)       : dest(self, .headerLabel.text),
        source(model.carClassNameTitle) : dest(self, .titleLabel.text),
        source(model.makeModelTitle)    : dest(self, .subtitleLabel.text),
        source(model.transmissionTitle) : dest(self, .transmissionLabel.text),
        source(model.isAutomatic)       : dest(self, .transmissionIconWidth.isDisabled),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect frame = [self.contentContainer convertRect:self.contentContainer.frame toView:self.contentView];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame)
    };
}

@end
