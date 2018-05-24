//
//  EHIReservationPriceMileageCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceMileageCell.h"
#import "EHIReservationPriceMileageViewModel.h"

@interface EHIReservationPriceMileageCell ()
@property (strong, nonatomic) EHIReservationPriceMileageViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet UIView *subtitleContainer;
@end

@implementation EHIReservationPriceMileageCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationPriceMileageViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPriceMileageViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSubtitle:)];
    
    model.bind.map(@{
        source(model.title)        : dest(self, .titleLabel.text),
        source(model.subtitle)     : dest(self, .subtitleLabel.text),
    });
}

- (void)invalidateSubtitle:(MTRComputation *)computation
{
    BOOL hasSubtitle = self.viewModel.subtitle.length > 0;
    
    MASLayoutPriority priority = hasSubtitle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.subtitleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat height = CGRectGetHeight(self.titleContainer.frame) + CGRectGetHeight(self.subtitleContainer.frame);
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + 8.0f
    };
}

@end
