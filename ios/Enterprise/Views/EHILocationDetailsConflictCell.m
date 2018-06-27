//
//  EHILocationDetailsConflictCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/7/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDetailsConflictCell.h"
#import "EHILocationDetailsConflictViewModel.h"

@interface EHILocationDetailsConflictCell ()
@property (strong, nonatomic) EHILocationDetailsConflictViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *openHoursLabel;
@end

@implementation EHILocationDetailsConflictCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHILocationDetailsConflictViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsConflictViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
		source(model.title) : dest(self, .titleLabel.text),
		source(model.openHours) : dest(self, .openHoursLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.openHoursLabel.frame) + 8.0f
    };
}

@end
