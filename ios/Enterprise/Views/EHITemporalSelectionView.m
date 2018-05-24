//
//  EHITemporalSelectionView.m
//  Enterprise
//
//  Created by Rafael Ramos on 03/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITemporalSelectionView.h"
#import "EHITemporalSelectionViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHITemporalSelectionView ()
@property (strong, nonatomic) EHITemporalSelectionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *valueLabel;
@property (weak  , nonatomic) IBOutlet UIView *buttonContainer;
@end

@implementation EHITemporalSelectionView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITemporalSelectionViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITemporalSelectionViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateButtonContainer:)];
    
    model.bind.map(@{
        source(model.valueString) : dest(self, .valueLabel.attributedText),
    });
}

- (void)invalidateButtonContainer:(MTRComputation *)computation
{
    CGFloat alpha = self.viewModel.hideClear ? 0.0f : 1.0f;
    
    [UIView animateWithDuration:0.2 animations:^{
        self.buttonContainer.alpha  = alpha;
        self.buttonContainer.hidden = self.viewModel.hideClear;
    }];
}

# pragma mark - Actions

- (IBAction)didTapCleanValue:(UIControl *)sender
{
    [self ehi_performAction:@selector(temporalSelectionViewDidTapClean:) withSender:self];
}

- (IBAction)didTap:(UIControl *)sender
{
    [self ehi_performAction:@selector(temporalSelectionViewDidTap:) withSender:self];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = self.valueLabel.intrinsicContentSize.width,
        .height = EHILayoutValueNil
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
